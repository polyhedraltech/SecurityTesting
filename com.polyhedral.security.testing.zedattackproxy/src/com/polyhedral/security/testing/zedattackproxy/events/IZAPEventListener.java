package com.polyhedral.security.testing.zedattackproxy.events;

import java.util.EventListener;

import com.polyhedral.security.testing.zedattackproxy.actions.ScanProgress;

/**
 * ZAP event listener interface.
 */
public interface IZAPEventListener extends EventListener {
	public void handleZAPEvent(ZAPEventType eventType, ScanProgress scanProgress);
}
