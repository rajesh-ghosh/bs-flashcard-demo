package org.bigspring.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;


@Entity
@Table(name = "bs_locales", uniqueConstraints = {@UniqueConstraint(columnNames = {"locale_code"})})
@AttributeOverride(name="id", column=@Column(name="locale_id", nullable=false))
@Indexed(index = "BSDataIndex")
@NamedQueries({
        @NamedQuery(name = "LocaleEntity.findByLocale", query = "SELECT t from LocaleEntity t WHERE t.locale = :locale "),
        @NamedQuery(name = "LocaleEntity.findByEnabledLocales", query = "SELECT t from LocaleEntity t WHERE t.enabled = true ")
})
@JsonFilter("lovFilter")
public class LocaleEntity extends BaseEntity {

    @Column(name="locale_code", nullable = false)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private String locale;

    @Column(name="locale_name", nullable = false)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private String name;

    @Column(name="iso_charset", nullable = false)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private String isoCharSet;

    @Column(name="rtl_lang_flag", nullable = false)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private boolean rtlLang;

    @Column(name="enabled_flag", nullable = false)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private boolean enabled;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsoCharSet() {
        return isoCharSet;
    }

    public void setIsoCharSet(String isoCharSet) {
        this.isoCharSet = isoCharSet;
    }

    public boolean isRtlLang() {
        return rtlLang;
    }

    public void setRtlLang(boolean rtlLang) {
        this.rtlLang = rtlLang;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
