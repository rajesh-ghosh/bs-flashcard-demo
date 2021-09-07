package org.bigspring.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFilter;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.*;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bs_cards")
@AttributeOverride(name="id", column=@Column(name="card_id", nullable=false))
@Indexed(index = "BSDataIndex")
@JsonFilter("lovFilter")
public class CardEntity extends BaseEntity {

    @Column(name="challenge_type", nullable = false)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    @Enumerated(EnumType.STRING)
    private AllEnums.CardType challengeType;

    @Column(name="challenge_text", nullable = true)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO),
            @Field(name="challenge-analyzed", analyzer = @Analyzer(definition = "analyzerSet1"))
    })
    private String challengeText;

    @Column(name="challenge_image_file", nullable = true)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private String challengeImageFileLoc;

    @Column(name="challenge_sound_file", nullable = true)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private String challengeSoundFileLoc;

    @Column(name="answer_type", nullable = false)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    @Enumerated(EnumType.STRING)
    private AllEnums.CardType answerType;

    @Column(name="answer_text", nullable = true)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO),
            @Field(name="answer-analyzed", analyzer = @Analyzer(definition = "analyzerSet1"))
    })
    private String answerText;

    @Column(name="answer_image_file", nullable = true)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private String answerImageFileLoc;

    @Column(name="answer_sound_file", nullable = true)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private String answerSoundFileLoc;

    @Column(name="complexity", nullable = true)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    @Enumerated(EnumType.STRING)
    private AllEnums.Complexity complexity;

    @Column(name="tag_string", nullable = false)
    @Fields({
            @Field(name="tags-raw", index = Index.YES, analyze = Analyze.NO, store = Store.NO),
            @Field(name = "tags-analyzed", analyzer = @Analyzer(definition = "analyzerSet1"))
    })
    @Facet(forField = "tags-raw")
    private String tagString;

    @Transient
    @IndexedEmbedded
    @Field(name="tags-int-raw", index = Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Facet(forField = "tags-int-raw")
    private Set<String> tagStringInt = new HashSet<>();

    @JsonBackReference
    @ManyToOne(optional=false)
    @JoinColumn(name="card_group_ref", nullable=false)
    @ContainedIn
    private CardGroupEntity cardGroupRef;

    @Transient
//    @Fields({
//            @Field(name="group-title-raw", index = Index.YES, analyze = Analyze.NO, store = Store.NO),
//            @Field(name = "group-title-analyzed", analyzer = @Analyzer(definition = "analyzerSet1"))
//    })
    private String cardGroupTitle;

//    @OneToMany(mappedBy="cardRef", cascade={CascadeType.ALL}, orphanRemoval=true)
//    @IndexedEmbedded(includeEmbeddedObjectId=true, depth=1)
//    private Set<CardTLEntity> translations = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name="card_translations", nullable = true)
    @IndexedEmbedded(depth = 1)
    private Set<CardTLBean> translations = new HashSet<>();

    public AllEnums.CardType getChallengeType() {
        return challengeType;
    }

    public void setChallengeType(AllEnums.CardType challengeType) {
        this.challengeType = challengeType;
    }

    public String getChallengeText() {
        return challengeText;
    }

    public void setChallengeText(String challengeText) {
        this.challengeText = challengeText;
    }

    public String getChallengeImageFileLoc() {
        return challengeImageFileLoc;
    }

    public void setChallengeImageFileLoc(String challengeImageFileLoc) {
        this.challengeImageFileLoc = challengeImageFileLoc;
    }

    public String getChallengeSoundFileLoc() {
        return challengeSoundFileLoc;
    }

    public void setChallengeSoundFileLoc(String challengeSoundFileLoc) {
        this.challengeSoundFileLoc = challengeSoundFileLoc;
    }

    public AllEnums.CardType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(AllEnums.CardType answerType) {
        this.answerType = answerType;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public String getAnswerImageFileLoc() {
        return answerImageFileLoc;
    }

    public void setAnswerImageFileLoc(String answerImageFileLoc) {
        this.answerImageFileLoc = answerImageFileLoc;
    }

    public String getAnswerSoundFileLoc() {
        return answerSoundFileLoc;
    }

    public void setAnswerSoundFileLoc(String answerSoundFileLoc) {
        this.answerSoundFileLoc = answerSoundFileLoc;
    }

    public AllEnums.Complexity getComplexity() {
        return complexity;
    }

    public void setComplexity(AllEnums.Complexity complexity) {
        this.complexity = complexity;
    }

    public String getTagString() {
        return tagString;
    }

    public void setTagString(String tagString) {
        //System.out.println("### tagString setter called");
        this.tagString = tagString;
        if (tagString != null && !"".equals(tagString)) {
            String[] tokens = tagString.split("\\s+");
            var list = new HashSet<>(Arrays.asList(tokens));
            setTagStringInt(list);
        }
    }

    public Set<String> getTagStringInt() {
        return tagStringInt;
    }

    public void setTagStringInt(Set<String> tagStringInt) {
        this.tagStringInt.clear();
        if (tagStringInt != null)
            this.tagStringInt.addAll(tagStringInt);
    }

    public CardGroupEntity getCardGroupRef() {
        return cardGroupRef;
    }

    public void setCardGroupRef(CardGroupEntity cardGroupRef) {
        this.cardGroupRef = cardGroupRef;
    }

    public String getCardGroupTitle() {
        return cardGroupTitle;
    }

    public void setCardGroupTitle(String cardGroupTitle) {
        this.cardGroupTitle = cardGroupTitle;
    }

    public Set<CardTLBean> getTranslations() {
        return translations;
    }

    public void setTranslations(Set<CardTLBean> translations) {
        this.translations.clear();
        if (translations != null)
            this.translations.addAll(translations);
    }

    @PostLoad
    @PostConstruct
    private void postLoad() {
        if (tagString != null && !"".equals(tagString)) {
            String[] tokens = tagString.split("\\s+");
            var list = new HashSet<>(Arrays.asList(tokens));
            setTagStringInt(list);
        }
    }

}
