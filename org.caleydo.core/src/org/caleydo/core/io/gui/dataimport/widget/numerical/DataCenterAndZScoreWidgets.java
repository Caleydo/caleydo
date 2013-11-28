/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget.numerical;

import org.caleydo.core.io.NumericalProperties;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

/**
 * Combined widget of {@link DataCenterWidgets} and {@link ZScoreNormalizationWidgets}, as they are not independent.
 *
 * @author Christian Partl
 *
 */
public class DataCenterAndZScoreWidgets implements INumericalDataPropertiesWidgets, IRowAndColumnDenominationUser {

	protected ZScoreNormalizationWidgets zScoreWidgets;
	protected DataCenterWidgets dataCenterWidgets;

	@Override
	public void create(Composite parent, Listener listener) {
		zScoreWidgets = new ZScoreNormalizationWidgets();
		dataCenterWidgets = new DataCenterWidgets();

		zScoreWidgets.create(parent, listener);
		dataCenterWidgets.create(parent, listener);

		zScoreWidgets.addUsageSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				useZScoreNormalization(zScoreWidgets.useNormalizationButton.getSelection());
			}
		});
	}

	private void useZScoreNormalization(boolean isUsed) {
		dataCenterWidgets.setEnabled(!isUsed);
		if (isUsed) {
			dataCenterWidgets.useDataCenterButton.setSelection(true);
			dataCenterWidgets.dataCenterTextField.setText("0");
		}
	}

	@Override
	public boolean isContentValid() {
		return zScoreWidgets.isContentValid() && dataCenterWidgets.isContentValid();
	}

	@Override
	public void updateProperties(NumericalProperties numericalProperties) {
		zScoreWidgets.updateProperties(numericalProperties);
		dataCenterWidgets.updateProperties(numericalProperties);

	}

	@Override
	public void setProperties(NumericalProperties numericalProperties) {
		zScoreWidgets.setProperties(numericalProperties);
		dataCenterWidgets.setProperties(numericalProperties);

	}

	@Override
	public void dispose() {
		zScoreWidgets.dispose();
		dataCenterWidgets.dispose();

	}

	@Override
	public void setRowAndColumnDenomination(String rowName, String columnName) {
		zScoreWidgets.setRowAndColumnDenomination(rowName, columnName);
	}

}
