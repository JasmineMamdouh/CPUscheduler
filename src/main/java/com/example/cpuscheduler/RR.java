package com.example.cpuscheduler;

import java.util.LinkedList;

public class RR extends Schedulers {
    private int quantum; //RR timeslice
    private int itr=1;// iterator to keep track if the timeslice finished or not.
    Process running;

    public RR(int quantum){
        // super(new PriorityQueue<>(Comparator.comparing(Process::getLastProcessedTime).reversed()));
        super(new LinkedList<>());
        this.quantum=quantum; //time slice for round robin
    }

    public boolean fetchNextTask(int time) {
        if (itr == quantum) { // if timeslice finished
            queue.add(running); // remove the process from head of queue and add it at the end
            itr = 0; // reset itr
            running = null;
        }
        if (running == null) { // if last task is completed, get next task
            running = queue.poll();
        }
        if (running == null && queue.peek() == null) {
            updateGanttChart(-1, 1);
            return false;// return false if queue is empty
        }
        if (running.getStartTime() == -1) {
            running.setStartTime(time); //set starting time of process once it starts
        }
        running.decrement(1); //run the process for 1 unit time
        updateGanttChart(running, 1); //update gantt chart
        running.setLastProcessedTime(time+1);

        if (running.getRemainingTime() == 0) { //in case process is done
            running.setCompletionTime(time+1); //set completion time to (time+1)
            completedProcesses.add(running); //add process to completedProcess Queue
            running=null;
            itr=0;//reset itr
            return true;
        }

        itr++; //increment itr
        return true;
    }




}
