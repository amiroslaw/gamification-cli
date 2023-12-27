package ovh.miroslaw.gamification;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("deck")
public final class Deck {

    private List<Card> cards;
    private int size;

    public Deck(List<Card> cards, Integer size) {
        this.cards = cards;
        this.size = size == null ? 30 : size;
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
    public void drawCards(List<Card> drawnCards, int drawNumber) {
        cards.removeAll(drawnCards);
        size -= drawNumber;
    }
}

record Card(int id, @NotNull int type, @NotNull String title, String description, String url) {
    Card {
        description = description == null ? "" : description;
        url = url == null ? "" : url;
    }

}
