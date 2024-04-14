package com.example.cpuscheduler;

public class Process implements Comparable<Process> {
    int pid;
    int burstTime;
    int priority;
    int arrivalTime;
    int completionTime;
    int turnaroundTime;
    int waitingTime;
    int startTime;


    public Process(int pid, int burstTime, int priority, int arrivalTime) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.completionTime = 0;
        this.turnaroundTime = 0;
        this.waitingTime = 0;
        this.startTime = -1;
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

    public int getCompletionTime() {
        return completionTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getStartTime() {
        return startTime;
    }

    
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
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

    /*public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }
*/


    public void calculateTurnaroundTime(){
        this.turnaroundTime = this.completionTime - this.arrivalTime;
    }

    public void calculateWaitingTime (){
        this.waitingTime = this.turnaroundTime - this.burstTime;
    }



    @Override
    public int compareTo(Process other) {
        return Integer.compare(this.priority, other.priority);
    }

}
