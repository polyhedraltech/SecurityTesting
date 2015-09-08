package com.polyhedral.security.testing.zedattackproxy.actions.jobs.runscan;

/**
 * The status of the current ZAP scan.
 */
public class ScanStatus {

	private boolean scanIsCancelled = false;

	public boolean isScanCancelled() {
		return scanIsCancelled;
	}

	public void cancelScan() {
		scanIsCancelled = true;
	}

}
