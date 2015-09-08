package com.polyhedral.security.testing.zedattackproxy.actions.jobs.runscan;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;

import com.polyhedral.security.testing.zedattackproxy.actions.CancelZAPScanAction;
import com.polyhedral.security.testing.zedattackproxy.actions.RunZAPScanAction;
import com.polyhedral.security.testing.zedattackproxy.actions.ScanProgress;
import com.polyhedral.security.testing.zedattackproxy.actions.jobs.CreatePopupMessageJob;
import com.polyhedral.security.testing.zedattackproxy.actions.jobs.SignalZAPEventJob;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventHandler;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventType;

/**
 * Top-level Eclipse {@link Job} called by {@link RunZAPScanAction}.
 */
public class RunZAPScanJob extends Job {

	private Display display;
	private ScanTarget target;
	private ZAPEventHandler eventHandler;

	private Job spiderJob;
	private Job ascanJob;
	private ScanStatus scanStatus;

	/**
	 * Default constructor.
	 * 
	 * @param name
	 *            The name to be displayed in the Eclipse progress view while
	 *            this is executing.
	 * @param display
	 *            The {@link Display} where this will trigger signal events.
	 * @param target
	 *            The {@link ScanTarget} that is being processed in the ZAP
	 *            ascan.
	 * @param eventHandler
	 *            The event handler for signal events.
	 */
	public RunZAPScanJob(String name, Display display, ScanTarget target, ZAPEventHandler eventHandler) {
		super(name);
		this.display = display;
		this.target = target;
		this.eventHandler = eventHandler;

		scanStatus = new ScanStatus();
	}

	/**
	 * Execution of the run ZAP scan job.
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		ScanProgress scanProgress = new ScanProgress();

		// Make sure that all of the necessary scan parameters have been
		// entered.
		if (StringUtils.isBlank(target.getTargetUrl()) || StringUtils.isBlank(target.getFileName())
				|| StringUtils.isBlank(target.getReportFormat())) {
			CreatePopupMessageJob incompleteParamsWarningJob = new CreatePopupMessageJob(MessageDialog.WARNING,
					"The Scan Target URL, Scan Result File, and Report Format fields are required to run a scan.");
			incompleteParamsWarningJob.setPriority(Job.INTERACTIVE);
			incompleteParamsWarningJob.schedule();
			return Status.CANCEL_STATUS;
		}

		// Give the user a warning if the scan will overwrite an existing scan
		// file.
		try {
			if (ZAPScanHelper.scanFilesExist(target.getFileName())) {
				CreatePopupMessageJob fileOverwriteWarning = new CreatePopupMessageJob(MessageDialog.CONFIRM,
						"The specified Scan Result File matches an existing scan file in the ZAPScanResults folder.  Executing the scan may overwrite existing scan results data.  Do you wish to proceed?");
				fileOverwriteWarning.setPriority(Job.INTERACTIVE);
				fileOverwriteWarning.schedule();
				while (fileOverwriteWarning.getResult() == null) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						ConsolePlugin.log(e);
					}
				}

				if (fileOverwriteWarning.getResult() == Status.CANCEL_STATUS) {
					return Status.CANCEL_STATUS;
				}
			}
		} catch (CoreException e1) {
			ConsolePlugin.log(e1);
		}

		// Signal that the spider has started.
		Job signalZAPSpiderJob = new SignalZAPEventJob(display, "Signal ZAP Spider Started...",
				ZAPEventType.SCAN_SPIDER_STARTED, eventHandler);
		signalZAPSpiderJob.setPriority(Job.INTERACTIVE);
		signalZAPSpiderJob.schedule();

		// Set the attack levels.
		Job attackLevelJob = new ZAPSetZAPLevelsJob("Setting ZAP Attack Levels..", target.getZapPolicyList());
		attackLevelJob.setPriority(LONG);
		attackLevelJob.schedule();

		// Perform a spider of the target URL.
		spiderJob = new ZAPSpiderJob("Running Zed Attack Proxy Spider...", target, display, scanProgress, eventHandler,
				scanStatus);
		spiderJob.setPriority(LONG);
		spiderJob.schedule();
		while (spiderJob.getResult() == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				ConsolePlugin.log(e);
			}
		}

		if (scanStatus.isScanCancelled()) {
			return Status.CANCEL_STATUS;
		}

		// Signal that the spider has started.
		Job signalZAPAscanJob = new SignalZAPEventJob(display, "Signal ZAP Ascan Started...",
				ZAPEventType.SCAN_ASCAN_STARTED, eventHandler);
		signalZAPAscanJob.setPriority(Job.INTERACTIVE);
		signalZAPAscanJob.schedule();

		// Perform an ascan on the target URL.
		ascanJob = new ZAPAscanJob("Running Zed Attack Proxy Ascan...", target, display, scanProgress, eventHandler,
				scanStatus);
		ascanJob.setPriority(LONG);
		ascanJob.schedule();
		while (ascanJob.getResult() == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				ConsolePlugin.log(e);
			}
		}

		if (scanStatus.isScanCancelled()) {
			return Status.CANCEL_STATUS;
		}

		// Clear out scan data after run is complete.
		Job clearZapJob = new ClearZAPJob("Clearing Zed Attack Proxy Run Data...");
		clearZapJob.setPriority(LONG);
		clearZapJob.schedule();
		while (clearZapJob.getResult() == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				ConsolePlugin.log(e);
			}
		}

		// Signal that the scan is complete.
		Job signalZAPScanCompleteJob = new SignalZAPEventJob(display, "Signal ZAP Scan Complete",
				ZAPEventType.SCAN_COMPLETE, eventHandler);
		signalZAPScanCompleteJob.setPriority(Job.INTERACTIVE);
		signalZAPScanCompleteJob.schedule();

		return Status.OK_STATUS;
	}

	/**
	 * Indicator that a {@link CancelZAPScanAction} has been performed. This
	 * signals the run ZAP job that it needs to stop processing.
	 */
	public void cancelScan() {
		scanStatus.cancelScan();
	}
}
