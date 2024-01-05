package ovh.miroslaw.gamification;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Service;
import ovh.miroslaw.gamification.model.Card;
import ovh.miroslaw.gamification.model.Deck;
import ovh.miroslaw.gamification.model.OutputOptions;
import ovh.miroslaw.gamification.model.Task;
import ovh.miroslaw.gamification.model.TimewDuration;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static ovh.miroslaw.gamification.TerminalUtil.ANSI;
import static ovh.miroslaw.gamification.TerminalUtil.ANSI_PRINT;
import static ovh.miroslaw.gamification.TerminalUtil.getCardTableView;
import static ovh.miroslaw.gamification.TerminalUtil.getTaskSummaryTableView;

/**
 * Manages operations on a Deck of cards.
 */
@RequiredArgsConstructor
@Service
public class DeckManager {

    public static final int MAIN_AWARD_CARD_TYPE = 1;
    private final Deck deck;
    private final DataReader dataReader;
    @Value("${pomodoro.duration}")
    private float pomodoroDuration;
    @Value("${spring.config.import}")
    private String configPath;

    @PostConstruct
    public void deckChecker() {
        if (deck.getCards() == null || deck.getCards().isEmpty()) {
            ANSI_PRINT.accept("No cards found. Please check: " + configPath, AnsiColor.RED);
        }
    }

    /**
     * Prints a summary of the current deck.
     */
    public String list() {
        final List<Card> cards = deck.getCards();
        if (cards.isEmpty()) {
            return ANSI.apply("No cards found" + Strings.LINE_SEPARATOR, AnsiColor.RED);
        }
        return String.format("Deck size: %s%nCards reminds: %d%n%s", deck.getSize(), cards.size(),
                getCardTableView(cards));
    }

    /**
     * Generates a summary table view of timewarrior data.
     *
     * @param duration The time duration to summarize
     * @param outputOptions Options for formatting the output
     * @return A string containing the formatted task summary table.
     */
    public String timewSummary(TimewDuration duration, OutputOptions outputOptions) {
        return getTaskSummaryTableView(dataReader.getData(duration), outputOptions);
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

        System.out.printf("Cards drew: %d%n%s", drawNumber, getCardTableView(polledCards));

        deck.drawCards(polledCards, drawNumber);
        return deck;
    }

    /**
     * Draws cards from the deck based on Timewarrior data.
     *
     * @param duration The time duration for Timewarrior
     * @return The updated deck instance after drawing cards
     */
    public Deck timewDraw(TimewDuration duration) {
        return draw(readPomodoroAwardsAmount(duration));
    }

    private int readPomodoroAwardsAmount(TimewDuration duration) {
        final long minutes = dataReader.getData(duration).parallelStream()
                .map(Task::mapToDuration)
                .reduce(Duration.ZERO, Duration::plus)
                .toMinutes();
        return Math.round(minutes / pomodoroDuration);
    }

    private int validateDrawAmount(int numCardsToDraw) {
        if (deck.getSize() < numCardsToDraw) {
            ANSI_PRINT.accept("Draws reminded: " + (numCardsToDraw - deck.getSize()), AnsiColor.GREEN);
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
        List<Card> shuffled = Stream.generate(Card.createEmpty)
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
