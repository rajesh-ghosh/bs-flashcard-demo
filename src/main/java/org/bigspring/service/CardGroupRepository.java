package org.bigspring.service;

import org.bigspring.model.CardGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

@Repository("cardGroupRepository")
public interface CardGroupRepository extends JpaRepository<CardGroupEntity, Long> {

    @Transactional(readOnly = true, noRollbackFor = {NoResultException.class})
    public CardGroupEntity findByTitle(String title);

}
