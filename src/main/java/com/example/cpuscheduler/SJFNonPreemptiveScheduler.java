package com.example.cpuscheduler;

import java.util.ArrayList;
import java.util.Scanner;

import static com.example.cpuscheduler.Priority_NonPreemptive.avgTurnaroundTime;
import static com.example.cpuscheduler.Priority_NonPreemptive.avgWaitingTime;


public class SJFNonPreemptiveScheduler {
    public ArrayList<Process> SJFNonPreemptive (ArrayList<Process> processes) {

        ArrayList<Process> result = new ArrayList<>();
        //set the priority of processes to be = burst time, where smallest burst = higher priority
        for (Process process : processes) {
            process.setPriority( process.getBurstTime());

        }
        //call fn of priority non-preemptive class and pass if the list of processes
        Priority_NonPreemptive pnp = new Priority_NonPreemptive();
        result = pnp.priorityScheduler(processes);
        return result;

    }

    public static void main(String[] args) {

        ArrayList<Process> processes = new ArrayList<>();

        ArrayList<Process> res = new ArrayList<>();

        /* example 1
        processes.add(new Process(1, 10, 0));
        processes.add(new Process(2, 1, 0));
        processes.add(new Process(3, 2, 0));
        processes.add(new Process(4, 1, 0));
        processes.add(new Process(5, 5, 0));
        ex2
        processes.add(new Process(1, 8, 0));
        processes.add(new Process(2, 4, 1));
        processes.add(new Process(3, 9, 2));
        processes.add(new Process(4, 5, 2));
        Average Turnaround Time: 14.25
        Average waiting Time: 7.75

        */

        System.out.println("Please enter the number of processes:");
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        System.out.println("Please enter process info (pid, burstTime, arrivalTime):");
        for (int i = 0; i < n; i++) {
            int pid = scanner.nextInt();
            int burstTime = scanner.nextInt();
            //int priority = scanner.nextInt();
            int arrivalTime = scanner.nextInt();
            processes.add(new Process(pid, burstTime, arrivalTime));
        }

        SJFNonPreemptiveScheduler SJFnp = new SJFNonPreemptiveScheduler();
        res = SJFnp.SJFNonPreemptive(processes);

        System.out.println("PID\tBurst\tArrival\tCompletion");
        for (Process process : res) {
            System.out.println(process.getPid() + "\t" + process.getBurstTime()
                    + "\t\t" + process.getArrivalTime() + "\t\t" + process.getCompletionTime());
        }
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
        System.out.println("Average waiting Time: " + avgWaitingTime);
    }

}


