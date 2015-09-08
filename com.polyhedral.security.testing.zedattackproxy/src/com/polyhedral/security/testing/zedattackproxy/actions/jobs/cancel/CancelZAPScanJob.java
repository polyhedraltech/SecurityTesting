package com.polyhedral.security.testing.zedattackproxy.actions.jobs.cancel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.ConsolePlugin;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import com.polyhedral.security.testing.zedattackproxy.utils.ZAPHelper;

/**
 * Eclipse {@link Job} for calling ZAp to cancel a running ZAP scan.
 */
public class CancelZAPScanJob extends Job {

	/**
	 * Default constructor.
	 * 
	 * @param name
	 *            The name to be displayed in the Eclipse progress view while
	 *            this is executing.
	 */
	public CancelZAPScanJob(String name) {
		super(name);
	}

	/**
	 * Execution of the call to ZAP to stop the current scan.
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			ClientApi zapAPI = ZAPHelper.getInstance().getZAPClient();

			/*
			 * TODO [On Hold] There is a defect in ZAP 2.4.1 where calling
			 * stopAllScans on the spider process locks up ZAP if there is an
			 * active spider. Disable this feature until this is resolved.
			 */
			// zapAPI.spider.stopAllScans(ZAPHelper.getInstance().getZapApiKey());

			zapAPI.ascan.stopAllScans(ZAPHelper.getInstance().getZapApiKey());
			Thread.sleep(5000); // give the scans time to stop
		} catch (ClientApiException | InterruptedException e) {
			ConsolePlugin.log(e);
		}

		return Status.OK_STATUS;
	}
}
