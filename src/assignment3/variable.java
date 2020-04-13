package assignment3;

public class variable {

	private String ID; //variable ID
	private int lastAccess; //Last accessed time
	private int value; //Stored value

	variable(String ID, int value) { //constructor
		this.setID(ID);
		this.setValue(value);
	}
	//getters and setters
	public int getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(int lastAccess) {
		this.lastAccess = lastAccess;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
