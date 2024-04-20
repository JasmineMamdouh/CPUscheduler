package com.example.cpuscheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.ResourceBundle;


public class ProcessController implements Initializable {
    @FXML
    private TextField pidTextField;
    @FXML
    private TextField arrivalTimeTextField;
    @FXML
    private TextField burstTimeTextField;
    @FXML
    private TextField additionalTextField;
    @FXML
    private Button addProcess;
    @FXML
    private Button deleteProcess;
    @FXML
    private Button liveScheduling;
    @FXML
    private Button notLiveScheduling;
    @FXML
    private Label warningLabel;
    @FXML
    private Label AdditionalFieldLabel;
    @FXML
    private VBox additionalSection;
    @FXML
    private TableView<Process> processTable;
    @FXML
    private TableColumn<Process, Integer> arrivalTimeColumn;
    @FXML
    private TableColumn<Process, Integer> pidColumn;
    @FXML
    private TableColumn<Process, Integer> burstTimeColumn;
    @FXML
    private TableColumn<Process, Integer> priorityColumn;


    ObservableList<Process> processes = FXCollections.observableArrayList();
    private Map<Integer, Color> processColorMap = new HashMap<>();
    private boolean quantumSet = false;
    private int quantum = 0;


    @FXML
    protected void onAddButtonClick() {
        boolean safe = true;
        Process process;
        warningLabel.setText("");
        if(
            pidTextField.getText().isEmpty()
            || burstTimeTextField.getText().isEmpty()
            || arrivalTimeTextField.getText().isEmpty()
        ) {
            safe = false;
            warningLabel.setText("Please Fill All The Fields.");
        }
        if (
            HelloController.processType.contains("Priority")
            || (HelloController.processType.contains("Round") && !quantumSet)
        ) {
            if(additionalTextField.getText().isEmpty()) {
                safe = false;
                warningLabel.setText("Please Fill All The Fields.");
            }
        }
        if(safe){
            for (Process proc : processes) {
                if (proc.getPid() == Integer.parseInt(pidTextField.getText())) {
                    safe = false;
                    warningLabel.setText("This PID Already Exists.");
                }
            }
            if(
                Integer.parseInt(pidTextField.getText()) < 0
                || Integer.parseInt(arrivalTimeTextField.getText()) < 0
            ) {
                safe =  false;
                warningLabel.setText("Only Non Negative Integers Allowed.");
            }
            if (Integer.parseInt(burstTimeTextField.getText()) <= 0) {
                safe = false;
                warningLabel.setText("Burst Time Should Be A Positive Integer.");
            }
            if (HelloController.processType.contains("Priority")) {
                if(Integer.parseInt(additionalTextField.getText()) < 0
                    || Integer.parseInt(additionalTextField.getText()) > 10) {
                    safe = false;
                    warningLabel.setText("Priority Should Be Between 0 And 10.");
                }
            }
            if(HelloController.processType.contains("Round") && !quantumSet) {
                if (Integer.parseInt(additionalTextField.getText()) <= 0) {
                    safe = false;
                    warningLabel.setText("Quantum Should Be A Positive Integer.");
                }
            }
        }
        if(safe) {
            if (HelloController.processType.contains("Priority")) {
                process = new Process(
                        Integer.parseInt(pidTextField.getText()),
                        Integer.parseInt(burstTimeTextField.getText()),
                        Integer.parseInt(additionalTextField.getText()),
                        Integer.parseInt(arrivalTimeTextField.getText())
                );
                additionalTextField.clear();
            } else {
                process = new Process(
                        Integer.parseInt(pidTextField.getText()),
                        Integer.parseInt(burstTimeTextField.getText()),
                        Integer.parseInt(arrivalTimeTextField.getText())
                );

                if (HelloController.processType.contains("Round") && quantum == 0) {
                    quantum = Integer.parseInt(additionalTextField.getText());
                    additionalSection.setDisable(true); // Disable the additional field after setting the quantum
                    quantumSet = true;
                }
            }
            
            double red = Math.random();
            double green = Math.random();
            double blue = Math.random();
            Color color = Color.color(red, green, blue);
            processColorMap.put(process.getPid(), color);
            processTable.getItems().add(process);
            processes = processTable.getItems();

            // Clearing the fields
            pidTextField.clear();
            arrivalTimeTextField.clear();
            burstTimeTextField.clear();
        }
    }

    private PriorityQueue<Process> getTableProcesses() {
        PriorityQueue<Process> processesQueue = new PriorityQueue<Process>((px, py) -> px.getArrivalTime() - py.getArrivalTime());
        processes =  processTable.getItems();
        for (Process process : processes) {
            processesQueue.add((Process)process.clone());
        }
        return processesQueue;
    }

    @FXML
    protected void onDeleteButtonClick() throws IOException {
        int processIndex = processTable.getSelectionModel().getSelectedIndex();
        processTable.getItems().remove(processIndex);
        processes = processTable.getItems();
    }

    @FXML
    protected void onNotLiveButtonClick() throws IOException {
        NotLiveApplication notLiveApplication = new NotLiveApplication(this.getTableProcesses(), processColorMap, quantum);
        Stage notLiveStage = new Stage();
        notLiveApplication.start(notLiveStage);
        notLiveStage.show();
    }

    @FXML
    protected void onLiveTable() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("schedulers.fxml"));
        Stage schedulerStage = new Stage();
        schedulerStage.setScene(new Scene((Pane) loader.load()));
        SchedulerController cont = loader.<SchedulerController>getController();
        cont.initData(this.getTableProcesses(), processColorMap, quantum);
        schedulerStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pidColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("pid"));

        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("arrivalTime"));
        arrivalTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        arrivalTimeColumn.setOnEditCommit(e -> {
            Process p = e.getRowValue();
            p.setArrivalTime(e.getNewValue());
        });
        
        burstTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("burstTime"));
        burstTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        burstTimeColumn.setOnEditCommit(e -> {
            Process p = e.getRowValue();
            p.setBurstTime(e.getNewValue());
        });

        priorityColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("priority"));
        priorityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        priorityColumn.setOnEditCommit(e -> {
            Process p = e.getRowValue();
            p.setPriority(e.getNewValue());
        });

        additionalSection.managedProperty().bind(additionalSection.visibleProperty());

        if(HelloController.processType.contains("Priority")){
            priorityColumn.setVisible(true);
            AdditionalFieldLabel.setText("Priority:");
            additionalSection.setVisible(true);
        } else if (HelloController.processType.contains("Round")) {
            AdditionalFieldLabel.setText("Quantum:");
            additionalSection.setVisible(true);
        }

        processColorMap.put(-1, Color.rgb(197, 211, 232));
    }
}
