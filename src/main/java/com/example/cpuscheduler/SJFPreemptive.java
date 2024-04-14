package com.example.cpuscheduler;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class SJFPreemptive {
    PriorityQueue<Process> queue;
    ArrayList<GanttProcess> ganttChart;
    ArrayList<Process> completedProcesses;

    public SJFPreemptive() {
        this.queue = new PriorityQueue<>((p1, p2) -> p1.getRemainingTime() - p2.getRemainingTime());
        this.ganttChart = new ArrayList<GanttProcess>();
        this.completedProcesses = new ArrayList<Process>();
    }

    public void enqueue(Process p) {
        queue.add(p);
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

    public boolean fetchNextTask(int time) {
        Process running = queue.poll();
        if (running == null) {
            // updateGanttChart(new Process(-1, -1, -1), 1);
            return false;
        }
    
        if (running.getStartTime() == -1) {
            running.setStartTime(time);
        }

        running.decrement(1);
        updateGanttChart(running, 1);
        running.setLastProcessedTime(time + 1);

        if (running.getRemainingTime() == 0) {
            running.setCompletionTime(time + 1);
            completedProcesses.add(running);
            return true;
        }

        queue.add(running);
        return true;
    }

    public ArrayList<GanttProcess> getGanttChart() {
        return ganttChart;
    }

    public ArrayList<Process> getCompletedProcesses() {
        return completedProcesses;
    }
}