
/**
 * 
 * @author rohit
 * Helper class to pass integers by reference to functions
 */

public class passByReferenceInteger {
	int activeConnections;
	public passByReferenceInteger(int value) {
		this.activeConnections = value;
	}
	
	public void decrement() {
		activeConnections--;
	}
	
	public void increment() {
		activeConnections++;
	}
}
