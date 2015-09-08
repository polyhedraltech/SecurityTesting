package com.polyhedral.security.testing.zedattackproxy.actions.menu.ascan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * Eclipse {@link UIJob} for creating a Eclipse dialog which allows a user to
 * modify the list of anti-CSRF tokens that are supported by ZAP.
 */
public class ConfigureZapCsrfSettingsJob extends UIJob {

	public ConfigureZapCsrfSettingsJob(Display jobDisplay, String name) {
		super(jobDisplay, name);
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		ZAPConfigureCSRFSettingsDialog dialog = new ZAPConfigureCSRFSettingsDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		if (dialog.open() == Window.OK) {
			return Status.OK_STATUS;
		}
		return Status.CANCEL_STATUS;
	}
}
