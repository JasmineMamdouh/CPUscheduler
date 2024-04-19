package com.example.cpuscheduler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class RR extends Schedulers {
    int quantum; //RR timeslice
    int itr=1;// iterator to keep track if the timeslice finished or not.

    public RR(int quantum){
        super(new LinkedList());
        this.quantum=quantum; //time slice for round robin
    }

    public boolean fetchNextTask(int time) {
        Process running = queue.peek(); //acessing head of Queue
        if (running == null) {
            updateGanttChart(-1, 1);
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
            completedProcesses.add(queue.poll()); //add process to completedProcess Queue
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




}
