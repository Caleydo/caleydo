/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget.numerical;

import org.caleydo.core.io.NumericalProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Christian
 *
 */
public class ClippingWidgets implements INumericalDataPropertiesWidgets {

	/**
	 * Button to enable the {@link #maxTextField};
	 */
	protected Button maxButton;

	/**
	 * Button to enable the {@link #minTextField};
	 */
	protected Button minButton;

	/**
	 * Radio button to set standard dev clipping.
	 */
	protected Button stdDevClippingButton;

	/**
	 * Radio button to set min/max clipping.
	 */
	protected Button minMaxClippingButton;

	/**
	 * Multiple of std deviation to clip around mean value.
	 */
	protected Text stdDevFactorTextField;

	/**
	 * Text field that specifies the minimum data clipping value.
	 */
	protected Text minTextField;
	/**
	 * Text field that specifies the minimum data clipping value.
	 */
	protected Text maxTextField;

	protected Label stdDevFactorLabel;

	protected Label minMaxClippingExplanationLabel;
	protected Label stdDevClippingExplanationLabel;

	protected Group clippingGroup;

	@Override
	public void create(Composite parent, Listener listener) {
		clippingGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		clippingGroup.setText("Data Clipping");
		clippingGroup.setLayout(new GridLayout(2, false));
		clippingGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		minMaxClippingButton = new Button(clippingGroup, SWT.RADIO);
		minMaxClippingButton.setText("Minimum/Maximum Value Clipping");
		minMaxClippingButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 2, 1));
		minMaxClippingButton.addListener(SWT.Selection, listener);
		minMaxClippingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				useMinMaxClipping(true);
			}
		});

		minMaxClippingExplanationLabel = new Label(clippingGroup, SWT.WRAP);
		minMaxClippingExplanationLabel
				.setText("Specify the value range for the dataset using a minimum and/or maximum value. This can be used to specify bounds in the data. For example, if you want to see your data relative to 0 but all data points are greater than 0 you should specify 0 here as the minimum. You can also use this to clip your data. If you, for example, select '3' as the maximum every data point exceeding this range will be clipped to '3' for display.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 200;
		minMaxClippingExplanationLabel.setLayoutData(gridData);

		maxButton = new Button(clippingGroup, SWT.CHECK);
		maxButton.setText("Max");
		maxButton.addListener(SWT.Selection, listener);
		maxButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				maxButtonSelected();
			}
		});

		maxTextField = new Text(clippingGroup, SWT.BORDER);
		maxTextField.addListener(SWT.Modify, listener);
		maxTextField.setEnabled(false);
		maxButton.setEnabled(true);

		minButton = new Button(clippingGroup, SWT.CHECK);
		minButton.setText("Min");
		minButton.addListener(SWT.Selection, listener);
		minButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				minButtonSelected();
			}
		});

		minTextField = new Text(clippingGroup, SWT.BORDER);
		minTextField.addListener(SWT.Modify, listener);
		minTextField.setEnabled(false);

		stdDevClippingButton = new Button(clippingGroup, SWT.RADIO);
		stdDevClippingButton.setText("Standard Deviation Clipping");
		stdDevClippingButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 2, 1));
		stdDevClippingButton.addListener(SWT.Selection, listener);
		stdDevClippingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				useMinMaxClipping(false);
			}
		});

		stdDevClippingExplanationLabel = new Label(clippingGroup, SWT.WRAP);
		stdDevClippingExplanationLabel
				.setText("Specify the value range for the dataset using a scaling factor for the standard deviation of values in this dataset. All data points above mean value + (scaling factor * standard deviation) and mean value - (scaling factor * standard deviation) will be clipped.");
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 200;
		stdDevClippingExplanationLabel.setLayoutData(gridData);

		stdDevFactorLabel = new Label(clippingGroup, SWT.NONE);
		stdDevFactorLabel.setText("Standard Deviation Scaling Factor");
		stdDevFactorTextField = new Text(clippingGroup, SWT.BORDER);
		stdDevFactorTextField.addListener(SWT.Modify, listener);
		stdDevFactorTextField.setEnabled(false);

		clippingGroup.layout(true, true);
	}

	private void useMinMaxClipping(boolean enabled) {
		// boolean selected = stdDevClippingButton.getSelection();
		stdDevFactorTextField.setEnabled(!enabled);
		stdDevClippingExplanationLabel.setEnabled(!enabled);
		stdDevFactorLabel.setEnabled(!enabled);

		maxButton.setEnabled(enabled);
		minButton.setEnabled(enabled);
		maxTextField.setEnabled(maxButton.getSelection() && enabled);
		minTextField.setEnabled(minButton.getSelection() && enabled);
		minMaxClippingExplanationLabel.setEnabled(enabled);

	}

	/**
	 * Enables or disables maxTextField.
	 */
	private void maxButtonSelected() {
		maxTextField.setEnabled(maxButton.getSelection());
	}

	/**
	 * Enables or disables minTextField.
	 */
	private void minButtonSelected() {
		minTextField.setEnabled(minButton.getSelection());
	}

	@Override
	public boolean isContentValid() {
		if (minMaxClippingButton.getSelection()) {
			if (maxButton.getSelection()) {
				try {
					Float.parseFloat(maxTextField.getText());
				} catch (NumberFormatException e) {
					return false;
				}
			}
			if (minButton.getSelection()) {
				try {
					Float.parseFloat(minTextField.getText());
				} catch (NumberFormatException e) {
					return false;
				}
			}
		}
		if (stdDevClippingButton.getSelection()) {
			try {
				Float.parseFloat(stdDevFactorTextField.getText());
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void updateProperties(final NumericalProperties numericalProperties) {
		boolean maxDefined = numericalProperties.getMax() != null;
		maxButton.setEnabled(true);
		maxButton.setSelection(false);
		maxTextField.setEnabled(false);
		if (maxDefined)
			maxTextField.setText(numericalProperties.getMax().toString());
		else
			maxTextField.setText("");

		boolean minDefined = numericalProperties.getMin() != null;
		minButton.setEnabled(true);
		minButton.setSelection(false);
		minTextField.setEnabled(false);
		if (minDefined)
			minTextField.setText(numericalProperties.getMin().toString());
		else
			minTextField.setText("");
		minMaxClippingButton.setEnabled(true);

		boolean stdDevScalingFactorDefined = numericalProperties.getClipToStdDevFactor() != null;
		stdDevClippingButton.setEnabled(true);

		stdDevFactorTextField.setEnabled(stdDevScalingFactorDefined);
		if (stdDevScalingFactorDefined)
			stdDevFactorTextField.setText(numericalProperties.getClipToStdDevFactor().toString());
		else
			stdDevFactorTextField.setText("");

		if (stdDevScalingFactorDefined) {
			minMaxClippingButton.setSelection(false);
			stdDevClippingButton.setSelection(true);
			useMinMaxClipping(false);
		} else {
			// minmax clipping should be default, even if nothing is specified in properties
			stdDevClippingButton.setSelection(false);
			minMaxClippingButton.setSelection(true);
			useMinMaxClipping(true);
		}
	}

	@Override
	public void setProperties(NumericalProperties numericalProperties) {
		if (minMaxClippingButton.getSelection())
			if (minTextField.getEnabled() && !minTextField.getText().isEmpty()) {
				float min = Float.parseFloat(minTextField.getText());
				if (!Float.isNaN(min)) {
					numericalProperties.setClipToStdDevFactor(null);
					numericalProperties.setMin(min);
				}
			}
		if (maxTextField.getEnabled() && !maxTextField.getText().isEmpty()) {
			float max = Float.parseFloat(maxTextField.getText());
			if (!Float.isNaN(max)) {
				numericalProperties.setClipToStdDevFactor(null);
				numericalProperties.setMax(max);
			}
		} else if (stdDevClippingButton.getSelection()) {
			if (stdDevFactorTextField.getEnabled() && !stdDevFactorTextField.getText().isEmpty()) {
				float factor = Float.parseFloat(stdDevFactorTextField.getText());
				if (!Float.isNaN(factor)) {
					numericalProperties.setMin(null);
					numericalProperties.setMax(null);
					numericalProperties.setClipToStdDevFactor(factor);
				}
			}
		}
	}

	@Override
	public void dispose() {
		clippingGroup.dispose();
	}

}
