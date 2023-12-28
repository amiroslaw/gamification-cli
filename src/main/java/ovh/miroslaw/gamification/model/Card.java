package ovh.miroslaw.gamification.model;

import jakarta.validation.constraints.NotNull;

public record Card(int id, @NotNull int type, @NotNull String title, String description, String url) {

    public Card {
        description = description == null ? "" : description;
        url = url == null ? "" : url;
    }

}
