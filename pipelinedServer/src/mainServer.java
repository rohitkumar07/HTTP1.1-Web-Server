import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author rohit
 * The class containing main method
 */

public class mainServer {
	private static ServerSocket server;

	static ArrayList<Socket> serverConnectionQueue; // A queue to hold the list of incoming connections
	public static HashMap<String, Integer> webCache;  // Shows the presence of a file in the cache
	
	public static ArrayList<byte[]> cachedFiles;	// The pointer of the file data array
	public static ArrayList<String> cachedFileNames;  // The List of file names present currently in the web cache
	
	public static void main(String[] args) throws IOException, InterruptedException  {
			server = new ServerSocket(8080);
			Socket connection;
			
			serverConnectionQueue = new ArrayList<Socket>();	
			
			queuingServer newQueuingServer = new queuingServer(serverConnectionQueue);  // A new class to implement the connection queue
			Thread queueThread = new Thread(newQueuingServer);
			queueThread.start();
			
			fireWall fireWallFilter =  new fireWall();		// New Firewall To filter DoS attacks
			webCache = new HashMap<String, Integer>();		
			cachedFiles = new ArrayList<byte []>();			
			cachedFileNames = new ArrayList<String>();		
			
		while(true){
			
			try {	
				System.out.println( "Waiting for a client to connect ");
				connection = server.accept();
				
				if (fireWallFilter.IPfilter(connection.getInetAddress().toString())){ // If the IP is filtered by firewall
					serverConnectionQueue.add(connection);
					System.out.println("Connection request received from " + connection.getInetAddress().getCanonicalHostName() + " and added to queue.\n");
				}
				else{	// If the ip is blocked
					System.out.println("This host-name " + connection.getInetAddress().getCanonicalHostName() + " is blocked by FireWall. \n");
				}
				Thread.sleep(500);
				
			}catch (IOException e) {
				// TODO: handle exception
				// System.out.println(e);					
				
			}
		}
	}
}

