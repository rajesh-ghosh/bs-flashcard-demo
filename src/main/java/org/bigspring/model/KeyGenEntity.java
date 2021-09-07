package org.bigspring.model;

import javax.persistence.*;

@Entity
@Table(name = "bs_key_gen")
public class KeyGenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "key_id", nullable = false, updatable=false)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
