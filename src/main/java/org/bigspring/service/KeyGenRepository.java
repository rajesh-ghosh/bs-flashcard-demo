package org.bigspring.service;


import org.bigspring.model.KeyGenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("keyGenRepository")
public interface KeyGenRepository extends JpaRepository<KeyGenEntity, Long> {

}
