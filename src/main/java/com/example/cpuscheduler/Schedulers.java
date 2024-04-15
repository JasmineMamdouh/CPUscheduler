package com.example.cpuscheduler;

import java.util.ArrayList;
import java.util.Queue;

public class Schedulers {
    Queue<Process> queue;
    ArrayList<Process> completedProcesses;
    ArrayList<GanttProcess> ganttChart;

    public Schedulers(Queue<Process> queue) {
        this.queue = queue;
        this.completedProcesses = new ArrayList<Process>();
        this.ganttChart = new ArrayList<GanttProcess>();
    }

    public void enqueue(Process p) {
        queue.add(p);
    }

    public ArrayList<Process> getCompletedProcesses() {
        return completedProcesses;
    }

    public ArrayList<GanttProcess> getGanttChart() {
        return ganttChart;
    }

    public void updateGanttChart(Process p, int quantum) {
        if (!ganttChart.isEmpty()) {
            GanttProcess lastProcess = ganttChart.get(ganttChart.size() - 1);
            if (lastProcess.getPid() == p.getPid()) {
                lastProcess.increment(quantum);
                return;
            }
        }

        ganttChart.add(new GanttProcess(p, quantum));
    }
}
