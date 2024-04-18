package com.example.cpuscheduler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
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
            ProcessApplication processApplication = new ProcessApplication();
            Stage processStage = new Stage();
            processApplication.start(processStage);
            processStage.show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choiceBox.getItems().addAll("FCFS", "Round Robin", "SJF Preemptive",
                "SJF Non-Preemptive", "Priority Preemptive", "Priority Non-Preemptive");
    }
}