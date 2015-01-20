package ch.kosh.kirasurveillancesystem.phonestates;

public abstract class StateLogEntry {
	
	final String LOGENTRYENDCHARS = ".";
	
	long timestamp;

	public StateLogEntry(long eventTimestamp) {
		this.timestamp = eventTimestamp;
	}

	public String toHTMLString() {
		return formatToString();
	}

	public String toString() {
		return formatToString();
	}
	

	String timestampToString() {
		StringBuilder sb = new StringBuilder();
		sb.append(PhoneIsAvailableState
				.formatTimeMillisToDate(timestamp));
		sb.append(": ");
		return sb.toString();
	}

	abstract String formatToString();

}
