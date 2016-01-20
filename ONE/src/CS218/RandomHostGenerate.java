package CS218;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomHostGenerate {

	public static List<Long> hostList;
	public static int randomHostCount = 80;
	
	public static void main(String[] args) {
		
		Random rng = new Random(System.currentTimeMillis());
		
		hostList = new ArrayList<Long>();
		BufferedReader br = null;

		try {

			String sCurrentLine;
			br = new BufferedReader(new FileReader("host_list"));

			while ((sCurrentLine = br.readLine()) != null) {
				long hostID = Long.parseLong(sCurrentLine.split(":")[0]);
				hostList.add(hostID);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		BufferedWriter bw = null;
		 
	    try {
	    	bw = new BufferedWriter(new FileWriter("host_list", true));
	     
	    	for(int i=0; i<randomHostCount; i++) {
				long hostID = rng.nextInt(284939103);
				String mobility = "[";
				String hostLine = "";
				
				if(hostList.contains(hostID) == true) {
					i--;
				}else {
					int pointCounts = rng.nextInt(7) + 2;
					
					for(int j=0; j<pointCounts; j++) {
						int pointX = rng.nextInt(1000) + 1;
						int pointY = rng.nextInt(1000) + 1;
						
						if(j == pointCounts-1) {
							mobility += "'" + Integer.toString(pointX) + "/" + Integer.toString(pointY) + "']";
						}else {
							mobility += "'" + Integer.toString(pointX) + "/" + Integer.toString(pointY) + "', ";
						}
					}
					
					hostLine = hostID + ":" + mobility;
					
					bw.write(hostLine);
					bw.newLine();
			    	bw.flush();
				}
			} 	
	    	
	     } catch (IOException ioe) {
	    	 ioe.printStackTrace();
	     } finally {
	    	 if (bw != null) try {
	    		 bw.close();
	    	 } catch (IOException ioe2) {
	        // just ignore it
	    	 }
	     } // end try/catch/finally
		
	}

}
