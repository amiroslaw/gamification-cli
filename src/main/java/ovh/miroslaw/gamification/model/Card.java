package ovh.miroslaw.gamification.model;

import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.util.Strings;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public record Card(int id, @NotNull int type, @NotNull String title, String description, String url) {

    public static final int EMPTY_CARD_VALUE = 0;
    public static final String EMPTY_CARD_DESC = "Sorry no award this time";
    public static final String EMPTY_CARD_TITLE = "Empty";
    public static final Supplier<Card> createEmpty = () -> new Card(EMPTY_CARD_VALUE, EMPTY_CARD_VALUE,
            EMPTY_CARD_TITLE, EMPTY_CARD_DESC, Strings.EMPTY);
    public static final BiFunction<Integer, Integer, Card> newCard = (i, t) -> new Card(i, t,
            Strings.EMPTY, Strings.EMPTY, Strings.EMPTY);

    public Card {
        description = description == null ? Strings.EMPTY : description;
        url = url == null ? Strings.EMPTY : url;
    }
}
