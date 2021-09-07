package org.bigspring.service;

import org.bigspring.model.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("card" +
        "Repository")
public interface CardRepository extends JpaRepository<CardEntity, Long> {

}
