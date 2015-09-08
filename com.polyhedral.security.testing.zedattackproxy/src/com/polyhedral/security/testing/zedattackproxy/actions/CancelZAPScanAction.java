package com.polyhedral.security.testing.zedattackproxy.actions;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;

import com.polyhedral.security.testing.zedattackproxy.actions.jobs.cancel.RunCancelZAPScanJob;

/**
 * Eclipse {@link Action} for canceling a running ZAP scan.
 */
public class CancelZAPScanAction extends ZAPAction {

	/**
	 * Default constructor. Set the enabled/disabled icons for the action and
	 * the tool tip text.
	 */
	public CancelZAPScanAction() {
		try {
			this.setImageDescriptor(ImageDescriptor.createFromURL(new URL(
					"platform:/plugin/com.polyhedral.security.testing.zedattackproxy/icons/enabled/cancel_scan.gif")));
			this.setDisabledImageDescriptor(ImageDescriptor.createFromURL(new URL(
					"platform:/plugin/com.polyhedral.security.testing.zedattackproxy/icons/disabled/cancel_scan.gif")));
			this.setToolTipText("Cancel ZAP Spider/Ascan");
		} catch (MalformedURLException e) {
			ConsolePlugin.log(e);
		}
	}

	/**
	 * {@link Job} to be performed when this action is clicked on.
	 */
	@Override
	public void run() {
		Job tempJob = new RunCancelZAPScanJob("Cancelling ZAP Scan...", PlatformUI.getWorkbench().getDisplay(),
				getZAPEventHandler());
		tempJob.setPriority(Job.LONG);
		tempJob.schedule();
	}
}
