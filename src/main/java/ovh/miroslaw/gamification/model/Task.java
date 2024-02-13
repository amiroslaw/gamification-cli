package ovh.miroslaw.gamification.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Getter
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

    public TagDuration mapToTagDuration(boolean hasTags) {
        if (hasTags) {
            return new TagDuration(String.join(", ", this.tags), calculateDuration());
        }
        return new TagDuration(this.tags.getLast(), calculateDuration());
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
