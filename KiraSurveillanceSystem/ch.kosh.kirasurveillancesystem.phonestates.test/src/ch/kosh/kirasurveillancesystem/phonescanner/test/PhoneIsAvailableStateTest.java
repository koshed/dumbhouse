package ch.kosh.kirasurveillancesystem.phonescanner.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import ch.kosh.kirasurveillancesystem.phonestates.PhoneIsAvailableState;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneIsAvailableState.State;

public class PhoneIsAvailableStateTest {

	@Test
	public void extendedAwayRemovedTest() throws ParseException, IOException, InterruptedException {
		String name = "dummy";
		PhoneIsAvailableState phoneIsAvailableState = new PhoneIsAvailableState(
				"AA:22:33", name);
		phoneIsAvailableState.updateState(State.AWAY);
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
		Date date = dateFormat.parse("2014.12.30 22:54:33");
		long time = date.getTime();
		phoneIsAvailableState.setExtendedAway(time);
		phoneIsAvailableState.updateState(State.AVAILABLE);
		String html = phoneIsAvailableState.getExtendedAwayLogAsHTML();
		String expectedStringEnd = name + " is back.<br/>";
		assertEquals("expected away text expected", true,
				html.contains(expectedStringEnd));
	}

	@Test(expected = RuntimeException.class)
	public void extendedNeedsAwayStateTest() throws IOException, InterruptedException {
		PhoneIsAvailableState phoneIsAvailableState = new PhoneIsAvailableState(
				"AA:22:33", null);
		phoneIsAvailableState.setExtendedAway(0);
	}

	@Test
	public void extendedAwayLogTest() throws ParseException, IOException, InterruptedException {
		String name = "dummy";
		PhoneIsAvailableState phoneIsAvailableState = new PhoneIsAvailableState(
				"AA:22:33", name);
		phoneIsAvailableState.updateState(State.AWAY);
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
		Date date = dateFormat.parse("2014.12.30 22:54:33");
		long time = date.getTime();
		phoneIsAvailableState.setExtendedAway(time);
		String html = phoneIsAvailableState.getExtendedAwayLogAsHTML();
		assertEquals("timestamp test", "2014-12-30", html.substring(0, 10));
		String expectedStringEnd = name + " is extended away.<br/>";
		assertEquals("expected away text expected", expectedStringEnd,
				html.substring(html.length() - expectedStringEnd.length()));

		// second time nothing should happen
		phoneIsAvailableState.setExtendedAway(dateFormat.parse(
				"2015.12.30 11:44:23").getTime());
		html = phoneIsAvailableState.getExtendedAwayLogAsHTML();
		assertEquals("timestamp test", "2014-12-30", html.substring(0, 10));
	}

	@Test
	public void extendedAwayStateSetTest() throws IOException, InterruptedException {
		PhoneIsAvailableState phoneIsAvailableState = new PhoneIsAvailableState(
				"11:22:33", "dummy");
		phoneIsAvailableState.updateState(State.AWAY);
		phoneIsAvailableState.setExtendedAway(0);
		assertEquals("expected extended away", true,
				phoneIsAvailableState.isExtendedAway());
	}

	@Test
	public void updateStateTest() throws IOException, InterruptedException {
		PhoneIsAvailableState phoneIsAvailableState = new PhoneIsAvailableState(
				null, null);
		State expectedNewState = State.AWAY;

		phoneIsAvailableState.updateState(expectedNewState);
		State actualNewState = phoneIsAvailableState.getMyState();

		assertEquals("Must have chaned to AWAY!", expectedNewState,
				actualNewState);
	}

	@Test
	public void updateStateTestAvailable() throws IOException, InterruptedException {
		PhoneIsAvailableState phoneIsAvailableState = new PhoneIsAvailableState(
				null, null);
		State expectedNewState = State.AVAILABLE;

		phoneIsAvailableState.updateState(expectedNewState);
		State actualNewState = phoneIsAvailableState.getMyState();

		assertEquals("Must have chaned to Available!", expectedNewState,
				actualNewState);
	}

	@Test
	public void updateStateTestInitial() throws IOException, InterruptedException {
		PhoneIsAvailableState phoneIsAvailableState = new PhoneIsAvailableState(
				null, null);
		State expectedNewState = State.UNKNOWN;
		State actualNewState = phoneIsAvailableState.getMyState();
		assertEquals("Must be unknown", expectedNewState, actualNewState);
		phoneIsAvailableState.updateState(expectedNewState);
		actualNewState = phoneIsAvailableState.getMyState();
		assertEquals("Must be unknown", expectedNewState, actualNewState);
	}

	@Test
	public void updateStateHTMLFormatterTest() throws IOException, InterruptedException {
		PhoneIsAvailableState phoneIsAvailableState = new PhoneIsAvailableState(
				null, null);
		String actualOutput = phoneIsAvailableState.toHTMLWebString();
		String expected = State.UNKNOWN.toString();
		assertEquals(expected, actualOutput);
		phoneIsAvailableState.updateState(State.AWAY);
		String actualAwayOutput = phoneIsAvailableState.toHTMLWebString();
		assertEquals("Must contain state away", 0,
				actualAwayOutput.indexOf(State.AWAY.toString()));
		assertTrue(actualAwayOutput.indexOf("last seen") > 0);
	}
}
