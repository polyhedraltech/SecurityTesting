package com.polyhedral.security.testing.zedattackproxy.actions;

import org.eclipse.jface.action.Action;

import com.polyhedral.security.testing.zedattackproxy.events.IZAPEventHandler;
import com.polyhedral.security.testing.zedattackproxy.events.IZAPEventListener;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventHandler;

/**
 * Base class for all ZAP actions. Includes support for the
 * {@link IZAPEventHandler} interface.
 */
public abstract class ZAPAction extends Action implements IZAPEventHandler {

	private ZAPEventHandler zapEventHandler = new ZAPEventHandler();

	/**
	 * Add a new ZAP event listener.
	 */
	@Override
	public void addZAPEventListener(IZAPEventListener listener) {
		zapEventHandler.addZAPEventListener(listener);
	}

	/**
	 * Remove a ZAP event listener.
	 */
	@Override
	public void removeZAPEventListener(IZAPEventListener listener) {
		zapEventHandler.removeZAPEventListener(listener);
	}

	/**
	 * Get the ZAP event handler to be passed on to ZAP actions for additional
	 * processing.
	 * 
	 * @return The ZAP event handler.
	 */
	protected ZAPEventHandler getZAPEventHandler() {
		return this.zapEventHandler;
	}

}
