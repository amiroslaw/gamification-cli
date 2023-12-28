package ovh.miroslaw.gamification;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ovh.miroslaw.gamification.model.Deck;
import ovh.miroslaw.gamification.model.TimewDuration;

//TODO add deck initializator - change optional:file in properties
@RequiredArgsConstructor
@Command
public class Gamification {

    private final DeckManager deckManager;
    private final DeckWriter deckWriter;
    private final String defaultDuration = "yesterday";
    private final String durationDesc = """
            yesterday     The 24 hours of the previous day
            day           The 24 hours of the current day
            week          This week
            fortnight     This week and the one before
            month         This month
            quarter       This quarter
            year          This year
            lastweek      Last week
            lastmonth     Last month
            lastquarter   Last quarter
            lastyear      Last year
            monday        Previous monday
            tuesday       Previous tuesday
            wednesday     Previous wednesday
            thursday      Previous thursday
            friday        Previous friday
            saturday      Previous saturday
            sunday        Previous sunday
                        """;

    @Command(description = "Draw cards", alias = "d")
    public void draw(@Option(defaultValue = "1", longNames = "draw-number", shortNames = 'n') int drawNumber) {
        final Deck deck = deckManager.draw(drawNumber);
        deckWriter.write(deck);
    }

    @Command(description = "Draw cards from finished tasks from timewarrior", alias = "t")
    public void timewDraw(@Option(defaultValue = defaultDuration, longNames = "duration", shortNames = 'd',
                                  description = durationDesc) TimewDuration duration) {
        final Deck deck = deckManager.timewDraw(duration);
        deckWriter.write(deck);
    }

    @Command(description = "Show timew summary", alias = "s")
    public String timewSummary(@Option(defaultValue = defaultDuration, longNames = "duration", shortNames = 'd',
                                       description = durationDesc)
            TimewDuration duration) {
        return deckManager.timewSummary(duration);
    }

    @Command(description = "List cards", alias = "l")
    public String list() {
        return deckManager.list();
    }
}
