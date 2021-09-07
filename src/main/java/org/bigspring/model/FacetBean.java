package org.bigspring.model;

import java.io.Serializable;

public class FacetBean implements Serializable {

    private String value;

    private int count;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
