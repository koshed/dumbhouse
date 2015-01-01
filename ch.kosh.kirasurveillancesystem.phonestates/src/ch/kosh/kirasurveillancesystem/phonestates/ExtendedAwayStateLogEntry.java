package ch.kosh.kirasurveillancesystem.phonestates;

public class ExtendedAwayStateLogEntry {

	private long timestamp;
	private String eventOwnerName;
	private boolean changeToAway;

	public ExtendedAwayStateLogEntry(long timestamp, String eventOwnerName, boolean changeToAway) {
		this.timestamp = timestamp;
		this.eventOwnerName = eventOwnerName;
		this.changeToAway = changeToAway;
	}

	public String toHTMLString() {
		StringBuilder html = new StringBuilder();
		html.append(PhoneIsAvailableState.formatTimeMillisToDate(timestamp));
		html.append(": ");
		html.append(eventOwnerName);
		html.append(" is ");
		if (changeToAway) {
			html.append("extended away");
		}
		else{
			html.append("back");
		}
		html.append(".");
		return html.toString();
	}
}
