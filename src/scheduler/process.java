package scheduler;



public class process extends Thread
{
	private boolean started;
	private int arrivalTime; //ready time
	private int runTime; //execution time
	private int remainingTime; //burst time
	private int waitingTime; //time spent in queue
	private int quantum; //10% of remaining time
	
	process(int arrival, int run)
	{
		setStarted(false);
		setArrivalTime(arrival);
		setRunTime(run);
		setRemainingTime(run);
		setWaitingTime(0);
		setQuantum((int)java.lang.Math.round((0.1 * run)));
		if (getQuantum() ==0)
		{
			setQuantum(1);
		}
	}
	
	public int burst()
	{
		
		setRemainingTime(remainingTime - quantum);
		int time = quantum;
		if (getRemainingTime() <1)
		{
			time += getRemainingTime();
			setRemainingTime(0);
		}
		return time;
	}
	
	public void run(int elapsedTime)
	{
		synchronized(this)
		{
			setStarted(true);
			System.out.println("Time " + elapsedTime + ", Process " + getArrivalTime() + " started.");
			elapsedTime += this.burst();
			this.setWaitingTime(elapsedTime-(getRunTime()-getRemainingTime())-getArrivalTime());
			System.out.println("Time " + elapsedTime + ", Process " + getArrivalTime() + " paused.");
		}
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getRunTime() {
		return runTime;
	}

	public void setRunTime(int runTime) {
		this.runTime = runTime;
	}

	public int getQuantum() {
		return quantum;
	}

	public void setQuantum(int quantum) {
		this.quantum = quantum;
	}

	public int getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(int remainingTime) {
		this.remainingTime = remainingTime;
	}

	public int getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}
}