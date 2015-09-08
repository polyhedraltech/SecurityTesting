package com.polyhedral.security.testing.zedattackproxy.actions.menu;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;

/**
 * Abstract {@link MenuManager} implementation that creates toggle on/off
 * options for a menu option.
 */
public abstract class AbstractMenuToggle extends MenuManager {

	private Action toggleOn;
	private Action toggleOff;

	/**
	 * Default constructor.
	 * 
	 * @param menuText
	 *            The text for the menu item.
	 */
	public AbstractMenuToggle(String menuText) {
		this(menuText, "On", "Off");
	}

	/**
	 * Constructor that accounts for non-standard toggle values.
	 * 
	 * @param menuText
	 *            The text for the menu item.
	 * @param toggleOnText
	 *            The text of the "on" toggle option.
	 * @param toggleOffText
	 *            The text of the "off" toggle option.
	 */
	public AbstractMenuToggle(String menuText, String toggleOnText, String toggleOffText) {
		super(menuText);

		toggleOn = new Action() {
			@Override
			public void run() {
				Job toggleOnJob = new ToggleOnJob("Set Toggle On");
				toggleOnJob.setPriority(Job.INTERACTIVE);
				toggleOnJob.schedule();

				setToggleActionState(true);
			}
		};
		toggleOn.setText(toggleOnText);
		add(toggleOn);

		toggleOff = new Action() {
			@Override
			public void run() {
				Job toggleOffJob = new ToggleOffJob("Set Toggle Off");
				toggleOffJob.setPriority(Job.INTERACTIVE);
				toggleOffJob.schedule();

				setToggleActionState(false);
			}
		};
		toggleOff.setText(toggleOffText);
		add(toggleOff);

		setToggleActionState(initToggleState());
	}

	/**
	 * Set the toggle action to its current state.
	 * 
	 * @param toggleOnSelected
	 *            A boolean determining if the On or Off toggle should be
	 *            selected.
	 */
	private void setToggleActionState(boolean toggleOnSelected) {
		toggleOn.setChecked(toggleOnSelected);
		toggleOff.setChecked(!toggleOnSelected);
	}

	/**
	 * Determination of the initial toggle state, to be implemented by the child
	 * class.
	 * 
	 * @return A boolean determining the initial toggle state.
	 */
	protected abstract boolean initToggleState();

	/**
	 * An action to be performed when the toggle on option is selected.
	 */
	protected abstract void executeToggleOn();

	/**
	 * An action to be performed when the toggle off option is selected.
	 */
	protected abstract void executeToggleOff();

	/**
	 * Eclipse {@link Job} triggered when the on option is selected.
	 */
	private class ToggleOnJob extends Job {

		public ToggleOnJob(String name) {
			super(name);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			executeToggleOn();
			return Status.OK_STATUS;
		}
	}

	/**
	 * Eclipse {@link Job} triggered when the off option is selected.
	 */
	private class ToggleOffJob extends Job {

		public ToggleOffJob(String name) {
			super(name);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			executeToggleOff();
			return Status.OK_STATUS;
		}
	}
}
