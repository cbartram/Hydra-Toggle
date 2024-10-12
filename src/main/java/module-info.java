module com.hydra.hydratoggle {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires static lombok;
    requires com.fasterxml.jackson.databind;
    requires com.google.guice;

    exports com.hydra.hydratoggle;
    exports com.hydra.hydratoggle.controller;
    exports com.hydra.hydratoggle.model;

    opens com.hydra.hydratoggle.controller to javafx.fxml, com.google.guice;
    opens com.hydra.hydratoggle to com.google.guice, javafx.fxml;
    opens com.hydra.hydratoggle.model to javafx.fxml, com.google.guice, com.fasterxml.jackson.databind;
}