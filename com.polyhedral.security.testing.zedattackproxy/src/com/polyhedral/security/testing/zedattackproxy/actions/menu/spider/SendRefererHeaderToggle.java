package com.polyhedral.security.testing.zedattackproxy.actions.menu.spider;

import org.eclipse.ui.console.ConsolePlugin;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApiException;

import com.polyhedral.security.testing.zedattackproxy.actions.menu.AbstractMenuToggle;
import com.polyhedral.security.testing.zedattackproxy.utils.ZAPHelper;

/**
 * Menu item for toggling the ZAP send referrer header spider option.
 */
public class SendRefererHeaderToggle extends AbstractMenuToggle {

	public SendRefererHeaderToggle() {
		super("Send Referer Header");
	}

	@Override
	protected boolean initToggleState() {
		try {
			ApiResponseElement response = (ApiResponseElement) ZAPHelper.getInstance().getZAPClient().spider
					.optionSendRefererHeader();
			return "true".equalsIgnoreCase(response.getValue());
		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}

		return false;
	}

	@Override
	protected void executeToggleOn() {
		try {
			ZAPHelper.getInstance().getZAPClient().spider
					.setOptionSendRefererHeader(ZAPHelper.getInstance().getZapApiKey(), true);
		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}
	}

	@Override
	protected void executeToggleOff() {
		try {
			ZAPHelper.getInstance().getZAPClient().spider
					.setOptionSendRefererHeader(ZAPHelper.getInstance().getZapApiKey(), false);
		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}
	}

}
