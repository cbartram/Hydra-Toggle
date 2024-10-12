package com.hydra.hydratoggle;

import com.google.inject.Inject;
import com.hydra.hydratoggle.model.HydraConfig;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log4j2
@Getter
public class ClientToggle {

    @Inject
    private HydraConfig config;

    /**
     * Returns true when the RuneLite.jar and RuneLite-hydra.jar files are appropriately named in the users
     * RuneLite directory or the RuneLite.jar and RuneLite-real.jar files are appropriately named in the user's directory.
     * This allows for the user to start this application with hydra enabled.
     * @return Boolean
     */
    public boolean canDetectClientJars() {
        boolean runeLiteEnabled = Files.exists(Paths.get(config.getHydraJarPath())) && Files.exists(Paths.get(config.getRuneLiteJarPath()));
        boolean hydraEnabled = Files.exists(Paths.get(config.getRuneLiteJarPath())) && Files.exists(Paths.get(config.getRuneLiteRealJarPath()));

        return runeLiteEnabled || hydraEnabled;
    }

    /**
     * Renames a file on the disk from an old name to a new name.
     * @param oldFileName String the old name of the file.
     * @param newFileName String the new name of the file.
     */
    public boolean renameFile(String oldFileName, String newFileName) {
        try {
            Files.move(new File(config.getRuneLiteDirectory() + FileSystems.getDefault().getSeparator() + oldFileName).toPath(), new File(config.getRuneLiteDirectory() + FileSystems.getDefault().getSeparator() + newFileName).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException ex) {
            log.error("Failed to rename file {} to {}. Error = {}", oldFileName, newFileName, ex.getMessage());
            return false;

        }
    }

}
