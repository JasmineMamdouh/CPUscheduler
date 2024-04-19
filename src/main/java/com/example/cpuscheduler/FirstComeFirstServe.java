package com.example.cpuscheduler;

import java.util.LinkedList;

public class FirstComeFirstServe extends Schedulers{
    public FirstComeFirstServe() {
        super(new LinkedList<Process>());
    }

    public boolean fetchNextTask(int time) {
        Process running = queue.peek();
        if (running == null) {
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
            completedProcesses.add(queue.poll());
            return true;
        }

       // queue.add(running);
        return true;
    }
}