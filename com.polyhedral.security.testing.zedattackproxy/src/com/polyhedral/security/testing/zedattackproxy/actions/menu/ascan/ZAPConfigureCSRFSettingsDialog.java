package com.polyhedral.security.testing.zedattackproxy.actions.menu.ascan;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.console.ConsolePlugin;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApiException;

import com.polyhedral.security.testing.zedattackproxy.utils.ZAPHelper;

/**
 * A {@link TitleAreaDialog} prompting a user to edit the list of anti-CSRF
 * tokens that can be used during a ZAP scan.
 */
public class ZAPConfigureCSRFSettingsDialog extends TitleAreaDialog {

	List csrfTokens;
	Text addCsrfTokenText;

	/**
	 * Standard constructor.
	 * 
	 * @param parentShell
	 */
	public ZAPConfigureCSRFSettingsDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Set the title and message fields of the {@link TitleAreaDialog}.
	 */
	@Override
	public void create() {
		super.create();
		setTitle("Configure ZAP Anti-CSRF Tokens");
		setMessage("Configure token values that ZAP will use to detect anti-CSRF protections in a website.");
	}

	/**
	 * Generate the content of the {@link TitleAreaDialog} body.
	 */
	@Override
	public Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());

		// Build the separator line
		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite container = new Composite(composite, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout containerLayout = new GridLayout(2, false);
		container.setLayout(containerLayout);

		// Load all of the existing ZAP anti-CSRF tokens.
		initCsrfTokenList(container);

		// Add the Remove button to remove an anti-CSRF token from the list.
		GridData dataRemoveButton = new GridData();
		dataRemoveButton.widthHint = 75;
		dataRemoveButton.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_VERTICAL;
		Button removeButton = new Button(container, SWT.PUSH);
		removeButton.setLayoutData(dataRemoveButton);
		removeButton.setText("Remove");
		removeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (csrfTokens != null && csrfTokens.getSelectionCount() > 0) {
					try {
						ZAPHelper.getInstance().getZAPClient().acsrf.removeOptionToken(
								ZAPHelper.getInstance().getZapApiKey(), csrfTokens.getSelection()[0]);
						updateCsrfTokenList();

					} catch (ClientApiException e1) {
						ConsolePlugin.log(e1);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing with default selection
			}
		});

		// Add an input text field to add a new anti-CSRF token.
		GridData dataGrabHorizontalSpace = new GridData();
		dataGrabHorizontalSpace.grabExcessHorizontalSpace = true;
		dataGrabHorizontalSpace.horizontalAlignment = GridData.FILL;
		addCsrfTokenText = new Text(container, SWT.BORDER);
		addCsrfTokenText.setLayoutData(dataGrabHorizontalSpace);

		// Add the Add button to add an anti-CSRF token to the list.
		GridData dataAddButton = new GridData();
		dataAddButton.widthHint = 75;
		Button addButton = new Button(container, SWT.PUSH);
		addButton.setLayoutData(dataAddButton);
		addButton.setText("Add");
		addButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (StringUtils.isNotBlank(addCsrfTokenText.getText())) {
					try {
						ZAPHelper.getInstance().getZAPClient().acsrf
								.addOptionToken(ZAPHelper.getInstance().getZapApiKey(), addCsrfTokenText.getText());
						addCsrfTokenText.setText("");
						updateCsrfTokenList();

					} catch (ClientApiException e1) {
						ConsolePlugin.log(e1);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing with default selection
			}
		});

		return composite;
	}

	/**
	 * Load the list of current anti-CSRF tokens supported by ZAP.
	 * 
	 * @param container
	 *            The {@link Composite} container for the list.
	 */
	private void initCsrfTokenList(Composite container) {
		GridData dataGrabAllSpace = new GridData();
		dataGrabAllSpace.grabExcessHorizontalSpace = true;
		dataGrabAllSpace.horizontalAlignment = GridData.FILL;
		dataGrabAllSpace.minimumHeight = 150;
		dataGrabAllSpace.heightHint = 150;

		csrfTokens = new List(container, SWT.BORDER | SWT.V_SCROLL);
		csrfTokens.setLayoutData(dataGrabAllSpace);

		updateCsrfTokenList();
	}

	/**
	 * When a new anti-CSRF token has been added or removed from the list or
	 * when the view page is first being loaded, display the list.
	 */
	private void updateCsrfTokenList() {
		csrfTokens.removeAll();

		try {
			ApiResponseElement response = (ApiResponseElement) ZAPHelper.getInstance().getZAPClient().acsrf
					.optionTokensNames();
			// The ZAP API returns the CSRF tokens as a string with the format
			// "[token1, token2, token3, ... tokenN]"
			// so the extra characters need to be parsed out.
			String[] csrfTokenValues = response.getValue().replace("[", "").replace("]", "").replace(" ", "")
					.split(",");
			for (String csrfTokenEntry : csrfTokenValues) {
				csrfTokens.add(csrfTokenEntry);
			}

		} catch (ClientApiException e) {
			ConsolePlugin.log(e);
		}
	}
}
