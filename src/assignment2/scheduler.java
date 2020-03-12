package assignment2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import assignment2.process;

public class scheduler implements Runnable {

	public static Semaphore hasCPU = new Semaphore(1); // initialize semaphore
	static List<process> processList = new ArrayList<process>(); // initialize needed listArrays
	static List<process> completeList = new ArrayList<process>();

	public static void main(String args[]) {

		process p1 = new process(1, 5, hasCPU);
		process p2 = new process(2, 3, hasCPU);
		process p3 = new process(3, 1, hasCPU);
		processList.add(p1);
		processList.add(p2);
		processList.add(p3);

		//TODO: Add input from file
		//Format for input should be to read line, then pass input to constructor
		//Constuctor looks like: process(<arrival_time> , <process_time>, hasCPU)
		//hasCPU is the semaphore, see above block for example.

		scheduler myScheduler = new scheduler(); // Creating runnable object
		synchronized (myScheduler) { // Ensuring single thread execution
			while (processList.size() > 0) { // runs if elements exist in process list
				myScheduler.run();
			}
		}
		System.out.println("-----------------------------");
		System.out.println("Waiting Times:");
		for (int i = 0; i < completeList.size(); i++) { // Printing waiting times
			process processI = completeList.get(i);
			System.out.println(processI.getName() + ": " + processI.getWaitingTime());
		}
		
		//TODO: Add output to file
	}

	@Override
	public void run() {
		try {
			hasCPU.tryAcquire(); // Acquiring CPU
			process next = processList.get(0); //Initialize the next process to run
			for (int i = 0; i < processList.size(); i++) { //Loop to check for shortest started process
				process processI = processList.get(i);
				if (processI.getRemainingTime() < next.getRemainingTime() && processI.isStarted()) {
					next = processI;
				}
			}
			for (int i = 0; i < processList.size(); i++) { //Loop to check for newly arrived method
				process processI = processList.get(i);
				if (processI.getCurrentTime() >= processI.getArrivalTime() && !processI.isStarted()) {
					next = processI;
					break;
				}
			}
			if (next.getCurrentTime() < next.getArrivalTime()) { //Check to see if a process is being started before it arrives
				next.setCurrentTime(next.getCurrentTime() + 1);
				next = null;
			}
			if (next != null) {
				next.run();
				if (next.isFinished()) { //Removes a process and adds it to the completed list of processes
					completeList.add(next);
					processList.remove(next);
				}
				
			}
			
		} finally {
			hasCPU.release(); //Releases semaphore
		}
	}

}
