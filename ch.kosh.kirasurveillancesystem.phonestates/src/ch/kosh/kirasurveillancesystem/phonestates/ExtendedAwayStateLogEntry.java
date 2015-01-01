package ch.kosh.kirasurveillancesystem.phonestates;

public class ExtendedAwayStateLogEntry {

	private long timestamp;
	private String eventOwnerName;

	public ExtendedAwayStateLogEntry(long timestamp, String eventOwnerName) {
		this.timestamp = timestamp;
		this.eventOwnerName = eventOwnerName;
	}

	public String toHTMLString() {
		StringBuilder html = new StringBuilder();
		html.append(PhoneIsAvailableState.formatTimeMillisToDate(timestamp));
		html.append(": ");
		html.append(eventOwnerName);
		html.append(" is extended away.");
		return html.toString();
	}

}
