package ovh.miroslaw.gamification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import org.apache.logging.log4j.util.Strings;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Service;
import ovh.miroslaw.gamification.model.Card;
import ovh.miroslaw.gamification.model.Deck;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static ovh.miroslaw.gamification.TerminalUtil.ANSI_PRINT;

/**
 * Service for writing Deck data to YAML file.
 */
@Service
public class DeckWriter {

    public static final String OPTIONAL_FILE_REGEX = "^(optional:)?(file:)?";
    @Value("${init.deck.size}")
    private int deckSize;
    @Value("${spring.config.import}")
    private String configPath;

    /**
     * Writes the provided Deck object to a YAML file.
     *
     * @param deck The Deck object to write to file. Cannot be null.
     */
    @RegisterReflectionForBinding(Deck.class)
    public void write(Deck deck) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(Feature.WRITE_DOC_START_MARKER));
        Map<String, Deck> map = new HashMap<>();
        map.put("deck", deck);
        try {
            mapper.writeValue(getConfigFile(), map);
        } catch (IOException e) {
            ANSI_PRINT.accept("Could not save deck: " + e.getMessage(), AnsiColor.RED);
            e.printStackTrace();
        }
    }

    /**
     * Initializes the deck writer by creating a new deck of cards and writing it to file.
     *
     * Finally, the application is exited with code 0.
     */
    public void init() {
        final ArrayList<Card> cards = createCards();
        final Deck deck = new Deck(cards, deckSize);
        write(deck);
        ANSI_PRINT.accept("Created in " + getConfigFile(), AnsiColor.GREEN);
        System.exit(0);
    }

    private ArrayList<Card> createCards() {
        final ArrayList<Card> cards = new ArrayList<>();
        cards.add(Card.newCard.apply(1, 1));
        final List<Card> type2 = IntStream.rangeClosed(2, 4).mapToObj(i -> Card.newCard.apply(i, 2)).toList();
        final List<Card> type3 = IntStream.rangeClosed(5, 10).mapToObj(i -> Card.newCard.apply(i, 3)).toList();
        cards.addAll(type2);
        cards.addAll(type3);
        return cards;
    }

    private File getConfigFile() {
        return new File(configPath.replaceFirst(OPTIONAL_FILE_REGEX, Strings.EMPTY));
    }

}
