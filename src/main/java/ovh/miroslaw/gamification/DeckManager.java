package ovh.miroslaw.gamification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.random.RandomGenerator;

@RequiredArgsConstructor
@Service
public class DeckManager {

    private final Deck deck;

    public Deck draw(int drawNumber) {
        final int deckSize = deck.size();

        List<Card> pooledCards = RandomGenerator.getDefault()
                .ints(drawNumber, 1, deckSize)
                .filter(n -> n <= deck.cards().size())
                .mapToObj(this::pollCards)
                .toList();
        System.out.println(pooledCards);
        deck.cards().removeAll(pooledCards);
        return new Deck(deck.cards(), deckSize - drawNumber);
    }

    private Card pollCards(int id) {
        return deck.cards().get(id - 1);
    }
}
