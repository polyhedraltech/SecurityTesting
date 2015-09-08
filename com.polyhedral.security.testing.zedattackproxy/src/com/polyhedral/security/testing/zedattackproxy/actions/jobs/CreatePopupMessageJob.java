package com.polyhedral.security.testing.zedattackproxy.actions.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * Eclipse {@link UIJob} for creating a popup message to communicate information
 * to the user.
 */
public class CreatePopupMessageJob extends UIJob {

	private int popupType;
	private String message;

	/**
	 * Default constructor.
	 * 
	 * @param popupType
	 *            The type of popup to be created. Some options include
	 *            confirmation, warning, and error popups.
	 * @param message
	 *            The message of the popup window.
	 */
	public CreatePopupMessageJob(int popupType, String message) {
		super("");
		this.popupType = popupType;
		this.message = message;
	}

	/**
	 * Execution of the popup window job using the Eclipse {@link MessageDialog}
	 * feature.
	 */
	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		if (MessageDialog.open(popupType, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"ZAP Scanner", message, SWT.NONE)) {
			return Status.OK_STATUS;
		} else {
			return Status.CANCEL_STATUS;
		}
	}

}
