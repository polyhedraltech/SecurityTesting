package com.polyhedral.security.testing.zedattackproxy.actions.jobs.runscan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.zaproxy.clientapi.core.ApiResponseSet;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import com.polyhedral.security.testing.zedattackproxy.actions.ScanProgress;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventHandler;
import com.polyhedral.security.testing.zedattackproxy.utils.ZAPHelper;

/**
 * Eclipse {@link Job} for calling ZAP to perform an ascan.
 */
public class ZAPAscanJob extends Job {

	public static final String HIGH_RISK = "HIGH";
	public static final String MEDIUM_RISK = "MEDIUM";
	public static final String LOW_RISK = "LOW";
	public static final String OTHER_RISK = "OTHER";

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
	 *            The target information for the ascan.
	 * @param display
	 *            The {@link Display} where this will trigger signal events.
	 * @param scanProgress
	 *            The progress tracker of the current ascan.
	 * @param eventHandler
	 *            The event handler needed for signal events.
	 * @param scanStatus
	 *            The status for the current scan. Indicates if the scan has
	 *            been cancelled before it completes execution.
	 */
	public ZAPAscanJob(String name, ScanTarget target, Display display, ScanProgress scanProgress,
			ZAPEventHandler eventHandler, ScanStatus scanStatus) {
		super(name);
		this.target = target;
		this.display = display;
		this.scanProgress = scanProgress;
		this.eventHandler = eventHandler;
		this.scanStatus = scanStatus;
	}

	/**
	 * Execution of the ZAP ascan job.
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IStatus runStatus = Status.OK_STATUS;

		try {
			// Perform Ascan of URL.
			ClientApi zapAPI = ZAPHelper.getInstance().getZAPClient();

			ApiResponse ascanResponse = zapAPI.ascan.scan(ZAPHelper.getInstance().getZapApiKey(), target.getTargetUrl(),
					"True", "False", "", "", "");
			String ascanScanID = ((ApiResponseElement) ascanResponse).getValue();
			scanProgress.setAscanId(ascanScanID);
			while (true) {
				// While the ascan is progressing, send progress updates to the
				// ZAP view.
				int progress = Integer.parseInt(((ApiResponseElement) zapAPI.ascan.status(ascanScanID)).getValue());
				if (scanStatus.isScanCancelled() || progress >= 100) {
					break;
				} else {
					Job scanProgressJob = new ZAPScanProgressJob(display, "Spider Progress", scanProgress,
							eventHandler);
					scanProgressJob.setPriority(Job.INTERACTIVE);
					scanProgressJob.schedule();

					Thread.sleep(500);
				}
			}

			Thread.sleep(5000); // give Ascan scanner time to complete
								// logging

			// Store Ascan results in project file.
			if (!scanStatus.isScanCancelled()) {
				ZAPScanHelper.generateAscanReport(target.getFileName(), target.getReportFormat(),
						sortAscanResults((ApiResponseList) zapAPI.core.alerts(target.getTargetUrl(), "", "")));
			}

		} catch (ClientApiException | InterruptedException | CoreException e) {
			ConsolePlugin.log(e);
			runStatus = Status.CANCEL_STATUS;
		}

		return runStatus;
	}

	/**
	 * Sort ascan results into maps based on the priority of findings.
	 * 
	 * @param ascanResults
	 *            The ZAP ascan results.
	 * @return A map of ascan results sorted by priority.
	 */
	private Map<String, List<ApiResponseSet>> sortAscanResults(ApiResponseList ascanResults) {
		Map<String, List<ApiResponseSet>> riskMap = new HashMap<>();
		riskMap.put(HIGH_RISK, new ArrayList<ApiResponseSet>());
		riskMap.put(MEDIUM_RISK, new ArrayList<ApiResponseSet>());
		riskMap.put(LOW_RISK, new ArrayList<ApiResponseSet>());
		riskMap.put(OTHER_RISK, new ArrayList<ApiResponseSet>());

		// Sort the report findings based on risk.
		for (ApiResponse responseItem : ascanResults.getItems()) {
			ApiResponseSet responseSet = (ApiResponseSet) responseItem;
			switch (responseSet.getAttribute("risk")) {
			case "High":
				riskMap.get(HIGH_RISK).add(responseSet);
				break;
			case "Medium":
				riskMap.get(MEDIUM_RISK).add(responseSet);
				break;
			case "Low":
				riskMap.get(LOW_RISK).add(responseSet);
				break;
			default:
				riskMap.get(OTHER_RISK).add(responseSet);
				break;
			}
		}

		return riskMap;
	}
}
