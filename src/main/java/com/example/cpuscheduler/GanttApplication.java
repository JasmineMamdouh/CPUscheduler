package com.example.cpuscheduler;

import javafx.application.Application;
import javafx.application.Platform;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;



public class GanttApplication extends Application implements Runnable {
    private PriorityQueue<Process> processes;
    HashMap<Integer, Color> colors;
    ArrayList<GanttProcess> ganttChart;
    ScheduledFuture<?> t;

    Pane layout;
    Text timeText;
    int time = 0;
    int lastx = 20;
    int y = 200;
    RR scheduler = new RR(3);
    Color color;
    int current_pid, last_pid = -1;

    GanttApplication(PriorityQueue<Process> processes, HashMap<Integer, Color> colors){
        this.processes = processes;
        this.colors = colors;
    }

    @Override
    public void run() {
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    while (!processes.isEmpty() && time == processes.peek().getArrivalTime()) {
                            scheduler.enqueue(processes.poll());
                        }

                        boolean running = scheduler.fetchNextTask(time);

                        // Queue and scheduler have finished serving all processes
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
                });
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
    }


    @Override
    public void start(Stage stage) throws IOException {
        layout = new Pane();
        layout.setStyle("-fx-background-color: #EEEEEE");
        Scene scene = new Scene(layout,370,370);
        stage.setTitle("RunLive");

        //draw time 0
        timeText = new Text("Time = " + time + "second");
        timeText.setX(lastx);
        timeText.setY(y);
        layout.getChildren().add(timeText);

        stage.setScene(scene);
        stage.show();
        
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        t = executor.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);

        // Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> run()));
        // timeline.setCycleCount(Animation.INDEFINITE);
        // timeline.play();

        
    }





    public static void main(String[] args) {
        launch(args);
    }
}
