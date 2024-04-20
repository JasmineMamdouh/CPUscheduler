package com.example.cpuscheduler;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LiveSchedulerController implements Runnable {
    @FXML
    private TableView<Process> processTable;
    @FXML
    private TableColumn<Process, Integer> pidColumn;
    @FXML
    private TableColumn<Process, Integer> priorityColumn;
    @FXML
    private TableColumn<Process, Integer> arrivalTimeColumn;
    @FXML
    private TableColumn<Process, Integer> burstTimeColumn;
    @FXML
    private TableColumn<Process, Integer> startTimeColumn;
    @FXML
    private TableColumn<Process, Integer> endTimeColumn;
    @FXML
    private TableColumn<Process, Integer> remainingTimeColumn;
    @FXML
    private Pane chartPane;
    @FXML
    private VBox prioritySection;
    @FXML
    private Label warningLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label avgTurnaroundTime;
    @FXML
    private Label avgWaitTime;
    @FXML
    private TextField pidTextField;
    @FXML
    private TextField arrivalTimeTextField;
    @FXML
    private TextField burstTimeTextField;
    @FXML
    private TextField priorityTextField;
    @FXML
    private Button startButton;
    @FXML
    private Button addProcessButton;
    @FXML
    private Button pauseButton;


    private ArrayList<Process> processes;
    private ObservableList<Process> processesList = FXCollections.observableArrayList();
    private ScheduledFuture<?> t;
    private Schedulers scheduler;
    private Map<Integer, Color> processColorMap;
    private ArrayList<GanttProcess> ganttChart;
    private int time = 0;
    private Text text;
    private boolean sameProcess = false;
    private int xAxis = 14;
    private int last_pid = -1;
    private ArrayList<Shape> holder = new ArrayList<>(2);
    
    public void initData(ArrayList<Process> processes, Map<Integer, Color> processColorMap, int quantum) {
        this.processes = processes;
        this.processColorMap = processColorMap;

        switch (HelloController.processType) {
            case "FCFS" -> scheduler = new FirstComeFirstServe();
            case "Round Robin" -> scheduler = new RR(quantum);
            case "SJF Non-Preemptive" -> scheduler = new SJFNonPreemptive();
            case "SJF Preemptive" -> scheduler = new SJFPreemptive();
            case "Priority Non-Preemptive" -> scheduler = new Priority_NonPreemptive();
            case "Priority Preemptive" -> scheduler = new PriorityPreemptive();
            default -> scheduler = new FirstComeFirstServe();
        }

        executScheduler();
    }

    private void calculateAndDisplayAverages() {
        float avgWait = scheduler.calcAvgWaitingTime();
        float avgTurnaround = scheduler.calcAvgTurnaroundTime();

        // Check if average waiting time is not NaN before updating the text
        if (!Float.isNaN(avgWait)) {
            avgWaitTime.setText("Avg. Waiting Time = " + String.format("%.2f", avgWait) + "s");
        } else {
            avgWaitTime.setText("Avg. Waiting Time = ");
        }

        // Check if average turnaround time is not NaN before updating the text
        if (!Float.isNaN(avgTurnaround)) {
            avgTurnaroundTime.setText("Avg. Turnaround Time = " + String.format("%.2f", avgTurnaround) + "s");
        } else {
            avgTurnaroundTime.setText("Avg. Turnaround Time = ");
        }
    }

    @Override
    public void run() {
        try {
            Platform.runLater(() -> {
                while (!processes.isEmpty() && time == processes.get(0).getArrivalTime()) {
                    scheduler.enqueue(processes.get(0));
                    processesList.add(processes.removeFirst());
                }

                processesList.sort((p1, p2) -> p1.getStartTime() - p2.getStartTime());

                processTable.refresh();

                boolean running = scheduler.fetchNextTask(time);

                int yAxis = (int)chartPane.getLayoutBounds().getCenterY();
                int width = 50;
                int length = 30;

                timeLabel.setText("Time = " + time + " s");
                // Calculate average turnaround time and average waiting time
                calculateAndDisplayAverages();

                if (!running && processes.isEmpty()) {
                    if (sameProcess) {
                        chartPane.getChildren().remove(text);
                    }
                    sameProcess = false;
                    text = new Text(time + "s");
                    text.setFont(new Font(15));
                    text.setX(xAxis - text.getLayoutBounds().getWidth() / 2);
                    text.setY(yAxis + length + text.getLayoutBounds().getHeight());
                    chartPane.getChildren().addAll(holder);
                    holder.clear();
                    chartPane.getChildren().add(text);
                    t.cancel(false);
                    return;
                }

                ganttChart = scheduler.getGanttChart();
                int current_pid = ganttChart.get(ganttChart.size() - 1).getPid();
                Color color = processColorMap.get(current_pid);

                if (sameProcess) {
                    chartPane.getChildren().remove(text);
                    sameProcess = false;
                }

                if (current_pid == last_pid) {
                    sameProcess = true;
                }

                if (!holder.isEmpty()) {
                    chartPane.getChildren().addAll(holder);
                    holder.clear();
                }

                text = new Text(time + "s");
                text.setFont(new Font(15));
                text.setX(xAxis - text.getLayoutBounds().getWidth() / 2);
                text.setY(yAxis + length + text.getLayoutBounds().getHeight());
                chartPane.getChildren().add(text);

                Rectangle rectangle = new Rectangle(width, length);
                rectangle.setLayoutX(xAxis);
                rectangle.setLayoutY(yAxis);
                xAxis += width;
                rectangle.setFill(color);
                holder.add(rectangle);
                // chartPane.getChildren().add(rectangle);

                if (current_pid != last_pid) {
                    Text processName = new Text();
                    if (current_pid == -1) {
                        processName.setText(" ");
                    } else {
                        processName.setText("P" + current_pid);
                    }
                    processName.setFont(new Font(15));
                    processName.setLayoutX(rectangle.getLayoutX() + processName.getLayoutBounds().getWidth());
                    processName.setLayoutY(rectangle.getLayoutY() + processName.getLayoutBounds().getHeight());
                    holder.add(processName);
                    // chartPane.getChildren().add(processName);
                }

                last_pid = current_pid;
                time++;
                arrivalTimeTextField.setText(String.valueOf(time));
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void executScheduler() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        t = executor.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
    }

    @FXML
    public void initialize() {
        pidColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("pid"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("priority"));
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("arrivalTime"));
        burstTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("burstTime"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("completionTime"));
        remainingTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("remainingTime"));

        processTable.setItems(processesList);

        addProcessButton.setOnAction(event -> onAddButtonClick());
        startButton.setOnAction(event -> executScheduler());
        pauseButton.setOnAction(event -> t.cancel(false));

        prioritySection.managedProperty().bind(prioritySection.visibleProperty());

        if (HelloController.processType.contains("Priority")) {
            priorityColumn.setVisible(true);
            prioritySection.setVisible(true);
        }
    }

    private void onAddButtonClick(){
        Process process;
        warningLabel.setText("");
        if(
            pidTextField.getText().isEmpty()
            || burstTimeTextField.getText().isEmpty()
            || arrivalTimeTextField.getText().isEmpty()
        ) {
            warningLabel.setText("Please Fill All The Fields.");
            return;
        }
        if (
            HelloController.processType.contains("Priority")
            && priorityTextField.getText().isEmpty()
        ) {
            warningLabel.setText("Please Fill All The Fields.");
            return;
        }
        for (Process proc : processes) {
            if (proc.getPid() == Integer.parseInt(pidTextField.getText())) {
                warningLabel.setText("This PID Already Exists.");
                return;
            }
        }
        for (Process proc : processesList) {
            if (proc.getPid() == Integer.parseInt(pidTextField.getText())) {
                warningLabel.setText("This PID Already Exists.");
                return;
            }
        }
        if(
            Integer.parseInt(pidTextField.getText()) < 0
            || Integer.parseInt(arrivalTimeTextField.getText()) < 0
        ) {
            warningLabel.setText("Only Non Negative Integers Allowed.");
            return;
        }
        if(
            Integer.parseInt(pidTextField.getText()) < 0
            || Integer.parseInt(arrivalTimeTextField.getText()) < 0
        ) {
            warningLabel.setText("Only Non Negative Integers Allowed.");
            return;
        }
        if (Integer.parseInt(arrivalTimeTextField.getText()) < time) {
            warningLabel.setText("Arrival Time Should Be " + time + " At Least.");
            return;
        }
        if (Integer.parseInt(burstTimeTextField.getText()) <= 0) {
            warningLabel.setText("Burst Time Should Be A Positive Integer.");
            return;
        }
        if (HelloController.processType.contains("Priority")) {
            if (Integer.parseInt(priorityTextField.getText()) < 0
                || Integer.parseInt(priorityTextField.getText()) > 10) {
                    warningLabel.setText("Priority Should Be Between 0 And 10.");
                    return;
            }
        }
                
        if (HelloController.processType.contains("Priority")) {
            process = new Process(
                    Integer.parseInt(pidTextField.getText()),
                    Integer.parseInt(burstTimeTextField.getText()),
                    Integer.parseInt(priorityTextField.getText()),
                    Integer.parseInt(arrivalTimeTextField.getText())
            );
            priorityTextField.clear();
        } else {
            process = new Process(
                    Integer.parseInt(pidTextField.getText()),
                    Integer.parseInt(burstTimeTextField.getText()),
                    Integer.parseInt(arrivalTimeTextField.getText())
            );
        }

        double red = Math.random();
        double green = Math.random();
        double blue = Math.random();
        Color color = Color.color(red, green, blue);
        processColorMap.put(process.getPid(), color);
        processes.add(process);

        // if(t.isCancelled()){
        //     executScheduler();
        // }

        // Clearing the fields
        pidTextField.clear();
        arrivalTimeTextField.clear();
        burstTimeTextField.clear();
    }
}