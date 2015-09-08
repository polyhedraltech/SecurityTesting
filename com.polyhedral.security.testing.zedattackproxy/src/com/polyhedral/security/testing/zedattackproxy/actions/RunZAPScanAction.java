package com.polyhedral.security.testing.zedattackproxy.actions;

import java.net.URL;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;

import com.polyhedral.security.testing.zedattackproxy.actions.jobs.runscan.RunZAPScanJob;
import com.polyhedral.security.testing.zedattackproxy.actions.jobs.runscan.ScanTarget;
import com.polyhedral.security.testing.zedattackproxy.views.ZAPView;

/**
 * Eclipse {@link Action} for starting a new scan in ZAP.
 */
public class RunZAPScanAction extends ZAPAction {

	private ZAPView zapView;
	private RunZAPScanJob runZapScanJob;

	/**
	 * Default constructor. Set the enabled/disabled icons for the action and
	 * the tool tip text.
	 */
	public RunZAPScanAction() {
		try {
			this.setImageDescriptor(ImageDescriptor.createFromURL(new URL(
					"platform:/plugin/com.polyhedral.security.testing.zedattackproxy/icons/enabled/run_scan.gif")));
			this.setDisabledImageDescriptor(ImageDescriptor.createFromURL(new URL(
					"platform:/plugin/com.polyhedral.security.testing.zedattackproxy/icons/disabled/run_scan.gif")));
			this.setToolTipText("Run ZAP Spider/Ascan");
		} catch (Exception e) {
			ConsolePlugin.log(e);
		}
	}

	/**
	 * Set the ZAP view so that it can be interacted with later.
	 * 
	 * @param zapView
	 *            The ZAP view to be set.
	 */
	public void setZapView(ZAPView zapView) {
		this.zapView = zapView;
	}

	/**
	 * {@link Job} to be performed when this action is clicked on.
	 */
	@Override
	public void run() {
		runZapScanJob = new RunZAPScanJob("Run Zed Attack Proxy Scan...", PlatformUI.getWorkbench().getDisplay(),
				new ScanTarget(zapView.getFileNameText(), zapView.getUrlText(), zapView.getReportFormat(),
						zapView.getZapPolicyList()),
				getZAPEventHandler());
		runZapScanJob.setPriority(Job.LONG);
		runZapScanJob.schedule();
	}

	/**
	 * Indicator that the current scan was stopped by the cancel scan action.
	 * Call to the scan job and cancel its operation.
	 */
	public void cancelScan() {
		if (runZapScanJob != null) {
			runZapScanJob.cancelScan();
		}
	}
}
