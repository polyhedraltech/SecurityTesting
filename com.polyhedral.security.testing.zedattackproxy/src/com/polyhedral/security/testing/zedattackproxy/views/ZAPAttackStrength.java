package com.polyhedral.security.testing.zedattackproxy.views;

/**
 * Enum of the attack strength options available within ZAP.
 */
public enum ZAPAttackStrength {
	DEFAULT("DEFAULT", 0), 
	LOW("LOW", 1), 
	MEDIUM("MEDIUM", 2), 
	HIGH("HIGH", 3), 
	INSANE("INSANE", 4);

	private final String value;
	private final int rank;

	ZAPAttackStrength(String value, int rank) {
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
