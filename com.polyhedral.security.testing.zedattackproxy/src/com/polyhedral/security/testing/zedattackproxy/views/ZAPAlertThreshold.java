package com.polyhedral.security.testing.zedattackproxy.views;

/**
 * Enum of the alert threshold options available in ZAP.
 */
public enum ZAPAlertThreshold {
	OFF("OFF", 0), 
	DEFAULT("DEFAULT", 1), 
	LOW("LOW", 2), 
	MEDIUM("MEDIUM", 3), 
	HIGH("HIGH", 4);

	private final String value;
	private final int rank;

	ZAPAlertThreshold(String value, int rank) {
		this.value = value;
		this.rank = rank;
	}

	public String getValue() {
		return this.value;
	}

	public int getRank() {
		return this.rank;
	}
}
