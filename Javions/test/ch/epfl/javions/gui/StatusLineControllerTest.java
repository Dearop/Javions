package ch.epfl.javions.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import static javafx.application.Application.launch;
import static org.junit.jupiter.api.Assertions.*;

class StatusLineControllerTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        StatusLineController controller = new StatusLineController();
        primaryStage.setScene(new Scene(controller.pane()));
        primaryStage.show();
    }
}