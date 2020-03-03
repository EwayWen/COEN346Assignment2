package scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class scheduler
{
	private static int elapsedTime = 1;
	process process;
	public static void main(String args[])
	{
		List<process>processList = new ArrayList<process>();
		
		try
		{
			File input = new File("C:/Users/ewayw/eclipse-workspace/Assignment2/src/scheduler/input.txt");
			Scanner sc = new Scanner(input);
			while(sc.hasNextLine()) {
				processList.add(new process(sc.nextInt(),sc.nextInt()));
			}
			sc.close();
		}
		catch(Exception except)
		{
			System.out.println("Exception caught!");
		}
		
		/*
		 * for (int i = 0; i < processList.size(); i++) {
		 * System.out.println(processList.get(i).getQuantum()+ " " +
		 * processList.get(i).getRunTime()); }
		 */
		
		while(processList.size() > 0)
		{
			process current;
			process priority;
			int priorityInt;
			
			priority = processList.get(0);
			priorityInt = priority.getRemainingTime();
			
			for(int i = 0; i < processList.size(); i++)
			{
				current = processList.get(i);
				if(current.isStarted() && current.getRemainingTime() < priorityInt)
				{
					priority = current;
					priorityInt = priority.getRemainingTime();
				}
				else if (current.isStarted() && current.getRemainingTime() == priorityInt)
				{
					if (priority.getArrivalTime() > current.getArrivalTime())
					{
						priority = current;
					}
				}
				
			}
			
			for (int i = 0; i < processList.size(); i ++)
			{
				current = processList.get(i);
				if (current.getArrivalTime() <= elapsedTime && !current.isStarted())
				{
					priority = current;
					break;
				}
			}
			priority.start();
			try {
				priority.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (priority.getRemainingTime() == 0)
			{
				processList.remove(processList.indexOf(priority));
			}
		}
		
	}
	
}
