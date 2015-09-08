package com.polyhedral.security.testing.zedattackproxy.views;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.zaproxy.clientapi.core.ApiResponseSet;

/**
 * Eclipse View fragment representing a single policy entry from ZAP. Includes
 * policy name as well as dropdowns to select the attack strength and alert
 * threshold.
 */
public class ZAPPolicyEditor {

	private Composite parent;
	private ApiResponseSet policyItem;
	private Combo attackStrengthCombo;
	private Combo alertThresholdCombo;

	/**
	 * Generate the view fragment from the provided ZAP {@link ApiResponseSet}.
	 * 
	 * @param parent
	 * @param policyItem
	 */
	public ZAPPolicyEditor(Composite parent, ApiResponseSet policyItem) {
		this.parent = parent;
		this.policyItem = policyItem;

		generateUIContent();
	}

	/**
	 * Initialize the view fragment.
	 */
	private void generateUIContent() {

		// Set the policy name.
		Label policyLabel = new Label(parent, SWT.WRAP);
		GridData policyGrid = new GridData(SWT.FILL, SWT.FILL, false, true);
		policyGrid.widthHint = 75;
		policyLabel.setLayoutData(policyGrid);
		policyLabel.setText(policyItem.getAttribute("name"));

		// Set the attack strength. If one has not been specified in ZAP, select
		// DEFAULT.
		attackStrengthCombo = new Combo(parent, SWT.READ_ONLY);
		for (ZAPAttackStrength attackStrength : ZAPAttackStrength.values()) {
			attackStrengthCombo.add(attackStrength.getValue());
		}
		String policyAttackStrength = policyItem.getAttribute("attackStrength");
		if (StringUtils.isNotBlank(policyAttackStrength)) {
			attackStrengthCombo.select(ZAPAttackStrength.valueOf(policyAttackStrength).getRank());
		} else {
			attackStrengthCombo.select(ZAPAttackStrength.DEFAULT.getRank());
		}

		// Set the alert threshold. If one has not be specified in ZAP, select
		// DEFAULT.
		alertThresholdCombo = new Combo(parent, SWT.READ_ONLY);
		for (ZAPAlertThreshold alertThreshold : ZAPAlertThreshold.values()) {
			alertThresholdCombo.add(alertThreshold.getValue());
		}
		String policyAlertThreshold = policyItem.getAttribute("alertThreshold");
		if (StringUtils.isNotBlank(policyAlertThreshold)) {
			alertThresholdCombo.select(ZAPAlertThreshold.valueOf(policyAlertThreshold).getRank());
		} else {
			alertThresholdCombo.select(ZAPAlertThreshold.DEFAULT.getRank());
		}
	}

	/**
	 * @return The ZAP policy ID {@link String}.
	 */
	public String getPolicyId() {
		return policyItem.getAttribute("id");
	}

	/**
	 * @return the currently selected attack strength {@link String}.
	 */
	public String getSelectedAttackStrength() {
		return attackStrengthCombo.getText();
	}

	/**
	 * @return The currently selected alert threshold {@link String}.
	 */
	public String getSelectedAlertThreshold() {
		return alertThresholdCombo.getText();
	}
}
