package com.polyhedral.security.testing.zedattackproxy.actions.jobs.stop;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.console.ConsolePlugin;

import com.polyhedral.security.testing.zedattackproxy.actions.jobs.CreatePopupMessageJob;
import com.polyhedral.security.testing.zedattackproxy.utils.ZAPHelper;

/**
 * Eclipse {@link Job} for calling ZAP to stop a running ZAP instance.
 */
public class StopZAPJob extends Job {

	/**
	 * Default constructor.
	 * 
	 * @param name
	 *            The name to be displayed in the Eclipse progress view while
	 *            this is executing.
	 */
	public StopZAPJob(String name) {
		super(name);
	}

	/**
	 * Execution of the call to ZAP to stop the running server.
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (ZAPHelper.getInstance().isZapRunning()) {
			try {
				ZAPHelper.getInstance().getZAPClient().core.shutdown(ZAPHelper.getInstance().getZapApiKey());
				while (ZAPHelper.getInstance().isZapRunning()) {
					Thread.sleep(1000); // give server time to stop
				}
			} catch (Exception e) {
				ConsolePlugin.log(e);
			}
		} else {
			CreatePopupMessageJob job = new CreatePopupMessageJob(MessageDialog.ERROR,
					"The ZAP Server is not running.");
			job.setPriority(Job.INTERACTIVE);
			job.schedule();
		}

		return Status.OK_STATUS;
	}
}
