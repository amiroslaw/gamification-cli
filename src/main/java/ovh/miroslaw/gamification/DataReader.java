package ovh.miroslaw.gamification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Service;
import ovh.miroslaw.gamification.model.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataReader {

    @RegisterReflectionForBinding(Task.class)
    public List<Task> getData(String duration) {
        Process process;
        int exitCode = 0;
        String output = "";
        try {
            process = new ProcessBuilder("timew", "export", ":" + duration).start();
            output = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
            exitCode = process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Could not find or run timewarrior");
            Thread.currentThread().interrupt();
            e.printStackTrace();
            return Collections.emptyList();
        }
        if (exitCode != 0) {
            System.err.println("timewarrior exited with code " + exitCode);
            return Collections.emptyList();
        }

        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        TypeReference<List<Task>> taskRef = new TypeReference<>() {
        };

        List<Task> tasks;
        try {
            tasks = mapper.readValue(output, taskRef);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        return tasks;
    }
}
