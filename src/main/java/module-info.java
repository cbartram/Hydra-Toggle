module com.hydra.hydratoggle {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires static lombok;

    opens com.hydra.hydratoggle to javafx.fxml;
    exports com.hydra.hydratoggle;
    exports com.hydra.hydratoggle.controller;
    opens com.hydra.hydratoggle.controller to javafx.fxml;
    exports com.hydra.hydratoggle.model;
    opens com.hydra.hydratoggle.model to javafx.fxml;
}