package com.example.cpuscheduler;/*
 /**
 *
 * @author : Mariz
 *
 */

import java.util.*;
public class PriorityPreemptiveScheduler {
    static int time = 0;
    static PriorityQueue<Process> queue;
    static ArrayList<GanttProcess> ganttChart;
    static int totalTurnaroundTime = 0;
    static int totalWaitingTime = 0;

    public static void priorityPreemptive(List<Process> processes) {
        int totalProcesses= processes.size();
        queue = new PriorityQueue<>(Comparator.comparingInt(Process::getPriority));
        ganttChart = new ArrayList<GanttProcess>();
        for(int i=0; i<processes.size(); i++)
        {
            totalWaitingTime-=processes.get(i).getBurstTime();
        }

        while (!processes.isEmpty() || !queue.isEmpty()) {
            // move processes from list to queue
            while (!processes.isEmpty() && processes.get(0).arrivalTime <= time)
            {
                // add the process in the queue and remove it from the processes list
                queue.add(processes.remove(0));
            }
            if (!queue.isEmpty())
            {
                Process p = queue.poll();
                ganttChart.add(new GanttProcess(p, time));
                System.out.println("Executing process " + p.pid + " at time " + time);
                p.setBurstTime(p.burstTime-1);
                if (p.burstTime > 0)
                {
                    //re-add the processs to the queue again till it finishes its burst time
                    queue.add(p);
                }
                else
                {
                    p.setCompletionTime(time);
                    System.out.println("Process " + p.pid + " completed execution at time " + time);
                    totalWaitingTime+=p.calcWaitingTime();
                    totalTurnaroundTime += p.calcTurnaroundTime();
                }
                time += 1;
            }
            else
            {
                // CPU is idle
                time++;
            }
        }
        double avgTurnaroundTime = (double) totalTurnaroundTime / totalProcesses;
        double avgWaitingTime = (double) totalWaitingTime / totalProcesses;
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
        System.out.println("Average Waiting Time: " + avgWaitingTime);
    }

    public ArrayList<GanttProcess> getGanttChart() {
        return ganttChart;
    }

    public static void main(String[] args) {
        // Example processes
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 4, 2, 0));
        processes.add(new Process(2, 4, 2, 0));
        processes.add(new Process(3, 2, 3, 2));
        processes.add(new Process(4, 1, 4, 3));

        // Call scheduler
        priorityPreemptive(processes);
    }
}

