package com.hydra.hydratoggle;

import com.hydra.hydratoggle.model.ClientType;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Log4j2
@Getter
public class ClientToggle {

    private final String runeliteDirectory;
    private final String runeliteJarPath;
    private final String hydraJarPath;
    private final String hydraConfFilePath;

    private final String sep = FileSystems.getDefault().getSeparator();

    public ClientToggle() {
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

    public void persistActiveClient(final ClientType value) throws IOException {
        if(!Objects.equals(ClientType.RUNELITE.getValue(), "0") && !Objects.equals(ClientType.HYDRA.getValue(), "1")) {
            throw new IllegalArgumentException("The value to write must be either 0 for RuneLite or 1 for Hydra RuneLite");
        }

        // The jar that is launched by the Jagex launcher HAS to be named RuneLite.jar so this file tells hydra-toggle
        // which jar RuneLite.jar actually is since the last time this has been run.  0 = The jar is RuneLite, 1 = the Jar is Hydra
        List<String> lines = List.of(value.getValue());
        Path file = Paths.get(this.hydraConfFilePath);
        Files.write(file, lines, StandardCharsets.UTF_8);
        log.debug("Successfully wrote value: {} to ", value);
    }

    public ClientType readActiveClient() {
        try {
            List<String> lines = Files.readAllLines(Path.of(this.hydraConfFilePath));
            String value = lines.get(0);

            if (Objects.equals(value, "0")) {
                return ClientType.RUNELITE;
            }
            return ClientType.HYDRA;
        } catch (IOException e) {
            log.error("Failed to read hydra toggle status file.");
            e.printStackTrace();
        }
        return ClientType.UNKNOWN;
    }

    public void renameFile(String oldFileName, String newFileName) {
        try {
            Files.move(new File(this.runeliteDirectory + sep + oldFileName).toPath(), new File(this.runeliteDirectory + sep + newFileName).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            log.error("Failed to rename file {} to {}", oldFileName, newFileName);
        }
    }

}
