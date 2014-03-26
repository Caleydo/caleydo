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
		clippingGroup.setLayout(new GridLayout(2, true));
		clippingGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		stdDevClippingButton = new Button(clippingGroup, SWT.RADIO);
		stdDevClippingButton.setText("Standard Deviation Clipping");
		stdDevClippingButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		stdDevClippingButton.addListener(SWT.Selection, listener);
		stdDevClippingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				useMinMaxClipping(false);
			}
		});

		minMaxClippingButton = new Button(clippingGroup, SWT.RADIO);
		minMaxClippingButton.setText("Minimum/Maximum Value Clipping");
		minMaxClippingButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		minMaxClippingButton.addListener(SWT.Selection, listener);
		minMaxClippingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				useMinMaxClipping(true);
			}
		});

		Composite stdDevComposite = new Composite(clippingGroup, SWT.NONE);
		stdDevComposite.setLayout(new GridLayout(2, false));
		stdDevComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Composite minMaxComposite = new Composite(clippingGroup, SWT.NONE);
		minMaxComposite.setLayout(new GridLayout(2, false));
		minMaxComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		minMaxClippingExplanationLabel = new Label(minMaxComposite, SWT.WRAP);
		minMaxClippingExplanationLabel.setText("Specify a minimum and/or maximum value beyond which to clip the data.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 200;
		minMaxClippingExplanationLabel.setLayoutData(gridData);

		maxButton = new Button(minMaxComposite, SWT.CHECK);
		maxButton.setText("Max");
		maxButton.addListener(SWT.Selection, listener);
		maxButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				maxButtonSelected();
			}
		});

		maxTextField = new Text(minMaxComposite, SWT.BORDER);
		maxTextField.addListener(SWT.Modify, listener);
		maxTextField.setEnabled(false);
		maxButton.setEnabled(true);

		minButton = new Button(minMaxComposite, SWT.CHECK);
		minButton.setText("Min");
		minButton.addListener(SWT.Selection, listener);
		minButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				minButtonSelected();
			}
		});

		minTextField = new Text(minMaxComposite, SWT.BORDER);
		minTextField.addListener(SWT.Modify, listener);
		minTextField.setEnabled(false);

		stdDevClippingExplanationLabel = new Label(stdDevComposite, SWT.WRAP);
		stdDevClippingExplanationLabel
				.setText("Specify a \"clipping factor\" that is used to determine which values are clipped. Values outside of mean +/- (factor * standard deviation) are clipped to the respective maximum or minimum.");
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 200;
		stdDevClippingExplanationLabel.setLayoutData(gridData);

		stdDevFactorLabel = new Label(stdDevComposite, SWT.NONE);
		stdDevFactorLabel.setText("Clipping Factor");
		stdDevFactorTextField = new Text(stdDevComposite, SWT.BORDER);
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
				float val = Float.parseFloat(stdDevFactorTextField.getText());
				if (val < 0)
					return false;
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
			stdDevFactorTextField.setText("3");

		// stddev clipping should be default, even if minmax is specified (which is automatically set when parsing for
		// the data type) or nothing is specified in properties
		minMaxClippingButton.setSelection(false);
		stdDevClippingButton.setSelection(true);
		useMinMaxClipping(false);

	}

	@Override
	public void setProperties(NumericalProperties numericalProperties) {
		if (minMaxClippingButton.getSelection()) {
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
		} else {
			numericalProperties.setMin(null);
			numericalProperties.setMax(null);
			numericalProperties.setClipToStdDevFactor(null);
		}

	}

	@Override
	public void dispose() {
		clippingGroup.dispose();
	}

}
