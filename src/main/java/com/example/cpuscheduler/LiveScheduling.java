package com.example.cpuscheduler;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
    private int lastx = 20;
    private int y = 50;
    private int last_pid = -1;
    private int quantum;

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

                timeText.setText("Time = " + time + "second");

                int length = 30;
                int width = 20;
                Text text;

                if (!running && processes.isEmpty()) {
                    text = new Text(time + "");
                    text.setX(lastx);
                    text.setY(y + width + 20);
                    text.setFill(Color.DARKBLUE);
                    layout.getChildren().add(text);
                    t.cancel(false);
                    return;
                }

                ganttChart = scheduler.getGanttChart();
                int current_pid = ganttChart.get(ganttChart.size() - 1).getPid();
                Color color = processColorMap.get(current_pid);

                if (current_pid != last_pid) {
                    last_pid = current_pid;
                    text = new Text(time + "");
                    text.setX(lastx);
                    text.setY(y + width + 20);
                    text.setFill(Color.DARKBLUE);
                    layout.getChildren().add(text);
                }

                Rectangle rectangle = new Rectangle(length, width);
                rectangle.setX(lastx);
                rectangle.setY(y);
                lastx += length;
                rectangle.setFill(color);

                layout.getChildren().add(rectangle);

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

        Label label2 = new Label("Gantt Chart");
        label2.setAlignment(javafx.geometry.Pos.CENTER);
        label2.setStyle("-fx-background-color: #E8D5C4; -fx-border-radius: 10px;");
        label2.setPrefHeight(40.0);
        label2.setFont(new javafx.scene.text.Font(24.0));
        label2.setLayoutX(10.0);
        label2.setLayoutY(420.0);


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
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(processesList);

        mainLayout.setStyle("-fx-background-color: #EEEEEE");
        mainLayout.getChildren().addAll(label, table,label2);

        Scene scene = new Scene(mainLayout, 700, 620);
        stage.setScene(scene);
        stage.setTitle("Scheduling Table");

        layout = new Pane();
        layout.setStyle("-fx-background-color: #EEEEEE");
        timeText = new Text("Time = " + time + "second");
        timeText.setX(lastx);
        timeText.setY(y - 20);

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

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
