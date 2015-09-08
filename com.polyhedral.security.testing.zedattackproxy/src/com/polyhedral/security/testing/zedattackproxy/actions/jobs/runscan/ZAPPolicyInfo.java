package com.polyhedral.security.testing.zedattackproxy.actions.jobs.runscan;

/**
 * Storage of ZAP policy information.
 */
public class ZAPPolicyInfo {
	private String id;
	private String attackStrength;
	private String alertThreshold;

	public ZAPPolicyInfo(String id, String attackStrength, String alertThreshold) {
		this.id = id;
		this.attackStrength = attackStrength;
		this.alertThreshold = alertThreshold;
	}

	public String getId() {
		return this.id;
	}

	public String getAttackStrength() {
		return this.attackStrength;
	}

	public String getAlertThreshold() {
		return this.alertThreshold;
	}
}
