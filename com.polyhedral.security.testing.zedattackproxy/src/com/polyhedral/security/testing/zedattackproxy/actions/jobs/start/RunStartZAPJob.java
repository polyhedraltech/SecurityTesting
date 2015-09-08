package com.polyhedral.security.testing.zedattackproxy.actions.jobs.start;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;

import com.polyhedral.security.testing.zedattackproxy.actions.StartZAPAction;
import com.polyhedral.security.testing.zedattackproxy.actions.jobs.SignalZAPEventJob;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventHandler;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventType;

/**
 * Top-level Eclipse {@link Job} called by {@link StartZAPAction}.
 */
public class RunStartZAPJob extends Job {

	private Display display;
	private ZAPEventHandler eventHandler;

	/**
	 * Default constructor.
	 * 
	 * @param name
	 *            The name to be displayed in the Eclipse progress view while
	 *            this is executing.
	 * @param display
	 *            the {@link Display} where this will trigger signal events.
	 * @param eventHandler
	 *            The event handler needed for signal events.
	 */
	public RunStartZAPJob(String name, Display display, ZAPEventHandler eventHandler) {
		super(name);
		this.display = display;
		this.eventHandler = eventHandler;
	}

	/**
	 * Execution of the start ZAP job.
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// Signal that the ZAP start action is starting.
		Job signalZAPStartJob = new SignalZAPEventJob(display, "Signal ZAP Start...", ZAPEventType.SERVER_STARTED,
				eventHandler);
		signalZAPStartJob.setPriority(Job.INTERACTIVE);
		signalZAPStartJob.schedule();

		// Run the start ZAP job.
		Job startZAPJob = new StartZAPJob("Running ZAP Start Job...");
		startZAPJob.setPriority(Job.LONG);
		startZAPJob.schedule();
		while (startZAPJob.getResult() == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				ConsolePlugin.log(e);
			}
		}

		// Signal that the ZAp start action is complete.
		Job signalZAPStartCompleteJob = new SignalZAPEventJob(display, "ZAP Start Complete",
				ZAPEventType.SERVER_STARTUP_COMPLETE, eventHandler);
		signalZAPStartCompleteJob.setPriority(Job.INTERACTIVE);
		signalZAPStartCompleteJob.schedule();

		return Status.OK_STATUS;
	}
}
