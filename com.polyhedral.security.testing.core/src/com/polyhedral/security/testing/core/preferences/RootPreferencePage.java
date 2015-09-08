package com.polyhedral.security.testing.core.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Root preference page for Security Testing preferences. This page does not
 * contain any content, but will be used as the parent page for all other
 * Security Testing preference pages.
 */
public class RootPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench arg0) {
		setDescription("Expand the tree to provide configuration for the supported security testing tools.");
	}

	@Override
	protected void createFieldEditors() {
		// no field editors, this is a blank screen
	}

}
