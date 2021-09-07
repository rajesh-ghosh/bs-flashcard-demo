package org.bigspring.service;

import org.bigspring.model.LocaleEntity;
import org.bigspring.model.RevisionControlBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
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
        if (locales != null && !locales.isEmpty()) {
            locales.forEach(locale -> { if (locale.getRevisionControl() == null) locale.setRevisionControl(makeRevision()); });
        }
        var saved = localeRepo.saveAll((Iterable<LocaleEntity>) locales);
        return saved;
    }

    @Transactional
    public LocaleEntity save(LocaleEntity locale) {

        if (locale.getRevisionControl() == null)
            locale.setRevisionControl(makeRevision());

        var locale2 = localeRepo.save(locale);
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
