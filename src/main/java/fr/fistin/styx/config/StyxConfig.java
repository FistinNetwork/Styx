package fr.fistin.styx.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import fr.fistin.hydra.api.protocol.data.RedisData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by AstFaster
 * on 06/11/2022 at 09:35
 */
public class StyxConfig {

    private FistinAPIConfig fistinAPI;

    private StyxConfig() {}

    public StyxConfig(FistinAPIConfig fistinAPI) {
        this.fistinAPI = fistinAPI;
    }

    public FistinAPIConfig getFistinAPI() {
        return this.fistinAPI;
    }

    public static StyxConfig load() {
        System.out.println("Loading configuration...");

        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try {
            final File file = Paths.get("./config.yml").toFile();

            if (file.exists()) {
                return mapper.readValue(file, StyxConfig.class);
            }

            final StyxConfig config = new StyxConfig(new FistinAPIConfig(new FistinAPIConfig.Leveling("root", "", "localhost", "fistin", 3306), new RedisData("localhost", 6379, "")));

            mapper.writeValue(file, config);

            return config;
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while loading Styx config!", e);
        }
    }

}
