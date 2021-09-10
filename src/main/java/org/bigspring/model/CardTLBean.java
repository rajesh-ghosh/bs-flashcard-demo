package org.bigspring.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.search.annotations.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Indexed
public class CardTLBean implements Serializable {

    @Column(name="locale_code", nullable = false)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private String localeCode;

    @JsonBackReference
    @ManyToOne(optional=false)
    @JoinColumn(name="locale_ref", nullable=false)
    @ContainedIn
    private LocaleEntity localeRef;

    @Column(name="challenge_text", nullable = true)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO),
            @Field(name="challenge-tl-analyzed", analyzer = @Analyzer(definition = "analyzerSet1"))
    })
    private String challengeText;

    @Column(name="challenge_image_file", nullable = true)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private String challengeImageFileLoc;

    @Column(name="answer_text", nullable = true)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO),
            @Field(name="answer-tl-analyzed", analyzer = @Analyzer(definition = "analyzerSet1"))
    })
    private String answerText;

    @Column(name="answer_image_file", nullable = true)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    })
    private String answerImageFileLoc;

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

    public String getAnswerImageFileLoc() {
        return answerImageFileLoc;
    }

    public void setAnswerImageFileLoc(String answerImageFileLoc) {
        this.answerImageFileLoc = answerImageFileLoc;
    }
}
