package ovh.miroslaw.gamification;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("deck")
public record Deck(List<Card> cards, int size) {
}

record Card(int id, int type, String title, String description) {
}
