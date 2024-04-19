package com.example.cpuscheduler;

public class Process implements Cloneable {
    int pid;
    int burstTime;
    int priority;
    int arrivalTime;
    int startTime;
    int lastProcessedTime;
    int remainingTime;
    int completionTime;


    public Process(int pid, int burstTime, int priority, int arrivalTime) {
        this.pid = pid;
        this.burstTime = burstTime;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.startTime = -1;
        this.lastProcessedTime = -1;
        this.remainingTime = burstTime;
        this.completionTime = -1;
    }

    public Process(int pid, int burstTime, int arrivalTime) {
        this(pid, burstTime, 5, arrivalTime);
    }

    public int getPid() {
        return pid;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getLastProcessedTime() {
        return lastProcessedTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setLastProcessedTime(int lastProcessedTime) {
        this.lastProcessedTime = lastProcessedTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    public int calcTurnaroundTime() {
        return completionTime - arrivalTime;
    }

    public int calcWaitingTime() {
        return completionTime - arrivalTime - burstTime;
    }

    public int calcResponseTime() {
        return startTime - arrivalTime;
    }
    
    public void decrement(int quantum) {
        if (remainingTime < quantum) {
            remainingTime = 0;
        } else {
            remainingTime -= quantum;
        }
    }

    @Override
    protected Object clone() {
        Process ProcessClone = new Process(this.pid, this.burstTime, this.priority, this.arrivalTime);
        return ProcessClone;
    }
}
