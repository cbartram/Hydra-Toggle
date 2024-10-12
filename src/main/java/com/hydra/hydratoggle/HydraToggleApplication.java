package com.hydra.hydratoggle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydra.hydratoggle.model.ClientType;
import com.hydra.hydratoggle.model.HydraConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Log4j2
public class HydraToggleApplication extends Application {

    @Inject
    private HydraConfig config;

    private static Injector injector;

    @Override
    public void init() throws Exception {
        super.init();
        injector = Guice.createInjector(new HydraToggleBindings());
        injector.injectMembers(this);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        loader.setControllerFactory(injector::getInstance);

        Image icon = new Image(getClass().getResourceAsStream("hydra_logo.png"));
        stage.getIcons().add(icon);

        Scene scene = new Scene(loader.load(), 390, 320);
        stage.setResizable(false);
        stage.setTitle("Hydra Toggle");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        try {
            if(!Objects.equals(ClientType.RUNELITE.getValue(), "RUNELITE") && !Objects.equals(ClientType.HYDRA.getValue(), "HYDRA")) {
                throw new IllegalArgumentException("The value to write must be either RUNELITE for RuneLite or HYDRA for Hydra RuneLite");
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(config.getHydraConfFilePath()), config);
        } catch (IOException e) {
            System.err.println("Error occurred while persisting configuration on program exit.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}