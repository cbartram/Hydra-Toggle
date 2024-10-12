package com.hydra.hydratoggle.controller;

import com.google.inject.Inject;
import com.hydra.hydratoggle.ClientToggle;
import com.hydra.hydratoggle.HydraToggleApplication;
import com.hydra.hydratoggle.model.ClientType;
import com.hydra.hydratoggle.model.HydraConfig;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
public class MainController {

    @Inject
    private ClientToggle toggle;

    @Inject
    private HydraConfig config;

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
    private ToggleButton darkModeButton;

    @FXML
    private VBox root;

    @FXML
    private Button launchButton;

    private SVGPath moonSVG;
    private AtomicBoolean isRunning;
    private ReentrantLock lock;

    private static final String PRIMARY_BUTTON_STYLE = "-fx-background-color: #007bff;" +
            "-fx-border-color: #007bff;" +
            "-fx-text-fill: white;" +
            "-fx-font-family: 'Helvetica Neue', Arial, sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 4px 8px;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 4px;" +
            "-fx-border-radius: 4px;";


    private static final String SUCCESS_BUTTON_STYLE = "-fx-background-color: #0AC835;" +
            "-fx-border-color: #0AC835;" +
            "-fx-text-fill: white;" +
            "-fx-font-family: 'Helvetica Neue', Arial, sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 4px 8px;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 4px;" +
            "-fx-border-radius: 4px;";

    private static final String DEFAULT_BUTTON_STYLE = "-fx-background-color: rgba(248,249,250,0);" +
            "-fx-border-color: rgba(248,249,250,0);" +
            "-fx-text-fill: #212529;" +
            "-fx-font-family: \"Helvetica Neue\", Arial, sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 6px 12px;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 4px;" +
            "-fx-border-radius: 4px;";

    private void setStyles() {
        runeLiteButton.setStyle(PRIMARY_BUTTON_STYLE);
        hydraButton.setStyle(PRIMARY_BUTTON_STYLE);
        launchButton.setStyle(SUCCESS_BUTTON_STYLE);
        imageView.setEffect(new DropShadow(20, Color.BLACK));

        moonSVG = new SVGPath();
        moonSVG.setContent("M144.7 98.7c-21 34.1-33.1 74.3-33.1 117.3c0 98 62.8 181.4 150.4 211.7c-12.4 2.8-25.3 4.3-38.6 4.3C126.6 432 48 353.3 48 256c0-68.9 39.4-128.4 96.8-157.3zm62.1-66C91.1 41.2 0 137.9 0 256C0 379.7 100 480 223.5 480c47.8 0 92-15 128.4-40.6c1.9-1.3 3.7-2.7 5.5-4c4.8-3.6 9.4-7.4 13.9-11.4c2.7-2.4 5.3-4.8 7.9-7.3c5-4.9 6.3-12.5 3.1-18.7s-10.1-9.7-17-8.5c-3.7 .6-7.4 1.2-11.1 1.6c-5 .5-10.1 .9-15.3 1c-1.2 0-2.5 0-3.7 0l-.3 0c-96.8-.2-175.2-78.9-175.2-176c0-54.8 24.9-103.7 64.1-136c1-.9 2.1-1.7 3.2-2.6c4-3.2 8.2-6.2 12.5-9c3.1-2 6.3-4 9.6-5.8c6.1-3.5 9.2-10.5 7.7-17.3s-7.3-11.9-14.3-12.5c-3.6-.3-7.1-.5-10.7-.6c-2.7-.1-5.5-.1-8.2-.1c-3.3 0-6.5 .1-9.8 .2c-2.3 .1-4.6 .2-6.9 .4z");
        moonSVG.setScaleX(0.05);
        moonSVG.setScaleY(0.05);

        darkModeButton.setGraphic(moonSVG);
        darkModeButton.setStyle(DEFAULT_BUTTON_STYLE);

    }

    @FXML
    public void initialize() {
        setStyles();

        this.isRunning = new AtomicBoolean(false);
        this.lock = new ReentrantLock();

        if(!toggle.canDetectClientJars()) {
            String errorMessage = "No jars detected at:" + config.getRuneLiteDirectory() +
                    " Please make sure the JAR files exist and are named correctly.";
            log.error(errorMessage);
            errorLabel.setText("Ensure RuneLite");
            return;
        }

        if (config.getClientType() == ClientType.UNKNOWN) {
            log.error("Could not determine current client type. Make sure your Hydra client is named \"RuneLite-hydra.jar\" and your RuneLite client is named \"RuneLite.jar\".");

            // Attempt to delete the hydra conf file so the next time this application is started AND the jar files are
            // named correctly it will work.
            try {
                Files.delete(Path.of(config.getHydraConfFilePath()));
            } catch (IOException e) {
                log.error("Failed to delete hydra-toggle conf file.");
            }
        }

        currentClientLabel.setText(config.getClientType().toString());
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
            InputStream is = HydraToggleApplication.class.getResourceAsStream("hydra_logo.png");
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
            moonSVG.setFill(Color.WHITESMOKE);
        } else {
            root.setStyle("-fx-background-color: #ffffff");
            staticCurrentClientLabel.setTextFill(Color.BLACK);
            title.setTextFill(Color.BLACK);
            moonSVG.setFill(Color.BLACK);
        }
    }

    @FXML
    protected void onRuneLiteButtonClick() {
        if(toggle.renameFile("RuneLite.jar", "RuneLite-hydra.jar") && toggle.renameFile("RuneLite-real.jar", "RuneLite.jar")) {
            toggle.getConfig().setClientType(ClientType.RUNELITE);
            toggleClientType();
        } else {
            errorLabel.setText("Could not switch clients. See logs for more info.");
        }
    }

    @FXML
    protected void onHydraButtonClick() {
        if(toggle.renameFile("RuneLite.jar", "RuneLite-real.jar") && toggle.renameFile("RuneLite-hydra.jar", "RuneLite.jar")) {
            toggle.getConfig().setClientType(ClientType.HYDRA);
            toggleClientType();
        } else {
            errorLabel.setText("Could not switch clients. See logs for more info.");
        }
    }

    @FXML
    protected void onDarkModeClick() {
        toggle.getConfig().setDarkModeEnabled(!toggle.getConfig().isDarkModeEnabled());
        toggleDarkMode();
    }

    @FXML
    protected void onLaunch() {
        new Thread(() -> {
            if (lock.tryLock()) {
                try {
                    if (!isRunning.get()) {
                        isRunning.set(true);

                        try {
                            ProcessBuilder processBuilder = new ProcessBuilder(config.getRuneLiteExePath());
                            Process process = processBuilder.start();

                            System.out.println("Process started.");
                            Platform.runLater(() -> {
                                launchButton.setDisable(true);
                                launchButton.setText("Client is Running...");
                            });

                            // TODO This takes a long time to detect that the client closed (i.e. > 25 seconds). Find a way around this.
                            int exitCode = process.waitFor();
                            System.out.println("Process exited with code: " + exitCode);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            isRunning.set(false);
                            Platform.runLater(() -> {
                                launchButton.setDisable(false);
                                launchButton.setText("Launch Client");
                            });
                        }
                    } else {
                        System.out.println("Process is already running. Cannot start a new instance.");
                    }
                } finally {
                    lock.unlock();

                }
            } else {
                System.out.println("Another thread is attempting to start the process. Try again later.");
            }
        }).start();
    }
}