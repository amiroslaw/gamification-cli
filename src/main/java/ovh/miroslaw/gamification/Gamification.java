package ovh.miroslaw.gamification;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ovh.miroslaw.gamification.model.Deck;
import ovh.miroslaw.gamification.model.OutputOptions;
import ovh.miroslaw.gamification.model.Style;
import ovh.miroslaw.gamification.model.TimewDuration;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Command
public class Gamification {

    private final DeckManager deckManager;
    private final DeckWriter deckWriter;
    private static final String DEFAULT_DURATION = "yesterday";
    private static final String DURATION_DESC = """
            yesterday     The 24 hours of the previous day
            day           The 24 hours of the current day
            week          This week
            fortnight     This week and the one before
            month         This month
            quarter       This quarter
            year          This year
            lastweek      Last week
            lastmonth     Last month
            lastquarter   Last quarter
            lastyear      Last year
            monday        Previous monday
            tuesday       Previous tuesday
            wednesday     Previous wednesday
            thursday      Previous thursday
            friday        Previous friday
            saturday      Previous saturday
            sunday        Previous sunday
                        """;

    @Command(description = "Draw cards", alias = "d")
    public void draw(@Option(defaultValue = "1", longNames = "draw-number", shortNames = 'n')
                    @Positive int drawNumber) {
        final Deck deck = deckManager.draw(drawNumber);
        deckWriter.write(deck);
    }

    @Command(description = "List cards", alias = "l")
    public String list() {
        return deckManager.list();
    }

    @Command(description = "Initialize deck", alias = "i")
    public void init() {
        deckWriter.init();
    }

    @Bean
    CommandRegistration timewDraw() {
        return CommandRegistration.builder()
                .command("draw-timew")
                .description("Draw cards from finished tasks from timewarrior")
                .withAlias()
                    .command("t")
                .and()
                .withOption()
                    .longNames("duration")
                    .shortNames('d')
                    .description(DURATION_DESC)
                    .defaultValue(DEFAULT_DURATION)
                    .completion(this::createDurationCompletion)
                .and()
                .withTarget()
                    .consumer(ctx -> {
                        final String duration = ctx.getOptionValue("d");
                        final Deck deck = deckManager.timewDraw(TimewDuration.valueOf(duration.toUpperCase()));
                        deckWriter.write(deck);
                    })
                .and()
                .build();
    }

    @Bean
    CommandRegistration timewSummary() {
        return CommandRegistration.builder()
                .command("summary-timew")
                .description("Show timew summary")
                .withAlias()
                    .command("s")
                .and()
                .withOption()
                    .longNames("duration")
                    .shortNames('d')
                    .description(DURATION_DESC)
                    .defaultValue(DEFAULT_DURATION)
                    .completion(this::createDurationCompletion)
                .and()
                .withOption()
                    .type(boolean.class)
                    .longNames("dayFormat")
                    .shortNames('f')
                    .defaultValue("false")
                    .description("Day time format will cast hours to days")
                .and()
                .withOption()
                    .longNames("style")
                    .shortNames('s')
                    .description("Table style")
                    .defaultValue(Style.fancy.toString())
                    .completion(ctx -> Arrays.asList(
                            new CompletionProposal(Style.slim.toString()),
                            new CompletionProposal(Style.fancy.toString())))
                .and()
                .withTarget()
                    .function(ctx -> {
                        final String duration = ctx.getOptionValue("d");
                        final OutputOptions opts = new OutputOptions(Style.valueOf(ctx.getOptionValue("s")), ctx.getOptionValue("f"));
                        return deckManager.timewSummary(TimewDuration.valueOf(duration.toUpperCase()), opts);
                    })
                .and()
                .build();
    }

    private List<CompletionProposal> createDurationCompletion(CompletionContext completionContext) {
        return Arrays.stream(TimewDuration.values())
            .map(e -> new CompletionProposal(e.toString().toLowerCase()))
            .toList();
    }
}
