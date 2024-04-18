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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import kotlin.Pair;

import java.io.IOException;
import java.util.*;

public class NotLiveApplication extends Application {
    private PriorityQueue<Process> processes;
    private int quantum;

    NotLiveApplication(PriorityQueue<Process> processes, int quantum){
        this.processes = processes;
        this.quantum = quantum;
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
            case "Round Robin" -> scheduler = new RR(quantum);
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
        Map<Integer, Color> processColorMap = new HashMap<>();

        int time = 0;
        //Gantt Chart
        for(GanttProcess ganttProcess: ganttProcesses) {
            Color color = processColorMap.get(ganttProcess.getPid());

            if (color == null) {
                // Generate random color if new PID
                Random random = new Random();
                double red = Math.random();
                double green = Math.random();
                double blue = Math.random();
                color = Color.color(red, green, blue);
                processColorMap.put(ganttProcess.getPid(), color);
            }

            Rectangle rectangle = new Rectangle(ganttProcess.getRunningTime() * 30, 15, color);
            rectangle.setX(xAxis);
            rectangle.setY(yAxis);
            xAxis += ganttProcess.getRunningTime() * 30;

            double endX = rectangle.getX() + rectangle.getWidth();

            time += ganttProcess.getRunningTime();
            Text timeText = new Text(String.valueOf(time) + "s");
            timeText.setFont(new Font(10));

            timeText.setX(endX - timeText.getLayoutBounds().getWidth() - 2); // Adjust padding as needed
            timeText.setY(rectangle.getY() + rectangle.getHeight() + 10);

            Text processName = new Text("P" + ganttProcess.getPid()); // Adjust format if needed
            processName.setFont(new Font(10)); // Adjust font size and style as desired

            processName.setX(rectangle.getX() + 10); // Adjust padding as needed
            processName.setY(rectangle.getY() + 10);
            layout.getChildren().addAll(rectangle, processName, timeText);
        }

        //adding labels for the average waiting time and average turn around time
        Label avgWaitingTimeLabel = new Label("Average Waiting Time:");
        avgWaitingTimeLabel.setLayoutX(10.0);
        avgWaitingTimeLabel.setLayoutY(510.0);
        avgWaitingTimeLabel.setFont(new Font(14.0));
        layout.getChildren().add(avgWaitingTimeLabel);

        Label avgTurnaroundTimeLabel = new Label("Average Turnaround Time:");
        avgTurnaroundTimeLabel.setLayoutX(10.0);
        avgTurnaroundTimeLabel.setLayoutY(530.0);
        avgTurnaroundTimeLabel.setFont(new Font(14.0));
        layout.getChildren().add(avgTurnaroundTimeLabel);

        // Set text of the labels to display the averages
        Label avgWaitingTimeValueLabel = new Label(String.format("%.2f", scheduler.calcAvgWaitingTime()) + "s");
        avgWaitingTimeValueLabel.setLayoutX(200.0);
        avgWaitingTimeValueLabel.setLayoutY(510.0);
        avgWaitingTimeValueLabel.setFont(new Font(14.0));
        layout.getChildren().add(avgWaitingTimeValueLabel);

        Label avgTurnaroundTimeValueLabel = new Label(String.format("%.2f", scheduler.calcAvgTurnaroundTime()) + "s");
        avgTurnaroundTimeValueLabel.setLayoutX(200.0);
        avgTurnaroundTimeValueLabel.setLayoutY(530.0);
        avgTurnaroundTimeValueLabel.setFont(new Font(14.0));
        layout.getChildren().add(avgTurnaroundTimeValueLabel);


        layout.setStyle("-fx-background-color: #EEEEEE");
        layout.getChildren().add(table);
        Scene scene = new Scene(layout,370,520);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
