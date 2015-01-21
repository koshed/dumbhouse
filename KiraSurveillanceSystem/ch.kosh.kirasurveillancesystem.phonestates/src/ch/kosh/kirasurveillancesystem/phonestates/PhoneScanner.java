package ch.kosh.kirasurveillancesystem.phonestates;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.kosh.kirasurveillancesystem.btscan.L2pingRunner;
import ch.kosh.kirasurveillancesystem.deviceswitcher.SwitchWlanPowerController;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneIsAvailableState.State;


public class PhoneScanner {
	public static final String CAM_STATUS = "Cam status: ";
	private static final Logger log4j = LogManager.getLogger(PhoneScanner.class.getName());

	public enum KiraState {
		UNKNOWN, INHABITED, ABANDONED
	}

	public enum CamState {
		UNKNOWN, OFF, ON;
	}

	public class KiraStates {
		KiraState kiraState;
		CamState camState;

		public KiraStates() {
			this.kiraState = KiraState.UNKNOWN;
			this.camState = CamState.UNKNOWN;
		}

		public void setState(KiraState newState) {
			this.kiraState = newState;
			switch (newState) {
			case ABANDONED:
				this.camState = CamState.ON;
				break;
			case INHABITED:
				this.camState = CamState.OFF;
				break;
			case UNKNOWN:
				this.camState = CamState.UNKNOWN;
				break;
			default:
				break;
			}
		}
		
		public KiraState getKiraState(){
			return this.kiraState;
		}
		public CamState getCamState(){
			return this.camState;
		}
	}

	ArrayList<String> addresses = new ArrayList<String>();
	L2pingRunner l2pingRunner;
	private KiraStates kiraState;
	private SwitchWlanPowerController switchWlanPowerController;
	private ArrayList<KiraStateLogEntry> kiraStateLog;

	public PhoneScanner(L2pingRunner l2pingRunner,
			SwitchWlanPowerController switchWlanPowerController) {
		this.l2pingRunner = l2pingRunner;
		this.switchWlanPowerController = switchWlanPowerController;
		kiraState = new KiraStates();
		kiraStateLog = new ArrayList<KiraStateLogEntry>();
	}

	private boolean pingBTAddress(String macAddress) throws IOException, InterruptedException {
		String output = l2pingRunner.pingAdress(macAddress);
		int indexOfReceivedText = output.indexOf("1 received");
		// System.out.println("Index of received:" + indexOfReceivedText);
		return indexOfReceivedText > 0;
	}

	public void addAdress(String address) {
		addresses.add(address);
	}

	public PhoneIsAvailableState.State pingAddress(String macAddress) {
		PhoneIsAvailableState.State newState = PhoneIsAvailableState.State.AWAY;
		boolean addressIsHere;
		try {
			addressIsHere = pingBTAddress(macAddress);
			if (addressIsHere) {
				newState = PhoneIsAvailableState.State.AVAILABLE;
			}

		} catch (IOException | InterruptedException e) {
			// log4j.trace("Exception: " + e.getMessage());
			// e.printStackTrace();
		}
		return newState;
	}

	public void updateStates(PhoneStateList phoneList) throws IOException, InterruptedException {
		// Scan
		scanPhones(phoneList);

		// Check for ABANDONED
		checkStateChange(phoneList, System.currentTimeMillis());

	}

	public void checkStateChange(PhoneStateList phoneList, long stateChangeTimestamp) {
		try {
			boolean isAbandoned = true;
			for (PhoneIsAvailableState phones : phoneList.getAll()) {
				if (!phones.isExtendedAway()) {
					isAbandoned = false;
					break; // just for faster looping
				}
			}
			if (isAbandoned) {
				if (kiraState.getKiraState() != KiraState.ABANDONED) {
					// State Change to away
					KiraState newState = KiraState.ABANDONED;
					kiraState.setState(newState);
					addToKiraStateLog(stateChangeTimestamp, newState);
					log4j.debug("Switching power on");

					switchWlanPowerController.switchPower(true);

					phoneList.addPingResponse(PhoneIsAvailableState
							.formatTimeMillisToDate(stateChangeTimestamp)
							+ ": Switched cam power on");
				}
			}
			if (!isAbandoned) {
				if (kiraState.getKiraState() != KiraState.INHABITED) {
					// State change to @home
					KiraState newState = KiraState.INHABITED;
					kiraState.setState(newState);
					addToKiraStateLog(stateChangeTimestamp, newState);

					log4j.debug("Switching power off");

					switchWlanPowerController.switchPower(false);

					phoneList.addPingResponse(PhoneIsAvailableState
							.formatTimeMillisToDate(stateChangeTimestamp)
							+ ": Switched cam power off");
				}
			}
		} catch (IOException | InterruptedException e) {
			// log4j.trace("Exception: " + e.getMessage());
			// e.printStackTrace();
		}
	}

	private void addToKiraStateLog(long stateChangeTimestamp, KiraState newState) {
		kiraStateLog.add(new KiraStateLogEntry(stateChangeTimestamp, newState));
		while (kiraStateLog.size() > 100) {
			kiraStateLog.remove(0);
		}
	}

	private void scanPhones(PhoneStateList phoneList) throws IOException, InterruptedException {
		for (PhoneIsAvailableState phoneState : phoneList.getAll()) {
			String macAddress = phoneState.getMacAddress();
			State newState = pingAddress(macAddress);
			String responseText = phoneState.updateState(newState);
			phoneList.addPingResponse(responseText);
		}
	}

	public String getCamSwitchLogAsHTML() {
		String html = "";
		for (int i = kiraStateLog.size() - 1; i >= 0; i--) {
			KiraStateLogEntry entry = kiraStateLog.get(i);
			html += entry.toHTMLString() + "<br/>";
		}
		return html;
	}

	public String getCamSwitchStateAsHTML() {
		StringBuilder sb = new StringBuilder();
		sb.append(CAM_STATUS);
		sb.append(kiraState.getCamState());
		return sb.toString();
	}

	public KiraStates getKiraStateObject() {
		return this.kiraState;
	}
}
