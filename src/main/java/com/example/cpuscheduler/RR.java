package com.example.cpuscheduler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class RR {
    Queue<Process> queue;//queue for serving processess in RR
    ArrayList<GanttProcess> ganttChart;
    ArrayList<Process> CompletedProcess; //list for processes that finished execution
    int quantum; //RR timeslice
    int itr=1;// iterator to keep track if the timeslice finished or not.

    public RR(int quantum){
        this.queue=new LinkedList();
        this.ganttChart = new ArrayList<GanttProcess>();
        this.CompletedProcess= new ArrayList<Process>();
        this.quantum=quantum; //time slice for round robin
    }
    public void updateGanttChart(Process p, int quantum) {
        if (!ganttChart.isEmpty()) {
            GanttProcess lastProcess = ganttChart.get(ganttChart.size() - 1);
            if (lastProcess.getPid() == p.getPid()) {//checking if we are at the sameprocess
                lastProcess.increment(quantum);
                return;
            }
        }

        ganttChart.add(new GanttProcess(p, quantum));
    }
    public void enqueue(Process p) {
        queue.add(p);
    }
    public boolean fetchNextTask(int time) {
        Process running = queue.peek(); //acessing head of Queue
        if (running == null) {
            return false; //return false if queue is empty
        }
        if (running.getStartTime() == -1) {
            running.setStartTime(time); //set starting time of process once it starts
        }
        running.decrement(1); //run the process for 1 unit time
        updateGanttChart(running, 1); //update gantt chart

        if (running.getRemainingTime() == 0) { //in case process is done
            running.setCompletionTime(time+1); //set completion time to (time+1)
           // running.setWaitingTime((time+1)- running.getArrivalTime()- running.getBurstTime());
            CompletedProcess.add(queue.poll()); //add process to completedProcess Queue
            itr=1;//reset itr
            return true;
        }

        if(itr== quantum){ //if timeslice finished
            queue.add(queue.poll()); //remove the process from head of queue and add it at the end
            itr=1; //reset itr
            return true;
        }

        else itr++; //increment itr
        return true;
    }
    public ArrayList<GanttProcess> getGanttChart() {
        return ganttChart;
    }

    public ArrayList<Process> getCompletedProcesses() {
        return CompletedProcess;
    }




}
