package ovh.miroslaw.gamification;

import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;
import ovh.miroslaw.gamification.model.Card;
import ovh.miroslaw.gamification.model.StyleOption;
import ovh.miroslaw.gamification.model.TagDuration;
import ovh.miroslaw.gamification.model.Task;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

public final class TerminalUtil {

    private TerminalUtil() {
    }

    public static String getCardTableView(List<Card> cards) {
        if (cards.isEmpty()) {
            return "No cards found";
        }
        final List<Object[]> data = cards.stream()
                .map(i -> new Object[]{i.type(), i.title(), i.description(), i.url()})
                .collect(toList());
        data.addFirst(new Object[]{"Type", "Title", "Description", "URL"});

        return getView(data, StyleOption.fancy);
    }

    public static String getTaskSummaryTableView(List<Task> tasks, StyleOption style) {
        if (tasks.isEmpty()) {
            return "No tasks found";
        }
        final List<TagDuration> tagDurations = tasks.parallelStream()
                .map(t -> t.mapToTagDuration(style))
                .collect(toList());

        final Map<String, Duration> summary = tagDurations.parallelStream()
                .collect(groupingBy(TagDuration::name,
                        mapping(TagDuration::duration, reducing(Duration.ZERO, Duration::plus))));

        final List<Object[]> data = summary.entrySet().parallelStream()
                .map(e -> new Object[]{e.getKey(), durationFormat(e.getValue())})
                .collect(toList());
        if (StyleOption.fancy == style) {
            data.addFirst(new Object[]{"Tags", "Duration"});
        }

        Duration sum = tagDurations.parallelStream()
                .map(TagDuration::duration)
                .reduce(Duration.ZERO, Duration::plus);

        return String.format("%sTotal: %s", getView(data, style), durationFormat(sum));
    }

    private static String durationFormat(Duration duration) {
        final String days = duration.toDays() == 0 ? "" : String.format("%d days", duration.toDays());
        return String.format("%s %2d:%02d", days, duration.toHoursPart(), duration.toMinutesPart());
    }

    private static String getView(List<Object[]> data, StyleOption style) {
        TableModel model = new ArrayTableModel(data.toArray(Object[][]::new));
        TableBuilder tableBuilder = new TableBuilder(model);
        final var builderfun = getBuilderFromOption(style);
        builderfun.apply(tableBuilder);
        return tableBuilder.build().render(110);
    }

    private static UnaryOperator<TableBuilder> getBuilderFromOption(StyleOption style) {
        return switch (style) {
            case slim -> b -> b.addFullBorder(BorderStyle.air);
            case fancy -> b -> b.addFullBorder(BorderStyle.fancy_double).addHeaderBorder(BorderStyle.fancy_double);
        };
    }
}
