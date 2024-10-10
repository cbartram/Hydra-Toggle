package com.hydra.hydratoggle.controller;

import com.hydra.hydratoggle.ClientToggle;
import com.hydra.hydratoggle.HydraToggleApplication;
import com.hydra.hydratoggle.model.ClientType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
public class MainController {

    @FXML
    private Label currentClientLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Button runeLiteButton;

    @FXML
    private Button hydraButton;

    @FXML
    private ImageView imageView;

    private final ClientToggle toggle;

    private static final String PRIMARY_BUTTON_STYLE = "-fx-background-color: #007bff;" +
            "-fx-border-color: #007bff;" +
            "-fx-text-fill: white;" +
            "-fx-font-family: 'Helvetica Neue', Arial, sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 4px 8px;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 4px;" +
            "-fx-border-radius: 4px;";

    public MainController() {
        this.toggle = new ClientToggle();
    }

    @FXML
    public void initialize() {
        runeLiteButton.setStyle(PRIMARY_BUTTON_STYLE);
        hydraButton.setStyle(PRIMARY_BUTTON_STYLE);

        // Check for the existence of the hydra-toggle.conf file.
        if (!Files.exists(Path.of(toggle.getHydraConfFilePath()))) {
            log.info("No hydra-toggle conf file exists. Attempting to create at: {}", toggle.getHydraConfFilePath());

            if(!toggle.canDetectClientJars()) {
                String errorMessage = "No jars detected at:" + toggle.getRuneliteDirectory() +
                        " Please make sure the JAR files exist and are named correctly.";
                log.error(errorMessage);
                errorLabel.setText(errorMessage);
                return;
            } else {
                // Jars are found and named correctly, so we can proceed to create the conf file, update the label
                // and set the button disabled status accordingly
                try {
                    toggle.createConfFile();
                    switchClientType(ClientType.RUNELITE);
                } catch (IOException e) {
                    log.error("Failed to write the hydra toggle file. Error: ");
                    e.printStackTrace();
                }
                return;
            }
        }
        log.info("Hydra toggle file exists at {}.", toggle.getHydraConfFilePath());

        ClientType activeClient = this.toggle.readActiveClient();
        if(activeClient == ClientType.UNKNOWN) {
            log.error("Could not determine current client type. Make sure your Hydra client is named \"RuneLite-hydra.jar\" and your RuneLite client is named \"RuneLite.jar\".");

            // Attempt to delete the hydra conf file so the next time this application is started AND the jar files are
            // named correctly it will work.
            try {
                Files.delete(Path.of(this.toggle.getHydraConfFilePath()));
            } catch (IOException e) {
                log.error("Failed to delete hydra-toggle conf file.");
            }
        }

        currentClientLabel.setText(activeClient.toString());
        switchClientType(activeClient);
    }

    /**
     * Updates the GUI to reflect the latest client type (Hydra or RuneLite). This will update the buttons
     * to be clickable and disabled, write the value to the hydra-toggle conf file, and update the GUI text label
     * accordingly.
     * @param clientType The client type to switch to either ClientType.HYDRA or ClientType.RUNELITE
     */
    private void switchClientType(ClientType clientType) {
        try {
            toggle.persistActiveClient(clientType);
        } catch (IOException e) {
            log.error("Failed to update hydra-toggle conf file with value {}", clientType.toString());
            e.printStackTrace();
        }
        if(clientType == ClientType.RUNELITE) {
            runeLiteButton.setDisable(true);
            hydraButton.setDisable(false);
            currentClientLabel.setText(ClientType.RUNELITE.toString());
            currentClientLabel.setTextFill(Paint.valueOf("#e67512"));
            InputStream is = HydraToggleApplication.class.getResourceAsStream("runelite_logo.png");
            if (is != null) {
                imageView.setImage(new Image(is));
            } else {
                log.info("Input stream is null");
                System.out.println("IS NULL");
            }
        } else {
            runeLiteButton.setDisable(false);
            hydraButton.setDisable(true);
            currentClientLabel.setText(ClientType.HYDRA.toString());
            currentClientLabel.setTextFill(Paint.valueOf("#1e73fc"));
            InputStream is = HydraToggleApplication.class.getResourceAsStream("hydra_logo.jpg");
            if (is != null) {
                imageView.setImage(new Image(is));
            }
        }
    }

    @FXML
    protected void onRuneLiteButtonClick() {
        this.toggle.renameFile("RuneLite.jar", "RuneLite-hydra.jar");
        this.toggle.renameFile("RuneLite-real.jar", "RuneLite.jar");
        switchClientType(ClientType.RUNELITE);
    }

    @FXML
    protected void onHydraButtonClick() {
        this.toggle.renameFile("RuneLite.jar", "RuneLite-real.jar");
        this.toggle.renameFile("RuneLite-hydra.jar", "RuneLite.jar");
        switchClientType(ClientType.HYDRA);
    }
}