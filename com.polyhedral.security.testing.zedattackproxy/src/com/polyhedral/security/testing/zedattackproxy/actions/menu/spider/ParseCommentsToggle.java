package com.polyhedral.security.testing.zedattackproxy.actions.menu.spider;

import org.eclipse.ui.console.ConsolePlugin;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApiException;

import com.polyhedral.security.testing.zedattackproxy.actions.menu.AbstractMenuToggle;
import com.polyhedral.security.testing.zedattackproxy.utils.ZAPHelper;

/**
 * Menu item for toggling the ZAP parse comments spider option.
 */
public class ParseCommentsToggle extends AbstractMenuToggle {

	public ParseCommentsToggle() {
		super("Parse Comments");
	}

	@Override
	protected boolean initToggleState() {
		try {
			ApiResponseElement response = (ApiResponseElement) ZAPHelper.getInstance().getZAPClient().spider
					.optionParseComments();
			return "true".equalsIgnoreCase(response.getValue());
		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}

		return false;
	}

	@Override
	protected void executeToggleOn() {
		try {
			ZAPHelper.getInstance().getZAPClient().spider.setOptionParseComments(ZAPHelper.getInstance().getZapApiKey(),
					true);
		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}
	}

	@Override
	protected void executeToggleOff() {
		try {
			ZAPHelper.getInstance().getZAPClient().spider.setOptionParseComments(ZAPHelper.getInstance().getZapApiKey(),
					false);
		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}
	}

}
