package com.polyhedral.security.testing.zedattackproxy.actions.jobs.runscan;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.ConsolePlugin;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import com.polyhedral.security.testing.zedattackproxy.utils.ZAPHelper;

/**
 * Eclipse {@link Job} for setting the levels for the ZAP scan policies.
 */
public class ZAPSetZAPLevelsJob extends Job {

	private List<ZAPPolicyInfo> zapPolicyList;

	/**
	 * Default constructor.
	 * 
	 * @param name
	 *            The name to be displayed in the Eclipse progress view while
	 *            this is executing.
	 * @param zapPolicyList
	 */
	public ZAPSetZAPLevelsJob(String name, List<ZAPPolicyInfo> zapPolicyList) {
		super(name);
		this.zapPolicyList = zapPolicyList;
	}

	/**
	 * Excution of the update for the ZAP scan policies based on the user input
	 * from the ZAP view.
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		try {
			ClientApi zapAPI = ZAPHelper.getInstance().getZAPClient();
			for (ZAPPolicyInfo policyItem : zapPolicyList) {
				zapAPI.ascan.setPolicyAttackStrength(ZAPHelper.getInstance().getZapApiKey(), policyItem.getId(),
						policyItem.getAttackStrength(), "");
				zapAPI.ascan.setPolicyAlertThreshold(ZAPHelper.getInstance().getZapApiKey(), policyItem.getId(),
						policyItem.getAlertThreshold(), "");
			}

		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}

		return Status.OK_STATUS;
	}
}
