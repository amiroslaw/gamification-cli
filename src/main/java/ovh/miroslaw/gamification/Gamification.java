package ovh.miroslaw.gamification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Command
public class Gamification {

    //    @Value("#{deck[1].title}")
//    private String title;

//    @Autowired
    private Deck deck;
    @ConstructorBinding
    Gamification(Deck deck){
        this.deck = deck;
    }

    @Command(description = "list of the featured events", alias = "d")
    public void draw(@Option(defaultValue ="1", longNames = "cant-not-have-spaces", shortNames = 'n') int cardDraw) {
        System.out.println(cardDraw);
        System.out.println(deck);

    }
}
