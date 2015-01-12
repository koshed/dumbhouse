package ch.kosh.kirasurveillancesystem.phonestates;

import ch.kosh.kirasurveillancesystem.phonestates.PhoneScanner.KiraState;

public class KiraStateLogEntry extends StateLogEntry {
	private KiraState state;

	public KiraStateLogEntry(long timestamp, KiraState newKiraState) {
		super(timestamp);
		this.state = newKiraState;
	}

	@Override
	String formatToString() {
		StringBuilder toStringText = new StringBuilder();
		toStringText.append(timestampToString());
		toStringText.append(state.toString());
		toStringText.append(LOGENTRYENDCHARS);
		return toStringText.toString();
	}

}
