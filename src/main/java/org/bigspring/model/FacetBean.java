package org.bigspring.model;

import java.io.Serializable;

public class FacetBean implements Serializable {

    private String facetName;

    private int count;

    public String getFacetName() {
        return facetName;
    }

    public void setFacetName(String facetName) {
        this.facetName = facetName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
