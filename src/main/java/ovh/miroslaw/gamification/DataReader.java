package ovh.miroslaw.gamification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Service;
import ovh.miroslaw.gamification.model.Task;
import ovh.miroslaw.gamification.model.TimewDuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ovh.miroslaw.gamification.TerminalUtil.ANSI_PRINT;

@Service
public class DataReader {

    @RegisterReflectionForBinding(Task.class)
    public List<Task> getData(TimewDuration duration) {

        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        TypeReference<List<Task>> taskRef = new TypeReference<>() {
        };

        List<Task> tasks;
        try {
            final String output = executeTimew(duration);
            tasks = mapper.readValue(output, taskRef);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        return tasks;
    }

    private String executeTimew(TimewDuration duration) {
        int exitCode = 0;
        String output = null;
        try {
            Process process = new ProcessBuilder("timew", "export", ":" + duration.toString().toLowerCase()).start();
            output = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
            exitCode = process.waitFor();
        } catch (IOException | InterruptedException e) {
            ANSI_PRINT.accept("Could not find or run timewarrior", AnsiColor.RED);
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        if (exitCode != 0) {
            ANSI_PRINT.accept("timewarrior exited with code ", AnsiColor.RED);
        }
        return output;
    }
}
