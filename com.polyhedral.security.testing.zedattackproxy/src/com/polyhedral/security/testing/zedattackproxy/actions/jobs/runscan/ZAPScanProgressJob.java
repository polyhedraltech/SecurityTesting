package com.polyhedral.security.testing.zedattackproxy.actions.jobs.runscan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

import com.polyhedral.security.testing.zedattackproxy.actions.ScanProgress;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventHandler;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventType;

/**
 * Eclipse {@link UIJob} for displaying the progress of a ZAP ascan in the ZAP
 * view.
 */
public class ZAPScanProgressJob extends UIJob {

	private ScanProgress scanProgress;
	private ZAPEventHandler eventHandler;

	/**
	 * Default constructor.
	 * 
	 * @param jobDisplay
	 *            The {@link Display} for the UI action.
	 * @param name
	 *            The name to be displayed in the Eclipse progress view while
	 *            this is executing.
	 * @param scanProgress
	 *            The progress information to be reported.
	 * @param eventHandler
	 *            The event handler to handle a progress indicator.
	 */
	public ZAPScanProgressJob(Display jobDisplay, String name, ScanProgress scanProgress,
			ZAPEventHandler eventHandler) {
		super(jobDisplay, name);
		this.scanProgress = scanProgress;
		this.eventHandler = eventHandler;
	}

	/**
	 * Execution of the ZAP ascan progress job.
	 */
	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		eventHandler.fireZAPEvent(ZAPEventType.SCAN_PROGRESS, scanProgress);
		return Status.OK_STATUS;
	}
}
