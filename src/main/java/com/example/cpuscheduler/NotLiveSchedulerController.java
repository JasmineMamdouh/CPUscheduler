package com.example.cpuscheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
import java.util.ArrayList;
import java.util.Map;

public class NotLiveSchedulerController {
    @FXML
    private TableView<Process> processTable;
    @FXML
    private TableColumn<Process, Integer> pidColumn;
    @FXML
    private TableColumn<Process, Integer> startTimeColumn;
    @FXML
    private TableColumn<Process, Integer> endTimeColumn;
    @FXML
    private TableColumn<Process, Integer> arrivalTimeColumn;
    @FXML
    private Pane chartPane;
    @FXML
    private VBox prioritySection;
    @FXML
    private Label avgTurnaroundTime;
    @FXML
    private Label avgWaitTime;


    private ArrayList<Process> processes;
    private ObservableList<Process> processesList = FXCollections.observableArrayList();
    private Schedulers scheduler;
    private Map<Integer, Color> processColorMap;
    
    public void initData(ArrayList<Process> processes, Map<Integer, Color> processColorMap, int quantum) {
        this.processes = processes;
        this.processColorMap = processColorMap;

        switch (HelloController.processType) {
            case "FCFS" -> scheduler = new FirstComeFirstServe();
            case "Round Robin" -> scheduler = new RR(quantum);
            case "SJF Non-Preemptive" -> scheduler = new SJFNonPreemptive();
            case "SJF Preemptive" -> scheduler = new SJFPreemptive();
            case "Priority Non-Preemptive" -> scheduler = new Priority_NonPreemptive();
            case "Priority Preemptive" -> scheduler = new PriorityPreemptive();
            default -> scheduler = new FirstComeFirstServe();
        }

        runScheduler();
    }

    private void startScheduler(Schedulers scheduler) {
        int time = 0;
        while (true) {
            while (!processes.isEmpty() && time == processes.get(0).getArrivalTime()) {
                scheduler.enqueue(processes.removeFirst());
            }

            if (!scheduler.fetchNextTask(time) && processes.isEmpty())
                break;
            time++;
        }
    }

    @FXML
    public void initialize() {
        pidColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("pid"));
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("arrivalTime"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("completionTime"));

        processTable.setItems(processesList);
    }

    public void runScheduler() {
        this.startScheduler(scheduler);
        ArrayList<GanttProcess> ganttProcesses = scheduler.getGanttChart();
        ArrayList<Process> completProcesses = scheduler.getCompletedProcesses();

        processesList.addAll(completProcesses);

        int xAxis = 14;
        int yAxis = (int)chartPane.getLayoutBounds().getCenterY();
        int width = 50;
        int length = 30;
        int time = 0;

        //Draw time 0
        Text timeText = new Text(time + "s");
        timeText.setFont(new Font(15));
        timeText.setX(xAxis - (timeText.getLayoutBounds().getWidth()) / 2); // Adjust padding as needed
        timeText.setY(yAxis + length + timeText.getLayoutBounds().getHeight());
        chartPane.getChildren().addAll(timeText);

        //Gantt Chart
        for(int i = 0; i < ganttProcesses.size() - 1; i++) {
            GanttProcess ganttProcess = ganttProcesses.get(i);
            Color color = processColorMap.get(ganttProcess.getPid());

            Rectangle rectangle = new Rectangle(ganttProcess.getRunningTime() * width, length, color);
            rectangle.setLayoutX(xAxis);
            rectangle.setLayoutY(yAxis);
            xAxis += ganttProcess.getRunningTime() * width;

            time += ganttProcess.getRunningTime();
            timeText = new Text(time + "s");
            timeText.setFont(new Font(15));
            timeText.setX(xAxis - (timeText.getLayoutBounds().getWidth())/ 2); // Adjust padding as needed
            timeText.setY(yAxis + length + timeText.getLayoutBounds().getHeight());

            int pid = ganttProcess.getPid();
            Text processName = new Text();
            if (pid == -1){
                processName.setText(" "); // Adjust format if needed
            } else {
                processName.setText("P" + ganttProcess.getPid()); // Adjust format if needed
            }

            processName.setFont(new Font(15));  // Adjust font size and style as desired
            processName.setLayoutX(rectangle.getLayoutX() + processName.getLayoutBounds().getWidth()); // Adjust padding as needed
            processName.setLayoutY(rectangle.getLayoutY() + processName.getLayoutBounds().getHeight());
            chartPane.getChildren().addAll(rectangle, processName, timeText);
        }

        //adding texts for the average waiting time and average turn around time
        avgTurnaroundTime.setText("Avg. Turnaround Time:  " + String.format("%.2f", scheduler.calcAvgTurnaroundTime()) + "s");
        avgWaitTime.setText("Avg. Waiting Time:  " + String.format("%.2f", scheduler.calcAvgWaitingTime()) + "s");
    }
}
