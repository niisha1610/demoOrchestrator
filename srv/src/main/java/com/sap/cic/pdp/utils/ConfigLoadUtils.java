package com.sap.cic.pdp.utils;


import com.sap.cic.pdp.entity.Configuration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.ConstructorException;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>This class is responsible for loading configuration from yaml file.</p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ConfigLoadUtils {

    private static final String FILE_NAME = "/config.yaml";

    /**
     * <p>Read the configuration from yaml file & convert it into Configuration class.</p>
     *
     * @return Configuration
     */
    public static Configuration getConfiguration(String filename) {
        Configuration configuration = null;
        try (InputStream inputStream = ConfigLoadUtils.class.getResourceAsStream(filename)) {
            Yaml yaml = new Yaml();
            configuration = yaml.loadAs(inputStream, Configuration.class);
        } catch (IOException | ConstructorException e) {
            log.error("Exception while reading configurations:", e);
        }
        return configuration;
    }
}
