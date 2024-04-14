package com.example.cpuscheduler;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.*;
public class PriorityPreemptiveScheduler{
    public static void priorityPreemptive(List<Process> processes)
    {
        int time=0;
        PriorityQueue<Process> queue = new PriorityQueue<>();
        
        while(!processes.isEmpty() || !queue.isEmpty())
        {
            // move processes from list to queue
            while(!processes.isEmpty() && processes.get(0).arrivalTime <=time)
            {
             // add the process in the queue and remove it from the processes list
                queue.add(processes.remove(0));
            }
            if(!queue.isEmpty())
            {
                Process p=queue.poll();
                System.out.println("Executing process " + p.pid + " at time " + time);
                time += 1;
                p.burstTime-=1;
                if(p.burstTime>0)
                {
                    //re-add the processs to the queue again till it finishes its burst time
                    queue.add(p);
                }
                else
                {
                    System.out.println("Process " + p.pid + " completed execution at time " + time);
                }  
            }
            else
            {
                // CPU is idle
                time++;
            }           
        }      
    }
     public static void main(String[] args) {
        // Example processes
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 0, 4, 2));
        processes.add(new Process(2, 0, 4, 2));
        processes.add(new Process(3, 2, 2, 3));
        processes.add(new Process(4, 3, 1, 4));

        // Call scheduler
        priorityPreemptive(processes);
    }
}

//Process Class
/*
class Process implements Comparable<Process>{
    int id;
    int arrivalTime;
    int remainingTime;
    int burstTime;
    int priority;
    
    public Process(int id,int arrivalTime,int burstTime,int priority)
    {
        this.id=id;
        this.arrivalTime=arrivalTime;
        this.burstTime=burstTime;
        this.remainingTime=burstTime;
        this.priority=priority;
    }
    //if the arrival time is not given
    public Process(int id,int burstTime,int priority)
    {
        this.id=id;
        this.arrivalTime=0;
        this.burstTime=burstTime;
        this.remainingTime=burstTime;
        this.priority=priority;
    }
    // to gaurantee that processes are ordered in periority queue according to the periority attribute
    @Override
    public int compareTo(Process other) {
        return Integer.compare(this.priority, other.priority);
    }
    
}*/