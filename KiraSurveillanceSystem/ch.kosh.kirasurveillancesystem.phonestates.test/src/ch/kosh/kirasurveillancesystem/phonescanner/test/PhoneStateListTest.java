package ch.kosh.kirasurveillancesystem.phonescanner.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import ch.kosh.kirasurveillancesystem.phonestates.PhoneIsAvailableState;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneIsAvailableState.State;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneStateList;

public class PhoneStateListTest {

	@Test
	public void extendedAwayHTMLTest() throws IOException, InterruptedException {
		PhoneStateList list = new PhoneStateList();
		PhoneIsAvailableState phoneState = new PhoneIsAvailableState("11:42",
				"extendedAwayHTMLTest");
		list.addNewPhone(phoneState);
		String html = list.getExtendedAwayLogAsHTML();
		assertEquals("expect almoust empty html text, but...", 28,
				PhoneStateList.PHONE_SEPARATOR_HTML.length() + "<br/>".length()
						* 2);
		phoneState.updateState(State.AWAY);
		phoneState.setExtendedAway(0);
		html = list.getExtendedAwayLogAsHTML();
		assertTrue("expect extended away text, but...", html.indexOf("xtended") > 0);
	}

	@Test
	public void add10NewPhonesTest() {
		PhoneStateList phoneStateList = new PhoneStateList();
		PhoneIsAvailableState phoneIsAvailableState = null;
		for (int i = 0; i < 10; i++) {
			phoneStateList.addNewPhone(phoneIsAvailableState);
		}
		assertEquals("List size test", 10, phoneStateList.getAll().size());
	}

	@Test
	public void addPingResponse100LimitTest() {
		PhoneStateList phoneStateList = new PhoneStateList();
		String responseText = "dummy text";
		for (int i = 0; i < 300; i++) {
			phoneStateList.addPingResponse(responseText);
		}
		phoneStateList.addPingResponse(null);
		assertEquals(10, phoneStateList.getPingResponses().size());
	}

	@Test
	public void getResponsesAsHTMLReversedTest() {
		PhoneStateList phoneStateList = new PhoneStateList();
		String responseText = "dummy text: ";
		for (int i = 0; i < 5; i++) {
			phoneStateList.addPingResponse(responseText + i);
		}
		String logAsHtML = phoneStateList.getLogAsHtML();
		int indexOfLinefeed = logAsHtML.indexOf("<br/>");
		String firstElementHTMLString = logAsHtML.substring(0, indexOfLinefeed);
		assertTrue(firstElementHTMLString.indexOf(responseText + 4) >= 0);
	}
}
