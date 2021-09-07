package org.bigspring.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.ngram.NGramFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Parameter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bs_card_groups", uniqueConstraints = {@UniqueConstraint(columnNames = {"card_group_title"})})
@AttributeOverride(name="id", column=@Column(name="card_group_id", nullable=false))
@Indexed(index = "BSDataIndex")
@AnalyzerDef(name = "analyzerSet1",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class ),
        filters = {
                @TokenFilterDef(factory = StandardFilterFactory.class),
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = StopFilterFactory.class),
                @TokenFilterDef(factory = NGramFilterFactory.class,
                        params = {
                                @Parameter(name = "minGramSize", value = "3"),
                                @Parameter(name = "maxGramSize", value = "3") } )
        }
)
@NamedQueries({
        @NamedQuery(name = "CardGroupEntity.findByTitle", query = "SELECT t from CardGroupEntity t WHERE t.title = :title ")
})
@JsonFilter("lovFilter")
public class CardGroupEntity extends BaseEntity {

    @Column(name="card_group_title", nullable = false)
    @Fields({
        @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO),
        @Field(name="title-analyzed", analyzer = @Analyzer(definition = "analyzerSet1"))
    })
    private String title;

    @Column(name="enabled_flag", nullable = false)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private boolean enabled;

    @JsonManagedReference
    @OneToMany(mappedBy="cardGroupRef", cascade={CascadeType.ALL}, orphanRemoval=true)
    @IndexedEmbedded(includeEmbeddedObjectId=true, depth=1)
    private Set<CardEntity> cards = new HashSet<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<CardEntity> getCards() {
        return cards;
    }

    public void setCards(Set<CardEntity> cards) {
        this.cards.clear();
        if (cards != null)
            this.cards.addAll(cards);
    }
}
