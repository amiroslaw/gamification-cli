package ovh.miroslaw.gamification.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Task {

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime start;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime end;
    private List<String> tags;

    public Task() {
        super();
    }

    public Task(LocalDateTime start, LocalDateTime end, List<String> tags) {
        if (start == null || end == null || tags == null) {
            throw new IllegalArgumentException("Invalid argument");
        }

        this.start = start;
        this.end = end;
        this.tags = tags;
    }

    public Duration mapToDuration() {
        return calculateDuration();
    }

    private Duration calculateDuration() {
        return end == null ? Duration.ZERO : Duration.between(end, start).abs();
    }

    public TagDuration mapToTagDuration(Style style) {
        final String tag = switch (style) {
            case fancy -> String.join(", ", this.tags);
            case slim -> this.tags.getLast();
        };
        return new TagDuration(tag, calculateDuration());
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public List<String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "Task{" +
               "start=" + start +
               ", end=" + end +
               ", tags=" + tags +
               '}';
    }
}
