package ch.kosh.kirasurveillancesystem.phonestates;

public class ExtendedAwayStateLogEntry {

	private long timestamp;
	private String eventOwnerName;
	private boolean changeToAway;

	public ExtendedAwayStateLogEntry(long timestamp, String eventOwnerName,
			boolean changeToAway) {
		this.timestamp = timestamp;
		this.eventOwnerName = eventOwnerName;
		this.changeToAway = changeToAway;
	}

	public String toHTMLString() {
		return formatToString();
	}

	@Override
	public String toString() {
		return formatToString();
	}

	private String formatToString() {
		StringBuilder toStringText = new StringBuilder();
		toStringText.append(PhoneIsAvailableState
				.formatTimeMillisToDate(timestamp));
		toStringText.append(": ");
		toStringText.append(eventOwnerName);
		toStringText.append(" is ");
		if (changeToAway) {
			toStringText.append("extended away");
		} else {
			toStringText.append("back");
		}
		toStringText.append(".");
		return toStringText.toString();
	}
}
