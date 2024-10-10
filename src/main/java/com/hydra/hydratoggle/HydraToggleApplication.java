package com.hydra.hydratoggle;

import com.hydra.hydratoggle.model.HydraConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
public class HydraToggleApplication extends Application {

    public static ClientToggle toggle = new ClientToggle(new HydraConfig());

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HydraToggleApplication.class.getResource("main-view.fxml"));
        Image icon = new Image(HydraToggleApplication.class.getResourceAsStream("hydra_logo.jpg"));
        stage.getIcons().add(icon);

        Scene scene = new Scene(fxmlLoader.load(), 390, 240);
        stage.setTitle("Hydra Toggle");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        try {
            toggle.persistConfig();
        } catch (IOException e) {
            System.err.println("Error occurred while persisting configuration on program exit.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}