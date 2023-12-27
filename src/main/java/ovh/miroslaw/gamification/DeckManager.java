package ovh.miroslaw.gamification;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class DeckManager {

    public static final int EMPTY_CARD_VALUE = 0;
    public static final String EMPTY_CARD_DESC = "Sorry no award this time";
    public static final String EMPTY_CARD_TITLE = "Empty";
    public static final int MAIN_AWARD_CARD_TYPE = 1;
    private final Deck deck;

    private final Supplier<Card> createEmptyCard = () -> new Card(EMPTY_CARD_VALUE, EMPTY_CARD_VALUE, EMPTY_CARD_TITLE, EMPTY_CARD_DESC,
            Strings.EMPTY);

    private final Consumer<Card> printCard = d -> System.out.printf("Type: %d  \t %s - %s %s%n", d.type(), d.title(), d.description(), d.url());

    public Deck draw(int drawNumber) {
        drawNumber = validateDrawAmount(drawNumber);
        final List<Card> polledCards = pollCards(drawNumber);
        polledCards.forEach(printCard);
        deck.drawCards(polledCards, drawNumber);
        return deck;
    }

    private int validateDrawAmount(int numCardsToDraw) {
        if (deck.getSize() < numCardsToDraw) {
            System.out.println("Draws reminded: " + (numCardsToDraw - deck.getSize()));
            return deck.getSize();
        }
        return numCardsToDraw;
    }

    private List<Card> pollCards(int numCards) {
        final ArrayDeque<Card> deque = createDeque();

        return Stream.generate(() -> pollCard(deque))
                .limit(numCards)
                .toList();
    }

    private ArrayDeque<Card> createDeque() {
        List<Card> shuffled = Stream.generate(createEmptyCard)
                .limit((long) deck.getSize() - deck.getCards().size())
                .collect(Collectors.toList());
        shuffled.addAll(deck.getCards());
        Collections.shuffle(shuffled);
        return new ArrayDeque<>(shuffled);
    }

    private Card pollCard(ArrayDeque<Card> deque) {
        Card card = deque.poll();
        if (card.type() == MAIN_AWARD_CARD_TYPE) {
            deque.addLast(card);
            card = deque.poll();
        }
        return card;
    }

    public void list() {
        System.out.println("Deck size: " + deck.getSize());
        System.out.printf("Cards reminds - %d:%n", deck.getCards().size());
        deck.getCards().forEach(printCard);
    }
}
