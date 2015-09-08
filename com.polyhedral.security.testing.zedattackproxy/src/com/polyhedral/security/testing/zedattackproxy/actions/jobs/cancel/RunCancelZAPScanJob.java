package com.polyhedral.security.testing.zedattackproxy.actions.jobs.cancel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;

import com.polyhedral.security.testing.zedattackproxy.actions.CancelZAPScanAction;
import com.polyhedral.security.testing.zedattackproxy.actions.jobs.SignalZAPEventJob;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventHandler;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventType;

/**
 * Top-level Eclipse {@link Job} called by {@link CancelZAPScanAction}.
 */
public class RunCancelZAPScanJob extends Job {

	private Display display;
	private ZAPEventHandler eventHandler;

	/**
	 * Default constructor.
	 * 
	 * @param name
	 *            The name to be displayed in the Eclipse progress view while
	 *            this is executing.
	 * @param display
	 *            The {@link Display} where this will trigger signal events.
	 * @param eventHandler
	 *            The event handler needed for signal events.
	 */
	public RunCancelZAPScanJob(String name, Display display, ZAPEventHandler eventHandler) {
		super(name);
		this.display = display;
		this.eventHandler = eventHandler;
	}

	/**
	 * Execution of the cancel ZAP scan job.
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// Signal that the cancel ZAP scan action is starting.
		Job signalCancelJob = new SignalZAPEventJob(display, "Signal Scan Cancel", ZAPEventType.SCAN_CANCEL_STARTED,
				eventHandler);
		signalCancelJob.setPriority(Job.INTERACTIVE);
		signalCancelJob.schedule();

		// Cancel the current ZAP scan.
		Job cancelZapScanJob = new CancelZAPScanJob("Cancelling ZAP Scan...");
		cancelZapScanJob.setPriority(Job.LONG);
		cancelZapScanJob.schedule();
		while (cancelZapScanJob.getResult() == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				ConsolePlugin.log(e);
			}
		}

		// Signal that the cancel ZAP scan action is complete.
		Job signalCancelCompleteJob = new SignalZAPEventJob(display, "Signal Scan Cancel Complete",
				ZAPEventType.SCAN_CANCEL_COMPLETE, eventHandler);
		signalCancelCompleteJob.setPriority(Job.INTERACTIVE);
		signalCancelCompleteJob.schedule();

		return Status.OK_STATUS;
	}
}
