import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
/**
 * 
 * @author rohit
 * 
 * This class implements the response i.e. writes to the output stream using the request queue
 * Uses Web Cache to speed up file extraction
 *
 */

public class dataResponder implements Runnable{

	public Socket connection;
	private String threadCount;
	requestQueue queue;
	boolean alive;
	
	int maxCacheSize = 20;		// Max no. of files stored in cache
	
	passByReferenceInteger activeConnections;
	public dataResponder(String name, Socket socket, requestQueue queue, passByReferenceInteger intRef) {
		threadCount = name;
		connection = socket;
		this.queue = queue;
		this.activeConnections = intRef;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		try {
			//connection.setSoTimeout(30000);
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			
			while(true){
				
				if (queue.requests.size() > 0 ) {
					//if (queue.requests.size() > 1 || queue.requests.get(0).contains("index")){
				String filename = queue.requests.get(0);
				queue.requests.remove(0);
				
				if (filename.equals("BreakCode")) {
					// System.out.println("Exiting thread : " + threadCount + "\n");
					break;
				}
				
				if (filename.equals("INVALID ACCESS")){
					out.writeBytes("HTTP/1.1 404 Not Found" + "\r\n");
					out.writeBytes("Content-Type: text/html" + "\r\n");
					out.writeBytes("\r\n");
					out.writeBytes("<html><body><center>Error : INVALID ACCESS </center> </body><html>");;
				}
				else
					try{
							byte[] fileInBytes = null;
							boolean cached = false;
							File file = new File(filename);				
							int num_of_bytes = (int) file.length();
							
							
							/**
							 *  Implementation of the Web Cache.
							 *  Least Recently Used (LRU) policy is used.
							 */
							
							if (mainServer.webCache.containsKey(filename)){
								if (mainServer.webCache.get(filename) == 1){ // means present in the cache
									for (int i = 0; i < mainServer.cachedFileNames.size(); i++){
										if (mainServer.cachedFileNames.get(i).equals(filename)){
											fileInBytes = mainServer.cachedFiles.get(i);
											cached = true;
											System.out.println("Extracted " + filename + " from cache. \n");
											break;
										}
									}
								}
							}
							
						
						if (!cached){
						    fileInBytes = new byte[num_of_bytes];
						    
						    FileInputStream inFile = new FileInputStream(filename);
						    inFile.read(fileInBytes);
						    inFile.close();
						    mainServer.cachedFileNames.add(filename);
						    mainServer.cachedFiles.add(fileInBytes);
						    mainServer.webCache.put(filename, 1);	// Indicates presence in web cache
						    if (mainServer.cachedFileNames.size() > maxCacheSize){
						    	String toBeRemoved = mainServer.cachedFileNames.get(0);
								mainServer.cachedFiles.remove(0);
								mainServer.cachedFileNames.remove(0);
						    	mainServer.webCache.put(toBeRemoved,0); // Indicates absence in web cache
						    }
						}
						
						
						out.writeBytes("HTTP/1.1 200 Document Follows\r\n");				      
						out.writeBytes("Content-Length: " + num_of_bytes + "\r\n");
						out.writeBytes("\r\n");
						out.write(fileInBytes, 0, num_of_bytes);
						
						System.out.println("Sent Filename : " + filename + /*" in threadcount " + threadCount + */ "\n");
						
					} catch (FileNotFoundException fnfe){
						// System.out.println(fnfe);
						out.writeBytes("HTTP/1.1 404 Not Found" + "\r\n");
						out.writeBytes("Content-Type: text/html" + "\r\n");
						out.writeBytes("\r\n");
						out.writeBytes("<html><body><center>Error 404: Not Found</center> </body><html>");
					}
				
				connection.setKeepAlive(true);
				connection.setSoTimeout(10000);
				
			}//}
				else{
					Thread.sleep(3000);
				}
			
		}
		
		System.out.println("---------------------- Closing thread : " + threadCount + "----------------------\n");	
		activeConnections.decrement();
		connection.close();	
			
			
	} catch(SocketTimeoutException s) {
		try {
			System.out.println("---------------------- Response Thread Timeout: " + threadCount + "----------------------\n");
			activeConnections.decrement();
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}	
		
	} catch(IOException e){
			e.printStackTrace();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}
	
}
