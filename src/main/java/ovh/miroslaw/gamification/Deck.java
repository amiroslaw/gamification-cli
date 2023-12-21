package ovh.miroslaw.gamification;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

//@ConfigurationProperties("deck")
//public class Deck {
//}

//@Configuration
@ConfigurationProperties(prefix = "deck")
public record Deck(List<Card> cards) {
}
record Card(int id, int type, String title, String description) {}
