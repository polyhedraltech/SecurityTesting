package com.polyhedral.security.testing.zedattackproxy.events;

/**
 * Enum of the ZAP events that are triggered by the ZAP plugin.
 */
public enum ZAPEventType {
	SERVER_STARTED, 
	SERVER_STARTUP_COMPLETE, 
	SERVER_STOP_REQUESTED, 
	SERVER_STOPPED, 
	SCAN_SPIDER_STARTED, 
	SCAN_ASCAN_STARTED, 
	SCAN_PROGRESS, 
	SCAN_COMPLETE, 
	SCAN_CANCEL_STARTED, 
	SCAN_CANCEL_COMPLETE, 
	CONFIGURATION_CHANGED;
}
