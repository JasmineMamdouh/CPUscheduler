package com.example.cpuscheduler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class ProcessApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("processes-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Image iconImage = new Image(getClass().getResourceAsStream("icon.png"));
        stage.getIcons().add(iconImage);
        stage.setTitle("CPU Scheduler");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}