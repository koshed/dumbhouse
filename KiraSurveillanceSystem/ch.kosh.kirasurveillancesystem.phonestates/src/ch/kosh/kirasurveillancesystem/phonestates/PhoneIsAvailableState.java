package ch.kosh.kirasurveillancesystem.phonestates;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.SimpleTimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PhoneIsAvailableState {
	private static final Logger log4j = LogManager
			.getLogger(PhoneIsAvailableState.class.getName());
	private static final long EXTENDED_AWAY_TIMEOUT = 1000 * 60;

	public enum State {
		UNKNOWN, AVAILABLE, AWAY
	}

	private State myState;
	private String macAddress;
	private String phoneOwnerName;
	private long lastSeenTimestamp;
	private boolean extendedAwayState;
	private List<ExtendedAwayStateLogEntry> extendedAwayStateLog;

	public PhoneIsAvailableState(String macAddress, String phoneOwnerName) {
		this.setMacAddress(macAddress);
		this.phoneOwnerName = phoneOwnerName;
		this.myState = State.UNKNOWN;
		this.lastSeenTimestamp = -1;
		this.extendedAwayState = false;
		extendedAwayStateLog = new ArrayList<ExtendedAwayStateLogEntry>();
	}

	public String updateState(State newState) throws IOException,
			InterruptedException {
		if (newState == State.AVAILABLE && extendedAwayState) {
			removeExtendedAwayState();
		}

		String stateChangeText = null;
		if (newState != myState) {
			stateChangeText = stateChanged(newState);

		}

		if (newState == State.AWAY) {
			detectExtendedAwayState();
		}

		// log4j.trace(macAddress + " " + phoneOwnerName + " "
		// + " already in this state: " + myState);
		return stateChangeText;
	}

	private void removeExtendedAwayState() throws IOException,
			InterruptedException {
		extendedAwayState = false;
		lastSeenTimestamp = System.currentTimeMillis();
		addExtendedAwayLog(System.currentTimeMillis(), false);
	}

	private void detectExtendedAwayState() throws IOException,
			InterruptedException {
		if (!extendedAwayState) {
			long nowMillis = System.currentTimeMillis();
			long awayTime = nowMillis - lastSeenTimestamp;
			if (awayTime > EXTENDED_AWAY_TIMEOUT) {
				setExtendedAway(nowMillis);
			}
		}
	}

	private String stateChanged(State newState) {
		if (newState == State.AWAY) {
			this.lastSeenTimestamp = System.currentTimeMillis();
		}

		String stateChangeText = formatStateChangeText(newState == State.AVAILABLE);
		log4j.trace(stateChangeText);
		this.myState = newState;
		return stateChangeText;
	}

	private String formatStateChangeText(boolean nowAvailable) {
		StringBuilder sb = new StringBuilder();

		String date = formatTimeMillisToDate(System.currentTimeMillis());

		sb.append(date);
		sb.append(": ");

		sb.append(phoneOwnerName);
		sb.append(" has ");
		if (nowAvailable) {
			sb.append("entered");
		} else {
			sb.append("left");
		}
		sb.append(" the building.");
		return sb.toString();
	}

	public static String formatTimeMillisToDate(final long timeMillis) {
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		simpleDateFormat
				.setTimeZone(new SimpleTimeZone(3600000, "Europe/Paris"));
		return simpleDateFormat.format(timeMillis);
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public State getMyState() {
		return this.myState;
	}

	public String toHTMLWebString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.myState.name());
		if (myState == State.AWAY) {
			sb.append(", last seen ");
			sb.append(formatTimeMillisToDate(lastSeenTimestamp));
			sb.append(".");
		}
		return sb.toString();
	}

	public String getExtendedAwayLogAsHTML() {
		String html = "";
		for (int i = extendedAwayStateLog.size() - 1; i >= 0; i--) {
			ExtendedAwayStateLogEntry entry = extendedAwayStateLog.get(i);
			html += entry.toHTMLString() + "<br/>";
		}
		return html;
	}

	public void setExtendedAway(long timestamp) throws IOException,
			InterruptedException {
		if (myState != State.AWAY) {
			throw new RuntimeException(
					"Cannot enter extended away state, since not even away!");
		}

		if (!this.extendedAwayState) {
			// log thiss event
			addExtendedAwayLog(timestamp, true);
			this.extendedAwayState = true;
		}
	}

	private void addExtendedAwayLog(long timestamp, boolean isAwayMode) {
		ExtendedAwayStateLogEntry extendedAwayStateLogEntry = new ExtendedAwayStateLogEntry(
				timestamp, this.phoneOwnerName, isAwayMode);
		this.extendedAwayStateLog.add(extendedAwayStateLogEntry);
		log4j.trace("Extended Away event created: "
				+ extendedAwayStateLogEntry.toString());
		while (this.extendedAwayStateLog.size() > 20) {
			this.extendedAwayStateLog.remove(0);
		}

	}

	public boolean isExtendedAway() {
		return this.extendedAwayState;
	}

}
