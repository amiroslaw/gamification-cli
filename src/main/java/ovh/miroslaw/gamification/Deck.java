package ovh.miroslaw.gamification;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("deck")
public final class Deck {

    private List<Card> cards;
    private int size;

    public Deck(List<Card> cards, int size) {
        this.cards = cards;
        this.size = size;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}

record Card(int id, int type, String title, String description) {

}
