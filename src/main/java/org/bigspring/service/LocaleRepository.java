package org.bigspring.service;

import org.bigspring.model.LocaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.List;

@Repository("localeRepository")
public interface LocaleRepository extends JpaRepository<LocaleEntity, Long> {

    @Transactional(readOnly = true, noRollbackFor = {NoResultException.class})
    public LocaleEntity findByLocale(String locale);

    @Transactional(readOnly = true, noRollbackFor = {NoResultException.class})
    public List<LocaleEntity> findByEnabledLocales();

}
