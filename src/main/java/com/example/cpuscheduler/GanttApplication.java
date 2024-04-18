package com.example.cpuscheduler;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;



public class GanttApplication extends Application {
    private PriorityQueue<Process> processes;
    HashMap<Integer, Color> colors;

    GanttApplication(PriorityQueue<Process> processes, HashMap<Integer, Color> colors){
        this.processes = processes;
        this.colors = colors;
    }

    @Override
    public void start(Stage stage) throws IOException {
        Pane layout = new Pane();
        layout.setStyle("-fx-background-color: #EEEEEE");
        Scene scene = new Scene(layout,370,370);
        stage.setTitle("RunLive");

        //draw time 0
        int time = 0;
        Text timeText = new Text("Time = " + time + "second");
        int lastx = 20;
        int y=200;
        timeText.setX(lastx);
        timeText.setY(y);
        layout.getChildren().add(timeText);

        Text text;

        int length=30;
        int width=20;
        Color color;
        RR scheduler = new RR(3);

        boolean running;
        ArrayList<GanttProcess> ganttChart;
        int current_pid, last_pid = -1;

        while (true) {
            while (!processes.isEmpty() && time == processes.peek().getArrivalTime()) {
                scheduler.enqueue(processes.poll());
            }

            running = scheduler.fetchNextTask(time);

            // Queue and scheduler have finished serving all processes
            timeText.setText("Time = " + time + "second");

            if (!running && processes.isEmpty()) {
                text = new Text(time + "");
                text.setX(lastx);
                text.setY(y + width + 20);
                text.setFill(Color.DARKBLUE);
                layout.getChildren().add(text);
                break;
            }

            ganttChart = scheduler.getGanttChart();
            current_pid = ganttChart.get(ganttChart.size() - 1).getPid();
            color = colors.get(current_pid);
            
            // Add time if new process is running
            if(current_pid != last_pid) {
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
        }
        stage.setScene(scene);
        stage.show();
    }





    public static void main(String[] args) {
        launch(args);
    }
}
