package com.polyhedral.security.testing.zedattackproxy.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import com.polyhedral.security.testing.zedattackproxy.Activator;
import com.polyhedral.security.testing.zedattackproxy.events.IZAPEventHandler;
import com.polyhedral.security.testing.zedattackproxy.events.IZAPEventListener;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventHandler;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventType;
import com.polyhedral.security.testing.zedattackproxy.preferences.ZAPPreferencePage;

/**
 * Convenience class for the ZAP scanner. Contains configuration information
 * derived from the ZAP properties page, console logging capabilities, and ZAP
 * API access features.
 */
public class ZAPHelper implements IZAPEventHandler {
	private static final ZAPHelper instance = new ZAPHelper();

	private static final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private static final String ZAP_PLUGIN_CONSOLE = "ZAP Scanner Console";

	private static final String WINDOWS_JAVA_EXECUTABLE = "\\bin\\java.exe";
	private static final String NIX_JAVA_EXECUTABLE = "/bin/java";
	private static final String ZAP_SESSION_EXTENSION = ".session";

	public static final String JAVA_JAR_FLAG = "-jar";
	public static final String ZAP_FLAG_DAEMON = "-daemon";
	public static final String ZAP_FLAG_PORT = "-port";
	public static final String ZAP_FLAG_SESSION = "-session";
	public static final String ZAP_FLAG_NEWSESSION = "-newsession";
	public static final String ZAP_SERVER_NAME = "localhost";
	public static final String ZAP_FLAG_CONFIG = "-config";
	public static final String ZAP_CONFIG_API_KEY = "api.key";

	private ZAPEventHandler zapEventHandler;
	private MessageConsoleStream zapConsoleStream;
	private boolean zapConfigurationValid;

	/**
	 * Constructor for the {@link ZAPHelper} singleton instance.
	 */
	private ZAPHelper() {
		zapEventHandler = new ZAPEventHandler();

		loadConsoleStream();
		zapConfigurationValid = false;
	}

	/**
	 * @return The {@link ZAPHelper} singleton instance.
	 */
	public static ZAPHelper getInstance() {
		return instance;
	}

	/**
	 * @return The location where ZAP executable JAR is located.
	 */
	public String getZapJarLocation() {
		return preferenceStore.getString(ZAPPreferencePage.ZAP_JAR_LOCATION);
	}

	/**
	 * @return The path to the directory where the ZAP session files will be
	 *         stored.
	 */
	public String getZapSessionDirectory() {
		return preferenceStore.getString(ZAPPreferencePage.ZAP_SESSION_DIRECTORY);
	}

	/**
	 * @return The file name for the ZAP session.
	 */
	public String getZapSessionName() {
		return preferenceStore.getString(ZAPPreferencePage.ZAP_SESSION_NAME);
	}

	/**
	 * @return The port where the ZAP proxy will execute.
	 */
	public int getZapProxyPort() {
		return preferenceStore.getInt(ZAPPreferencePage.ZAP_PROXY_PORT);
	}

	/**
	 * The ZAP API key for the ZAP server. Starting with version 2.4.1 of ZAP
	 * the API key became a required field.
	 * 
	 * @return The ZAP API key value.
	 */
	public String getZapApiKey() {
		return preferenceStore.getString(ZAPPreferencePage.ZAP_API_KEY);
	}

	/**
	 * @return The full path value for the ZAP session. This is needed when
	 *         starting ZAP in headless mode.
	 */
	public String getFullSessionPath() {
		return getZapSessionDirectory() + File.separator + getZapSessionName();
	}

	/**
	 * When starting a ZAP instance in headless mode, it is necessary to
	 * determine if the session file already exists. If one does not exist, it
	 * needs to be created. If it already exists, it requires a different flag
	 * for the startup command. Otherwise, ZAP will throw an error stating the
	 * session file has a problem.
	 * 
	 * @return A boolean value determining whether a ZAP session file already
	 *         exists. Returns true if a ZAP session file exists, false if it
	 *         does not exist.
	 */
	public boolean zapSessionExists() {
		return new File(getFullSessionPath() + ZAP_SESSION_EXTENSION).exists();
	}

	/**
	 * Get the path to the default Java JVM installation. This is needed when
	 * starting ZAP in headless mode to know where to call Java from.
	 * 
	 * @return The path to the Java JVM.
	 */
	public String getJavaExecutable() {
		return JavaRuntime.getDefaultVMInstall().getInstallLocation()
				+ ((Platform.OS_WIN32.equals(Platform.getOS())) ? WINDOWS_JAVA_EXECUTABLE : NIX_JAVA_EXECUTABLE);
	}

	/**
	 * @return The appropriate session flag for starting ZAP in headless mode,
	 *         depending on whether the session file already exists.
	 */
	public String getZapSessionFlag() {
		return zapSessionExists() ? ZAP_FLAG_SESSION : ZAP_FLAG_NEWSESSION;
	}

	/**
	 * @return A ZAP {@link ClientApi} instance for calling ZAP through the API.
	 */
	public ClientApi getZAPClient() {
		return new ClientApi(ZAP_SERVER_NAME, getZapProxyPort());
	}

	/**
	 * Checks the ZAP Eclipse properties and determines if they have all been
	 * set. This is called when the ZAP view initializes and any time a user
	 * makes a change to the ZAP properties.
	 */
	public void checkZapConfiguration() {
		this.zapConfigurationValid = StringUtils.isNotBlank(getZapJarLocation())
				&& StringUtils.isNotBlank(getZapSessionDirectory()) && StringUtils.isNotBlank(getZapSessionName())
				&& StringUtils.isNotBlank(getZapApiKey()) && getZapProxyPort() > 0;
		zapEventHandler.fireZAPEvent(ZAPEventType.CONFIGURATION_CHANGED);
	}

	/**
	 * @return A boolean for whether ZAP has been properly configured in
	 *         Eclipse. True if it has, false otherwise.
	 */
	public boolean isZapConfigurationValid() {
		return this.zapConfigurationValid;
	}

	/**
	 * Check to see if the ZAP server is currently running.
	 * 
	 * @return A boolean for the ZAP server status. Returns true if the ZAP
	 *         server is running, false otherwise.
	 */
	public boolean isZapRunning() {
		try {
			getZAPClient().core.version();
		} catch (ClientApiException e) {
			return false;
		}

		return true;
	}

	/**
	 * Load the Eclispe Console view and activate a new stream for the ZAP
	 * plugin. This stream will be used to capture logging information for the
	 * ZAP plugin.
	 */
	private void loadConsoleStream() {
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		IConsole[] existingConsoles = manager.getConsoles();
		MessageConsole zapConsole = null;
		for (IConsole console : existingConsoles) {
			if (ZAP_PLUGIN_CONSOLE.equals(console.getName())) {
				zapConsole = (MessageConsole) console;
			}
		}

		if (zapConsole == null) {
			zapConsole = new MessageConsole(ZAP_PLUGIN_CONSOLE, null, null, true);
			zapConsole.activate();
			manager.addConsoles(new IConsole[] { zapConsole });
		}

		zapConsoleStream = zapConsole.newMessageStream();
		zapConsoleStream.setActivateOnWrite(true);
	}

	/**
	 * Log a {@link String} message to the ZAP Console view.
	 * 
	 * @param message
	 *            The {@link String} message to add to the ZAP Console view.
	 */
	public void logConsoleMessage(String message) {
		try {
			zapConsoleStream.println(message);
			zapConsoleStream.flush();
		} catch (IOException e) {
			// If the Console failed to write the message, dump the stack trace
			// since the Console apparently cannot accept writing at this time.
			ConsolePlugin.log(e);
		}
	}

	/**
	 * Add a ZAP event listener. This is used to trigger ZAP events outside of a
	 * standard event.
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
}
