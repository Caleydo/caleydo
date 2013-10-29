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
 * Widgets for specifying the data center.
 *
 * @author Christian Partl
 *
 */
public class DataCenterWidgets implements INumericalDataPropertiesWidgets {

	/**
	 * Button to determine whether a data center is used.
	 */
	protected Button useDataCenterButton;

	/**
	 * Text field used to define the data center.
	 */
	protected Text dataCenterTextField;

	/**
	 * Group that contains widgets associated with determining the data center.
	 */
	protected Group dataCenterGroup;

	@Override
	public void create(Composite parent, Listener listener) {
		dataCenterGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		dataCenterGroup.setText("Data Center");
		dataCenterGroup.setLayout(new GridLayout(2, false));
		dataCenterGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label dateCenterExplanationLabel = new Label(dataCenterGroup, SWT.WRAP);
		dateCenterExplanationLabel
				.setText("The data center is a value that, if set, determines a neutral center point of the data. A common example is that 0 is the neutral value, lower values are in the negative and larger values are in the positive range. If the data center is set it is assumed that the extend into both, positive and negative direction is the same. For example, for a dataset [-0.5, 0.7] with a center set at 0, the value range will be set to -0.7 to 0.7.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 600;
		dateCenterExplanationLabel.setLayoutData(gridData);
		useDataCenterButton = new Button(dataCenterGroup, SWT.CHECK);
		// useDataCenterButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
		// true, true, 1,
		// 1));
		useDataCenterButton.setText("Use data center ");
		useDataCenterButton.addListener(SWT.Selection, listener);
		useDataCenterButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				useDataCenterButtonSelected();
			}
		});
		useDataCenterButton.setSelection(false);

		dataCenterTextField = new Text(dataCenterGroup, SWT.BORDER);
		gridData = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gridData.widthHint = 70;
		dataCenterTextField.setLayoutData(gridData);
		dataCenterTextField.addListener(SWT.Modify, listener);
		dataCenterTextField.setEnabled(false);
		dataCenterGroup.layout(true, true);
	}

	private void useDataCenterButtonSelected() {
		dataCenterTextField.setEnabled(useDataCenterButton.getSelection());
	}

	@Override
	public boolean isContentValid() {
		if (useDataCenterButton.getSelection()) {
			try {
				Double.parseDouble(dataCenterTextField.getText());
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void updateProperties(final NumericalProperties numericalProperties) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProperties(NumericalProperties numericalProperties) {
		if (useDataCenterButton.getSelection()) {
			numericalProperties.setDataCenter(Double.parseDouble(dataCenterTextField.getText()));
		}
	}

	@Override
	public void dispose() {
		dataCenterGroup.dispose();
	}

}
