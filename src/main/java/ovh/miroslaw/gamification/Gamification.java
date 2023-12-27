package ovh.miroslaw.gamification;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

//TODO read finished tasks from timewarrior
// add deck generator - change optional:file in properties
@RequiredArgsConstructor
@Command
public class Gamification {

    private final DeckManager deckManager;
    private final DeckWrite deckWrite;

    @Command(description = "Draw cards", alias = "d")
    public void draw(@Option(defaultValue = "1", longNames = "draw-number", shortNames = 'n') int drawNumber) {
        final Deck deck = deckManager.draw(drawNumber);
        deckWrite.write(deck);
    }

    @Command(description = "List cards", alias = "l")
    public void list() {
        deckManager.list();
    }
}
