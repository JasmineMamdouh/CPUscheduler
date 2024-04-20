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
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SchedulerController implements Runnable {
    @FXML
    private TableView<Process> processTable;
    @FXML
    private TableColumn<Process, Integer> pidColumn;
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


    private PriorityQueue<Process> processes;
    private ObservableList<Process> processesList = FXCollections.observableArrayList();
    private ScheduledFuture<?> t;
    private Schedulers scheduler;
    private int time = 0;
    private Map<Integer, Color> processColorMap;
    private ArrayList<GanttProcess> ganttChart;
    private Text text;
    private boolean sameProcess = false;
    private int lastx = 14;
    private int last_pid = -1;
    private int quantum;
    private ArrayList<Shape> holder = new ArrayList<>(2);
    
    public void initData(PriorityQueue<Process> processes, Map<Integer, Color> processColorMap, int quantum) {
        this.processes = processes;
        this.processColorMap = processColorMap;
        this.quantum = quantum;

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
                while (!processes.isEmpty() && time == processes.peek().getArrivalTime()) {
                    scheduler.enqueue(processes.peek());
                    processesList.add(processes.poll());
                }

                processesList.sort((p1, p2) -> p1.getStartTime() - p2.getStartTime());

                processTable.refresh();

                boolean running = scheduler.fetchNextTask(time);

                int width = 50;
                int length = 30;

                timeLabel.setText("Time = " + time + " s");
                // Calculate average turnaround time and average waiting time
                calculateAndDisplayAverages();

                if (!running && processes.isEmpty()) {
                    chartPane.getChildren().remove(text);
                    text = new Text(time + "s");
                    text.setX(lastx);
                    text.setY(length + 17);
                    text.setFont(new Font(12));
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
                text.setX(lastx);
                text.setY(length + 17);
                text.setFont(new Font(12));
                chartPane.getChildren().add(text);

                Rectangle rectangle = new Rectangle(width, length);
                rectangle.setLayoutX(lastx);
                rectangle.setLayoutY(6);
                lastx += width;
                rectangle.setFill(color);
                holder.add(rectangle);

                if (current_pid != last_pid) {
                    Text processName = new Text();
                    if (current_pid == -1) {
                        processName.setText(" "); // Adjust format if needed
                    } else {
                        processName.setText("P" + current_pid);
                    }
                    processName.setFont(new Font(15));
                    processName.setLayoutX(rectangle.getLayoutX() + 13);
                    processName.setLayoutY(rectangle.getLayoutY() + 20);
                    holder.add(processName);
                }

                last_pid = current_pid;
                time++;
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
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("startTime"));
        remainingTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("remainingTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("completionTime"));

        processTable.setItems(processesList);

        addProcessButton.setOnAction(event -> onAddButtonClick());
        startButton.setOnAction(event -> executScheduler());
        pauseButton.setOnAction(event -> t.cancel(false));

        prioritySection.managedProperty().bind(prioritySection.visibleProperty());

        if (HelloController.processType.contains("Priority")) {
            prioritySection.setVisible(true);
        }
    }

    private void onAddButtonClick(){
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
            && priorityTextField.getText().isEmpty()
        ) {
            safe = false;
            warningLabel.setText("Please Fill All The Fields.");
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
                if(Integer.parseInt(priorityTextField.getText()) < 0
                        || Integer.parseInt(priorityTextField.getText()) > 10) {
                    safe = false;
                    warningLabel.setText("Priority Should Be Between 0 And 10.");
                }
            }
        }
        if(safe) {
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

            // Clearing the fields
            pidTextField.clear();
            arrivalTimeTextField.clear();
            burstTimeTextField.clear();
        }
    }
}