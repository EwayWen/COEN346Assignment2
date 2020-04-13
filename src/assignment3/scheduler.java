package assignment3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

import assignment3.process;

import java.io.*;

public class scheduler<myScheduler> implements Runnable {

	public static String output = "";
	public static Semaphore hasCPU = new Semaphore(2); // initialize semaphore
	static List<process> processList = new ArrayList<process>(); // initialize needed listArrays
	static List<process> completeList = new ArrayList<process>();
	static int TimeStep = 3000;

	public static void main(String args[]) {

		// TODO: Add input from file
		// Format for input should be to read line, then pass input to constructor
		// Constuctor looks like: process(<arrival_time> , <process_time>, hasCPU)
		// hasCPU is the semaphore, see above block for example.

		try {
			Scanner scProcesses = new Scanner(new File("processes.txt"));
			do {
				// Instantiate process inputs for each Process for count for
				// each input. If count is 2 or greater, it would have an out
				// of bounds error.
				int[] processInput;
				String line = scProcesses.nextLine();
				String[] splitString = line.split("\\s");
				if (splitString.length > 2) {
					throw (new Exception("There are too many inputs on the same line"));
				}

				processInput = new int[2];
				for (int n = 0; n < 2; n++) {
					// Will throw error if not an int
					processInput[n] = Integer.parseInt(splitString[n]);
				}
				process p1 = new process(processInput[0], processInput[1], hasCPU);

				// add new Process to processList
				processList.add(p1);
			} while (scProcesses.hasNextLine());

			scProcesses.close();

			scheduler myScheduler = new scheduler(); // Creating runnable object
//		synchronized (myScheduler) { // Ensuring single thread execution
//			while (processList.size() > 0) { // runs if elements exist in process list
//				myScheduler.run();
//			}
//		}
			myScheduler.run();
			// Saves string to then have it on system out and onto text file
			output += ("-----------------------------\n");
			output += ("Waiting Times:\n");
			for (int i = 0; i < completeList.size(); i++) { // Printing waiting times
				process processI = completeList.get(i);
				output += (processI.getName() + ": " + processI.getWaitingTime() + "\n");
			}
			System.out.println(output);

			// Output to text
			try (PrintWriter out = new PrintWriter("output.txt")) {
				out.println(output);
			}

		} catch (Exception e) {
			System.out.println("There was an error running the program");
			e.printStackTrace();
		}
	}

	public static void addToOutputString(String stringtoAdd) {
		output += stringtoAdd;
	}

	@Override
	public void run() {
		while (processList.size() > 0) {
			try {
				hasCPU.tryAcquire(); // Acquiring CPU
				process[] nextArr = { processList.get(0), processList.get(processList.size() - 1) }; // Initialize the
																										// next process
																										// to run
				for (int i = 0; i < processList.size(); i++) { // Loop to check for shortest started process
					process processI = processList.get(i);
					if (processI.getRemainingTime() < nextArr[0].getRemainingTime() && processI.isStarted()) {
						nextArr[1] = nextArr[0];
						nextArr[0] = processI;
					}
				}

				for (int i = processList.size() - 1; i > -1; i--) { // Loop to check for newly arrived method
					process processI = processList.get(i);
					if (processI.getCurrentTime() >= processI.getArrivalTime() && !processI.isStarted()) {
						nextArr[1] = nextArr[0];
						nextArr[0] = processI;
					}
				}

				if (nextArr[0].getCurrentTime() < nextArr[0].getArrivalTime()) { // Check to see if a process is being
																					// started before it arrives
					nextArr[0].setCurrentTime(nextArr[0].getCurrentTime() + 1000);
					// since we know this process is the priority, but arrives after the current
					// time, we simply increment the time.
					nextArr[0] = null;
				}

				if (nextArr[0] != null) {
					nextArr[0].run();
					if (nextArr[0].isFinished()) { // Removes a process and adds it to the completed list of processes
						completeList.add(nextArr[0]);
						processList.remove(nextArr[0]);
					}
				}
				if (nextArr[1] != null && nextArr[0] != nextArr[1]) {
					nextArr[1].run();
					if (nextArr[1].isFinished()) { // Removes a process and adds it to the completed list of processes
						completeList.add(nextArr[1]);
						processList.remove(nextArr[1]);
					}

				}

			} finally {
				hasCPU.release(); // Releases semaphore
			}
		}
	}

}
