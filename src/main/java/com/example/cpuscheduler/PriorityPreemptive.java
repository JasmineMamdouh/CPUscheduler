package com.example.cpuscheduler;

import java.util.Comparator;
import java.util.PriorityQueue;

public class PriorityPreemptive extends Schedulers{

    public  PriorityPreemptive(){
        super(new PriorityQueue<>(Comparator.comparingInt(Process::getPriority)));
    }
    public boolean fetchNextTask(int time) {
        Process running = queue.poll();
        if (running == null) {
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
}

