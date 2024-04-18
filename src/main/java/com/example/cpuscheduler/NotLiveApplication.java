package com.example.cpuscheduler;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class NotLiveApplication extends Application {
    private PriorityQueue<Process> processes;

    NotLiveApplication(PriorityQueue<Process> processes){
        this.processes = processes;
    }

    private ArrayList<GanttProcess> startScheduler(Schedulers scheduler) {
        int time = 0;
        while (true) {
            while (!processes.isEmpty() && time == processes.peek().getArrivalTime()) {
                scheduler.enqueue(processes.poll());
            }

            if (!scheduler.fetchNextTask(time) && processes.isEmpty())
                break;
            time++;
        }
        return scheduler.getGanttChart();
    }

    private TableView<GanttProcess> table = new TableView<>();

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Not Live Scheduling");
        
        Schedulers scheduler;
        switch (HelloController.processType) {
            case "FCFS" -> scheduler = new FirstComeFirstServe();
            case "Round Robin" -> scheduler = new RR(3);
            case "SJF Non-Preemptive" -> scheduler = new SJFNonPreemptive();
            case "SJF Preemptive" -> scheduler = new SJFPreemptive();
            case "Priority Non-Preemptive" -> scheduler = new Priority_NonPreemptive();
            case "Priority Preemptive" -> scheduler = new PriorityPreemptive();
            default -> scheduler = new FirstComeFirstServe();
        }

        ArrayList<GanttProcess> ganttProcesses = this.startScheduler(scheduler);

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
        System.out.println("Gantt Chart Reached = " + ganttProcesses.size());
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
