import java.util.ArrayList;

/**
 * 
 * @author rohit
 *	A class holding the request queue.
 *	Used for pipelining persistent connection
 */

public class requestQueue {
	ArrayList<String> requests;
	ArrayList<String> keepAlive;
	boolean toBreak;
	public requestQueue() {
		// TODO Auto-generated constructor stub
		requests = new ArrayList<String>();
		keepAlive = new ArrayList<String>();
		toBreak = false;
	}
	
}
