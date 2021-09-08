package org.bigspring.common;

import java.io.Serializable;

public class CardsSummaryBean implements Serializable {

    private int totalCards;

    private int fullTlCards;

    private int partTlCards;

    public int getTotalCards() {
        return totalCards;
    }

    public void setTotalCards(int totalCards) {
        this.totalCards = totalCards;
    }

    public int getFullTlCards() {
        return fullTlCards;
    }

    public void setFullTlCards(int fullTlCards) {
        this.fullTlCards = fullTlCards;
    }

    public int getPartTlCards() {
        return partTlCards;
    }

    public void setPartTlCards(int partTlCards) {
        this.partTlCards = partTlCards;
    }

    @Override
    public String toString() {
        return "CardsSummaryBean{" +
                "totalCards=" + totalCards +
                ", fullTlCards=" + fullTlCards +
                ", partTlCards=" + partTlCards +
                '}';
    }
}
