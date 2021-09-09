package org.bigspring.service;

import org.apache.lucene.search.Query;
import org.bigspring.model.CardEntity;
import org.bigspring.model.CardGroupEntity;
import org.bigspring.model.RevisionControlBean;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.*;

@Service("cardGroupService")
public class CardGroupService extends AbstractService<CardGroupEntity> {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    @Qualifier("cardGroupRepository")
    private CardGroupRepository groupRepo;

    @Autowired
    @Qualifier("cardService")
    private CardService cardSvc;

    @Transactional
    public List<CardGroupEntity> fuzzySearch(String searchTerm) {

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(CardGroupEntity.class).get();
        Query luceneQuery = qb.keyword()
                .fuzzy()
                .withEditDistanceUpTo(1)
                .withPrefixLength(1)
                .onFields("title", "title-analyzed")
                .matching(searchTerm)
                .createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, CardGroupEntity.class);

        List<CardGroupEntity> groups = null;
        try {
            groups = jpaQuery.getResultList();
        } catch (NoResultException nre) {
            //no-op
        }

        return groups;
    }

    @Transactional(readOnly = true, noRollbackFor = {javax.persistence.NoResultException.class})
    public List<CardGroupEntity> findAllCardGroups() {

        var all = groupRepo.findAll();
        return (all);
    }

    @Transactional(readOnly = true, noRollbackFor = {javax.persistence.NoResultException.class})
    public CardGroupEntity findById(Long id) {

        CardGroupEntity group = groupRepo.findById(id).orElseThrow(() -> {return new IllegalArgumentException("Could not find card group with id - " + id);});

        return (group);
    }

    @Transactional
    public List<CardGroupEntity> saveAll(List<CardGroupEntity> cardGroups) {

        List<CardGroupEntity> groups2 = new ArrayList<>();

        if (cardGroups != null && !cardGroups.isEmpty()) {

            for (CardGroupEntity group : cardGroups) {

                CardGroupEntity group2 = null;
                if (group.getId() == null)
                    group2 = preProcess(PRE_INSERT, group, true);
                else
                    group2 = preProcess(PRE_UPDATE, group, true);

                HashSet<CardEntity> cards = new HashSet<>(group2.getCards());

                if (group.getCards() != null && !group.getCards().isEmpty()) {

                    for (CardEntity card : group.getCards()) {

                        //CardEntity card = iter1.next();
                        CardEntity card2 = new CardEntity();

                        cards.remove(card);

                        if (card.getId() == null) {

                            card2 = cardSvc.preProcess(PRE_INSERT, card, false);

                        } else {

                            card2 = cardSvc.preProcess(PRE_UPDATE, card, false);

                        }

                        card.setCardGroupRef(null);
                        card2.setCardGroupRef(group2);
                        cards.add(card2);

                    } // end for cards
                } //end if

                group2.setCards(cards);
                groups2.add(group2);

            } // end for groups

        }

        var saved = groupRepo.saveAll((Iterable<CardGroupEntity>) groups2);
        return (saved);
    }

    @Transactional
    public CardGroupEntity save(CardGroupEntity group) {

        CardGroupEntity group2 = null;

        if (group.getId() == null)
            group2 = preProcess(PRE_INSERT, group, true);
        else
            group2 = preProcess(PRE_UPDATE, group, true);

        HashSet<CardEntity> cards = new HashSet<>(group2.getCards());

        if (group.getCards() != null && !group.getCards().isEmpty()) {

            for (CardEntity card : group.getCards()) {

                //CardEntity card = iter1.next();
                CardEntity card2 = new CardEntity();

                cards.remove(card);

                if (card.getId() == null) {

                    card2 = cardSvc.preProcess(PRE_INSERT, card, false);

                } else {

                    card2 = cardSvc.preProcess(PRE_UPDATE, card, false);

                }

                card.setCardGroupRef(null);
                card2.setCardGroupRef(group2);
                cards.add(card2);

            } // end for cards
        } //end if

        group2.setCards(cards);

        var saved = groupRepo.save(group2);

        return (saved);
    }

    @Transactional
    public boolean deleteAll() {
        groupRepo.deleteAll();
        return (true);
    }

    @Transactional
    public List<CardGroupEntity> deleteAll(List<Long> iDs) {
        ArrayList<CardGroupEntity> groups = new ArrayList<>();
        if (iDs != null && !iDs.isEmpty()) {
            iDs.forEach( id -> {
                    var entity = groupRepo.findById(id).orElseThrow(() -> {return new IllegalArgumentException("Could not find card group with id - " + id);});
                    groups.add(entity);
            });
        }

        return (groups);
    }

    @Transactional
    public CardGroupEntity delete(Long id) {
        var group = groupRepo.findById(id).orElseThrow(() -> { return new IllegalArgumentException("Could not find card group with id - " + id);});
        groupRepo.delete(group);

        return(group);
    }

    @Override
    @Transactional
    public CardGroupEntity preProcess(int type, CardGroupEntity entity, boolean valParent) {
        CardGroupEntity group = null;
        switch (type) {
            case PRE_INSERT:
                group = preProcessAdd(entity);
                break;
            case PRE_UPDATE:
                group = preProcessUpd(entity);
                break;
            case PRE_DELETE:
                group = preProcessDel(entity);
                break;
            default:
                throw new IllegalArgumentException("invalid processing type - " + type + " for - " + entity.getTitle());
        }

        return group;
    }

    private CardGroupEntity preProcessAdd(CardGroupEntity entity) {

        if (entity.getRevisionControl() == null) {
            entity.setRevisionControl(makeRevision());
        }

        return entity;
    }

    private CardGroupEntity preProcessUpd(CardGroupEntity entity) {

        if (entity.getId() == null)
            throw new IllegalArgumentException("ID cannot be null for update of card group - " + entity.getTitle());

        var entity2 = groupRepo.getOne(entity.getId());
        if (entity2 == null)
            throw new IllegalArgumentException("Could not determine card group with ID - " + entity.getId());

        if (entity.getTitle() != null && !"".equals(entity.getTitle())) {
            entity2.setTitle(entity.getTitle());
        }
        entity2.setEnabled(entity.isEnabled());

        var rev = makeRevision();
        entity2.getRevisionControl().setRevisionDate(rev.getRevisionDate());
        entity2.getRevisionControl().setRevisedById(rev.getRevisedById());
        entity2.getRevisionControl().setRevisedBy(rev.getRevisedBy());

        return entity2;
    }

    private CardGroupEntity preProcessDel(CardGroupEntity entity) {

        if (entity.getId() == null)
            throw new IllegalArgumentException("ID cannot be null for update of card group - " + entity.getTitle());

        var entity2 = groupRepo.getOne(entity.getId());
        if (entity2 == null)
            throw new IllegalArgumentException("Could not determine card group with ID - " + entity.getId());

        return (entity2);
    }

}
