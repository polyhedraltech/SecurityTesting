package com.polyhedral.security.testing.zedattackproxy.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.polyhedral.security.testing.zedattackproxy.Activator;
import com.polyhedral.security.testing.zedattackproxy.utils.ZAPHelper;

/**
 * Preferences page for the ZAP plugin.
 */
public class ZAPPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String ZAP_JAR_LOCATION = "ZAP JAR Location";
	public static final String ZAP_SESSION_DIRECTORY = "ZAP Session Directory";
	public static final String ZAP_SESSION_NAME = "ZAP Session Name";
	public static final String ZAP_PROXY_PORT = "ZAP Proxy Port";
	public static final String ZAP_API_KEY = "ZAP API Key";

	/**
	 * Initialize the ZAP preferences page.
	 */
	@Override
	public void init(IWorkbench arg0) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Configuration information for using the Zed Attack Proxy security testing tool.");
	}

	/**
	 * Add the ZAP preferences to the preferences page.
	 */
	@Override
	protected void createFieldEditors() {
		FileFieldEditor zapJarLocation = new FileFieldEditor(ZAP_JAR_LOCATION, "ZAP JAR Location:",
				getFieldEditorParent());
		zapJarLocation.setEmptyStringAllowed(false);
		addField(zapJarLocation);

		DirectoryFieldEditor zapSessionDirectory = new DirectoryFieldEditor(ZAP_SESSION_DIRECTORY,
				"ZAP Session Directory", getFieldEditorParent());
		zapSessionDirectory.setEmptyStringAllowed(false);
		addField(zapSessionDirectory);

		StringFieldEditor zapSessionName = new StringFieldEditor(ZAP_SESSION_NAME, "ZAP Session Name",
				getFieldEditorParent());
		zapSessionName.setEmptyStringAllowed(false);
		addField(zapSessionName);

		IntegerFieldEditor zapProxyPort = new IntegerFieldEditor(ZAP_PROXY_PORT, "ZAP Proxy Port:",
				getFieldEditorParent());
		zapProxyPort.setValidRange(0, 99999);
		zapProxyPort.setEmptyStringAllowed(false);
		addField(zapProxyPort);

		StringFieldEditor zapApiKey = new StringFieldEditor(ZAP_API_KEY, "ZAP API Key", getFieldEditorParent());
		zapApiKey.setEmptyStringAllowed(false);
		addField(zapApiKey);
	}

	/**
	 * When the user is done editing the ZAP preferences and clicks the "OK"
	 * button, verify that the prefernces are valid and fire a ZAP event to
	 * trigger the ZAP view buttons to enable/disable as needed.
	 */
	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		ZAPHelper.getInstance().checkZapConfiguration();
		return result;
	}

}
