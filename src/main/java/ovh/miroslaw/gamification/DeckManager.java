package ovh.miroslaw.gamification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

@RequiredArgsConstructor
@Service
public class DeckManager {

    private final Deck deck;

    public Deck draw(int drawNumber) {
        final int deckSize = deck.getSize();

        // one się powtarzają
        int pooledCardsAmount = (int) RandomGenerator.getDefault()
                .ints(drawNumber, 1, deckSize)
                .filter(n -> n <= deck.getCards().size())
                .count();
        System.out.println(pooledCardsAmount);
        final ArrayList<Card> polledCards = pollCards(pooledCardsAmount);
        // immunite
        deck.getCards().removeAll(polledCards);
        deck.setSize(deckSize - drawNumber);
        return deck;
    }

    private ArrayList<Card> pollCards(int amount) {
        final List<Card> shuffled = new ArrayList<>(deck.getCards());
        Collections.shuffle(shuffled);
        final ArrayDeque<Card> deque = new ArrayDeque<>(shuffled);
        final ArrayList<Card> polled = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Card card = deque.poll();
            if (card.type() == 1){
                deque.addLast(card);
                card = deque.poll();
            }
            polled.add(card);
        }
        return polled;
    }

    public void list() {
        System.out.println(deck.getSize());
        System.out.println(deck.getCards());
    }
}
