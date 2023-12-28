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
        this.start = start;
        this.end = end;
        this.tags = tags;
    }

    public Duration mapToDuration() {
        return end == null ? Duration.ofMinutes(0) : Duration.between(end, start).abs();
    }

    public TagDuration mapToTagDuration() {
        final Duration duration = end == null ? Duration.ofMinutes(0) : Duration.between(end, start);
        final String tagsJoin = String.join(", ", this.tags);
        return new TagDuration(tagsJoin, duration.abs());
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
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
