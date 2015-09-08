package com.polyhedral.security.testing.zedattackproxy.actions.jobs.stop;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;

import com.polyhedral.security.testing.zedattackproxy.actions.StopZAPAction;
import com.polyhedral.security.testing.zedattackproxy.actions.jobs.SignalZAPEventJob;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventHandler;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventType;

/**
 * Top-level Eclipse {@link Job} called by {@link StopZAPAction}.
 */
public class RunZAPStopJob extends Job {

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
	public RunZAPStopJob(String name, Display display, ZAPEventHandler eventHandler) {
		super(name);
		this.display = display;
		this.eventHandler = eventHandler;
	}

	/**
	 * Execution of the stop ZAP job.
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// Signal that the ZAP stop action is starting.
		Job signalZAPStopJob = new SignalZAPEventJob(display, "Signal ZAP Stop...", ZAPEventType.SERVER_STOP_REQUESTED,
				eventHandler);
		signalZAPStopJob.setPriority(Job.INTERACTIVE);
		signalZAPStopJob.schedule();

		// Run the stop ZAP job.
		Job stopZAPJob = new StopZAPJob("Running ZAP Stop Job...");
		stopZAPJob.setPriority(Job.LONG);
		stopZAPJob.schedule();
		while (stopZAPJob.getResult() == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				ConsolePlugin.log(e);
			}
		}

		// Signal that the ZAP stop action is complete.
		Job signalZAPStopCompleteJob = new SignalZAPEventJob(display, "ZAP Stop Complete", ZAPEventType.SERVER_STOPPED,
				eventHandler);
		signalZAPStopCompleteJob.setPriority(Job.INTERACTIVE);
		signalZAPStopCompleteJob.schedule();

		return Status.OK_STATUS;
	}
}
