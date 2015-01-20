package ch.kosh.kirasurveillancesystem.btscan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class L2pingRunner {

	public String pingAdress(String adress) throws IOException,
			InterruptedException {
		String lineFeedback = "";
		String line;
		//String command = "sh ~/btscan/work/l2pingScan.sh &";
		String command = "sudo l2ping -c 1 " + adress;
		Process p = Runtime.getRuntime().exec(command);
		
		BufferedReader bri = new BufferedReader
		        (new InputStreamReader(p.getInputStream()));
		BufferedReader bre = new BufferedReader
		    (new InputStreamReader(p.getErrorStream()));
		while ((line = bri.readLine()) != null) {
			lineFeedback += line;
		    //System.out.println(line);
		}
		bri.close();
		while ((line = bre.readLine()) != null) {
			lineFeedback += line;
		    //System.out.println(line);
		}
		bre.close();
		p.waitFor();
		//System.out.println("Done.");
		p.destroy();
		
		return lineFeedback;
	}
	
	
}
