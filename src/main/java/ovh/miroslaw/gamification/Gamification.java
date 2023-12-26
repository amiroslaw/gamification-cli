package ovh.miroslaw.gamification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@RequiredArgsConstructor
@Command
public class Gamification {

    @Autowired
    private DeckManager deckManager;

    @Autowired
    private DeckWrite deckWrite;

    @Command(description = "Draw cards", alias = "d")
    public void draw(@Option(defaultValue = "1", longNames = "draw-number", shortNames = 'n') int drawNumber) {
        final Deck deck = deckManager.draw(drawNumber);
        deckWrite.write(deck);
    }
}
