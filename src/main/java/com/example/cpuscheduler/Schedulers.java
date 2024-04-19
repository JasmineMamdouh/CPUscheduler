package com.example.cpuscheduler;

import java.util.ArrayList;
import java.util.Queue;

public abstract class Schedulers {
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

    public abstract boolean fetchNextTask(int time);

    public ArrayList<Process> getCompletedProcesses() {
        return completedProcesses;
    }

    public ArrayList<GanttProcess> getGanttChart() {
        return ganttChart;
    }

    public float calcAvgTurnaroundTime() {
        int sum = 0;
        for (Process process : completedProcesses) {
            sum += process.calcTurnaroundTime();
        }

        return (float) sum / completedProcesses.size();
    }

    public float calcAvgWaitingTime() {
        int sum = 0;
        for (Process process : completedProcesses) {
            sum += process.calcWaitingTime();
        }

        return (float) sum / completedProcesses.size();
    }

    public float calcAvgResponseTime() {
        int sum = 0;
        for (Process process : completedProcesses) {
            sum += process.calcResponseTime();
        }

        return (float) sum / completedProcesses.size();
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

    public void updateGanttChart(int pid, int quantum) {
        if (!ganttChart.isEmpty()) {
            GanttProcess lastProcess = ganttChart.get(ganttChart.size() - 1);
            if (lastProcess.getPid() == pid) {
                lastProcess.increment(quantum);
                return;
            }
        }

        ganttChart.add(new GanttProcess(pid, quantum));
    }
}
