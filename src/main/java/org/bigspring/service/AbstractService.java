package org.bigspring.service;

import org.bigspring.model.RevisionControlBean;

import java.util.Date;

public abstract class AbstractService<T> {

    protected RevisionControlBean makeRevision() {

        var rev = new RevisionControlBean();
        rev.setCreatedBy("admin");
        rev.setCreatedById(0);
        rev.setCreationDate(new Date());
        rev.setRevisedBy("admin");
        rev.setRevisedById(0);
        rev.setRevisionDate(new Date());

        return rev;
    }

    public abstract T preProcess(int type, T entity, boolean valParent);

    public static final int PRE_INSERT = 1;
    public static final int PRE_UPDATE = 2;
    public static final int PRE_DELETE = 3;

    public static final int POST_INSERT = 4;
    public static final int POST_UPDATE = 5;

}
