package com.polyhedral.security.testing.zedattackproxy.actions.jobs.runscan;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.owasp.encoder.Encode;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ApiResponseSet;

/**
 * Helper class for a ZAP scan. Provides interaction with Eclipse for creating
 * an output project and log files for ZAP scans.
 */
public class ZAPScanHelper {

	/**
	 * Determine if any files exist in the ZAP scan log folder for the specified
	 * file name.
	 * 
	 * @param fileName
	 *            The file name to check for existence.
	 * @return True of any files exist, false otherwise.
	 * @throws CoreException
	 */
	public static boolean scanFilesExist(String fileName) throws CoreException {
		IFolder zapFolder = openZAPScanResultsFolder(false);
		if (zapFolder != null) {
			return zapFolder.getFile(getSpiderFileName(fileName)).exists()
					|| zapFolder.getFile(getAscanHtmlFileName(fileName)).exists()
					|| zapFolder.getFile(getAscanXmlFileName(fileName)).exists();
		}

		return false;
	}

	/**
	 * Generate a report file for the results of a ZAP spider.
	 * 
	 * @param fileName
	 *            The file to write the report to.
	 * @param targetUrl
	 *            The URL that was used to start the spider.
	 * @param spiderResponse
	 *            The results of the spider from ZAP.
	 * @throws CoreException
	 */
	public static void generateSpiderReport(String fileName, String targetUrl, ApiResponseList spiderResponse)
			throws CoreException {
		// Get the file to write the report to.
		IFile file = getSpiderFile(fileName);
		if (file.exists()) {
			file.delete(false, false, null);
		}

		// Generate the log file.
		file.create(new ByteArrayInputStream(generateSpiderReport(targetUrl, spiderResponse)), IResource.NONE, null);

		// Refresh the ZAP scan folder to eliminate some Eclipse synchronization
		// issues.
		openZAPScanResultsFolder(true).refreshLocal(IFolder.DEPTH_ZERO, null);
	}

	/**
	 * Generate a report for the results of a ZAP ascan.
	 * 
	 * @param fileName
	 *            The file to write the report to.
	 * @param reportFormat
	 *            The format of the report. Currently XML and HTML are
	 *            supported.
	 * @param riskMap
	 *            The results of the ascan from ZAP, sorted based on risk level.
	 * @throws CoreException
	 */
	public static void generateAscanReport(String fileName, String reportFormat,
			Map<String, List<ApiResponseSet>> riskMap) throws CoreException {
		if ("HTML".equals(reportFormat)) {
			// Get the HTML file to write the report to.
			IFile file = getAscanHtmlFile(fileName);
			if (file.exists()) {
				file.delete(false, false, null);
			}

			// Generate the HTML file content.
			file.create(new ByteArrayInputStream(new String("<html><body>\n").getBytes()), IResource.NONE, null);

			generateHtmlEntries(file, riskMap.get(ZAPAscanJob.HIGH_RISK));
			generateHtmlEntries(file, riskMap.get(ZAPAscanJob.MEDIUM_RISK));
			generateHtmlEntries(file, riskMap.get(ZAPAscanJob.LOW_RISK));
			generateHtmlEntries(file, riskMap.get(ZAPAscanJob.OTHER_RISK));

			file.appendContents(new ByteArrayInputStream(new String("</body></html>").getBytes()), IFile.KEEP_HISTORY,
					null);
		} else if ("XML".equals(reportFormat)) {
			// get the XML file to write the report to.
			IFile file = getAscanXmlFile(fileName);
			if (file.exists()) {
				file.delete(false, false, null);
			}

			// Generate the XML file content.
			file.create(new ByteArrayInputStream(new String("<?xml version=\"1.0\"?>\n<ScanResults>\n").getBytes()),
					IResource.NONE, null);

			if (riskMap.get(ZAPAscanJob.HIGH_RISK).size() > 0) {
				file.appendContents(new ByteArrayInputStream(new String("<HighRisk>\n").getBytes()), IResource.NONE,
						null);
				generateXmlEntries(file, riskMap.get(ZAPAscanJob.HIGH_RISK));
				file.appendContents(new ByteArrayInputStream(new String("</HighRisk>\n").getBytes()), IResource.NONE,
						null);
			}
			if (riskMap.get(ZAPAscanJob.MEDIUM_RISK).size() > 0) {
				file.appendContents(new ByteArrayInputStream(new String("<MediumRisk>\n").getBytes()), IResource.NONE,
						null);
				generateXmlEntries(file, riskMap.get(ZAPAscanJob.MEDIUM_RISK));
				file.appendContents(new ByteArrayInputStream(new String("</MediumRisk>\n").getBytes()), IResource.NONE,
						null);
			}
			if (riskMap.get(ZAPAscanJob.LOW_RISK).size() > 0) {
				file.appendContents(new ByteArrayInputStream(new String("<LowRisk>\n").getBytes()), IResource.NONE,
						null);
				generateXmlEntries(file, riskMap.get(ZAPAscanJob.LOW_RISK));
				file.appendContents(new ByteArrayInputStream(new String("</LowRisk>\n").getBytes()), IResource.NONE,
						null);
			}
			if (riskMap.get(ZAPAscanJob.OTHER_RISK).size() > 0) {
				file.appendContents(new ByteArrayInputStream(new String("<Other>\n").getBytes()), IResource.NONE, null);
				generateXmlEntries(file, riskMap.get(ZAPAscanJob.OTHER_RISK));
				file.appendContents(new ByteArrayInputStream(new String("</Other>\n").getBytes()), IResource.NONE,
						null);
			}

			file.appendContents(new ByteArrayInputStream(new String("</ScanResults>").getBytes()), IFile.KEEP_HISTORY,
					null);
		}

		// Refresh the ZAP scan folder to eliminate some Eclipse synchronization
		// issues.
		openZAPScanResultsFolder(true).refreshLocal(IFolder.DEPTH_ZERO, null);
	}

	/**
	 * Get the spider file.
	 * 
	 * @param fileName
	 * @return
	 * @throws CoreException
	 */
	private static IFile getSpiderFile(String fileName) throws CoreException {
		return openZAPScanResultsFolder(true).getFile(getSpiderFileName(fileName));
	}

	/**
	 * Get the full name of the spider file.
	 * 
	 * @param fileName
	 * @return
	 */
	private static String getSpiderFileName(String fileName) {
		return fileName + "-SpiderResults.txt";
	}

	/**
	 * Get the ascan HTML file.
	 * 
	 * @param fileName
	 * @return
	 * @throws CoreException
	 */
	private static IFile getAscanHtmlFile(String fileName) throws CoreException {
		return openZAPScanResultsFolder(true).getFile(getAscanHtmlFileName(fileName));
	}

	/**
	 * Get the full name of the ascan HTML file.
	 * 
	 * @param fileName
	 * @return
	 */
	private static String getAscanHtmlFileName(String fileName) {
		return fileName + "-AscanResults.html";
	}

	/**
	 * Get the ascan XML file.
	 * 
	 * @param fileName
	 * @return
	 * @throws CoreException
	 */
	private static IFile getAscanXmlFile(String fileName) throws CoreException {
		return openZAPScanResultsFolder(true).getFile(getAscanXmlFileName(fileName));
	}

	/**
	 * Get the full name of the ascan XML file.
	 * 
	 * @param fileName
	 * @return
	 */
	private static String getAscanXmlFileName(String fileName) {
		return fileName + "-AscanResults.xml";
	}

	/**
	 * Generate the contents of the spider report.
	 * 
	 * @param targetUrl
	 * @param spiderResponse
	 * @return
	 */
	private static byte[] generateSpiderReport(String targetUrl, ApiResponseList spiderResponse) {
		StringBuilder report = new StringBuilder();

		report.append("Target URL: ").append(targetUrl).append("\n\n");

		// Display urls in scope.
		ApiResponseList inScope = (ApiResponseList) spiderResponse.getItems().get(0);
		report.append("URLs In Scope\n");

		// Loop through in scope products.
		for (ApiResponse scopeEntry : inScope.getItems()) {
			ApiResponseSet responseSet = (ApiResponseSet) scopeEntry;
			report.append("\tURL: ").append(responseSet.getAttribute("url")).append("\n");
			report.append("\tRequest Info:\n");
			report.append("\t\tMethod: ").append(responseSet.getAttribute("method")).append("\n");
			report.append("\t\tStatus: ").append(responseSet.getAttribute("statusReason")).append("\n\n");
		}

		// Display urls out of scope.
		ApiResponseList outOfScope = (ApiResponseList) spiderResponse.getItems().get(1);
		report.append("URLs Out of Scope\n");

		// Loop through out of scope products.
		for (ApiResponse outOfScopeEntry : outOfScope.getItems()) {
			ApiResponseElement responseElement = (ApiResponseElement) outOfScopeEntry;
			report.append("\tURL: ").append(responseElement.getValue()).append("\n");
		}

		return report.toString().getBytes();
	}

	/**
	 * Open the Security Testing project where all scan results are stored.
	 * 
	 * @param forceCreation
	 * @return
	 * @throws CoreException
	 */
	private static IProject openSecurityTestingProject(boolean forceCreation) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject("SecurityTesting");
		if (!project.exists()) {
			if (forceCreation) {
				project.create(null);
			} else {
				return null;
			}
		}

		if (!project.isOpen()) {
			project.open(null);
		}

		return project;
	}

	/**
	 * Open the ZAP scan results folder in the Security Testing project.
	 * 
	 * @param forceCreation
	 * @return
	 * @throws CoreException
	 */
	private static IFolder openZAPScanResultsFolder(boolean forceCreation) throws CoreException {
		IProject project = openSecurityTestingProject(forceCreation);
		if (project != null) {
			IFolder zapFolder = project.getFolder("ZAPScanResults");
			if (!zapFolder.exists()) {
				zapFolder.create(false, false, null);
			}

			return zapFolder;
		}

		return null;
	}

	/**
	 * Generate the HTML entries for an ascan report. As this report data may be
	 * opened in a browser, the report data is being HTML encoded for safety.
	 * 
	 * @param reportFile
	 * @param alertsResponseList
	 * @throws CoreException
	 */
	private static void generateHtmlEntries(IFile reportFile, List<ApiResponseSet> alertsResponseList)
			throws CoreException {
		StringBuilder responseData = new StringBuilder();
		int counter = 0;
		for (ApiResponseSet responseSet : alertsResponseList) {
			responseData.append("<table border=1>\n");
			responseData.append("<tr><td>URL:</td><td>").append(Encode.forHtml(responseSet.getAttribute("url")))
					.append("</td></tr>\n");
			responseData.append("<tr><td>Risk:</td><td>").append(Encode.forHtml(responseSet.getAttribute("risk")))
					.append("</td></tr>\n");
			responseData.append("<tr><td>Confidence:</td><td>")
					.append(Encode.forHtml(responseSet.getAttribute("confidence"))).append("</td></tr>\n");
			responseData.append("<tr><td>Alert:</td><td>").append(Encode.forHtml(responseSet.getAttribute("alert")))
					.append("</td></tr>\n");
			responseData.append("<tr><td>Attack:</td><td>").append(Encode.forHtml(responseSet.getAttribute("attack")))
					.append("</td></tr>\n");
			responseData.append("<tr><td>Description:</td><td>")
					.append(Encode.forHtml(responseSet.getAttribute("description"))).append("</td></tr>\n");
			responseData.append("<tr><td>Solution:</td><td>")
					.append(Encode.forHtml(responseSet.getAttribute("solution"))).append("</td></tr>\n");
			responseData.append("</table><br/><br/>\n");

			if (counter == 100) {
				reportFile.appendContents(new ByteArrayInputStream(responseData.toString().getBytes()),
						IFile.KEEP_HISTORY, null);
				responseData = new StringBuilder();
				counter = 0;
			} else {
				counter++;
			}
		}

		reportFile.appendContents(new ByteArrayInputStream(responseData.toString().getBytes()), IFile.KEEP_HISTORY,
				null);
	}

	/**
	 * Generate the XML entries for an ascan report. As this report data may be
	 * parsed by an XML reader, the report data is being XML encoded for safety.
	 * 
	 * @param reportFile
	 * @param alertsResponseList
	 * @throws CoreException
	 */
	private static void generateXmlEntries(IFile reportFile, List<ApiResponseSet> alertsResponseList)
			throws CoreException {
		StringBuilder responseData = new StringBuilder();
		int counter = 0;
		for (ApiResponseSet responseSet : alertsResponseList) {
			responseData.append("<Finding>\n");
			responseData.append("<Alert>").append(Encode.forXml(responseSet.getAttribute("alert")))
					.append("</Alert>\n");
			responseData.append("<Attack>").append(Encode.forXml(responseSet.getAttribute("attack")))
					.append("</Attack>\n");
			responseData.append("<Confidence>").append(Encode.forXml(responseSet.getAttribute("confidence")))
					.append("</Confidence>\n");
			responseData.append("<URL>").append(Encode.forXml(responseSet.getAttribute("url"))).append("</URL>\n");
			responseData.append("<Description>").append(Encode.forXml(responseSet.getAttribute("description")))
					.append("</Description>\n");
			responseData.append("<Solution>").append(Encode.forXml(responseSet.getAttribute("solution")))
					.append("</Solution>\n");
			responseData.append("</Finding>\n");

			if (counter == 100) {
				reportFile.appendContents(new ByteArrayInputStream(responseData.toString().getBytes()),
						IFile.KEEP_HISTORY, null);
				responseData = new StringBuilder();
				counter = 0;
			} else {
				counter++;
			}
		}
		reportFile.appendContents(new ByteArrayInputStream(responseData.toString().getBytes()), IFile.KEEP_HISTORY,
				null);
	}

}
