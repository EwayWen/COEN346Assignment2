package assignment2;

import java.util.concurrent.*;

//This class is the process class that will simulate processes running on a single thread

public class process implements Runnable {
	Semaphore hasCPU = null; // Used to ensure exclusion/simulate single thread
	private String name; // Process name
	private int arrivalTime; // Time of arrival
	private int processTime; // Total needed runtime
	private int quantum; // One interval of process time
	private int waitingTime; // Time spent in queue
	private int elapsedTime; // Elapsed runtime
	private int remainingTime; // Remaining time to completion
	private boolean isStarted; // Flag if process has been started
	private boolean isFinished; // Flag if process has been completed
	volatile static int currentTime = 1; // Static member tracking the system time
	static int nameIterator = 1; // Tracks the name of the process

	process(int arrivalT, int processT, Semaphore hasCPU) {
		// Class instantiation
		setArrivalTime(arrivalT);
		setProcessTime(processT);
		setRemainingTime(processT);
		setWaitingTime(0);
		setStarted(false);
		quantum = (int) (0.1 * processT); // We take the quantum to be an integer value.
		if (quantum < 1) {
			quantum = 1;
		}
		this.hasCPU = hasCPU;
		name = "Process " + nameIterator; // Automatically names the process based on order arrived.
		nameIterator += 1;
	}

	@Override
	public void run() {
		try {
			hasCPU.tryAcquire(); // Acquires semaphore
			if (!isStarted) { // Checks to see if the process has started
				scheduler.addToOutputString("Time " + currentTime + ", " + name + ", " + "Started" + "\n");
				//System.out.println("Time " + currentTime + ", " + name + ", " + "Started");
				setStarted(true);
			}
			scheduler.addToOutputString("Time " + currentTime + ", " + name + ", " + "Resumed" + "\n");
			//System.out.println("Time " + currentTime + ", " + name + ", " + "Resumed");
			elapsedTime += quantum; //Incrementing elapsed time by a quantum
			remainingTime -= quantum; //Decrementing remaining time by a quantum
			if (elapsedTime >= processTime) { //Checking for process completion
				currentTime += processTime - (elapsedTime - quantum); //Accounting for if quantum is larger than remaining time
				remainingTime = 0; //We are in this section so we can do a hard set
				elapsedTime = processTime; //We are in this section so we can do a hard set
				setFinished(true);
				setWaitingTime(currentTime - arrivalTime - processTime); 
				//Waiting time is calculated by total time elapsed, minus time spent processing and minus time before arrival
			} else {
				currentTime += quantum;
			}
			scheduler.addToOutputString("Time " + currentTime + ", " + name + ", " + "Paused" + "\n");
			//System.out.println("Time " + currentTime + ", " + name + ", " + "Paused");
			if (isFinished) {
				scheduler.addToOutputString("Time " + currentTime + ", " + name + ", " + "Finished" + "\n");
				//System.out.println("Time " + currentTime + ", " + name + ", " + "Finished");
			}
		} finally {
			hasCPU.release(); //release semaphore
		}

	}

	// getters and setters
	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getProcessTime() {
		return processTime;
	}

	public void setProcessTime(int processTime) {
		this.processTime = processTime;
	}

	public int getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}

	public boolean isStarted() {
		return isStarted;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public int getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(int elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public int getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(int i) {
		currentTime = i;
	}

	public int getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(int remainingTime) {
		this.remainingTime = remainingTime;
	}

	public String getName() {
		return name;
	}

}