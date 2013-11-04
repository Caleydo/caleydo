/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget.numerical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Christian
 *
 */
public class NumericalDataPropertiesCollectionWidget {

	public static enum ENumericalDataProperties {
		SCALING(ScalingWidgets.class), CLIPPING(ClippingWidgets.class), Z_SCORE_NORMALIZATION(
				ZScoreNormalizationWidgets.class), DATA_CENTER(DataCenterWidgets.class), IMPUTATION(
				ImputationWidgets.class);

		private Class<? extends INumericalDataPropertiesWidgets> propertiesWidgetClass;

		private ENumericalDataProperties(Class<? extends INumericalDataPropertiesWidgets> propertiesWidgetClass) {
			this.propertiesWidgetClass = propertiesWidgetClass;
		}

		/**
		 * @return the propertiesWidgetClass, see {@link #propertiesWidgetClass}
		 */
		protected Class<? extends INumericalDataPropertiesWidgets> getPropertiesWidgetClass() {
			return propertiesWidgetClass;
		}
	}

	/**
	 * Parent composite of all widgets.
	 */
	protected Composite parent;

	/**
	 * Combo to define the type of the data.
	 */
	protected Combo dataTypeCombo;

	protected Group dataTypeGroup;

	protected NumericalProperties numericalProperties;

	protected final List<INumericalDataPropertiesWidgets> propertiesWidgets;

	/**
	 *
	 */
	public NumericalDataPropertiesCollectionWidget(Composite parent, Listener listener,
			final EnumSet<ENumericalDataProperties> properties) {
		this.parent = parent;
		EnumSet<ENumericalDataProperties> props = EnumSet.copyOf(properties);
		boolean useDataCenterAndZScore = false;
		if (props.contains(ENumericalDataProperties.DATA_CENTER)
				&& props.contains(ENumericalDataProperties.Z_SCORE_NORMALIZATION)) {
			props.remove(ENumericalDataProperties.DATA_CENTER);
			props.remove(ENumericalDataProperties.Z_SCORE_NORMALIZATION);
			useDataCenterAndZScore = true;
		}

		List<INumericalDataPropertiesWidgets> propertiesWidgets = new ArrayList<>(properties.size());
		for (ENumericalDataProperties p : props) {
			try {
				propertiesWidgets.add(p.getPropertiesWidgetClass().newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				Logger.log(new Status(IStatus.ERROR, "Numerical properties", "Could not instanciate widgets for "
						+ p.name()));
			}
		}
		if (useDataCenterAndZScore) {
			propertiesWidgets.add(new DataCenterAndZScoreWidgets());
		}

		this.propertiesWidgets = Collections.unmodifiableList(propertiesWidgets);

		createDataTypeGroup(parent);

		for (INumericalDataPropertiesWidgets w : propertiesWidgets) {
			w.create(parent, listener);
		}
	}

	public void dataSetDescriptionUpdated(DataSetDescription dataSetDescription) {
		for (INumericalDataPropertiesWidgets w : propertiesWidgets) {
			if (dataSetDescription != null && w instanceof IRowAndColumnDenominationUser) {
				IRowAndColumnDenominationUser u = (IRowAndColumnDenominationUser) w;
				u.setRowAndColumnDenomination(dataSetDescription.getRowIDSpecification().getIdCategory(),
						dataSetDescription.getColumnIDSpecification().getIdCategory());
			}
		}
	}

	public void updateNumericalProperties(NumericalProperties numericalProperties) {
		this.numericalProperties = numericalProperties;

		for (INumericalDataPropertiesWidgets w : propertiesWidgets) {
			w.updateProperties(numericalProperties);

		}

	}

	private void createDataTypeGroup(Composite parent) {
		dataTypeGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		dataTypeGroup.setText("Data Type");
		dataTypeGroup.setLayout(new GridLayout(2, false));
		dataTypeGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label dataTypeExplanationLabel = new Label(dataTypeGroup, SWT.WRAP);
		dataTypeExplanationLabel.setText("Specify the type of numerical data.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 200;
		dataTypeExplanationLabel.setLayoutData(gridData);

		Label scalingMethodLabel = new Label(dataTypeGroup, SWT.NONE);
		scalingMethodLabel.setText("Type");

		dataTypeCombo = new Combo(dataTypeGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		gridData = new GridData();
		gridData.widthHint = 250;
		dataTypeCombo.setLayoutData(gridData);

		String[] scalingOptions = { "Real Number (Float)", "Natural Number (Integer)" };
		dataTypeCombo.setItems(scalingOptions);
		dataTypeCombo.setEnabled(true);

		dataTypeCombo.select(0);
		dataTypeGroup.layout(true, true);

	}

	public boolean isDataValid() {
		for (INumericalDataPropertiesWidgets w : propertiesWidgets) {
			if (!w.isContentValid())
				return false;
		}

		return true;
	}

	/**
	 * @return {@link NumericalProperties} as specified by the gui elements of this widget.
	 */
	public NumericalProperties getNumericalProperties() {
		NumericalProperties numericalProperties = new NumericalProperties();

		for (INumericalDataPropertiesWidgets w : propertiesWidgets) {
			w.setProperties(numericalProperties);
		}

		return numericalProperties;
	}

	public EDataType getDataType() {
		if (dataTypeCombo.getSelectionIndex() == 0)
			return EDataType.FLOAT;
		return EDataType.INTEGER;
	}

	public void setDataType(EDataType dataType) {
		if (dataType == EDataType.FLOAT)
			dataTypeCombo.select(0);
		else
			dataTypeCombo.select(1);
	}

	public void dispose() {
		for (INumericalDataPropertiesWidgets w : propertiesWidgets) {
			w.dispose();
		}
	}
}
