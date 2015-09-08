package com.polyhedral.security.testing.zedattackproxy.actions.menu.ascan;

import org.eclipse.ui.console.ConsolePlugin;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApiException;

import com.polyhedral.security.testing.zedattackproxy.actions.menu.AbstractMenuToggle;
import com.polyhedral.security.testing.zedattackproxy.utils.ZAPHelper;

/**
 * Menu item for toggling the ZAP rescan in attack mode ascan option.
 */
public class RescanInAttackModeToggle extends AbstractMenuToggle {

	public RescanInAttackModeToggle() {
		super("Rescan In Attack Mode");
	}

	@Override
	protected boolean initToggleState() {
		try {
			ApiResponseElement response = (ApiResponseElement) ZAPHelper.getInstance().getZAPClient().ascan
					.optionRescanInAttackMode();
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
					.setOptionRescanInAttackMode(ZAPHelper.getInstance().getZapApiKey(), true);
		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}
	}

	@Override
	protected void executeToggleOff() {
		try {
			ZAPHelper.getInstance().getZAPClient().ascan
					.setOptionRescanInAttackMode(ZAPHelper.getInstance().getZapApiKey(), false);
		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}
	}

}