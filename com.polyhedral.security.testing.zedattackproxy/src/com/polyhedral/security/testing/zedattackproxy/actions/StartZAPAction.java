package com.polyhedral.security.testing.zedattackproxy.actions;

import java.net.URL;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;

import com.polyhedral.security.testing.zedattackproxy.actions.jobs.start.RunStartZAPJob;

/**
 * Eclipse {@link Action} for starting the ZAP server.
 */
public class StartZAPAction extends ZAPAction {

	/**
	 * Default constructor. Set the enabled/disabled icons for the action and
	 * the tool tip text.
	 */
	public StartZAPAction() {
		try {
			this.setImageDescriptor(ImageDescriptor.createFromURL(new URL(
					"platform:/plugin/com.polyhedral.security.testing.zedattackproxy/icons/enabled/start.gif")));
			this.setDisabledImageDescriptor(ImageDescriptor.createFromURL(new URL(
					"platform:/plugin/com.polyhedral.security.testing.zedattackproxy/icons/disabled/start.gif")));
			this.setToolTipText("Start ZAP Server");
		} catch (Exception e) {
			ConsolePlugin.log(e);
		}
	}

	/**
	 * {@link Job} to be performed when this action is clicked on.
	 */
	@Override
	public void run() {
		Job runStartZAPJob = new RunStartZAPJob("Starting Zed Attack Proxy...", PlatformUI.getWorkbench().getDisplay(),
				getZAPEventHandler());
		runStartZAPJob.setPriority(Job.LONG);
		runStartZAPJob.schedule();
	}
}
