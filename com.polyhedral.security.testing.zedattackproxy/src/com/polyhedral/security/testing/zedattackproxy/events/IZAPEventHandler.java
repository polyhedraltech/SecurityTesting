package com.polyhedral.security.testing.zedattackproxy.events;

/**
 * ZAP event handler interface.
 */
public interface IZAPEventHandler {
	public void addZAPEventListener(final IZAPEventListener listener);

	public void removeZAPEventListener(final IZAPEventListener listener);

}
