package org.bigspring.model;

import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public class BaseEntity implements Serializable {

    protected static final int HASHCODE_SEED = 31;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @DocumentId
    protected Long id;

    @Column(name = "dyn_attribute1", nullable=true)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    protected String dynAttribute1;

    @Embedded
    @IndexedEmbedded
    protected RevisionControlBean revisionControl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDynAttribute1() {
        return dynAttribute1;
    }

    public void setDynAttribute1(String dynAttribute1) {
        this.dynAttribute1 = dynAttribute1;
    }

    public RevisionControlBean getRevisionControl() {
        return revisionControl;
    }

    public void setRevisionControl(RevisionControlBean revisionControl) {
        this.revisionControl = revisionControl;
    }
}
