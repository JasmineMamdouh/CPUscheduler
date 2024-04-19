package com.example.cpuscheduler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.PriorityQueue;
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
    @FXML
    private Button startScheduling;


    int quantum = 0;
    HashMap<Integer, Color> colors = new HashMap<>();

    public PriorityQueue<Process> onClick()
    {
        PriorityQueue<Process> p = new PriorityQueue<Process>((px, py) -> px.getArrivalTime() - py.getArrivalTime());
        for(Process P: processes) {
            if (HelloController.processType.contains("Priority"))
            {
                p.add(new Process(P.getPid(), P.getBurstTime(), P.getArrivalTime(), P.getPriority()));
            }
            else
            {
                p.add(new Process(P.getPid(), P.getBurstTime(), P.getArrivalTime()));
            }
        }
        return p;
    }
    private final PriorityQueue<Process> processes = new PriorityQueue<Process>((px, py) -> px.getArrivalTime() - py.getArrivalTime());
    private final TableView<Process> table = new TableView<>();
    private final TextField additionalField = new TextField();
    private final Text failText = new Text("");
    private boolean quantumSet = false;

    @FXML
    protected void onAddButtonClick() {
        boolean safe = true;
        Process process;
        failText.setText("");
        if(
                pid.getText().isEmpty()
                || burstTime.getText().isEmpty()
                || arrivalTime.getText().isEmpty()
        ) {
            safe = false;
            failText.setText("Please Fill All The Fields");
        }
        if (HelloController.processType.contains("Priority")
                || (HelloController.processType.contains("Round") && !quantumSet)) {
            if(additionalField.getText().isEmpty()) {
                safe = false;
                failText.setText("Please Fill All The Fields");
            }
        }
        if(safe){
            for (Process proc : processes) {
                if (proc.getPid() == Integer.parseInt(pid.getText())) {
                    safe = false;
                    failText.setText("This PID Already Exists.");
                }
            }
        }
        if(safe) {
            if (HelloController.processType.contains("Priority")) {
                process = new Process(
                        Integer.parseInt(pid.getText()),
                        Integer.parseInt(burstTime.getText()),
                        Integer.parseInt(additionalField.getText()),
                        Integer.parseInt(arrivalTime.getText())
                );
                additionalField.clear();
            } else {
                process = new Process(
                        Integer.parseInt(pid.getText()),
                        Integer.parseInt(burstTime.getText()),
                        Integer.parseInt(arrivalTime.getText())
                );

                if (HelloController.processType.contains("Round") && quantum == 0) {
                    quantum = Integer.parseInt(additionalField.getText());
                    additionalField.setDisable(true); // Disable the additional field after setting the quantum
                    quantumSet = true;
                }
            }
            processes.add(process);
            double red = Math.random();
            double green = Math.random();
            double blue = Math.random();
            Color color = Color.color(red, green, blue);
            colors.put(process.getPid(), color);
            table.getItems().add(process);

            // Clearing the fields
            pid.clear();
            arrivalTime.clear();
            burstTime.clear();
        }
    }


    @FXML
    protected void onNotLiveButtonClick() throws IOException {
        PriorityQueue<Process> p2 = new PriorityQueue<Process>((px, py) -> px.getArrivalTime() - py.getArrivalTime());
        p2=onClick();
        NotLiveApplication notLiveApplication = new NotLiveApplication(p2, quantum);
        Stage notLiveStage = new Stage();
        notLiveApplication.start(notLiveStage);
        notLiveStage.show();
    }

    @FXML
   protected void onLiveTable() throws IOException {
        PriorityQueue<Process> p3 = new PriorityQueue<Process>((px, py) -> px.getArrivalTime() - py.getArrivalTime());
        p3=onClick();
        LiveScheduling liveTable= new LiveScheduling(p3,colors, quantum);
        Stage liveStage =new Stage();
        liveTable.start(liveStage);
        liveStage.show();
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
            additionalField.setLayoutY(145);
            additionalField.setPrefWidth(85);
            pane.getChildren().add(additionalField);
            addProcess.setLayoutX(226);

            TableColumn<Process, Integer> priorityColumn = new TableColumn<>("Priority");
            priorityColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("priority"));

            table.getColumns().add(priorityColumn);
        }
        else if (HelloController.processType.contains("Round")) {
            additionalField.setPromptText("Quantum");
            additionalField.setLayoutX(28);
            additionalField.setLayoutY(145);
            additionalField.setPrefWidth(85);
            pane.getChildren().add(additionalField);
            addProcess.setLayoutX(224);
            //  RR scheduler = new RR(Integer.parseInt(addProcess.getText()));

        }
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        failText.setFont(new Font(15));
        failText.setX(30);
        failText.setY(220);
        failText.setFill(Color.RED);

        pane.getChildren().addAll(table, failText);
    }
}
