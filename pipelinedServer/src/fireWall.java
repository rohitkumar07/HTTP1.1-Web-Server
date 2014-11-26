import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author rohit
 * The firewall filter. Filters basic DoS attacks. If the number of requests from a particular host
 * exceeds a certain threshold within a certain time, it is blocked.
 */

public class fireWall {
	private HashMap<String, ArrayList<Long>> ipHashMap;
	private HashMap<String, Boolean> bannedIPs;
	
	private int maxRequestFire = 50; // This value denotes the maximum requests that can be fired within a span of 1 minute. Configurable.
	
	
	public fireWall() {
		// TODO Auto-generated constructor stub
		ipHashMap = new HashMap<String,ArrayList<Long>>();
		bannedIPs = new HashMap<String, Boolean>();
	}
	
	public boolean IPfilter(String ipAddress){
		
		Long currentTime = System.currentTimeMillis();
		if (bannedIPs.containsKey(ipAddress)){
			return false;
		}
		else {
			if (ipHashMap.containsKey(ipAddress)){
				ArrayList<Long> timeList = ipHashMap.get(ipAddress);
				if (timeList.size() < maxRequestFire) {
					timeList.add(currentTime);
					ipHashMap.put(ipAddress, timeList);
					return true;
				}
				else {
					if ( (currentTime - timeList.get(0)) < 60000){
						// Max Requests within a span of 1 minute exceeds threshold limit.
						bannedIPs.put(ipAddress,true);
						return false;
					}
					else {
						timeList.remove(0);
						timeList.add(currentTime);
						ipHashMap.put(ipAddress, timeList);
						return true;
					}
				}
			}
			else {
				ArrayList<Long> newTimeList = new ArrayList<Long>();
				newTimeList.add(currentTime);
				ipHashMap.put(ipAddress, newTimeList);
				return true;
			}
		}
	}
}
