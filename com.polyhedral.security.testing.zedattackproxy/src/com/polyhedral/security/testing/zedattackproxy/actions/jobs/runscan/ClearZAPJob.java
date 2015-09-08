package com.polyhedral.security.testing.zedattackproxy.actions.jobs.runscan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.ConsolePlugin;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import com.polyhedral.security.testing.zedattackproxy.utils.ZAPHelper;

/**
 * Eclipse {@link Job} for performing a cleanup after a ZAP scan.
 */
public class ClearZAPJob extends Job {

	/**
	 * Default constructor.
	 * 
	 * @param name
	 *            The name to be displayed in the Eclipse progress view while
	 *            this is executing.
	 */
	public ClearZAPJob(String name) {
		super(name);
	}

	/**
	 * Execution of the call to ZAP to clean up after a ZAP scan.
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			ClientApi zapAPI = ZAPHelper.getInstance().getZAPClient();
			zapAPI.spider.removeAllScans(ZAPHelper.getInstance().getZapApiKey());
			zapAPI.ascan.removeAllScans(ZAPHelper.getInstance().getZapApiKey());
			zapAPI.core.deleteAllAlerts(ZAPHelper.getInstance().getZapApiKey());

		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}

		return Status.OK_STATUS;
	}
}
