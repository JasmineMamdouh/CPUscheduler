package com.example.cpuscheduler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Glow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
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
    ScheduledFuture<?> t;
    Schedulers scheduler;
    int time = 0;

    LiveScheduling(PriorityQueue<Process> processes){
        this.processes = processes;
    }

    @Override
    public void run() {
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    while (!processes.isEmpty() && time == processes.peek().getArrivalTime()) {
                        scheduler.enqueue(processes.peek());
                        // table.getItems().add(processes.poll());
                        processesList.add(processes.poll());
                    }

                    processesList.sort((p1, p2) -> p1.getStartTime() - p2.getStartTime());

                    table.refresh();

                    if (!scheduler.fetchNextTask(time) && processes.isEmpty()) {
                        t.cancel(false);
                        return;
                    }

                    time++;
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void start(Stage stage) throws IOException {

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

        // Set properties
        Label label2 = new Label("Gantt Chart");
        label2.setAlignment(Pos.CENTER);
        label2.setLayoutX(20.0);
        label2.setLayoutY(400.0);
        label2.setPrefHeight(40.0);
        label2.setPrefWidth(350.0);
        label2.setStyle("-fx-background-color: #E8D5C4; -fx-border-radius: 10px;");

        // Set font size
        label2.setFont(new Font(24.0));
        label2.setEffect(glow);

        layout.getChildren().add(label2);

        switch (HelloController.processType) {
            case "FCFS" -> scheduler = new FirstComeFirstServe();
            case "Round Robin" -> scheduler = new RR(3);
            case "SJF Non-Preemptive" -> scheduler = new SJFNonPreemptive();
            case "SJF Preemptive" -> scheduler = new SJFPreemptive();
            case "Priority Non-Preemptive" -> scheduler = new Priority_NonPreemptive();
            case "Priority Preemptive" -> scheduler = new PriorityPreemptive();
            default -> scheduler = new FirstComeFirstServe();
        }

        TableColumn<Process, Integer> pidColumn = new TableColumn<>("PID");
        pidColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("pid"));

        TableColumn<Process, Integer> startColumn = new TableColumn<>("Start Time");
        startColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("startTime"));

        TableColumn<Process, Integer> burstColumn = new TableColumn<>("Remaining Time");
        burstColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("remainingTime"));

        TableColumn<Process, Integer> endColumn = new TableColumn<>("End Time");
        endColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("completionTime"));




        // Add columns to the table
        table.getColumns().addAll(pidColumn, startColumn,burstColumn,endColumn);
        table.setLayoutX(20);
        table.setLayoutY(60);
        table.setPrefWidth(350);
        table.setPrefHeight(350);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setItems(processesList);

        layout.setStyle("-fx-background-color: #EEEEEE");
        layout.getChildren().add(table);
        Scene scene = new Scene(layout,370,520);
        stage.setScene(scene);
        stage.show();


        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        t = executor.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        launch();
    }
}
