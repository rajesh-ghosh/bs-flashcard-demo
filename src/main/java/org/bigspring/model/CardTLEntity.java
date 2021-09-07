package org.bigspring.model;

import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;

@Entity
@Table(name = "bs_cards_tl")
@AttributeOverride(name="id", column=@Column(name="card_tl_id", nullable=false))
@Indexed(index = "BSDataIndex")
//@NamedQueries({
//        @NamedQuery(name = "CardTLEntity.findByTitle", query = "SELECT t from CardTLEntity t WHERE t.title = :title ")
//})
public class CardTLEntity extends BaseEntity {

    @Column(name="locale_code", nullable = false)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private String localeCode;

    @ManyToOne(optional=false)
    @JoinColumn(name="locale_ref", nullable=false)
    @ContainedIn
    private LocaleEntity localeRef;

    @ManyToOne(optional=false)
    @JoinColumn(name="card_ref", nullable=false)
    @ContainedIn
    private CardEntity cardRef;

    @Column(name="challenge_text", nullable = true)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private String challengeText;

    @Column(name="challenge_image_file", nullable = true)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private String challengeImageFileLoc;

    @Column(name="answer_text", nullable = false)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private String answerText;

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public LocaleEntity getLocaleRef() {
        return localeRef;
    }

    public void setLocaleRef(LocaleEntity localeRef) {
        this.localeRef = localeRef;
    }

    public CardEntity getCardRef() {
        return cardRef;
    }

    public void setCardRef(CardEntity cardRef) {
        this.cardRef = cardRef;
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

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
