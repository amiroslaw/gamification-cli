package ovh.miroslaw.gamification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DeckManager {

    @Autowired
    private Deck deck;

    public Deck draw(int drawNumber) {
        int deckSize = deck.getSize();
        if (deck.getSize() < drawNumber) {
            System.out.println("Draws reminded: " + (drawNumber - deck.getSize()));
            drawNumber = deck.getSize();
        }

        final List<Card> polledCards = pollCards(drawNumber);
        System.out.println(polledCards);
        System.out.println(polledCards.size());
        deck.getCards().removeAll(polledCards);
        deck.setSize(deckSize - drawNumber);
        return deck;
    }

    private List<Card> pollCards(int amount) {
        List<Card> shuffled = Stream.generate(() -> new Card(0, 0, "Empty", "Sorry no award this time"))
                .limit((long) deck.getSize() - deck.getCards().size())
                .collect(Collectors.toList());
        shuffled.addAll(deck.getCards());
        Collections.shuffle(shuffled);
        final ArrayDeque<Card> deque = new ArrayDeque<>(shuffled);

        return Stream.generate(() -> pollCard(deque))
                .limit(amount)
                .filter(c -> c.type() != 0)
                .toList();
    }

    private Card pollCard(ArrayDeque<Card> deque) {
        Card card = deque.poll();
        if (card.type() == 1) {
            deque.addLast(card);
            card = deque.poll();
        }
        return card;
    }

    public void list() {
        System.out.println(deck.getSize());
        System.out.println(deck.getCards());
        System.out.println(deck.getCards().size());
    }
}
