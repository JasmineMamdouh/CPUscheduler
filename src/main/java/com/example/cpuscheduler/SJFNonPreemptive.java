package com.example.cpuscheduler;

import java.util.PriorityQueue;

public class SJFNonPreemptive extends Schedulers {

    private Process running;

    SJFNonPreemptive(){
        super(new PriorityQueue<Process>((p1, p2) -> p1.getBurstTime() - p2.getBurstTime()));
        this.running = null;
    }

    public boolean fetchNextTask(int time) {

        if (running == null) {
            running = queue.poll();
        }

        if (running == null && queue.peek() == null){
            updateGanttChart(-1, 1);
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
            running = null;
            return true;
        }

        //queue.add(running);
        return true;
    }
}
