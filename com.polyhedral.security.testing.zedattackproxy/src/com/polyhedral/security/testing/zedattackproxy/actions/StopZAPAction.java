package com.polyhedral.security.testing.zedattackproxy.actions;

import java.net.URL;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;

import com.polyhedral.security.testing.zedattackproxy.actions.jobs.stop.RunZAPStopJob;

/**
 * Eclipse {@link Action} for stopping the ZAP server.
 */
public class StopZAPAction extends ZAPAction {

	/**
	 * Default constructor. Set the enabled/disabled icons for the action and
	 * the tool tip text.
	 */
	public StopZAPAction() {
		try {
			this.setImageDescriptor(ImageDescriptor.createFromURL(
					new URL("platform:/plugin/com.polyhedral.security.testing.zedattackproxy/icons/enabled/stop.gif")));
			this.setDisabledImageDescriptor(ImageDescriptor.createFromURL(new URL(
					"platform:/plugin/com.polyhedral.security.testing.zedattackproxy/icons/disabled/stop.gif")));
			this.setToolTipText("Stop ZAP Server");
		} catch (Exception e) {
			ConsolePlugin.log(e);
		}
	}

	/**
	 * {@link Job} to be performed when this action is clicked on.
	 */
	@Override
	public void run() {
		Job runZapStopJob = new RunZAPStopJob("Stopping Zed Attack Proxy...", PlatformUI.getWorkbench().getDisplay(),
				getZAPEventHandler());
		runZapStopJob.setPriority(Job.LONG);
		runZapStopJob.schedule();

	}
}
