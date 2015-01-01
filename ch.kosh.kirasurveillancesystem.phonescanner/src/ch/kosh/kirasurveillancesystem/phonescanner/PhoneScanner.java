package ch.kosh.kirasurveillancesystem.phonescanner;

import java.io.IOException;
import java.util.ArrayList;

import ch.kosh.kirasurveillancesystem.btscan.L2pingRunner;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneIsAvailableState;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneIsAvailableState.State;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneStateList;

public class PhoneScanner {

	ArrayList<String> addresses = new ArrayList<String>();
	L2pingRunner l2pingRunner;

	public PhoneScanner(L2pingRunner l2pingRunner) {
		this.l2pingRunner = l2pingRunner;
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

	public void updateStates(PhoneStateList phoneList) {
		for (PhoneIsAvailableState phoneState : phoneList.getAll()) {
			String macAddress = phoneState.getMacAddress();
			State newState = pingAddress(macAddress);
			String responseText = phoneState.updateState(newState);
			phoneList.addPingResponse(responseText);
		}
	}
}
