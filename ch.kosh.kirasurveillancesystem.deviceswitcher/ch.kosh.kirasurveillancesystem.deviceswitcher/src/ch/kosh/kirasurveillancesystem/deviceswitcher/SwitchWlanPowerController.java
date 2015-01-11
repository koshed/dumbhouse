package ch.kosh.kirasurveillancesystem.deviceswitcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SwitchWlanPowerController {
	
	public String switchPower(boolean turnOn) throws IOException,
			InterruptedException {
		String lineFeedback = "";
		String line;
		
		String offOn = "off";
		if (turnOn)
		{
			offOn = "on";
		}
		String command = "curl -d @" + offOn + ".xml http://admin:1111@10.10.10.74:10000/smartplug.cgi -o \"output.txt\"";
		
		Process p = Runtime.getRuntime().exec(command);

		BufferedReader bri = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		BufferedReader bre = new BufferedReader(new InputStreamReader(
				p.getErrorStream()));
		while ((line = bri.readLine()) != null) {
			lineFeedback += line;
			// System.out.println(line);
		}
		bri.close();
		while ((line = bre.readLine()) != null) {
			lineFeedback += line;
			// System.out.println(line);
		}
		bre.close();
		p.waitFor();
		// System.out.println("Done.");
		p.destroy();

		return lineFeedback;
	}
}
