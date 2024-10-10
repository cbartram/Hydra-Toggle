package com.hydra.hydratoggle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hydra.hydratoggle.model.ClientType;
import com.hydra.hydratoggle.model.HydraConfig;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Log4j2
@Getter
public class ClientToggle {

    @Getter
    private HydraConfig config;

    private final String runeliteDirectory;
    private final String runeliteJarPath;
    private final String hydraJarPath;
    private final String hydraConfFilePath;
    private final String sep = FileSystems.getDefault().getSeparator();

    public ClientToggle(final HydraConfig config) {
        this.config = config;
        this.runeliteDirectory = System.getProperty("user.home") + sep + "AppData" + sep + "Local" + sep + "RuneLite";
        this.runeliteJarPath = this.runeliteDirectory + sep + "RuneLite.jar";
        this.hydraJarPath = this.runeliteDirectory + sep + "RuneLite-hydra.jar";
        this.hydraConfFilePath = this.runeliteDirectory + sep + "hydra_toggle_status.conf";
    }

    /**
     * Returns true when the RuneLite.jar and RuneLite-hydra.jar files are appropriately named in the users
     * RuneLite directory.
     * @return Boolean
     */
    public boolean canDetectClientJars() {
        return Files.exists(Paths.get(hydraJarPath)) && Files.exists(Paths.get(runeliteJarPath));
    }

    /**
     * Creates the hydra-toggle.conf file which stores that last known state of the runelite directory. This file
     * allows the state of the application to persist between runs.
     * @throws IOException IOException is thrown when the file cannot be created
     */
    public void createConfFile() throws IOException {
        Files.createFile(Path.of(this.hydraConfFilePath));
    }

    public void persistConfig() throws IOException {
        if(!Objects.equals(ClientType.RUNELITE.getValue(), "RUNELITE") && !Objects.equals(ClientType.HYDRA.getValue(), "HYDRA")) {
            throw new IllegalArgumentException("The value to write must be either 0 for RuneLite or 1 for Hydra RuneLite");
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(this.hydraConfFilePath), config);
    }

    public HydraConfig readConfig() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.config = mapper.readValue(new File(this.hydraConfFilePath), HydraConfig.class);
            return this.config;
        } catch (IOException e) {
            log.error("Failed to read hydra toggle status file.");
            e.printStackTrace();
        }

        // These are defaults in case the file is corrupted or can't be read
        HydraConfig conf = new HydraConfig();
        conf.setClientType(ClientType.UNKNOWN);
        conf.setDarkModeEnabled(false);
        return conf;
    }

    public void renameFile(String oldFileName, String newFileName) {
        try {
            Files.move(new File(this.runeliteDirectory + sep + oldFileName).toPath(), new File(this.runeliteDirectory + sep + newFileName).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            log.error("Failed to rename file {} to {}", oldFileName, newFileName);
        }
    }

}
