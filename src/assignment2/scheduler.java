package assignment2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import assignment2.process;

public class scheduler implements Runnable {

	public static Semaphore hasCPU = new Semaphore(1);
	static List<process> processList = new ArrayList<process>();
	static List<process> completeList = new ArrayList<process>();

	public static void main(String args[]) {

		process p1 = new process(1, 5, hasCPU);
		process p2 = new process(2, 3, hasCPU);
		process p3 = new process(3, 1, hasCPU);
		processList.add(p1);
		processList.add(p2);
		processList.add(p3);
		
		System.out.println(processList.size());
		
		scheduler myScheduler = new scheduler();
		while(processList != null) {
			myScheduler.run();
		}
	}

	public boolean isDone(process process) {
		return process.isFinished();
	}

	@Override
	public void run() {
		process next = null;
		try {
			hasCPU.acquire();
			if (processList!=null) {
				next = processList.get(0);
				for (int i = 0; i < processList.size(); i++) {
					//System.out.println("pass");
					process processI = processList.get(i);
					if (processI.getRemainingTime() < next.getRemainingTime()) {
						next = processI;
					}
					//System.out.println(processI.getRemainingTime() +"<"+ next.getRemainingTime());
				}
				for (int i = 0; i < processList.size(); i++) {
					process processI = processList.get(i);
					if (processI.getCurrentTime() >= processI.getArrivalTime() && !processI.isStarted()) {
						next = processI;
						break;
					}
				}
			}
			else {
				for (int i = 0; i < completeList.size(); i++)
				System.out.print("");
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			hasCPU.release();
			if (next != null) {
				next.run();
				if (next.isFinished()) {
					completeList.add(next);
					processList.remove(next);
				}
			}
		}
	}

}
