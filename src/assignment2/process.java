package assignment2;

import java.util.concurrent.*;

public class process implements Runnable {
	Semaphore hasCPU = null;
	private String name;
	private int arrivalTime;
	private int processTime;
	private int quantum;
	private int waitingTime;
	private int elapsedTime;
	private int remainingTime;
	private boolean isStarted;
	private boolean isFinished;
	volatile static int currentTime = 1;

	process(int arrivalT, int processT, Semaphore hasCPU) {

		setArrivalTime(arrivalT);
		setProcessTime(processT);
		setRemainingTime(processT);
		quantum = (int) (0.1 * processT);
		setWaitingTime(0);
		setStarted(false);
		if (quantum < 1) {
			quantum = 1;
		}
		this.hasCPU = hasCPU;
		name = "Process " + getArrivalTime();
	}

	@Override
	public void run() {
		try {
			hasCPU.acquire();
			if (!isStarted) {
				System.out.println("Time " + currentTime + ", " + name + ", " + "Started");
				setStarted(true);
			}
			System.out.println("Time " + currentTime + ", " + name + ", " + "Resumed");
			setWaitingTime(currentTime - arrivalTime - elapsedTime);
			elapsedTime += quantum;
			remainingTime -= quantum;
			if (elapsedTime >= processTime) {
				currentTime += processTime - (elapsedTime - quantum);
				remainingTime = 0;
				elapsedTime = processTime;
				setFinished(true);
			} else {
				currentTime += quantum;
			}
			System.out.println("Time " + currentTime + ", " + name + ", " + "Paused");
			if (isFinished) {
				System.out.println("Time " + currentTime + ", " + name + ", " + "Finished");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			hasCPU.release();
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

	public int getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(int remainingTime) {
		this.remainingTime = remainingTime;
	}

}