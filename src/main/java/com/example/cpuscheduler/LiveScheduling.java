package com.example.cpuscheduler;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;

public class LiveScheduling extends Application implements Runnable {
    private TableView<Process> table = new TableView<>();
    private PriorityQueue<Process> processes;
    private ObservableList<Process> processesList = FXCollections.observableArrayList();
    private ScheduledFuture<?> t;
    private Schedulers scheduler;
    private int time = 0;
    private Map<Integer, Color> processColorMap;
    private ArrayList<GanttProcess> ganttChart;
    private Pane layout;
    private Text timeText;
    private Text avgWaitTime;
    private Text avgTurnaroundTime;
    private Text text;
    private boolean sameProcess = false;
    private int lastx = 20;
    private int y = 50;
    private int last_pid = -1;
    private int quantum;
    private ArrayList<Shape> holder = new ArrayList<>(2);

    LiveScheduling(PriorityQueue<Process> processes, Map<Integer, Color> processColorMap, int quantum) {
        this.processes = processes;
        this.processColorMap = processColorMap;
        this.quantum = quantum;
    }

    private void calculateAndDisplayAverages() {
        float avgWait = scheduler.calcAvgWaitingTime();
        float avgTurnaround = scheduler.calcAvgTurnaroundTime();

        // Check if average waiting time is not NaN before updating the text
        if (!Float.isNaN(avgWait)) {
            avgWaitTime.setText("Average Waiting Time: " + String.format("%.2f", avgWait) + "s");
        } else {
            avgWaitTime.setText("Average Waiting Time:  ");
        }

        // Check if average turnaround time is not NaN before updating the text
        if (!Float.isNaN(avgTurnaround)) {
            avgTurnaroundTime.setText("Average Turnaround Time: " + String.format("%.2f", avgTurnaround) + "s");
        } else {
            avgTurnaroundTime.setText("Average Turnaround Time:  ");
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

                table.refresh();

                boolean running = scheduler.fetchNextTask(time);

                int width = 30;
                int length = 20;

                timeText.setText("Time = " + time + " s");

                if (!running && processes.isEmpty()) {
                    layout.getChildren().remove(text);
                    text = new Text(time + "s");
                    text.setX(lastx);
                    text.setY(y + length + 10);
                    text.setFont(new Font(10));
                    layout.getChildren().addAll(holder);
                    holder.clear();
                    layout.getChildren().add(text);
                    t.cancel(false);
                    return;
                }

                ganttChart = scheduler.getGanttChart();
                int current_pid = ganttChart.get(ganttChart.size() - 1).getPid();
                Color color = processColorMap.get(current_pid);

                if (sameProcess) {
                    layout.getChildren().remove(text);
                    sameProcess = false;
                }

                if (current_pid == last_pid) {
                    sameProcess = true;
                }  

                if (!holder.isEmpty()) {
                    layout.getChildren().addAll(holder);
                    holder.clear();
                }

                text = new Text(time + "s");
                text.setX(lastx);
                text.setY(y + length + 10);
                text.setFont(new Font(10));
                layout.getChildren().add(text);

                Rectangle rectangle = new Rectangle(width, length);
                rectangle.setX(lastx);
                rectangle.setY(y);
                lastx += width;
                rectangle.setFill(color);
                holder.add(rectangle);

                if (current_pid != last_pid) {
                    Text processName = new Text("P" + current_pid);
                    processName.setFont(new Font(10));

                    processName.setX(rectangle.getX() + 10);
                    processName.setY(rectangle.getY() + 13);
                    holder.add(processName);
                }

                last_pid = current_pid;
                time++;
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Calculate average turnaround time and average waiting time
        calculateAndDisplayAverages();
    }

    @Override
    public void start(Stage stage) {
        VBox mainLayout = new VBox(); // Use VBox for vertical stacking

        Label label = new Label("Scheduling Table");
        label.setAlignment(javafx.geometry.Pos.CENTER);
        label.setStyle("-fx-background-color: #E8D5C4; -fx-border-radius: 10px;");
        label.setPrefHeight(40.0);
        label.setFont(new javafx.scene.text.Font(24.0));
        label.setPrefWidth(350.0);
        label.setLayoutX(10.0);
        label.setLayoutY(10.0);
        Glow glow = new Glow();
        label.setEffect(glow);

        Label label2 = new Label("Gantt Chart");
        label2.setAlignment(javafx.geometry.Pos.CENTER);
        label2.setStyle("-fx-background-color: #E8D5C4; -fx-border-radius: 10px;");
        label2.setPrefHeight(40.0);
        label2.setFont(new javafx.scene.text.Font(24.0));
        label2.setPrefWidth(350.0);
        label2.setLayoutX(10.0);
        label2.setLayoutY(430.0);
        label2.setEffect(glow);


        TableColumn<Process, Integer> pidColumn = new TableColumn<>("PID");
        pidColumn.setCellValueFactory(new PropertyValueFactory<>("pid"));

        TableColumn<Process, Integer> startColumn = new TableColumn<>("Start Time");
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        TableColumn<Process, Integer> burstColumn = new TableColumn<>("Remaining Time");
        burstColumn.setCellValueFactory(new PropertyValueFactory<>("remainingTime"));

        TableColumn<Process, Integer> endColumn = new TableColumn<>("End Time");
        endColumn.setCellValueFactory(new PropertyValueFactory<>("completionTime"));

        table.getColumns().addAll(pidColumn, startColumn, burstColumn, endColumn);
        table.setPrefWidth(370);
        table.setPrefHeight(350);
        table.setLayoutY(60);
        table.setLayoutX(10);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(processesList);

        mainLayout.setStyle("-fx-background-color: #EEEEEE");
        mainLayout.getChildren().addAll(label, table,label2);

        Scene scene = new Scene(mainLayout, 700, 780);
        stage.setScene(scene);
        stage.setTitle("Scheduling Table");

        layout = new Pane();
        layout.setStyle("-fx-background-color: #EEEEEE");
        timeText = new Text("Time = " + time + " s");
        timeText.setX(lastx);
        timeText.setY(y);
        y += 10;

        mainLayout.getChildren().add(layout);
        layout.getChildren().add(timeText);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        t = executor.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);

        switch (HelloController.processType) {
            case "FCFS" -> scheduler = new FirstComeFirstServe();
            case "Round Robin" -> scheduler = new RR(quantum);
            case "SJF Non-Preemptive" -> scheduler = new SJFNonPreemptive();
            case "SJF Preemptive" -> scheduler = new SJFPreemptive();
            case "Priority Non-Preemptive" -> scheduler = new Priority_NonPreemptive();
            case "Priority Preemptive" -> scheduler = new PriorityPreemptive();
            default -> scheduler = new FirstComeFirstServe();
        }

        //adding labels for the average waiting time and average turn around time
        avgWaitTime = new Text();
        avgWaitTime.setX(10.0);
        avgWaitTime.setY(120.0);
        avgWaitTime.setFont(new Font(14.0));
        layout.getChildren().add(avgWaitTime);

        avgTurnaroundTime = new Text();
        avgTurnaroundTime.setX(10.0);
        avgTurnaroundTime.setY(150.0);
        avgTurnaroundTime.setFont(new Font(14.0));
        layout.getChildren().add(avgTurnaroundTime);

        layout.setPadding(new Insets(10));

        Label label3 = new Label("New Processes");
        label3.setAlignment(javafx.geometry.Pos.CENTER);
        label3.setStyle("-fx-background-color: #E8D5C4; -fx-border-radius: 10px;");
        label3.setPrefHeight(40.0);
        label3.setFont(new javafx.scene.text.Font(24.0));
        label3.setPrefWidth(350.0);
        label3.setLayoutX(10.0);
        label3.setLayoutY(430.0);
        label3.setEffect(glow);
        mainLayout.getChildren().add(label3);

        Pane addProcessLayout = new Pane();

        TextField pidTextField = new TextField();
        pidTextField.setLayoutX(10.0);
        pidTextField.setLayoutY(10.0);
        pidTextField.setPrefHeight(25.0);
        pidTextField.setPrefWidth(85.0);
        pidTextField.setPromptText("PID");

        TextField arrivalTimeTextField = new TextField();
        arrivalTimeTextField.setLayoutX(120.0);
        arrivalTimeTextField.setLayoutY(10.0);
        arrivalTimeTextField.setPrefHeight(25.0);
        arrivalTimeTextField.setPrefWidth(85.0);
        arrivalTimeTextField.setPromptText("Arrival Time");

        TextField burstTimeTextField = new TextField();
        burstTimeTextField.setLayoutX(230.0);
        burstTimeTextField.setLayoutY(10.0);
        burstTimeTextField.setPrefHeight(25.0);
        burstTimeTextField.setPrefWidth(85.0);
        burstTimeTextField.setPromptText("Burst Time");

        Button addProcessButton = new Button();

        // Set properties
        addProcessButton.setLayoutX(350.0);
        addProcessButton.setLayoutY(10.0);
        addProcessButton.setMnemonicParsing(false);
        addProcessButton.setPrefHeight(36.0);
        addProcessButton.setPrefWidth(135.0);
        addProcessButton.setStyle("-fx-background-color: #3A98B9;");
        addProcessButton.setText("Add Process");
        addProcessButton.setTextFill(javafx.scene.paint.Color.WHITE);
        addProcessButton.setOnAction(event -> onAddButtonClick());

        addProcessLayout.getChildren().addAll(pidTextField,arrivalTimeTextField,burstTimeTextField, addProcessButton);
        mainLayout.getChildren().add(addProcessLayout);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setSpacing(10);

        stage.show();
    }
    private void onAddButtonClick(){
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
