package org.bigspring.service;

import org.apache.lucene.search.Query;
import org.bigspring.common.CardsSummaryBean;
import org.bigspring.model.*;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.engine.spi.FacetManager;
import org.hibernate.search.query.facet.Facet;
import org.hibernate.search.query.facet.FacetSortOrder;
import org.hibernate.search.query.facet.FacetingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service("cardService")
public class CardService extends AbstractService<CardEntity> {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    @Qualifier("cardRepository")
    private CardRepository cardRepo;

    @Autowired
    @Qualifier("cardGroupRepository")
    private CardGroupRepository groupRepo;

    @Autowired
    @Qualifier("localeRepository")
    private LocaleRepository locRepo;

    @Autowired
    @Qualifier("localeService")
    private LocaleService locSvc;

    @Autowired
    @Qualifier("keyGenRepository")
    private KeyGenRepository keyGenRepo;

    @Value("${app.base-locale:en_US}")
    private String BASE_LOCALE;

    @Value("${app.file-upload.image-folder}")
    private String imageFolder;

    private static final Long FILE_OFFSET = 100000L;

    private static final Logger logger = Logger.getLogger(CardEntity.class.getName());

    @Transactional
    public List<CardEntity> fuzzySearch(String searchTerm) {

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(CardEntity.class).get();
        Query luceneQuery = qb.keyword()
                .fuzzy()
                .withEditDistanceUpTo(1)
                .withPrefixLength(1)
                .onFields("challengeText", "answerText", "tags-raw", "challenge-analyzed", "answer-analyzed", "tags-analyzed")
                .matching(searchTerm)
                .createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, CardEntity.class);

        // execute search

        List<CardEntity> cards = null;
        try {
            cards = jpaQuery.getResultList();
            processGroupTitle(cards);
        } catch (NoResultException nre) {
            //no-op
            logger.info("### fuzzy search yielded no result !");
        }

        logger.info("### fuzzy search yielded " + (cards == null ? 0 : cards.size()) + " result(s)");

        return cards;
    }

    @Transactional
    public List<CardEntity> wildCardSearch(String term) {

        if (term == null)
            return null;

        String term2 = new String(term);
        if (!(term.contains("*"))) {
            term2 = term2.replaceAll("\\s+", "*");
        }
        if (!term2.endsWith("*"))
            term2 = term2 + "*";


        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(CardEntity.class).get();

        var fields = new String[]{"challengeText", "answerText", "tags-raw", "challenge-analyzed", "answer-analyzed", "tags-analyzed"};
        var cards = new ArrayList<CardEntity>();

        for (String field : fields) {
            Query luceneQuery = qb
                    .keyword()
                    .wildcard()
                    .onField(field)
                    .matching(term2)
                    .createQuery();

            javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, CardEntity.class);

            // execute search

            List<CardEntity> cards2 = null;
            try {
                cards2 = jpaQuery.getResultList();
                processGroupTitle(cards2);
            } catch (NoResultException nre) {
                //no-op
                logger.info("### wildcard search yielded no result !");
            }

            logger.info("### wildcard search yielded " + (cards2 == null ? 0 : cards2.size()) + " result(s)");

            cards.addAll(cards2);
        }

        return(cards);

    }

    @Transactional
    public List<FacetBean> tagCloud() {

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder builder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(CardEntity.class).get();

        FacetingRequest tagFaceting = builder.facet()
                .name("tagFaceting")
                .onField("tags-int-raw")
                .discrete()
                .orderedBy(FacetSortOrder.COUNT_DESC)
                .includeZeroCounts(false)
                .createFacetingRequest();

        Query luceneQuery = builder.all().createQuery();
        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery);

        FacetManager facetManager = fullTextQuery.getFacetManager();
        facetManager.enableFaceting(tagFaceting);

        List<Facet> facets = facetManager.getFacets("tagFaceting");
        logger.info("### tag cloud yielded " + (facets == null ? 0 : facets.size()) + " facet(s)");
        ArrayList<FacetBean> beans = new ArrayList<>();

        for (Facet f : facets) {
            //System.out.println( "### TagCloud : - " + f.getValue() + " (" + f.getCount() + ")");
            var facet = new FacetBean();
            facet.setValue(f.getValue()); facet.setCount(f.getCount());
            beans.add(facet);
        }

        return(beans);
    }

    @Transactional(readOnly = true, noRollbackFor = {NoResultException.class})
    public List<CardEntity> findAll() {
        var all = cardRepo.findAll();
        processGroupTitle(all);
        return (all);
    }

    @Transactional(readOnly = true, noRollbackFor = {NoResultException.class})
    public CardEntity findById(Long id) {
        var card = cardRepo.findById(id).orElseThrow(() -> {return new IllegalArgumentException("Could not find card with id - " + id);});
        processGroupTitle(card);
        return(card);
    }

    @Transactional
    public List<CardEntity> saveAll(List<CardEntity> cards) {

        List<CardEntity> cards2 = new ArrayList<>();

        if (cards != null && !cards.isEmpty()) {
            for (CardEntity card : cards) {

                CardEntity card2 = null;
                if (card.getId() == null) {
                    card2 = preProcess(PRE_INSERT, card, true);
                } else {
                    card2 = preProcess(PRE_UPDATE, card, true);
                }

                cards2.add(card2);
            }
        }

        var saved = cardRepo.saveAll(cards2);
        processGroupTitle(saved);

        return(saved);
    }

    @Transactional
    CardEntity save(CardEntity card) {

        CardEntity card2 = null;
        if (card.getId() == null) {
            card2 = preProcess(PRE_INSERT, card, true);
        } else {
            card2 = preProcess(PRE_UPDATE, card, true);
        }

        var saved = cardRepo.save(card2);
        processGroupTitle(saved);

        return (saved);
    }

//    @Transactional
//    public CardEntity save(MultipartFile challengeImgFile, MultipartFile answerImgFile, CardEntity card) {
//
//        if (challengeImgFile != null) {
//            KeyGenEntity key = keyGenRepo.save(new KeyGenEntity());
//            Long offset = FILE_OFFSET + key.getId();
//            String fname = "IMG" + Long.toString(offset) + ".jpg";
//
//            Path target = Paths.get(imageFolder).resolve(fname);
//            try {
//                Files.copy(challengeImgFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
//            } catch (IOException e) {
//                throw new IllegalStateException("Exception while uploading file - " + challengeImgFile.getName());
//            }
//            card.setChallengeType(CardType.PICTURE);
//            card.setChallengeImageFileLoc(fname);
//        }
//
//        if (answerImgFile != null) {
//            KeyGenEntity key = keyGenRepo.save(new KeyGenEntity());
//            Long offset = FILE_OFFSET + key.getId();
//            String fname = "IMG" + Long.toString(offset) + ".jpg";
//
//            Path target = Paths.get(imageFolder).resolve(fname);
//            try {
//                Files.copy(answerImgFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
//            } catch (IOException e) {
//                throw new IllegalStateException("Exception while uploading file - " + answerImgFile.getName());
//            }
//            card.setAnswerType(CardType.PICTURE);
//            card.setAnswerImageFileLoc(fname);
//        }
//
//        var card2 = save(card);
//        return (card2);
//    }

    @Transactional
    public CardEntity addTranslations(Long id, List<CardTLBean> tlBeans) {

        var card = cardRepo.findById(id).orElseThrow(() -> {return new IllegalArgumentException("Could not find card with id - " + id);});

        var tlBeans2 = processTl(tlBeans);
        card.getTranslations().addAll(tlBeans2);

        var card2 = save(card);

        return(card2);
    }

    @Transactional
    public boolean deleteAll() {
        cardRepo.deleteAll();
        return true;
    }

    @Transactional
    public List<CardEntity> deleteAll(List<Long> iDs) {

        ArrayList<CardEntity> cards = new ArrayList<>();
        if (iDs != null && !iDs.isEmpty()) {
            iDs.forEach( id -> {
                var entity = cardRepo.findById(id).orElseThrow(() -> {return new IllegalArgumentException("Could not find card group with id - " + id);});
                cards.add(entity);
            });
        }

        return (cards);
    }

    @Transactional
    public CardsSummaryBean getCardsSummary() {
        var cards = cardRepo.findAll();
        var bean = new CardsSummaryBean();
        if (cards != null && !cards.isEmpty()) {
            int all = cards.size();
            bean.setTotalCards(cards.size());
            Set<String> locCodes = locSvc.getLocaleCodes(true);
            var fullTls = cards.stream().filter(card -> getCardLocales(card).containsAll(locCodes)).collect(Collectors.toList());
            int countFullTls = fullTls == null ? 0 : fullTls.size();
            int countPartTls = all - countFullTls;
            bean.setFullTlCards(countFullTls);
            bean.setPartTlCards(countPartTls);
        }

        return (bean);
    }

    @Override
    @Transactional
    public CardEntity preProcess(int type, CardEntity entity, boolean valParent) {
        CardEntity processed = null;
        switch(type) {
            case PRE_INSERT:
                processed = preProcessAdd(entity, valParent);
                break;
            case PRE_UPDATE:
                processed = preProcessUpd(entity, valParent);
                break;
            case PRE_DELETE:
                break;
            default:
                throw new IllegalArgumentException("invalid processing type - " + type + " for - " + entity.getChallengeText());

        }
        return processed;
    }

    private CardEntity preProcessAdd(CardEntity entity, boolean valParent) {

        if (entity.getRevisionControl() == null)
            entity.setRevisionControl(makeRevision());

        validateContent(entity);

        CardGroupEntity group = entity.getCardGroupRef();
        if (group != null && valParent) {
            if (group.getId() == null)
                throw new IllegalStateException("Card group id cannot be null for adding new card - " + entity.getChallengeText());

            var group2 = groupRepo.findById(group.getId()).orElseThrow(() -> {return new IllegalArgumentException("Could not find card group with id - " + group.getId());});
            entity.setCardGroupRef(group2);
            group2.getCards().add(entity);
        }

        processTl(entity);

        return(entity);
    }

    private CardEntity preProcessUpd(CardEntity entity, boolean valParent) {

        if (entity.getId() == null)
            throw new IllegalStateException("ID cannot be null for update in card - " + entity.getChallengeText());

        var entity2 = cardRepo.getOne(entity.getId());
        if (entity2 == null)
            throw new IllegalArgumentException("Could not find card entity for ID - " + entity.getId());

        CardGroupEntity group = entity.getCardGroupRef();
        if (group != null && valParent) {
            if (group.getId() == null)
                throw new IllegalStateException("Card group id cannot be null for adding new card - " + entity.getChallengeText());

            var group2 = groupRepo.findById(group.getId()).orElseThrow(() -> {return new IllegalArgumentException("Could not find card group with id - " + group.getId());});
            entity.setCardGroupRef(group2);
            //group2.getCards().add(entity);
        }

        HashSet<CardTLBean> tl1 = new HashSet<>(entity.getTranslations());
        HashSet<CardTLBean> tl2 = new HashSet<>(entity2.getTranslations());

        if (!tl2.containsAll(tl1)) {
            // new language added
            HashSet<CardTLBean> tl3 = new HashSet<>();
            Iterator<CardTLBean> iter1 = tl1.iterator();
            while (iter1 != null && iter1.hasNext()) {
                CardTLBean bean1 = iter1.next();
                boolean found = false;
                Iterator<CardTLBean> iter2 = tl2.iterator();
                while (iter2 != null && iter2.hasNext()) {
                    CardTLBean bean2 = iter2.next();
                    if (Objects.equals(bean1.getLocaleCode(), bean2.getLocaleCode())) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    tl3.add(bean1);
            }
            if (!tl3.isEmpty()) {
                tl2.addAll(tl3);
                entity2.setTranslations(tl2);
            }
        }

        processTl(entity2);

        var rev = makeRevision();
        entity2.getRevisionControl().setRevisionDate(rev.getRevisionDate());
        entity2.getRevisionControl().setRevisedById(rev.getRevisedById());
        entity2.getRevisionControl().setRevisedBy(rev.getRevisedBy());

        return entity2;
    }

    private void validateContent(CardEntity entity) {
        switch(entity.getAnswerType()) {
            case TEXT:
                if (entity.getAnswerText() == null || "".equals(entity.getAnswerText()))
                    throw new IllegalArgumentException("Answer text cannot be null when answer type is TEXT");
                break;
            case PICTURE:
                if (entity.getAnswerImageFileLoc() == null)
                    throw new IllegalArgumentException("Answer image file cannot be null when answer type is picture !");
                break;
            default:
                throw new IllegalArgumentException("Unsupported answer type - " + entity.getAnswerType());

        }
        switch(entity.getChallengeType()) {
            case TEXT:
                if (entity.getAnswerText() == null || "".equals(entity.getAnswerText()))
                    throw new IllegalArgumentException("Challenge text cannot be null when challenge type is TEXT");
                break;
            case PICTURE:
                if (entity.getAnswerImageFileLoc() == null)
                    throw new IllegalArgumentException("Challenge image file cannot be null when challenge type is picture !");
                break;
            default:
                throw new IllegalArgumentException("Unsupported challenge type - " + entity.getAnswerType());

        }
    }

    private void processGroupTitle(List<CardEntity> cards) {
        var groups  = groupRepo.findAll();
        HashMap<Long, String> titles = new HashMap<>();
        if (groups != null && !groups.isEmpty()) {
            groups.forEach(group -> { titles.put(group.getId(), group.getTitle()); });
        }

        if (cards != null && !cards.isEmpty()) {
            cards.forEach(card -> {
                if (card.getCardGroupRef() != null) {
                    card.setCardGroupTitle(titles.get(card.getCardGroupRef().getId()));
                }
            });
        }
    }

    private void processGroupTitle(CardEntity card) {

        processGroupTitle(Arrays.asList(new CardEntity[]{card}));
    }

    private void processTl(CardEntity entity) {

        var baseLoc = locRepo.findByLocale(BASE_LOCALE);
        if (baseLoc == null)
            throw new IllegalStateException("Base locale " + BASE_LOCALE + " not found !!");

        Set<CardTLBean> translations = entity.getTranslations();
        boolean addBaseTl = false;

        if (translations != null && !translations.isEmpty()) {
            boolean found = false;
            for (CardTLBean tl : translations) {
                var loc = tl.getLocaleRef() == null ? locRepo.findByLocale(tl.getLocaleCode()) : locRepo.findByLocale(tl.getLocaleRef().getLocale());
                if (loc == null)
                    throw new IllegalArgumentException("Could not determine locale from code in TL - " + tl.getLocaleCode() +
                            " OR from Locale info - " + (tl.getLocaleRef() == null ? "none" : tl.getLocaleRef().getLocale()));

                tl.setLocaleRef(loc);
                tl.setLocaleCode(loc.getLocale());
                if (loc.getLocale().equals(BASE_LOCALE))
                    found = true;

            }
            if (!found)
                addBaseTl = true;
        } else {
            addBaseTl = true;
        }

        if (addBaseTl) {
            var tl = new CardTLBean();
            tl.setAnwserImageFileLoc(entity.getAnswerImageFileLoc());
            tl.setAnswerText(entity.getAnswerText());
            tl.setChallengeImageFileLoc(entity.getChallengeImageFileLoc());
            tl.setChallengeText(entity.getChallengeText());
            tl.setLocaleCode(BASE_LOCALE);
            tl.setLocaleRef(baseLoc);
            entity.getTranslations().add(tl);
        }

    }

    private List<CardTLBean> processTl(List<CardTLBean> tlBeans) {

        var translations = new ArrayList<CardTLBean>(tlBeans);
        if (translations != null && !translations.isEmpty()) {

            for (CardTLBean tl : translations) {
                var loc = tl.getLocaleRef() == null ? locRepo.findByLocale(tl.getLocaleCode()) : locRepo.findByLocale(tl.getLocaleRef().getLocale());
                if (loc == null)
                    throw new IllegalArgumentException("Could not determine locale from code in TL - " + tl.getLocaleCode() +
                            " OR from Locale info - " + (tl.getLocaleRef() == null ? "none" : tl.getLocaleRef().getLocale()));

                tl.setLocaleRef(loc);
                tl.setLocaleCode(loc.getLocale());

            }
        }

        return(translations);
    }

    private Set<String> getCardLocales(CardEntity card) {

        Set<String> codes = new HashSet<>();

        Set<CardTLBean> tls = card.getTranslations();
        if (tls != null && !tls.isEmpty()) {
            tls.forEach(tl -> codes.add(tl.getLocaleCode()));
        }

        return(codes);
    }
}
