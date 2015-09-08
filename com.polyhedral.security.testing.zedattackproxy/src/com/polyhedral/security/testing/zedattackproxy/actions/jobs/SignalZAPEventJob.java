package com.polyhedral.security.testing.zedattackproxy.actions.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventHandler;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventType;

/**
 * Eclipse {@link UIJob} used to signal a ZAP event has happened in the plugin.
 * This event includes possible updates to the ZAP view action buttons and ZAP
 * view content.
 */
public class SignalZAPEventJob extends UIJob {

	private ZAPEventType event;
	private ZAPEventHandler eventHandler;

	/**
	 * Default constructor.
	 * 
	 * @param jobDisplay
	 *            The {@link Display} where the event will update content.
	 * @param name
	 *            The name to be displayed in the Eclipse progress view while
	 *            this is executing.
	 * @param event
	 *            The signal event that is taking place.
	 * @param eventHandler
	 *            The event handler that will manage the signal event.
	 */
	public SignalZAPEventJob(Display jobDisplay, String name, ZAPEventType event, ZAPEventHandler eventHandler) {
		super(jobDisplay, name);
		this.event = event;
		this.eventHandler = eventHandler;
	}

	/**
	 * Execution of the signal ZAP event job.
	 */
	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		eventHandler.fireZAPEvent(event);
		return Status.OK_STATUS;
	}

}
