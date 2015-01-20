package ch.kosh.kirasurveillancesystem.phonestates;

import java.util.ArrayList;
import java.util.List;

public class PhoneStateList {

	public static final String PHONE_SEPARATOR_HTML = "==================";
	private List<PhoneIsAvailableState> phoneStateList;
	private List<String> pingResponses;

	public PhoneStateList() {
		phoneStateList = new ArrayList<PhoneIsAvailableState>();
		this.pingResponses = new ArrayList<String>();
	}

	public void addNewPhone(PhoneIsAvailableState phoneIsAvailableState) {
		this.phoneStateList.add(phoneIsAvailableState);
	}

	public void addPingResponse(String responseText) {
		if (responseText != null) {
			this.pingResponses.add(responseText);
			// memory clean up
			while (this.pingResponses.size() > 10) {
				this.pingResponses.remove(0);
			}
		}
	}

	public List<PhoneIsAvailableState> getAll() {
		return this.phoneStateList;
	}

	public String getLogAsHtML() {
		String html = "";
		for (int i = pingResponses.size() - 1; i >= 0; i--) {
			html += pingResponses.get(i) + "<br/>";
		}
		return html;
	}

	public String getExtendedAwayLogAsHTML() {
		String html = PHONE_SEPARATOR_HTML + "<br/>";
		for (PhoneIsAvailableState phone : phoneStateList) {
			html += phone.getExtendedAwayLogAsHTML() + "<br/>"
					+ PHONE_SEPARATOR_HTML + "<br/>";
		}
		return html;
	}

	public List<String> getPingResponses() {
		return pingResponses;
	}
}
