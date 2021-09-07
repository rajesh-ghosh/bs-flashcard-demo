package org.bigspring.model;

import org.hibernate.search.annotations.*;
import org.springframework.stereotype.Indexed;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Date;

@XmlRootElement(name="revisionControl", namespace="http://bigspringV1.com/schema/common")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"createdById", "createdBy", "creationDate", "revisedById", "revisedBy", "revisionDate"})
@Embeddable
@Indexed
public class RevisionControlBean implements Serializable {

    @XmlElement(name="createdById")
    @Column(name="created_by_id", nullable=false)
    private int createdById;

    @XmlElement(name="createdBy")
    @Column(name="created_by", nullable=false)
    @Field(analyze=Analyze.NO)
    private String createdBy;

    @XmlElement(name="createdDate")
    @Column(name="created_date", nullable=false)
    @Temporal(javax.persistence.TemporalType.DATE)
    @Fields({
            @Field,
            @Field(name="creationDate-sort", index=Index.YES, store= Store.YES, analyze=Analyze.NO)
    })
    //@SortableField
    @DateBridge(resolution= Resolution.SECOND)
    private Date creationDate;


    @XmlElement(name="revisedById")
    @Column(name="revised_by_id", nullable=false)
    private int revisedById;

    @XmlElement(name="revisedBy")
    @Column(name="revised_by", nullable=false)
    @Field(analyze=Analyze.NO)
    private String revisedBy;

    @XmlElement(name="revisedDate")
    @Column(name="revision_date", nullable=false)
    @Temporal(javax.persistence.TemporalType.DATE)
    @Fields({
            @Field,
            @Field(name="revisionDate-sort", index=Index.YES, store=Store.YES, analyze=Analyze.NO)
    })
    //@SortableField
    @DateBridge(resolution=Resolution.SECOND)
    private Date revisionDate;

    public int getCreatedById() {
        return createdById;
    }

    public void setCreatedById(int createdById) {
        this.createdById = createdById;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getRevisedById() {
        return revisedById;
    }

    public void setRevisedById(int revisedById) {
        this.revisedById = revisedById;
    }

    public String getRevisedBy() {
        return revisedBy;
    }

    public void setRevisedBy(String revisedBy) {
        this.revisedBy = revisedBy;
    }

    public Date getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(Date revisionDate) {
        this.revisionDate = revisionDate;
    }

}
