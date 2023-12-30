package ovh.miroslaw.gamification;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;
import ovh.miroslaw.gamification.model.Card;
import ovh.miroslaw.gamification.model.OutputOptions;
import ovh.miroslaw.gamification.model.Style;
import ovh.miroslaw.gamification.model.TagDuration;
import ovh.miroslaw.gamification.model.Task;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

public final class TerminalUtil {

    public static final BiFunction<String,AnsiColor,String> ANSI = (s, c) -> AnsiOutput.toString(c, s, AnsiColor.DEFAULT);
    public static final BiConsumer<String, AnsiColor> ANSI_PRINT = (s, c) ->
            System.out.println(AnsiOutput.toString(c, s, AnsiColor.DEFAULT));

    private TerminalUtil() {
    }

    public static String getCardTableView(List<Card> cards) {
        if (cards.isEmpty()) {
            return ANSI.apply("No cards found", AnsiColor.RED);
        }
        final List<Object[]> data = cards.stream()
                .map(i -> new Object[]{i.type(), i.title(), i.description(), i.url()})
                .collect(toList());
        data.addFirst(new Object[]{"Type", "Title", "Description", "URL"});

        return getView(data, Style.fancy);
    }

    public static String getTaskSummaryTableView(List<Task> tasks, OutputOptions outOptions) {
        if (tasks.isEmpty()) {
            return ANSI.apply("No tasks found", AnsiColor.RED);
        }
        final List<TagDuration> tagDurations = tasks.parallelStream()
                .map(t -> t.mapToTagDuration(outOptions.style()))
                .collect(toList());

        final Map<String, Duration> summary = tagDurations.parallelStream()
                .collect(groupingBy(TagDuration::name,
                        mapping(TagDuration::duration, reducing(Duration.ZERO, Duration::plus))));

        final List<Object[]> data = summary.entrySet().parallelStream()
                .map(e -> new Object[]{e.getKey(), durationFormat(e.getValue(), outOptions.hasDayFormat())})
                .collect(toList());
        if (Style.fancy == outOptions.style()) {
            data.addFirst(new Object[]{"Tags", "Duration"});
        }

        Duration sum = tagDurations.parallelStream()
                .map(TagDuration::duration)
                .reduce(Duration.ZERO, Duration::plus);

        return String.format("%sTotal: %s", getView(data, outOptions.style()), durationFormat(sum, outOptions.hasDayFormat()));
    }

    private static String durationFormat(Duration duration, boolean dayTimeFormat) {
        String days = "";
        long hours = duration.toHours();
        if (dayTimeFormat) {
            days = duration.toDays() == 0 ? "" : String.format("%d days", duration.toDays());
            hours = duration.toHoursPart();
        }
        return String.format("%s %2d:%02d", days, hours, duration.toMinutesPart());
    }

    private static String getView(List<Object[]> data, Style style) {
        TableModel model = new ArrayTableModel(data.toArray(Object[][]::new));
        TableBuilder tableBuilder = new TableBuilder(model);
        final var builderfun = getBuilderFromOption(style);
        builderfun.apply(tableBuilder);
        return tableBuilder.build().render(110);
    }

    private static UnaryOperator<TableBuilder> getBuilderFromOption(Style style) {
        return switch (style) {
            case slim -> b -> b.addFullBorder(BorderStyle.air);
            case fancy -> b -> b.addFullBorder(BorderStyle.fancy_double).addHeaderBorder(BorderStyle.fancy_double);
        };
    }
}
