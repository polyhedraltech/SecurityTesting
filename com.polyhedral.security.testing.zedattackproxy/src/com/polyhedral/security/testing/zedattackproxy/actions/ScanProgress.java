package com.polyhedral.security.testing.zedattackproxy.actions;

/**
 * Progress information for a running ZAP spider/ascan.
 */
public class ScanProgress {
	private String ascanId;
	private String spiderId;

	public String getAscanId() {
		return ascanId;
	}

	public String getSpiderId() {
		return spiderId;
	}

	public void setAscanId(String ascanId) {
		this.ascanId = ascanId;
	}

	public void setSpiderId(String spiderId) {
		this.spiderId = spiderId;
	}
}
