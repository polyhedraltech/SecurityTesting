package com.polyhedral.security.testing.zedattackproxy.actions.menu.ascan;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApiException;

import com.polyhedral.security.testing.zedattackproxy.actions.menu.AbstractMenuToggle;
import com.polyhedral.security.testing.zedattackproxy.utils.ZAPHelper;

/**
 * Menu item for toggling the ZAP handle anti-CSRF tokens ascan option.
 */
public class HandleAntiCSRFTokensToggle extends AbstractMenuToggle {

	public HandleAntiCSRFTokensToggle() {
		super("Handle Anti-CSRF Tokens");
		add(new Separator());
		add(new Action("Configure...") {
			@Override
			public void run() {
				ConfigureZapCsrfSettingsJob configureCSRFSettingsJob = new ConfigureZapCsrfSettingsJob(
						PlatformUI.getWorkbench().getDisplay(), "Configure ZAP CSRF Settings");
				configureCSRFSettingsJob.setPriority(Job.INTERACTIVE);
				configureCSRFSettingsJob.schedule();
			}
		});
	}

	@Override
	protected boolean initToggleState() {
		try {
			ApiResponseElement response = (ApiResponseElement) ZAPHelper.getInstance().getZAPClient().ascan
					.optionHandleAntiCSRFTokens();
			return "true".equalsIgnoreCase(response.getValue());
		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}

		return false;
	}

	@Override
	protected void executeToggleOn() {
		try {
			ZAPHelper.getInstance().getZAPClient().ascan
					.setOptionHandleAntiCSRFTokens(ZAPHelper.getInstance().getZapApiKey(), true);
		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}
	}

	@Override
	protected void executeToggleOff() {
		try {
			ZAPHelper.getInstance().getZAPClient().ascan
					.setOptionHandleAntiCSRFTokens(ZAPHelper.getInstance().getZapApiKey(), false);
		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}
	}
}
