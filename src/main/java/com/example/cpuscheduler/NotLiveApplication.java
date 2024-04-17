package com.example.cpuscheduler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class NotLiveApplication extends Application {
    private PriorityQueue<Process> processes;
    NotLiveApplication(PriorityQueue<Process> processes){
        this.processes = processes;
    }
    private TableView<GanttProcess> table = new TableView<>();
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Not Live Scheduling");
        ArrayList<GanttProcess> ganttProcesses = new ArrayList<>();

        switch (HelloController.processType) {
            case "FCFS" -> {
                FirstComeFirstServe fcfs = new FirstComeFirstServe();
                for (Process process : processes) {
                    fcfs.enqueue(process);
                }
                int time = 0;
                while (true) {
                    while (!processes.isEmpty() && time == processes.peek().getArrivalTime()) {
                        fcfs.enqueue(processes.poll());
                    }
                    if (!fcfs.fetchNextTask(time) && processes.isEmpty())
                        break;
                    time++;
                }
                ganttProcesses = fcfs.getGanttChart();
            }
            case "Round Robin" -> {
                RR roundRobin = new RR(2);
                for (Process process : processes) {
                    roundRobin.enqueue(process);
                }
                int time = 0;
                while (true) {
                    while (!processes.isEmpty() && time == processes.peek().getArrivalTime()) {
                        roundRobin.enqueue(processes.poll());
                    }
                    if (!roundRobin.fetchNextTask(time) && processes.isEmpty())
                        break;
                    time++;
                }
                ganttProcesses = roundRobin.getGanttChart();
            }
            case "SJF Preemptive" -> {
                SJFPreemptive sjfPreemptive = new SJFPreemptive();
                for (Process process : processes) {
                    sjfPreemptive.enqueue(process);
                }
                int time = 0;
                while (true) {
                    while (!processes.isEmpty() && time == processes.peek().getArrivalTime()) {
                        sjfPreemptive.enqueue(processes.poll());
                    }
                    if (!sjfPreemptive.fetchNextTask(time) && processes.isEmpty())
                        break;
                    time++;
                }
                ganttProcesses = sjfPreemptive.getGanttChart();
            }
            case "SJF Non-Preemptive" -> {
                SJFNonPreemptive sjfNonPreemptive = new SJFNonPreemptive();
                for (Process process : processes) {
                    sjfNonPreemptive.enqueue(process);
                }
                int time = 0;
                while (true) {
                    while (!processes.isEmpty() && time == processes.peek().getArrivalTime()) {
                        sjfNonPreemptive.enqueue(processes.poll());
                    }
                    if (!sjfNonPreemptive.fetchNextTask(time) && processes.isEmpty())
                        break;
                    time++;
                }
                ganttProcesses = sjfNonPreemptive.getGanttChart();
            }
            case "Priority Preemptive" -> {
                PriorityPreemptive priorityPreemptive = new PriorityPreemptive();
                for (Process process : processes) {
                    priorityPreemptive.enqueue(process);
                }
                int time = 0;
                while (true) {
                    while (!processes.isEmpty() && time == processes.peek().getArrivalTime()) {
                        priorityPreemptive.enqueue(processes.poll());
                    }
                    if (!priorityPreemptive.fetchNextTask(time) && processes.isEmpty())
                        break;
                    time++;
                }
                ganttProcesses = priorityPreemptive.getGanttChart();
            }
            case "Priority Non-Preemptive" -> {
                Priority_NonPreemptive priorityNonPreemptive = new Priority_NonPreemptive();
                for (Process process : processes) {
                    priorityNonPreemptive.enqueue(process);
                }
                int time = 0;
                while (true) {
                    while (!processes.isEmpty() && time == processes.peek().getArrivalTime()) {
                        priorityNonPreemptive.enqueue(processes.poll());
                    }
                    if (!priorityNonPreemptive.fetchNextTask(time) && processes.isEmpty())
                        break;
                    time++;
                }
                ganttProcesses = priorityNonPreemptive.getGanttChart();
            }
        }

        TableColumn<GanttProcess, Integer> pidColumn = new TableColumn<>("PID");
        pidColumn.setCellValueFactory(new PropertyValueFactory<GanttProcess, Integer>("pid"));

        TableColumn<GanttProcess, Integer> durationColumn = new TableColumn<>("Running Time");
        durationColumn.setCellValueFactory(new PropertyValueFactory<GanttProcess, Integer>("runningTime"));

        // Add columns to the table
        table.getColumns().addAll(pidColumn, durationColumn);
        table.setLayoutX(10);
        table.setLayoutY(10);
        table.setPrefWidth(350);
        table.setPrefHeight(350);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for(GanttProcess ganttProcess: ganttProcesses) {
            table.getItems().add(ganttProcess);
        }

        AnchorPane layout = new AnchorPane();
        layout.setStyle("-fx-background-color: #EEEEEE");
        layout.getChildren().add(table);
        Scene scene = new Scene(layout,370,370);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
