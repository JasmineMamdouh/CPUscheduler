package com.example.cpuscheduler;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private Label welcomeText;

    @FXML
    private ChoiceBox<String> choiceBox;

    static String processType;

    @FXML
    protected void onDoneButtonClick() throws IOException {
        processType = choiceBox.getValue();
        if(processType != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("processes-view.fxml"));
            Stage processStage = new Stage();
            Image iconImage = new Image(getClass().getResourceAsStream("icon.png"));
            processStage.getIcons().add(iconImage);
            processStage.setTitle("CPU Scheduler");
            processStage.setScene(new Scene((Pane)loader.load()));
            // ProcessController cont = loader.<ProcessController>getController();
            // cont.initData(quantum);
            processStage.show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choiceBox.getItems().addAll("FCFS", "Round Robin", "SJF Preemptive",
                "SJF Non-Preemptive", "Priority Preemptive", "Priority Non-Preemptive");
    }
}