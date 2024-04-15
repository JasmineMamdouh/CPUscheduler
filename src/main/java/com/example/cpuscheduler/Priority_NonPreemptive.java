
package com.example.cpuscheduler;

/**
 *
 * @author : Carol Maged
 * 
 */

import java.util.PriorityQueue;

public class Priority_NonPreemptive extends Schedulers {
    
    private Process running;
    
    public Priority_NonPreemptive() {
        super(new PriorityQueue<Process>((p1, p2) -> p1.getPriority() - p2.getPriority()));
        this.running=null;
    }
    
    public boolean fetchNextTask(int time) {
        
        
        if (running == null) {
            running =queue.poll();
        }
    
        
        if (running == null && queue.peek()==null) {
            return false;
        }
    
        // first time access process
        if (running.getStartTime() == -1) {
            running.setStartTime(time);
        }

        running.decrement(1);
        updateGanttChart(running, 1);
        running.setLastProcessedTime(time + 1);

        if (running.getRemainingTime() == 0) {
            running.setCompletionTime(time + 1);
            completedProcesses.add(running);
            running =null;
            return true;
        }

        //queue.add(running);
        return true;
    }
}
