package ch.kosh.kirasurveillancesystem.phonescanner.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import ch.kosh.kirasurveillancesystem.deviceswitcher.SwitchWlanPowerController;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneIsAvailableState;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneScanner;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneScanner.KiraState;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneStateList;

public class PhoneScannerTest {

	@Test
	public void getCamSwitchLogAsHtMLTest() throws IOException,
			InterruptedException {
		SwitchWlanPowerController camSwitch = new SwitchWlanPowerController();
		PhoneScanner ps = new PhoneScanner(null, camSwitch);
		String actualOutput = ps.getCamSwitchLogAsHTML();
		String expected = KiraState.UNKNOWN.toString();
		assertTrue("initial state " + expected,
				actualOutput.indexOf(expected) > 0);
		PhoneStateList phoneList = new PhoneStateList();
		PhoneIsAvailableState testPhone = new PhoneIsAvailableState(null, null);
		testPhone.updateState(PhoneIsAvailableState.State.AWAY);
		testPhone.setExtendedAway(0);
		phoneList.addNewPhone(testPhone);
		ps.checkStateChange(phoneList);
		String actualAwayOutput = ps.getCamSwitchLogAsHTML();
		String expectedAway = PhoneScanner.CAM_STATUS
				+ KiraState.ABANDONED.toString();
		assertEquals(expectedAway, actualAwayOutput);
	}
}
