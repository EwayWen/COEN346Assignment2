package assignment3;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.Random;

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
	volatile static int currentTime = 0; // Static member tracking the system time
	static int nameIterator = 1; // Tracks the name of the process
	public static Scanner nextCommand = null; // Scanner object to track position in file
	public static memoryManager VMM = new memoryManager(); // memory manager object

	process(int arrivalT, int processT, Semaphore hasCPU) {
		// Class instantiation
		// Multiply all times by 1000
		setArrivalTime(arrivalT * 1000);
		setProcessTime(processT * 1000);
		setRemainingTime(processT * 1000);
		setWaitingTime(0);
		setStarted(false);
		quantum = ((int) (0.1 * processT)) * 1000; // We take the quantum to be an integer value.
		if (quantum < 1000) {
			quantum = 1000;
		}
		this.hasCPU = hasCPU;
		name = "Process " + nameIterator; // Automatically names the process based on order arrived.
		nameIterator += 1;

		if (nextCommand == null) { // Initializing the scanner object. Will only initialize on first creation of a
									// process.
			try {
				process.nextCommand = new Scanner(new File("commands.txt"));
			} catch (Exception e) {
				System.out.println("Error initializing command scanner.");
				e.printStackTrace();
			}
		}

	}

	public String getNextCommand() { // Returns the next line from the command.txt or EOF
		if (nextCommand.hasNextLine()) {
			return nextCommand.nextLine();
		}
		return "EOF";
	}

	@Override
	public void run() {
		try {
			hasCPU.tryAcquire(); // Acquires semaphore
			String thisCommand = ""; // command string
			int commandTime = 0; // Random time counter
			if (!isStarted) { // Checks to see if the process has started
				scheduler.addToOutputString("Clock: " + currentTime + ", " + name + ", " + "Started" + "\n");
				// System.out.println("Time " + currentTime + ", " + name + ", " + "Started");
				setStarted(true);
			}
			scheduler.addToOutputString("Clock: " + currentTime + ", " + name + ", " + "Resumed" + "\n");
			// System.out.println("Time " + currentTime + ", " + name + ", " + "Resumed");
			elapsedTime += quantum; // Incrementing elapsed time by a quantum
			remainingTime -= quantum; // Decrementing remaining time by a quantum

			// ***NEW CODE***
			// while loop with random number increment to perform memory operations.
			// loops while the random time increment is less than a quantum.
			while ((commandTime += new Random().nextInt(500)) < this.quantum) {
				thisCommand = getNextCommand();
				if (thisCommand != "EOF") {
					scheduler.addToOutputString("Clock: " + (int) (currentTime + commandTime) + ", " + name + ", ");
					VMM.parseCommand(thisCommand, currentTime + commandTime);
				}
			}

			if (elapsedTime >= processTime) { // Checking for process completion
				currentTime += processTime - (elapsedTime - quantum); // Accounting for if quantum is larger than
																		// remaining time
				remainingTime = 0; // We are in this section so we can do a hard set
				elapsedTime = processTime; // We are in this section so we can do a hard set
				setFinished(true);
				setWaitingTime(currentTime - arrivalTime - processTime);
				// Waiting time is calculated by total time elapsed, minus time spent processing
				// and minus time before arrival
			} else {
				currentTime += quantum;
			}
			scheduler.addToOutputString("Clock: " + currentTime + ", " + name + ", " + "Paused" + "\n");
			// System.out.println("Time " + currentTime + ", " + name + ", " + "Paused");
			if (isFinished) {
				scheduler.addToOutputString("Clock: " + currentTime + ", " + name + ", " + "Finished" + "\n");
				// System.out.println("Time " + currentTime + ", " + name + ", " + "Finished");
			}
		} finally {
			hasCPU.release(); // release semaphore
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