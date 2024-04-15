package com.example.cpuscheduler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ProcessController implements Initializable {
    @FXML
    private TextField pid;
    @FXML
    private TextField arrivalTime;
    @FXML
    private TextField burstTime;
    @FXML
    private Pane pane;
    @FXML
    private Button addProcess;

    private ArrayList<Process> processes = new ArrayList<>();
    private TableView<Process> table = new TableView<>();
    TextField additionalField = new TextField();;
    @FXML
    protected void onAddButtonClick() {
        Process process;
        if(HelloController.processType.contains("Priority")) {
            process = new Process(
                    Integer.parseInt(pid.getText()),
                    Integer.parseInt(burstTime.getText()),
                    Integer.parseInt(additionalField.getText()),
                    Integer.parseInt(arrivalTime.getText())
            );
        }else{
            process = new Process(
                    Integer.parseInt(pid.getText()),
                    Integer.parseInt(burstTime.getText()),
                    Integer.parseInt(arrivalTime.getText())
            );
        }
        processes.add(process);
        table.getItems().add(process);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableColumn<Process, Integer> pidColumn = new TableColumn<>("PID");
        pidColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("pid"));

        TableColumn<Process, Integer> arrivalTimeColumn = new TableColumn<>("Arrival Time");
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("arrivalTime"));

        TableColumn<Process, Integer> burstTimeColumn = new TableColumn<>("Burst Time");
        burstTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("burstTime"));

        // Add columns to the table
        table.getColumns().addAll(pidColumn, arrivalTimeColumn, burstTimeColumn);
        table.setLayoutX(460);
        table.setLayoutY(100);
        table.setPrefWidth(350);

        if(HelloController.processType.contains("Priority")){
            additionalField.setPromptText("Priority");
            additionalField.setLayoutX(28);
            additionalField.setLayoutY(173);
            additionalField.setPrefWidth(85);
            pane.getChildren().add(additionalField);
            addProcess.setLayoutX(224);

            TableColumn<Process, Integer> priorityColumn = new TableColumn<>("Priority");
            priorityColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("priority"));

            table.getColumns().add(priorityColumn);
        }
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        pane.getChildren().add(table);
    }
}