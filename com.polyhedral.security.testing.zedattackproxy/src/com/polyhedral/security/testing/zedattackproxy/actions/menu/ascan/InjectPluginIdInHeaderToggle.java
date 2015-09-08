package com.polyhedral.security.testing.zedattackproxy.actions.menu.ascan;

import org.eclipse.ui.console.ConsolePlugin;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApiException;

import com.polyhedral.security.testing.zedattackproxy.actions.menu.AbstractMenuToggle;
import com.polyhedral.security.testing.zedattackproxy.utils.ZAPHelper;

/**
 * Menu item for toggling the ZAP inject plugin ID header ascan option.
 */
public class InjectPluginIdInHeaderToggle extends AbstractMenuToggle {

	public InjectPluginIdInHeaderToggle() {
		super("Inject Plugin ID In Header");
	}

	@Override
	protected boolean initToggleState() {
		try {
			ApiResponseElement response = (ApiResponseElement) ZAPHelper.getInstance().getZAPClient().ascan
					.optionInjectPluginIdInHeader();
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
					.setOptionInjectPluginIdInHeader(ZAPHelper.getInstance().getZapApiKey(), true);
		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}
	}

	@Override
	protected void executeToggleOff() {
		try {
			ZAPHelper.getInstance().getZAPClient().ascan
					.setOptionInjectPluginIdInHeader(ZAPHelper.getInstance().getZapApiKey(), false);
		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}
	}

}
