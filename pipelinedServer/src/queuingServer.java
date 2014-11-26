import java.net.Socket;
import java.util.ArrayList;

/**
 * 
 * @author rohit
 *	Implements the connection queue. Only a certain number (maxServerConnectionThreads) can be executed in parallel at a given time
 *	Spawns new thread for a new connection when total threads are in limit 
 */

public class queuingServer implements Runnable {
	
	ArrayList<Socket> serverConnectionQueue;
	public queuingServer(ArrayList<Socket> Queue){
		serverConnectionQueue = Queue;
	}
	
	
	Integer threadCount = 1;
	int maxServerConnectionThreads = 5;
	// Configurable : Maximum number of threads spawned by the main server
	
	passByReferenceInteger intRef = new passByReferenceInteger(0);
	Socket connection;
	
	public void run() {
		while(true){
			if ( intRef.activeConnections < maxServerConnectionThreads && serverConnectionQueue.size() > 0){
				//System.out.println(" Connection entering thread queue " + intRef.activeConnections  + " " + serverConnectionQueue.size() + "\n");
				requestQueue queue = new requestQueue();
				intRef.increment();
				connection = serverConnectionQueue.get(0);
				serverConnectionQueue.remove(0);
				
				System.out.println("--------------------- Starting connection thread No. " + threadCount + " ---------------------");
				requestListener threadListener = new requestListener(threadCount.toString(), connection, queue);
				dataResponder threadData = new dataResponder(threadCount.toString(), connection, queue, intRef);
				
				
				Thread l = new Thread(threadListener);
				Thread d = new Thread(threadData);
				l.start();
				d.start();
				threadCount++;
			}
			
			else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
