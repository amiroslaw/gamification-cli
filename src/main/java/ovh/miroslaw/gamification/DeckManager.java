package ovh.miroslaw.gamification;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ovh.miroslaw.gamification.model.Card;
import ovh.miroslaw.gamification.model.Deck;
import ovh.miroslaw.gamification.model.Task;
import ovh.miroslaw.gamification.model.TimewDuration;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static ovh.miroslaw.gamification.TerminalUtil.getCardTableView;
import static ovh.miroslaw.gamification.TerminalUtil.getTaskSummaryTableView;

/**
 * Manages operations on a Deck of cards.
 */
@RequiredArgsConstructor
@Service
public class DeckManager {

    public static final int EMPTY_CARD_VALUE = 0;
    public static final String EMPTY_CARD_DESC = "Sorry no award this time";
    public static final String EMPTY_CARD_TITLE = "Empty";
    public static final int MAIN_AWARD_CARD_TYPE = 1;
    private final Deck deck;
    private final DataReader dataReader;
    @Value("${pomodoro.duration}")
    private float pomodoroDuration;

    private final Supplier<Card> createEmptyCard = () -> new Card(EMPTY_CARD_VALUE, EMPTY_CARD_VALUE, EMPTY_CARD_TITLE,
            EMPTY_CARD_DESC,
            Strings.EMPTY);

    /**
     * Prints a summary of the current deck.
     */
    public String list() {
        return String.format("Deck size: %s%nCards reminds: %d%n%s", deck.getSize(), deck.getCards().size(),
                getCardTableView(deck.getCards()));
    }

    public String timewSummary(TimewDuration duration) {
        return getTaskSummaryTableView(dataReader.getData(duration.toString()));
    }

    /**
     * Draws the specified number of cards from the deck.
     *
     * Validates the draw amount does not exceed deck size. Polls the cards from the deck. Prints a table view of the
     * drawn cards. Updates the deck by removing the drawn cards.
     *
     * @param drawNumber The number of cards to draw from the deck.
     * @return The updated deck instance.
     */
    public Deck draw(int drawNumber) {
        drawNumber = validateDrawAmount(drawNumber);
        final List<Card> polledCards = pollCards(drawNumber);

        System.out.printf("Cards drew: %s%n%s" + drawNumber, getCardTableView(polledCards));

        deck.drawCards(polledCards, drawNumber);
        return deck;
    }

    public Deck timewDraw(TimewDuration duration) {
        return draw(readPomodoroAwardsAmount(duration));
    }

    private int readPomodoroAwardsAmount(TimewDuration duration) {
        final long minutes = dataReader.getData(duration.toString()).parallelStream()
                .map(Task::mapToDuration)
                .map(Duration::abs)
                .reduce(Duration.ZERO, Duration::plus)
                .toMinutes();
        return Math.round(minutes / pomodoroDuration);
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
                .collect(toList());
        shuffled.addAll(deck.getCards());
        Collections.shuffle(shuffled);
        return new ArrayDeque<>(shuffled);
    }

    private Card pollCard(ArrayDeque<Card> deque) {
        Card card = deque.poll();
        if (MAIN_AWARD_CARD_TYPE == card.type()) {
            deque.addLast(card);
            card = deque.poll();
        }
        return card;
    }
}
