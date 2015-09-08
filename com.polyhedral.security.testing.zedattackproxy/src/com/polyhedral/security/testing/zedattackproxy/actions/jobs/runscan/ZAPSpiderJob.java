package com.polyhedral.security.testing.zedattackproxy.actions.jobs.runscan;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import com.polyhedral.security.testing.zedattackproxy.actions.ScanProgress;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventHandler;
import com.polyhedral.security.testing.zedattackproxy.utils.ZAPHelper;

/**
 * Eclipse {@link Job} for calling ZAP to perform a spider.
 */
public class ZAPSpiderJob extends Job {

	private ScanTarget target;
	private Display display;
	private ScanProgress scanProgress;
	private ZAPEventHandler eventHandler;
	private ScanStatus scanStatus;

	/**
	 * Default constructor.
	 * 
	 * @param name
	 *            The name to be displayed in the Eclipse progress view while
	 *            this is executing.
	 * @param target
	 *            the target information for the spider.
	 * @param display
	 *            The {@link Display} where this will trigger signal events.
	 * @param scanProgress
	 *            The progress tracker of the current spider.
	 * @param eventHandler
	 *            The event handler needed for signal events.
	 * @param scanStatus
	 *            the status for the current scan. Indicates if the scan has
	 *            been cancelled before it completes execution.
	 */
	public ZAPSpiderJob(String name, ScanTarget target, Display display, ScanProgress scanProgress,
			ZAPEventHandler eventHandler, ScanStatus scanStatus) {
		super(name);
		this.target = target;
		this.display = display;
		this.scanProgress = scanProgress;
		this.eventHandler = eventHandler;
		this.scanStatus = scanStatus;
	}

	/**
	 * Execution of the ZAP spider job.
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IStatus runStatus = Status.OK_STATUS;

		try {
			// Perform spider of URL.
			ClientApi zapAPI = ZAPHelper.getInstance().getZAPClient();

			ApiResponse spiderResponse = zapAPI.spider.scan(ZAPHelper.getInstance().getZapApiKey(),
					target.getTargetUrl(), "100");

			String spiderScanID = ((ApiResponseElement) spiderResponse).getValue();
			scanProgress.setSpiderId(spiderScanID);
			while (true) {
				// While the spider is progressing, send progress updates to the
				// ZAP view.
				Job scanProgressJob = new ZAPScanProgressJob(display, "Spider Progress", scanProgress, eventHandler);
				scanProgressJob.setPriority(Job.INTERACTIVE);
				scanProgressJob.schedule();

				int progress = Integer.parseInt(((ApiResponseElement) zapAPI.spider.status(spiderScanID)).getValue());
				if (scanStatus.isScanCancelled() || progress >= 100) {
					break;
				} else {
					Thread.sleep(500);
				}
			}

			Thread.sleep(5000); // give Spider scanner time to complete
								// logging

			// Store Spider results in a file.
			if (!scanStatus.isScanCancelled()) {
				ZAPScanHelper.generateSpiderReport(target.getFileName(), target.getTargetUrl(),
						(ApiResponseList) zapAPI.spider.fullResults(spiderScanID));
			}

		} catch (ClientApiException | InterruptedException | CoreException e) {
			ConsolePlugin.log(e);
			runStatus = Status.CANCEL_STATUS;
		}

		return runStatus;
	}
}
