package ch.kosh.kirasurveillancesystem.phonestates;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.kosh.kirasurveillancesystem.btscan.L2pingRunner;
import ch.kosh.kirasurveillancesystem.deviceswitcher.SwitchWlanPowerController;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneIsAvailableState.State;

public class PhoneScanner {

	private static final Logger log4j = LogManager.getLogger(PhoneScanner.class
			.getName());

	private enum KiraState {
		UNKNOWN, INHABITED, ABANDONED
	}

	ArrayList<String> addresses = new ArrayList<String>();
	L2pingRunner l2pingRunner;
	private KiraState kiraState;
	private SwitchWlanPowerController switchWlanPowerController;

	public PhoneScanner(L2pingRunner l2pingRunner,
			SwitchWlanPowerController switchWlanPowerController) {
		this.l2pingRunner = l2pingRunner;
		this.switchWlanPowerController = switchWlanPowerController;
		kiraState = KiraState.UNKNOWN;
	}

	private boolean pingBTAddress(String address) throws IOException,
			InterruptedException {
		String output = l2pingRunner.pingAdress(address);
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
		}
		return newState;
	}

	public void updateStates(PhoneStateList phoneList) throws IOException,
			InterruptedException {
		// Scan
		scanPhones(phoneList);

		// Check for ABANDONED
		checkStateChange(phoneList);

	}

	private void checkStateChange(PhoneStateList phoneList) throws IOException,
			InterruptedException {
		boolean isAbandoned = true;
		for (PhoneIsAvailableState phones : phoneList.getAll()) {
			if (!phones.isExtendedAway()) {
				isAbandoned = false;
				break; // just for faster looping
			}
		}
		if (isAbandoned) {
			if (kiraState != KiraState.ABANDONED) {
				// State Change to away
				log4j.debug("Switching power on");
				switchWlanPowerController.switchPower(true);
				phoneList.addPingResponse(PhoneIsAvailableState
						.formatTimeMillisToDate(System.currentTimeMillis())
						+ ": Switched cam power on");
				kiraState = KiraState.ABANDONED;
			}
		}
		if (!isAbandoned) {
			if (kiraState != KiraState.INHABITED) {
				// State change to @home
				log4j.debug("Switching power off");
				switchWlanPowerController.switchPower(false);
				phoneList.addPingResponse(PhoneIsAvailableState
						.formatTimeMillisToDate(System.currentTimeMillis())
						+ ": Switched cam power off");
				kiraState = KiraState.INHABITED;
			}
		}
	}

	private void scanPhones(PhoneStateList phoneList) throws IOException,
			InterruptedException {
		for (PhoneIsAvailableState phoneState : phoneList.getAll()) {
			String macAddress = phoneState.getMacAddress();
			State newState = pingAddress(macAddress);
			String responseText = phoneState.updateState(newState);
			phoneList.addPingResponse(responseText);
		}
	}
}
