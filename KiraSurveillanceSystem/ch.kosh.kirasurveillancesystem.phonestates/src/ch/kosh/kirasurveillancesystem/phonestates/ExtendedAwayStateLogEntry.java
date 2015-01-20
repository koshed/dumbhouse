package ch.kosh.kirasurveillancesystem.phonestates;

public class ExtendedAwayStateLogEntry extends StateLogEntry{

	private String eventOwnerName;
	private boolean changeToAway;

	public ExtendedAwayStateLogEntry(long timestamp, String eventOwnerName,
			boolean changeToAway) {
		super(timestamp);
		this.eventOwnerName = eventOwnerName;
		this.changeToAway = changeToAway;
	}

	@Override
	String formatToString() {
		StringBuilder toStringText = new StringBuilder();
		toStringText.append(timestampToString());
		toStringText.append(eventOwnerName);
		toStringText.append(" is ");
		if (changeToAway) {
			toStringText.append("extended away");
		} else {
			toStringText.append("back");
		}
		toStringText.append(LOGENTRYENDCHARS);
		return toStringText.toString();
	}
}
