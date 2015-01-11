package ch.kosh.kirasurveillancesystem.phonescanner;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.kosh.kirasurveillancesystem.btscan.L2pingRunner;
import ch.kosh.kirasurveillancesystem.deviceswitcher.SwitchWlanPowerController;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneIsAvailableState;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneStateList;
import ch.kosh.kirasurveillancesystem.webserver.MicroWebServer;
import ch.kosh.kirasurveillancesystem.webserver.MicroWebServerThreadStarter;

public class PhoneScannerRunner implements Runnable {

	private static final Logger log4j = LogManager
			.getLogger(PhoneScannerRunner.class.getName());

	PhoneScanner phoneScanner;
	private PhoneStateList phoneList;

	private int scanIntervall;

	public PhoneScannerRunner(int scanIntervall) {
		SwitchWlanPowerController switchWlanPowerController = new SwitchWlanPowerController();
		
		this.phoneList = new PhoneStateList();
		
		L2pingRunner l2pingRunner = new L2pingRunner();
		PhoneScanner phoneScanner = new PhoneScanner(l2pingRunner, switchWlanPowerController);

		MicroWebServer ws = new MicroWebServer(getPhoneList());
		startWebServer(ws);

		this.scanIntervall = scanIntervall;
		log4j.info("BT scan intervall: " + scanIntervall + "s");
		this.phoneScanner = phoneScanner;
		String markNexus5Adress = "50:55:27:08:0C:C1";
		log4j.info("Added Mark's phone mac address: " + markNexus5Adress);
		String heleneiPhone5sAdress = "48:74:6E:A1:32:EA";

		this.phoneList.addNewPhone(
				new PhoneIsAvailableState(markNexus5Adress, "Elvis"));
		this.phoneList.addNewPhone(
				new PhoneIsAvailableState(heleneiPhone5sAdress, "Kahlan"));

	}

	public static void main(String[] args) {

		int scanIntervall = 5;
		Thread phoneScannerMinuteThread = new Thread(new PhoneScannerRunner(
				scanIntervall));
		phoneScannerMinuteThread.start();

	}

	private static void startWebServer(MicroWebServer ws) {
		MicroWebServerThreadStarter microWebServerThreadStarter = new MicroWebServerThreadStarter(
				ws);
		microWebServerThreadStarter.start();
	}

	@Override
	public void run() {
		// scan ever minute
		for (;;) {
			try {
				log4j.trace("Pinging devices...");
				phoneScanner.updateStates(this.getPhoneList());
				Thread.sleep(1000 * this.scanIntervall);
				// phoneScanner.pingAddresses();
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	public PhoneStateList getPhoneList() {
		return phoneList;
	}
}
