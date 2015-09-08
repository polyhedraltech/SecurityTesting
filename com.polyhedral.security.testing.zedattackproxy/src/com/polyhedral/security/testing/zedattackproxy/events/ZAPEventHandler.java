package com.polyhedral.security.testing.zedattackproxy.events;

import org.eclipse.core.runtime.ListenerList;

import com.polyhedral.security.testing.zedattackproxy.actions.ScanProgress;

/**
 * ZAP event handler implementation. When a ZAP event is fired, this will notify
 * all of the appropriate listeners.
 */
public class ZAPEventHandler implements IZAPEventHandler {
	private ListenerList actionList = new ListenerList();

	/**
	 * Add a ZAP event listener.
	 */
	public void addZAPEventListener(final IZAPEventListener listener) {
		actionList.add(listener);
	}

	/**
	 * Fire a new ZAP event.
	 * 
	 * @param eventType
	 *            The {@link ZAPEventType} event that was triggered.
	 */
	public void fireZAPEvent(final ZAPEventType eventType) {
		final Object[] list = actionList.getListeners();
		for (int i = 0; i < list.length; ++i) {
			((IZAPEventListener) list[i]).handleZAPEvent(eventType, null);
		}
	}

	/**
	 * Fire a new ZAP event. This event is part of a ZAP scan and includes
	 * {@link ScanProgress} information.
	 * 
	 * @param eventType
	 *            The {@link ZAPEventType} event that was triggered.
	 * @param scanProgress
	 *            The {@link ScanProgress} details to be reported with the
	 *            event.
	 */
	public void fireZAPEvent(final ZAPEventType eventType, ScanProgress scanProgress) {
		final Object[] list = actionList.getListeners();
		for (int i = 0; i < list.length; ++i) {
			((IZAPEventListener) list[i]).handleZAPEvent(eventType, scanProgress);
		}
	}

	/**
	 * Remove a ZAP event listener.
	 */
	public void removeZAPEventListener(final IZAPEventListener listener) {
		actionList.remove(listener);
	}

}
