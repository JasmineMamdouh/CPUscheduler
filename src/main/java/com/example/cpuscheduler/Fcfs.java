import java.util.*;

public class Fcfs {

    private static void firstComeFirstServe(Process[] arr) {

        // represents the number of processes
        int n = arr.length;
        int time = 0;

        // Priority queue to sort processes based on arrival time (can be modified for priority-based scheduling)
        PriorityQueue<Process> pq = new PriorityQueue<>((a, b) -> a.getArrivalTime() - b.getArrivalTime());

        // ArrayLists to track turnaround time, waiting time, and completion time for each process
        ArrayList<Integer> turnAroundTime = new ArrayList<>();
        ArrayList<Integer> waitingTime = new ArrayList<>();
        ArrayList<Integer> completionTime = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            pq.add(arr[i]);
        }

        for (int i = 0; i < n; i++) {
            completionTime.add(0);
            waitingTime.add(0);
            turnAroundTime.add(0);
        }

        int firstProcessArrivalTime = pq.peek().getArrivalTime();
        time = time + firstProcessArrivalTime;

        while (!pq.isEmpty()) {
            Process p = pq.poll();

            time += p.getBurstTime();

            // Completion time for the process
            completionTime.set(p.getPid(), time);

            // Turnaround time = Completion time - Arrival time
            turnAroundTime.set(p.getPid(), completionTime.get(p.getPid()) - p.getArrivalTime());

            // Waiting time = Turnaround time - Burst time
            waitingTime.set(p.getPid(), turnAroundTime.get(p.getPid()) - p.getBurstTime());
        }

        // Printing results
        System.out.println("Process ID\tArrival Time\tBurst Time\tCompletion Time\t\tTurnaround Time\t\tWaiting Time");
        for (int i = 0; i < n; i++) {
            System.out.println(arr[i].getPid() + "\t\t\t\t" + arr[i].getArrivalTime() + "\t\t\t\t" + arr[i].getBurstTime() + "\t\t\t\t" + completionTime.get(i) + "\t\t\t\t" + turnAroundTime.get(i) + "\t\t\t\t\t" + waitingTime.get(i));
        }
    }

    public static void main(String[] args) {

        Process[] arr = new Process[4];
        arr[0] = new Process(0, 5, 1, 1); // Process ID, Burst Time, Priority (example), Arrival Time
        arr[1] = new Process(1, 4, 2, 1);
        arr[2] = new Process(2, 2, 3, 2);
        arr[3] = new Process(3, 1, 4, 4);

        firstComeFirstServe(arr);
    }
}
