package pl.ptemich.ksef.localconf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;


@Service
public class LocalConfigService {

    @Value("${localConfigFileName}")
    private String configFileName = "settings.json";

    public LocalConfig loadFromDisk() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(configFileName), LocalConfig.class);
        } catch (IOException e) {
            //log.error("Failed to load config", e);
            throw new RuntimeException(e);
        }
    }

    public void saveToDisk(LocalConfig localConfig) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            mapper.writeValue(new File(configFileName), localConfig);
        } catch (IOException e) {
            //log.error("Failed to save config", e);
            throw new RuntimeException(e);
        }
    }

}
