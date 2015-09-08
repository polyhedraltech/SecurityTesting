package com.polyhedral.security.testing.zedattackproxy.actions.jobs.start;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.console.ConsolePlugin;

import com.polyhedral.security.testing.zedattackproxy.actions.jobs.CreatePopupMessageJob;
import com.polyhedral.security.testing.zedattackproxy.utils.ZAPHelper;

/**
 * Eclipse {@link Job} for calling ZAP to start a new ZAP instance.
 */
public class StartZAPJob extends Job {

	/**
	 * Default constructor.
	 * 
	 * @param name
	 *            The name to be displayed in the Eclipse progress view while
	 *            this is executing.
	 */
	public StartZAPJob(String name) {
		super(name);
	}

	/**
	 * Execution of the call to ZAP to start the server.
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (!ZAPHelper.getInstance().isZapRunning()) {

			// Build the UI command needed to start ZAP.
			List<String> commands = new ArrayList<>();
			addZapJavaExec(commands);
			addZapHeadless(commands);
			addZapProxyPort(commands);
			addZapSession(commands);
			addZapApiKey(commands);

			try {
				// Create a new process for executing ZAP.
				ProcessBuilder pb = new ProcessBuilder(commands.toArray(new String[commands.size()]));
				pb.directory(new File(ZAPHelper.getInstance().getZapJarLocation()).getParentFile());
				Process proc = pb.start();
				pipeToConsole(proc.getInputStream());

				while (!ZAPHelper.getInstance().isZapRunning()) {
					Thread.sleep(1000); // give server time to start
				}

			} catch (Exception e) {
				ConsolePlugin.log(e);
			}
		} else {
			CreatePopupMessageJob job = new CreatePopupMessageJob(MessageDialog.ERROR,
					"The ZAP Server is already running.");
			job.setPriority(Job.INTERACTIVE);
			job.schedule();
		}

		return Status.OK_STATUS;
	}

	/**
	 * Set the Java executable and ZAP jar file.
	 * 
	 * @param commands
	 *            The list to add this command to for the {@link ProcessBuilder}
	 *            .
	 */
	private void addZapJavaExec(List<String> commands) {
		commands.add(ZAPHelper.getInstance().getJavaExecutable());
		commands.add(ZAPHelper.JAVA_JAR_FLAG);
		commands.add(ZAPHelper.getInstance().getZapJarLocation());
	}

	/**
	 * Set the flag to mark ZAP to start in headless mode.
	 * 
	 * @param commands
	 *            The list to add this command to for the {@link ProcessBuilder}
	 *            .
	 */
	private void addZapHeadless(List<String> commands) {
		commands.add(ZAPHelper.ZAP_FLAG_DAEMON);
	}

	/**
	 * Set the port to run ZAP on.
	 * 
	 * @param commands
	 *            The list to add this command to for the {@link ProcessBuilder}
	 *            .
	 */
	private void addZapProxyPort(List<String> commands) {
		commands.add(ZAPHelper.ZAP_FLAG_PORT);
		commands.add(Integer.toString(ZAPHelper.getInstance().getZapProxyPort()));
	}

	/**
	 * Set the location where the ZAP session file will be stored.
	 * 
	 * @param commands
	 *            The list to add this command to for the {@link ProcessBuilder}
	 *            .
	 */
	private void addZapSession(List<String> commands) {
		commands.add(ZAPHelper.getInstance().getZapSessionFlag());
		commands.add(ZAPHelper.getInstance().getFullSessionPath());
	}

	/**
	 * Set the ZAP API key.
	 * 
	 * @param commands
	 *            The list to add this command to for the {@link ProcessBuilder}
	 *            .
	 */
	private void addZapApiKey(List<String> commands) {
		commands.add(ZAPHelper.ZAP_FLAG_CONFIG);
		commands.add(ZAPHelper.ZAP_CONFIG_API_KEY + "=" + ZAPHelper.getInstance().getZapApiKey());
	}

	/**
	 * Create a reader for grabbing all process output data from the ZAP
	 * executable and log it to the ZAP Console view.
	 * 
	 * @param stream
	 *            The input stream of the process log.
	 */
	private void pipeToConsole(final InputStream stream) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				BufferedInputStream bis = new BufferedInputStream(stream);
				InputStreamReader inread = new InputStreamReader(bis);
				BufferedReader bufferedReader = new BufferedReader(inread);
				String line;
				try {
					while ((line = bufferedReader.readLine()) != null) {
						ZAPHelper.getInstance().logConsoleMessage(line);
					}
				} catch (IOException e) {
					ConsolePlugin.log(e);
				}
			}
		}).start();
	}
}
