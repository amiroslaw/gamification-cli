package ovh.miroslaw.gamification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class DeckWrite {

    @Value("${spring.config.import}")
    private File configPath;

    @RegisterReflectionForBinding(Deck.class)
    public void write(Deck deck) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(Feature.WRITE_DOC_START_MARKER));
        Map<String, Deck> map = new HashMap<>();
        map.put("deck", deck);
        try {
            mapper.writeValue(configPath, map);
        } catch (IOException e) {
            System.out.println("Could not save deck: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
