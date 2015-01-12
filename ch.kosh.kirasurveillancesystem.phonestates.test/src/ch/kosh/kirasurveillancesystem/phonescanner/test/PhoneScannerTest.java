package ch.kosh.kirasurveillancesystem.phonescanner.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import ch.kosh.kirasurveillancesystem.deviceswitcher.SwitchWlanPowerController;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneIsAvailableState;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneScanner;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneIsAvailableState.State;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneScanner.KiraState;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneStateList;

public class PhoneScannerTest {

	@Test
	public void getCamSwitchLogAsHtMLTest() throws IOException, InterruptedException,
			ParseException {

		SwitchWlanPowerController camSwitch = new SwitchWlanPowerController();
		PhoneScanner ps = new PhoneScanner(null, camSwitch);

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String abandonedTimestamp = "2015-01-12 22:54:33";

		PhoneStateList phoneList = new PhoneStateList();
		PhoneIsAvailableState testPhone = new PhoneIsAvailableState(null, null);
		testPhone.updateState(PhoneIsAvailableState.State.AWAY);
		testPhone.setExtendedAway(0);
		phoneList.addNewPhone(testPhone);
		ps.checkStateChange(phoneList, dateFormat.parse(abandonedTimestamp).getTime());

		String abandonedHTML = ps.getCamSwitchLogAsHTML();
		assertEquals("timestamp test", abandonedTimestamp,
				abandonedHTML.substring(0, abandonedTimestamp.length()));
		String expectedAbandonedHTMLString = KiraState.ABANDONED + ".<br/>";
		assertEquals(
				"expected away text expected",
				expectedAbandonedHTMLString,
				abandonedHTML.substring(abandonedHTML.length()
						- expectedAbandonedHTMLString.length()));

		// second time nothing should happen
		ps.checkStateChange(phoneList, dateFormat.parse("2015-01-13 23:54:33").getTime());
		String html = ps.getCamSwitchLogAsHTML();
		assertEquals("timestamp test", abandonedTimestamp,
				abandonedHTML.substring(0, abandonedTimestamp.length()));

		String expectedAbandoned2HTMLString = KiraState.ABANDONED + ".<br/>";
		assertEquals("expected away text expected", expectedAbandoned2HTMLString,
				html.substring(html.length() - expectedAbandoned2HTMLString.length()));

		// change to inhabited
		testPhone.updateState(State.AVAILABLE);
		String inhabitedTimestamp = "2015-01-14 10:42:00";
		ps.checkStateChange(phoneList, dateFormat.parse(inhabitedTimestamp).getTime());
		String inhabitedHTML = ps.getCamSwitchLogAsHTML();
		String expectedInhabitedHTMLString = inhabitedTimestamp + ": " + KiraState.INHABITED
				+ ".<br/>";
		assertEquals("html test", expectedInhabitedHTMLString,
				inhabitedHTML.substring(0, expectedInhabitedHTMLString.length()));

	}

	@Test
	public void getCamSwitchStateAsHtMLTest() throws IOException, InterruptedException {
		SwitchWlanPowerController camSwitch = new SwitchWlanPowerController();
		PhoneScanner ps = new PhoneScanner(null, camSwitch);
		String actualOutput = ps.getCamSwitchStateAsHTML();
		String expected = KiraState.UNKNOWN.toString();
		assertTrue("initial state expected: " + expected, actualOutput.indexOf(expected) > 0);
		PhoneStateList phoneList = new PhoneStateList();
		PhoneIsAvailableState testPhone = new PhoneIsAvailableState(null, null);
		testPhone.updateState(PhoneIsAvailableState.State.AWAY);
		testPhone.setExtendedAway(0);
		phoneList.addNewPhone(testPhone);
		ps.checkStateChange(phoneList, 0);
		String actualAwayOutput = ps.getCamSwitchStateAsHTML();
		String expectedAway = PhoneScanner.CAM_STATUS + KiraState.ABANDONED.toString();
		assertEquals(expectedAway, actualAwayOutput);
	}
}
