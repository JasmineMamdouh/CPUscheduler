package com.example.cpuscheduler;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Glow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

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

        AnchorPane layout = new AnchorPane();
        Label label = new Label("Scheduling Table");

        // Set properties
        label.setAlignment(Pos.CENTER);
        label.setLayoutX(10.0);
        label.setLayoutY(10.0);
        label.setPrefHeight(40.0);
        label.setPrefWidth(350.0);
        label.setStyle("-fx-background-color: #E8D5C4; -fx-border-radius: 10px;");

        // Set font size
        label.setFont(new Font(24.0));

        Glow glow = new Glow();
        label.setEffect(glow);

        layout.getChildren().add(label);

        Label label2 = new Label("Gantt Chart");

        // Set properties
        label2.setAlignment(Pos.CENTER);
        label2.setLayoutX(10.0);
        label2.setLayoutY(420.0);
        label2.setPrefHeight(40.0);
        label2.setPrefWidth(350.0);
        label2.setStyle("-fx-background-color: #E8D5C4; -fx-border-radius: 10px;");

        // Set font size
        label2.setFont(new Font(24.0));
        label2.setEffect(glow);

        layout.getChildren().add(label2);

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
        table.setLayoutY(60);
        table.setPrefWidth(350);
        table.setPrefHeight(350);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for(GanttProcess ganttProcess: ganttProcesses) {
            table.getItems().add(ganttProcess);
        }

        int xAxis = 10;
        int yAxis = 470;
        //Gantt Chart
        for(GanttProcess ganttProcess: ganttProcesses) {
            Random random = new Random();
            double red = Math.random();
            double green = Math.random();
            double blue = Math.random();
            Color randomColor = Color.color(red, green, blue);
            Rectangle rectangle = new Rectangle(ganttProcess.runningTime*20, 15, randomColor);
            rectangle.setX(xAxis);
            rectangle.setY(yAxis);
            xAxis += ganttProcess.runningTime*20;

            layout.getChildren().add(rectangle);
        }



        layout.setStyle("-fx-background-color: #EEEEEE");
        layout.getChildren().add(table);
        Scene scene = new Scene(layout,370,500);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
