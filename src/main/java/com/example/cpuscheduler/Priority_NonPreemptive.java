
package com.example.cpuscheduler;

/**
 *
 * @author : Carol Maged
 * 
 */

import java.util.ArrayList;
import java.util.Scanner;


public class Priority_NonPreemptive {

    
    private float avgTurnaroundTime;
    private float avgWaitingTime;
    int totalTurnaroundTime = 0;
    int totalWaitingTime = 0;
    int currentTime = 0;
    int completed = 0;
    int[] isCompleted;

    public void priorityScheduler(ArrayList<Process> processes) {
        
        ArrayList<Process> result = new ArrayList<>();
        int totalProcess = processes.size();
        isCompleted = new int[totalProcess];

        //check if there is process that isnot served
        while (completed < totalProcess) {
            int minPriority = Integer.MAX_VALUE;
            int selectedProcessIdx = -1;

            //compare process according its priority and arrival time 
            for (int i = 0; i < totalProcess; i++) {
                if (processes.get(i).getArrivalTime() <= currentTime && isCompleted[i] == 0) {
                    if (processes.get(i).getPriority() < minPriority) {
                        minPriority = processes.get(i).getPriority();
                        selectedProcessIdx = i;
                    } 
                    //incase 2 process have the same priority 
                    else if (processes.get(i).getPriority() == minPriority
                            && processes.get(i).getArrivalTime() < processes.get(selectedProcessIdx).getArrivalTime()) {
                        minPriority = processes.get(i).getPriority();
                        selectedProcessIdx = i;
                    }
                }
            }

            if (selectedProcessIdx != -1) {
                Process selectedProcess = processes.get(selectedProcessIdx);
                selectedProcess.setStartTime(currentTime);
                selectedProcess.setCompletionTime(currentTime + selectedProcess.getBurstTime());
                //selectedProcess.setTurnaroundTime(selectedProcess.getCompletionTime() - selectedProcess.getArrivalTime());
                //selectedProcess.setWaitingTime(selectedProcess.getTurnaroundTime() - selectedProcess.getBurstTime());
                selectedProcess.calculateTurnaroundTime();
                selectedProcess.calculateWaitingTime();
                totalTurnaroundTime += selectedProcess.getTurnaroundTime();
                totalWaitingTime += selectedProcess.getWaitingTime();
                isCompleted[selectedProcessIdx] = 1;
                completed++;
                currentTime = selectedProcess.getCompletionTime();

                //result is array for the sorted process to be served
                result.add(new Process(selectedProcess.getPid(), selectedProcess.getBurstTime(), selectedProcess.getPriority(),
                        selectedProcess.getArrivalTime()));
                result.get(result.size() - 1).setStartTime(selectedProcess.getStartTime());
                result.get(result.size() - 1).setCompletionTime(selectedProcess.getCompletionTime());
            }
            else{
                currentTime++;
            }
        }
        
        avgTurnaroundTime = (float) totalTurnaroundTime / totalProcess;
        avgWaitingTime = (float) totalWaitingTime / totalProcess;
        
        System.out.println("PID\tBurst\tPriority\tArrival\tCompletion");
        for (Process process : result) {
            System.out.println(process.getPid() + "\t" + process.getBurstTime() + "\t" + process.getPriority()
                    + "\t\t" + process.getArrivalTime() + "\t" + process.getCompletionTime());
        }
         System.out.println("Average Turnaround Time"+avgTurnaroundTime);
         System.out.println("Average waiting Time"+avgWaitingTime);
    }

    public static void main(String[] args) {
        
        ArrayList<Process> processes = new ArrayList<>();
        //lecture example
        /*processes.add(new Process(1, 10, 3, 0));
        processes.add(new Process(2, 1, 1, 0));
        processes.add(new Process(3, 2, 4, 0));
        processes.add(new Process(4, 1, 5, 0));
        processes.add(new Process(5, 5, 2, 0));*/
        
        System.out.println("Please enter the number of processes:");
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        
        System.out.println("Please enter process info (pid, burstTime, priority, arrivalTime):");
        for (int i = 0; i < n; i++) {
            int pid = scanner.nextInt();
            int burstTime = scanner.nextInt();
            int priority = scanner.nextInt();
            int arrivalTime = scanner.nextInt();
            processes.add(new Process(pid, burstTime, priority, arrivalTime));
        }
        
        Priority_NonPreemptive pnp = new Priority_NonPreemptive();
        pnp.priorityScheduler(processes);
    }
}


