package org.bigspring.service;

import org.bigspring.model.LocaleEntity;
import org.bigspring.model.RevisionControlBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service("localeService")
public class LocaleService extends AbstractService<LocaleEntity> {

    @Autowired
    @Qualifier("localeRepository")
    private LocaleRepository localeRepo;

    @Transactional(readOnly=true, noRollbackFor={javax.persistence.NoResultException.class})
    public List<LocaleEntity> findAllLocales() {
        var all = localeRepo.findAll();
        return(all);
    }

    @Transactional(readOnly=true, noRollbackFor={javax.persistence.NoResultException.class})
    public List<LocaleEntity> findByEnabledLocales() {
        var enabled = localeRepo.findByEnabledLocales();
        return enabled;
    }

    @Transactional(readOnly=true, noRollbackFor={javax.persistence.NoResultException.class})
    public LocaleEntity findByLocale(String locale) {
        var one = localeRepo.findByLocale(locale);
        return  one;
    }

    @Transactional
    public List<LocaleEntity> saveAll(List<LocaleEntity> locales) {

        ArrayList<LocaleEntity> locales2 = new ArrayList<>();

        if (locales != null && !locales.isEmpty()) {

            for (LocaleEntity locale : locales) {
                if (locale.getId() == null) {
                    // new entity
                    if (locale.getRevisionControl() == null)
                        locale.setRevisionControl(makeRevision());

                    locales2.add(locale);
                } else {
                    // update
                    var locale2 = localeRepo.findById(locale.getId()).orElseThrow(() -> {return new IllegalArgumentException("Could not find locale with id - " + locale.getId()); });
                    locale2.setEnabled(locale.isEnabled());
                    locale2.setName(locale.getName());
                    locale2.setIsoCharSet(locale.getIsoCharSet());

                    var rev = makeRevision();
                    locale2.getRevisionControl().setRevisedBy(rev.getRevisedBy());
                    locale2.getRevisionControl().setRevisedById(rev.getRevisedById());
                    locale2.getRevisionControl().setRevisionDate(rev.getRevisionDate());

                    locales2.add(locale2);
                }
            }
        }

        var saved = localeRepo.saveAll((Iterable<LocaleEntity>) locales2);

        return saved;
    }

    @Transactional
    public LocaleEntity save(LocaleEntity locale) {

        LocaleEntity locale2 = null;

        if (locale != null) {
            var list = saveAll(Arrays.asList(new LocaleEntity[]{locale}));
            if (list != null && !list.isEmpty())
                locale2 = list.get(0);
        }

        return(locale2);
    }

    @Transactional
    public boolean deleteAll() {
        localeRepo.deleteAll();
        return true;
    }

    @Transactional
    public List<LocaleEntity> delete(List<Long> iDs) {
        ArrayList<LocaleEntity> locales = new ArrayList<>();
        if (iDs != null && !iDs.isEmpty()) {
            iDs.forEach(id -> {
                var entity = localeRepo.getOne(id);
                locales.add(entity);
            });
            localeRepo.deleteAll(locales);
        }
        return(locales);
    }

    @Override
    public LocaleEntity preProcess(int type, LocaleEntity entity, boolean valParent) {
        return(entity);
    }
}
