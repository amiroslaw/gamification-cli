package ovh.miroslaw.gamification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import lombok.SneakyThrows;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class DeckWrite {

    @Value("${spring.config.import}")
    private String configPath;
    @Value("${path.config}")
    private String configDir;

    @SneakyThrows
    @RegisterReflectionForBinding(Deck.class)
    public void write(Deck deck) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(Feature.WRITE_DOC_START_MARKER));
        Map<String, Deck> map = new HashMap<>();
        map.put("deck", deck);
        // change path
        mapper.writeValue(new File(configDir + "/deckOutput.yaml"), map);
    }
}
