package com.polyhedral.security.testing.zedattackproxy.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.part.ViewPart;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ApiResponseSet;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import com.polyhedral.security.testing.zedattackproxy.actions.CancelZAPScanAction;
import com.polyhedral.security.testing.zedattackproxy.actions.RunZAPScanAction;
import com.polyhedral.security.testing.zedattackproxy.actions.ScanProgress;
import com.polyhedral.security.testing.zedattackproxy.actions.StartZAPAction;
import com.polyhedral.security.testing.zedattackproxy.actions.StopZAPAction;
import com.polyhedral.security.testing.zedattackproxy.actions.ZAPAction;
import com.polyhedral.security.testing.zedattackproxy.actions.menu.ascan.AllowAttackOnStartToggle;
import com.polyhedral.security.testing.zedattackproxy.actions.menu.ascan.HandleAntiCSRFTokensToggle;
import com.polyhedral.security.testing.zedattackproxy.actions.menu.ascan.InjectPluginIdInHeaderToggle;
import com.polyhedral.security.testing.zedattackproxy.actions.menu.ascan.RescanInAttackModeToggle;
import com.polyhedral.security.testing.zedattackproxy.actions.menu.spider.ParseCommentsToggle;
import com.polyhedral.security.testing.zedattackproxy.actions.menu.spider.ParseGitToggle;
import com.polyhedral.security.testing.zedattackproxy.actions.menu.spider.ParseRobotsTxtToggle;
import com.polyhedral.security.testing.zedattackproxy.actions.menu.spider.ParseSitemapXmlToggle;
import com.polyhedral.security.testing.zedattackproxy.actions.menu.spider.ParseSvnEntriesToggle;
import com.polyhedral.security.testing.zedattackproxy.actions.menu.spider.PostFormToggle;
import com.polyhedral.security.testing.zedattackproxy.actions.menu.spider.ProcessFormToggle;
import com.polyhedral.security.testing.zedattackproxy.actions.menu.spider.SendRefererHeaderToggle;
import com.polyhedral.security.testing.zedattackproxy.events.IZAPEventListener;
import com.polyhedral.security.testing.zedattackproxy.events.ZAPEventType;
import com.polyhedral.security.testing.zedattackproxy.utils.ZAPHelper;

/**
 * The Zed Attack Proxy Eclipse view component. This view contains action
 * buttons for starting, stopping, and interacting with ZAP. It also includes
 * ZAP configuration options and scan configuration details. When a ZAP scan is
 * initiated, it will show the progress of the seledted scan.
 */
public class ZAPView extends ViewPart implements IZAPEventListener {

	private RunZAPScanAction runZAPScanAction;
	private ZAPAction startZapAction;
	private ZAPAction cancelZAPScanAction;
	private ZAPAction stopZapAction;

	private Composite parent;
	private ScrolledComposite pageScroll;
	private Text urlText;
	private Text fileNameText;
	private Composite reportFormat;
	private List<ZAPPolicyEditor> zapPolicyList;

	private String tempUrlTextValue;
	private String tempFileNameTextValue;
	private String tempReportFormatValue;
	private ScanProgress scanProgress;

	/**
	 * Initialize the ZAP view.
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;

		// Initialize view controls and display content.
		createToolbar();
		enableToolbarActions();
		disableCancelButton();

		// Enable view controls based on whether ZAP is currently running.
		if (ZAPHelper.getInstance().isZapRunning()) {
			drawZAPControls();
		} else {
			removeZAPControls();
		}
	}

	/**
	 * @return The URL text {@link String} entered by the user.
	 */
	public String getUrlText() {
		return (urlText != null && !urlText.isDisposed()) ? urlText.getText() : "";
	}

	/**
	 * @return The file name text {@link String} entered by by the user.
	 */
	public String getFileNameText() {
		return (fileNameText != null && !fileNameText.isDisposed()) ? fileNameText.getText() : "";
	}

	/**
	 * @return the {@link List} of {@link ZAPPolicyEditor} instances currently
	 *         available.
	 */
	public List<ZAPPolicyEditor> getZapPolicyList() {
		return (zapPolicyList != null) ? zapPolicyList : new ArrayList<ZAPPolicyEditor>();
	}

	/**
	 * @return The report format {@link String} selected by the user.
	 */
	public String getReportFormat() {
		if (reportFormat != null) {
			for (Control formatOption : reportFormat.getChildren()) {
				Button buttonOption = (Button) formatOption;
				if (buttonOption.getSelection()) {
					return buttonOption.getText();
				}
			}
		}

		return null;
	}

	/**
	 * 
	 */
	@Override
	public void setFocus() {
	}

	/**
	 * Initialize the {@link ScrolledComposite} wrapper for the ZAP view.
	 */
	private void initPageScroll() {
		if (pageScroll != null && !pageScroll.isDisposed()) {
			pageScroll.dispose();
		}
		pageScroll = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		pageScroll.setBackground(pageScroll.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		pageScroll.setBackgroundMode(SWT.INHERIT_DEFAULT);
		pageScroll.setMinHeight(250);
		pageScroll.setMinWidth(250);
	}

	/**
	 * Create the ZAP view toolbar.
	 */
	private void createToolbar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();

		// Create the Run ZAP Scan action.
		runZAPScanAction = new RunZAPScanAction();
		runZAPScanAction.addZAPEventListener(this);
		runZAPScanAction.setZapView(this);
		toolbarManager.add(runZAPScanAction);

		// Create the Start ZAP action.
		startZapAction = new StartZAPAction();
		startZapAction.addZAPEventListener(this);
		toolbarManager.add(startZapAction);

		// create the Cancel ZAP Scan action.
		cancelZAPScanAction = new CancelZAPScanAction();
		cancelZAPScanAction.addZAPEventListener(this);
		toolbarManager.add(cancelZAPScanAction);

		// Create the Stop ZAP action.
		stopZapAction = new StopZAPAction();
		stopZapAction.addZAPEventListener(this);
		toolbarManager.add(stopZapAction);

		// The ZAP view acts as the processor for all ZAP events, so it needs to
		// be added to the ZAPHelper instance to listen for properties
		// configuration changes.
		ZAPHelper.getInstance().addZAPEventListener(this);

		// Perform an inital check of the ZAP properties configuration to help
		// set the toolbar buttons to the correct state. This needs to be called
		// explicitly because incorrectly configured properties will not fire an
		// event until the ZAP properties tab is opened.
		ZAPHelper.getInstance().checkZapConfiguration();
	}

	/**
	 * Create the ZAP view menu dropdown. This is only enabled when the plugin
	 * is properly configured, the ZAP server is running, and a scan is not
	 * currently processing.
	 * 
	 * @param enableMenu
	 *            A boolean determining if the menu should be enabled or not.
	 *            The menu is enabled when set to true, disabled when set to
	 *            false.
	 */
	private void configureMenuManager(boolean enableMenu) {
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
		menuManager.setVisible(true);
		if (enableMenu) {
			menuManager.removeAll();

			MenuManager spiderMenu = new MenuManager("ZAP Spider Configuration");
			spiderMenu.add(new ParseCommentsToggle());
			spiderMenu.add(new ParseGitToggle());
			spiderMenu.add(new ParseRobotsTxtToggle());
			spiderMenu.add(new ParseSvnEntriesToggle());
			spiderMenu.add(new ParseSitemapXmlToggle());
			spiderMenu.add(new PostFormToggle());
			spiderMenu.add(new ProcessFormToggle());
			spiderMenu.add(new SendRefererHeaderToggle());
			menuManager.add(spiderMenu);

			menuManager.add(new Separator());

			MenuManager ascanMenu = new MenuManager("ZAP Ascan Configuration");
			ascanMenu.add(new AllowAttackOnStartToggle());
			ascanMenu.add(new HandleAntiCSRFTokensToggle());
			ascanMenu.add(new InjectPluginIdInHeaderToggle());
			ascanMenu.add(new RescanInAttackModeToggle());
			menuManager.add(ascanMenu);

		} else {
			menuManager.removeAll();
			Action noConfigurationAction = new Action("Configuration Unavailabile") {
			};
			noConfigurationAction.setEnabled(false);
			menuManager.add(noConfigurationAction);
		}
	}

	/**
	 * Enable/disable the toolbar action icons based on the current status of
	 * the ZAP server.
	 */
	private void enableToolbarActions() {
		boolean zapConfigurationValid = ZAPHelper.getInstance().isZapConfigurationValid();
		boolean zapRunning = ZAPHelper.getInstance().isZapRunning();
		runZAPScanAction.setEnabled(zapConfigurationValid && zapRunning);
		startZapAction.setEnabled(zapConfigurationValid && !zapRunning);
		stopZapAction.setEnabled(zapConfigurationValid && zapRunning);
		configureMenuManager(zapConfigurationValid && zapRunning);
	}

	/**
	 * Disable all toolbar action icons. This is generally done when an
	 * interrupting function has been triggered, such as starting/stopping the
	 * ZAP server or performing a scan.
	 */
	private void disableToolbarActions() {
		runZAPScanAction.setEnabled(false);
		startZapAction.setEnabled(false);
		stopZapAction.setEnabled(false);
		configureMenuManager(false);
	}

	/**
	 * Enable/disable the cancel button based on the current status of the ZAP
	 * server. This is performed separately from the rest of the toolbar actions
	 * because it has a unique state when a scan is running.
	 */
	private void enableCancelButton() {
		cancelZAPScanAction.setEnabled(true);
	}

	/**
	 * Disable the cancel button. This is performed separately from the rest of
	 * the toolbar actions because it has a unique state when a scan is running.
	 */
	private void disableCancelButton() {
		cancelZAPScanAction.setEnabled(false);
	}

	/**
	 * Update the ZAP view with the status of the ZAP scan.
	 */
	private void drawZAPProgress() {
		ClientApi zapAPI = ZAPHelper.getInstance().getZAPClient();

		// Re-initialize the ZAP view to remove the previous status data.
		initPageScroll();

		// Create the page layout.
		Composite pageComposite = new Composite(pageScroll, SWT.NONE);
		pageComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pageComposite.setLayout(new GridLayout(1, true));
		pageScroll.setContent(pageComposite);

		Composite inputComposite = new Composite(pageComposite, SWT.NONE);
		inputComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		inputComposite.setLayout(new GridLayout(1, false));

		// Display the current root URL being scanned.
		new Label(inputComposite, SWT.NONE).setText("Scan Target URL:");
		new Label(inputComposite, SWT.NONE).setText(tempUrlTextValue);
		new Label(inputComposite, SWT.NONE).setText("");

		Composite scanComposite = new Composite(pageComposite, SWT.NONE);
		scanComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		scanComposite.setLayout(new GridLayout(2, false));

		// If the spider has started, display it's current status. Otherwise
		// show its progress as 0%.
		if (scanProgress.getSpiderId() != null) {
			try {
				Label spiderProgress = new Label(scanComposite, SWT.WRAP);
				spiderProgress.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
				((GridData) spiderProgress.getLayoutData()).widthHint = 150;
				spiderProgress.setText("Spider Progress:");
				new Label(scanComposite, SWT.NONE).setText(
						((ApiResponseElement) zapAPI.spider.status(scanProgress.getSpiderId())).getValue() + "%");
				new Label(scanComposite, SWT.NONE).setText("");
				new Label(scanComposite, SWT.NONE).setText("");
			} catch (ClientApiException e) {
				ConsolePlugin.log(e);
			}
		} else {
			Label spiderProgress = new Label(scanComposite, SWT.WRAP);
			spiderProgress.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
			((GridData) spiderProgress.getLayoutData()).widthHint = 150;
			spiderProgress.setText("Spider Progress:");
			new Label(scanComposite, SWT.NONE).setText("0%");
			new Label(scanComposite, SWT.NONE).setText("");
			new Label(scanComposite, SWT.NONE).setText("");
		}

		// If the ascan has started, display it's current status with detailed
		// information about each ascan policy that is being triggered. If the
		// ascan has not started, show its progress as 0%.
		if (scanProgress.getAscanId() != null) {
			try {
				new Label(scanComposite, SWT.NONE).setText("Ascan Progress:");
				new Label(scanComposite, SWT.NONE).setText(
						((ApiResponseElement) zapAPI.ascan.status(scanProgress.getAscanId())).getValue() + "%");

				ApiResponseList ascanProgress = (ApiResponseList) zapAPI.ascan.scanProgress(scanProgress.getAscanId());
				ApiResponseList scanProgressList = (ApiResponseList) ascanProgress.getItems().get(1);
				for (ApiResponse scanItem : scanProgressList.getItems()) {
					ApiResponseList scanItemList = (ApiResponseList) scanItem;
					String name = "";
					String status = "";
					String timeCount = "";

					// The ZAP API returns scan information as a list of
					// ApiResponseList items instead of as a Map. To get the
					// current scan information, the whole list has to be
					// iterated through to get the scan name, status, and time
					// count.
					for (ApiResponse scanElementItem : scanItemList.getItems()) {
						ApiResponseElement scanElement = (ApiResponseElement) scanElementItem;
						if (scanElement.getName().equals("name")) {
							name = scanElement.getValue();
						} else if (scanElement.getName().equals("status")) {
							status = scanElement.getValue();
						} else if (scanElement.getName().equals("timeInMs")) {
							timeCount = scanElement.getValue();
						}
					}
					Label scanName = new Label(scanComposite, SWT.WRAP);
					scanName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
					((GridData) scanName.getLayoutData()).widthHint = 150;
					scanName.setText(name);
					new Label(scanComposite, SWT.NONE).setText(status + " (" + timeCount + " ms)");
				}
			} catch (ClientApiException e) {
				ConsolePlugin.log(e);
			}
		} else {
			new Label(scanComposite, SWT.NONE).setText("Ascan Progress:");
			new Label(scanComposite, SWT.NONE).setText("0%");
		}

		pageComposite.setSize(pageComposite.computeSize(300, SWT.DEFAULT));

		refreshParent();
	}

	/**
	 * Draw the user input fields for running a ZAP scan.
	 */
	private void drawZAPControls() {
		// Re-initialize the ZAP view to remove any previous view components.
		initPageScroll();

		// Create the page layout.
		Composite pageComposite = new Composite(pageScroll, SWT.NONE);
		pageComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pageComposite.setLayout(new GridLayout(1, true));
		pageScroll.setContent(pageComposite);

		Composite inputComposite = new Composite(pageComposite, SWT.NONE);
		inputComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		inputComposite.setLayout(new GridLayout(2, false));

		// Input field for the URL to be scanned.
		new Label(inputComposite, SWT.NONE).setText("Scan Target URL:");
		urlText = new Text(inputComposite, SWT.SINGLE | SWT.BORDER);
		urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		urlText.setText((tempUrlTextValue != null) ? tempUrlTextValue : "");

		// Input field for the file name where the scan will be saved.
		new Label(inputComposite, SWT.NONE).setText("Scan Result File:");
		fileNameText = new Text(inputComposite, SWT.SINGLE | SWT.BORDER);
		fileNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fileNameText.setText((tempFileNameTextValue != null) ? tempFileNameTextValue : "");

		// Selected report format for the scan. Current options are XML and
		// HTML.
		new Label(inputComposite, SWT.NONE).setText("Report Format:");
		reportFormat = new Composite(inputComposite, SWT.NULL);
		reportFormat.setLayout(new RowLayout());
		Button xmlButton = new Button(reportFormat, SWT.RADIO);
		xmlButton.setText("XML");
		if ("XML".equals(tempReportFormatValue)) {
			xmlButton.setSelection(true);
		}
		Button htmlButton = new Button(reportFormat, SWT.RADIO);
		htmlButton.setText("HTML");
		if ("HTML".equals(tempReportFormatValue)) {
			htmlButton.setSelection(true);
		}

		Composite levelsComposite = new Composite(pageComposite, SWT.NONE);
		levelsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		levelsComposite.setLayout(new GridLayout(3, false));

		// Display headers for the currently available ZAP scan policies.
		Label firstColumnHeader = new Label(levelsComposite, SWT.WRAP);
		firstColumnHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		((GridData) firstColumnHeader.getLayoutData()).widthHint = 80;
		firstColumnHeader.setText("Policy");

		Label secondColumnHeader = new Label(levelsComposite, SWT.WRAP);
		secondColumnHeader.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, false, true));
		((GridData) secondColumnHeader.getLayoutData()).widthHint = 110;
		secondColumnHeader.setText("Attack Strength");

		Label thirdColumnHeader = new Label(levelsComposite, SWT.WRAP);
		thirdColumnHeader.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, false, true));
		((GridData) thirdColumnHeader.getLayoutData()).widthHint = 110;
		thirdColumnHeader.setText("Alert Threshold");

		try {
			// Iterate through all of the available ZAP scan policies, set their
			// current values, and allow the user to select new values for
			// attack strength and alert threshold (if desired).
			ApiResponseList zapPolicies = (ApiResponseList) ZAPHelper.getInstance().getZAPClient().ascan.policies("",
					"");
			zapPolicyList = new ArrayList<ZAPPolicyEditor>();
			for (ApiResponse policyItem : zapPolicies.getItems()) {
				zapPolicyList.add(new ZAPPolicyEditor(levelsComposite, (ApiResponseSet) policyItem));
			}

		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}

		pageComposite.setSize(pageComposite.computeSize(300, SWT.DEFAULT));

		refreshParent();
	}

	/**
	 * Remove ZAP controls. This is done at times where there is no ZAP server
	 * to access so no information can be displayed in the ZAP view.
	 */
	private void removeZAPControls() {
		initPageScroll();
		refreshParent();
	}

	/**
	 * Repaint the ZAP view.
	 */
	private void refreshParent() {
		parent.layout();
	}

	/**
	 * Implementation of the ZAP event handler. The ZAP view actions will signal
	 * the view when specific events happen, and the ZAP view will repaint the
	 * ZAP view according to the current state.
	 */
	@Override
	public void handleZAPEvent(ZAPEventType eventType, ScanProgress scanProgress) {
		this.scanProgress = scanProgress;

		// TODO [On Hold] Currently, ZAP locks up when stopping a spider
		// request, so cancel button is not enabled until
		// the ascan is triggered. After ZAP fixes the spider problem, the
		// cancel button should be enabled
		// when the spider starts.
		switch (eventType) {
		case SERVER_STARTED:
			disableToolbarActions();
			break;
		case SERVER_STOP_REQUESTED:
			disableCancelButton();
			disableToolbarActions();
			break;
		case SCAN_PROGRESS:
			disableToolbarActions();
			drawZAPProgress();
			break;
		case SCAN_SPIDER_STARTED:
			disableToolbarActions();
			tempUrlTextValue = urlText.getText();
			tempFileNameTextValue = fileNameText.getText();
			tempReportFormatValue = getReportFormat();
			break;
		case SCAN_CANCEL_STARTED:
			runZAPScanAction.cancelScan();
			disableCancelButton();
			removeZAPControls();
			break;
		case SCAN_ASCAN_STARTED:
			enableCancelButton();
			break;
		case SCAN_COMPLETE:
			disableCancelButton();
			drawZAPControls();
			enableToolbarActions();
			break;
		case SERVER_STARTUP_COMPLETE:
			drawZAPControls();
			enableToolbarActions();
			break;
		case SCAN_CANCEL_COMPLETE:
			drawZAPControls();
			enableToolbarActions();
			break;
		case SERVER_STOPPED:
			removeZAPControls();
			enableToolbarActions();
			break;
		default:
			enableToolbarActions();
			break;
		}
	}
}