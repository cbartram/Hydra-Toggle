package com.hydra.hydratoggle.controller;

import com.hydra.hydratoggle.ClientToggle;
import com.hydra.hydratoggle.HydraToggleApplication;
import com.hydra.hydratoggle.model.ClientType;
import com.hydra.hydratoggle.model.HydraConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.hydra.hydratoggle.HydraToggleApplication.toggle;

@Log4j2
public class MainController {

    @FXML
    private Label currentClientLabel;

    @FXML
    private Label staticCurrentClientLabel;

    @FXML
    private Label title;

    @FXML
    private Label errorLabel;

    @FXML
    private Button runeLiteButton;

    @FXML
    private Button hydraButton;

    @FXML
    private ImageView imageView;

    @FXML
    private SVGPath moonSVG;

    @FXML
    private VBox root;

    private static final String MOON_SVG = "M21.87,21.45c-3.12,3.12-8.19,8.12-11.31,0c-3.12-3.12-3.12-8.2,0-11.32" +
            "c0.83-0.82,1.83-1.45,2.99-1.86c0.36-0.13,0.77-0.04,1.04,0.24c0.27,0.27,0.37,0.68,0.24,1.04" +
            "c-0.78,2.21-0.25,4.6,1.39,6.24c1.64,1.64,4.03,2.17,6.24,1.39c0.36-0.13,0.77-0.04,1.04,0.24" +
            "c0.27,0.27,0.36,0.68,0.24,1.04C23.32,19.62,22.69,20.62,21.87,21.45z M11.97,11.55" +
            "c-2.34,2.34-2.34,6.15,0,8.48c2.5,2.5,6.76,2.28,8.94-0.51c-2.25,0.14-4.46-0.67-6.11-2.32" +
            "c-1.65-1.65-2.46-3.86-2.32-6.11C12.3,11.24,12.13,11.39,11.97,11.55z";

    private static final String PRIMARY_BUTTON_STYLE = "-fx-background-color: #007bff;" +
            "-fx-border-color: #007bff;" +
            "-fx-text-fill: white;" +
            "-fx-font-family: 'Helvetica Neue', Arial, sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 4px 8px;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 4px;" +
            "-fx-border-radius: 4px;";


    @FXML
    public void initialize() {
        runeLiteButton.setStyle(PRIMARY_BUTTON_STYLE);
        hydraButton.setStyle(PRIMARY_BUTTON_STYLE);
        moonSVG.setContent(MOON_SVG);

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
                    toggle.getConfig().setClientType(ClientType.RUNELITE);
                    toggle.getConfig().setDarkModeEnabled(false);

                    toggleClientType();
                } catch (IOException e) {
                    log.error("Failed to write the hydra toggle file. Error: ");
                    e.printStackTrace();
                }
                return;
            }
        }
        log.info("Hydra toggle file exists at {}.", toggle.getHydraConfFilePath());

        HydraConfig conf = toggle.readConfig();
        if(conf.getClientType() == ClientType.UNKNOWN) {
            log.error("Could not determine current client type. Make sure your Hydra client is named \"RuneLite-hydra.jar\" and your RuneLite client is named \"RuneLite.jar\".");

            // Attempt to delete the hydra conf file so the next time this application is started AND the jar files are
            // named correctly it will work.
            try {
                Files.delete(Path.of(toggle.getHydraConfFilePath()));
            } catch (IOException e) {
                log.error("Failed to delete hydra-toggle conf file.");
            }
        }

        currentClientLabel.setText(conf.getClientType().toString());
        toggleClientType();
        toggleDarkMode();
    }

    /**
     * Updates the GUI to reflect the latest client type (Hydra or RuneLite). This will update the buttons
     * to be clickable and disabled, write the value to the hydra-toggle conf file, and update the GUI text label
     * accordingly.
     */
    private void toggleClientType() {
        if(toggle.getConfig().getClientType() == ClientType.RUNELITE) {
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

    /**
     * Changes the UI to be more friendly to dark environments by making the background and text darker.
     */
    private void toggleDarkMode() {
        if(toggle.getConfig().isDarkModeEnabled()) {
            root.setStyle("-fx-background-color: #11315c");
            staticCurrentClientLabel.setTextFill(Color.WHITESMOKE);
            title.setTextFill(Color.WHITESMOKE);
        } else {
            root.setStyle("-fx-background-color: #ffffff");
            staticCurrentClientLabel.setTextFill(Color.BLACK);
            title.setTextFill(Color.BLACK);
        }
    }

    @FXML
    protected void onRuneLiteButtonClick() {
        toggle.renameFile("RuneLite.jar", "RuneLite-hydra.jar");
        toggle.renameFile("RuneLite-real.jar", "RuneLite.jar");
        toggle.getConfig().setClientType(ClientType.RUNELITE);
        toggleClientType();
    }

    @FXML
    protected void onHydraButtonClick() {
        toggle.renameFile("RuneLite.jar", "RuneLite-real.jar");
        toggle.renameFile("RuneLite-hydra.jar", "RuneLite.jar");
        toggle.getConfig().setClientType(ClientType.HYDRA);
        toggleClientType();
    }

    @FXML
    protected void onDarkModeClick() {
        toggle.getConfig().setDarkModeEnabled(!toggle.getConfig().isDarkModeEnabled());
        toggleDarkMode();
    }
}