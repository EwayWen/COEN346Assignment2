package assignment3;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class memoryManager {

	private String memconfig; // name of the memory config file
	private int size; // number of pages in memory
	private String vm; // name of disk file
	static List<variable> memory = new ArrayList<variable>(); // ArrayList to store variable; simulates memory
	static int currentTime; // Current time passed from process class

	memoryManager() { // constructor
		this.memconfig = "memconfig.txt";
		this.vm = "vm.txt";
		try { // reads memconfig.txt for number of pages
			Scanner scMemconfig = new Scanner(new File(this.memconfig));
			this.size = Integer.parseInt(scMemconfig.nextLine());
			scMemconfig.close();
			if (this.size == 0) {
				System.out.println("Memory size is zero.");
			}
		} catch (Exception e) {
			System.out.println("Could not open memconfig.");
			e.printStackTrace();
		}
		//We open vm here to reset vm.txt, Since we append to it later in the code.
		//This prevents old data from previous runs from accumulating.
		//If you want to see what happens, comment the try/catch below and look at vm.txt after a few runs.
		try(PrintWriter pw = new PrintWriter(new File(vm))){
			pw.close();
		}catch (Exception e) {
			System.out.println("Could not open vm.");
			e.printStackTrace();
		}
	}

	// This method is synchronized to prevent the threads from setting the time and
	// manipulating I/O simultaneously
	public synchronized void parseCommand(String command, int time) {
		setCurrentTime(time); // Time is passed from process class
		String[] myArr = command.split(" "); // identifying command elements
		myArr[0] = myArr[0].toLowerCase();
		// if/else block to check for valid commands
		// Memory Storage
		if ((Arrays.asList(new String[] { "store", "memstore" })).contains(myArr[0])) {
			scheduler.addToOutputString("Store: Variable " + myArr[1] + ", Value: " + myArr[2] + "\n");
			memStore(myArr[1], Integer.parseInt(myArr[2]));
			// Memory Release
		} else if ((Arrays.asList(new String[] { "memfree", "release", "free" })).contains(myArr[0])) {
			scheduler.addToOutputString("Release: Variable " + myArr[1] + "\n");
			memFree(myArr[1]);
			// Memory Lookup
		} else if ((Arrays.asList(new String[] { "look", "memlookup", "memlook", "lookup" })).contains(myArr[0])) {
			scheduler.addToOutputString("Lookup: Variable " + myArr[1] + ", Value " + memLookup(myArr[1]) + "\n");
			// Ignores invalid input
		} else {

		}
	}

	// Memory Storage
	public void memStore(String varID, int value) {
		// Create new variable and set its access time
		variable var = new variable(varID, value);
		var.setLastAccess(currentTime);
		if (memory.size() >= this.size) { // If there is no room in memory, swap it
			swap(var);
		} else { // If there is room, add it.
			memory.add(var);
		}
		// NOTE: we do not check for duplicate variables in memory. It can be done if
		// needed.
	}

	// Memory Release
	public void memFree(String varID) {
		for (variable var : memory) {
			if (var.getID().equals(varID)) {
				memory.remove(var);
			}
		}
		// Removes element from memory; if the element does not exist, ignore. Can
		// implement check if needed.
	}

	// Memory Lookup
	public int memLookup(String varID) {
		for (variable var : memory) { // First check main memory
			if (var.getID().equals(varID)) {
				var.setLastAccess(currentTime);
				return var.getValue(); // Returns value on success
			}
		}
		if (checkID(varID)) { // on a fail, check secondary memory
			try {
				Scanner sc = new Scanner(new File(vm));
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					String[] lineArr = line.split(" ");
					if (lineArr[0].equals(varID)) { // If a hit is found, we swap old with new.
						// We have to generate a new variable object since 'old' ones are not stored.
						swap(new variable(varID, Integer.parseInt(lineArr[1])));
						return Integer.parseInt(lineArr[1]);
					}
				}
				sc.close();
			} catch (Exception e) {
				System.out.println("Error reading variable on disk.");
				e.printStackTrace();
			}
		}
		return -1;
	}

	public void swap(variable swapIn) { // Use this method to swap from main memory to secondary
		variable swapOut = memory.get(0); // arbitrarily choose first element
		for (variable var : memory) { // find most suitable element using linear search
			if ((var.getLastAccess() < swapOut.getLastAccess())) {
				swapOut = var;
			}
		}
		try (FileWriter fw = new FileWriter(this.vm, true)) { // Need to store to secondary storage. Added to bottom of list.
			PrintWriter pw = new PrintWriter(fw);
			pw.println(swapOut.getID() + " " + swapOut.getValue());
			pw.close();
			scheduler
					.addToOutputString("SWAP: Variable " + swapIn.getID() + " with Variable " + swapOut.getID() + "\n");
			//Variables removed from and added to main memory
			memory.remove(swapOut);
			memory.add(swapIn);
		} catch (Exception e) {
			System.out.println("Error attempting swap.");
			e.printStackTrace();
		}
	}
	//This method checks to see if a variable is already in secondary storage, and if so, overwrites it with the value.
	//May be legacy.
	public boolean checkID(String varID, int value) { //one of two overloaded methods. Check and replace.
		String contents = "";
		boolean onDisk = false;
		try {
			Scanner sc = new Scanner(new File(vm));
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] lineArr = line.split(" ");
				if (lineArr[0].equals(varID)) {
					contents += varID + " " + value + System.lineSeparator();
					onDisk = true;
				} else {
					contents += line + System.lineSeparator();
				}
			}
			sc.close();
			PrintWriter pw = new PrintWriter(vm);
			pw.println(contents);
			pw.close();
		} catch (Exception e) {
			System.out.println("Error overwriting variable on disk.");
			e.printStackTrace();
		}

		return onDisk;
	}
	//This method checks to see if a variable is already in secondary storage. May be legacy.
	public boolean checkID(String varID) {
		//String contents = "";
		boolean onDisk = false;
		try {
			Scanner sc = new Scanner(new File(vm));
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] lineArr = line.split(" ");
				if (lineArr[0].equals(varID)) {
					onDisk = true;
				} else {
					//contents += line + System.lineSeparator();
				}
			}
			sc.close();
		} catch (Exception e) {
			System.out.println("Error checking variable on disk.");
			e.printStackTrace();
		}

		return onDisk;
	}

	public void setCurrentTime(int time) {
		memoryManager.currentTime = time;
	}
}
