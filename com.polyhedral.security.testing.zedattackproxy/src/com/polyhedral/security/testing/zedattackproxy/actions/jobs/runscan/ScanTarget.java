package com.polyhedral.security.testing.zedattackproxy.actions.jobs.runscan;

import java.util.ArrayList;
import java.util.List;

import com.polyhedral.security.testing.zedattackproxy.views.ZAPPolicyEditor;

/**
 * ZAP scan target information.
 */
public class ScanTarget {
	private String fileName;
	private String targetUrl;
	private String reportFormat;
	private List<ZAPPolicyInfo> zapPolicyList;

	public ScanTarget(String fileName, String targetUrl, String reportFormat, List<ZAPPolicyEditor> zapPolicyList) {
		this.fileName = fileName;
		this.targetUrl = targetUrl;
		this.reportFormat = reportFormat;
		this.zapPolicyList = new ArrayList<ZAPPolicyInfo>();
		for (ZAPPolicyEditor zapPolicy : zapPolicyList) {
			this.zapPolicyList.add(new ZAPPolicyInfo(zapPolicy.getPolicyId(), zapPolicy.getSelectedAttackStrength(),
					zapPolicy.getSelectedAlertThreshold()));
		}
	}

	public String getFileName() {
		return this.fileName;
	}

	public String getTargetUrl() {
		return this.targetUrl;
	}

	public String getReportFormat() {
		return this.reportFormat;
	}

	public List<ZAPPolicyInfo> getZapPolicyList() {
		return this.zapPolicyList;
	}
}
