import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * 
 * @author rohit
 * A class to listen for the GET requests for a particular connection
 * Implements pipelined persistent. Listen to requests and adds them to request queue
 */

public class requestListener implements Runnable{
	
	public Socket connection;
	private String threadCount;
	requestQueue queue;
	boolean alive;
	public requestListener(String name, Socket socket, requestQueue queue) {
		threadCount = name;
		connection = socket;
		this.queue = queue;
	}
	
	public void run() {
		
		try{
			connection.setSoTimeout(50000);
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String readLine;
			
			while(true){
				
				readLine = inFromClient.readLine();
				if (readLine == null || readLine == "") {
					queue.requests.add("BreakCode");
					break;
				}
				
				System.out.println(readLine);			
				String[] header = readLine.split(" ");
				
				
					while(readLine!=null && !readLine.equals("")){
						readLine = inFromClient.readLine();
						//System.out.println(readLine);
						String[] tokens = readLine.split(" ");					
						if(tokens[0].equals("Connection:")){
							if(tokens[1].equals("keep-alive"))
									alive = true;
						}
					}
				
				if(header[0].equals("GET")){
					String filename = header[1];
						
						
						if (filename.indexOf("..") > 0 || filename.indexOf(":") > 0)
							filename = "INVALID ACCESS";
						// Deny Access of Super Directories
						
						else if(filename.endsWith("/")){
							filename = filename.concat("index.html");
						}
						
						if(filename.startsWith("/~")){
							filename = filename.substring(2);
							String userName = filename.substring(0, filename.indexOf("/"));
							filename = filename.substring(filename.indexOf("/")); 
							filename = "/users/ug12/" + userName  +"/public_html" + filename;
							
						} else {
							filename = "INVALID ACCESS";
						}
						
						// System.out.println("Filename is: " + filename + " in threadcount " + threadCount + "\n");		
						queue.requests.add(filename);
					
						if(alive){
							connection.setKeepAlive(true);
							connection.setSoTimeout(10000);
						} else {
							queue.requests.add("BreakCode");
							break;
						}
				}
				
			}
		} catch(SocketTimeoutException s) {
			queue.requests.add("BreakCode");
			System.out.println("---------------------- Request thread timeout : " + threadCount + "----------------------\n");
			
		} catch(IOException e){
			
		}
	}
}
	