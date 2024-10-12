package com.hydra.hydratoggle.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provider;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
public class HydraConfigProvider implements Provider<HydraConfig> {

    private static final String sep = FileSystems.getDefault().getSeparator();
    private static final String CONFIG_FILE_PATH = System.getProperty("user.home") + sep + "AppData" + sep + "Local" + sep + "RuneLite" + sep + "hydra_toggle.json";

    @Override
    public HydraConfig get() {
        Path path = Path.of(CONFIG_FILE_PATH);
        ObjectMapper mapper = new ObjectMapper();

        // If the file does not exist create it with default config. This will happen the first time the user
        // runs the application.
        if (!Files.exists(path)) {
            log.info("No hydra-toggle conf file exists. Attempting to create at: {}", CONFIG_FILE_PATH);
            try {
                Files.createFile(path);
                HydraConfig defaultConfig = new HydraConfig();
                defaultConfig.setClientType(ClientType.RUNELITE);
                defaultConfig.setDarkModeEnabled(false);
                mapper.writeValue(new File(CONFIG_FILE_PATH), defaultConfig);
                return defaultConfig;
            } catch (IOException e) {
                log.error("Failed to write the hydra toggle file to: {} Error: {}", CONFIG_FILE_PATH, e.getMessage());
            }
        }

        try {
            return mapper.readValue(new File(CONFIG_FILE_PATH), HydraConfig.class);
        } catch (IOException e) {
            log.error("Failed to load HydraConfig from {} Error: {}", CONFIG_FILE_PATH, e.getMessage());
            return null;
        }
    }
}