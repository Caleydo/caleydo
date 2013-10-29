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
		SCALING(ScalingWidgets.class), CLIPPING(ClippingWidgets.class), DATA_CENTER(DataCenterWidgets.class);

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

	// /**
	// * Text field that specifies the minimum data clipping value.
	// */
	// protected Text minTextField;
	// /**
	// * Text field that specifies the minimum data clipping value.
	// */
	// protected Text maxTextField;
	//
	// /**
	// * Button to determine whether a data center is used.
	// */
	// protected Button useDataCenterButton;
	//
	// /**
	// * Text field used to define the data center.
	// */
	// protected Text dataCenterTextField;
	//
	// /**
	// * Button to enable the {@link #maxTextField};
	// */
	// protected Button maxButton;
	//
	// /**
	// * Button to enable the {@link #minTextField};
	// */
	// protected Button minButton;
	//
	// /**
	// * Combo to define the scaling method that should be applied to the data.
	// */
	// protected Combo scalingCombo;
	//
	// /**
	// * Group that contains widgets associated with determining the data center.
	// */
	// protected Group dataCenterGroup;
	//
	/**
	 * Combo to define the type of the data.
	 */
	protected Combo dataTypeCombo;
	//
	// protected Group scalingGroup;
	//
	// protected Group clippingGroup;
	//
	protected Group dataTypeGroup;
	//
	protected NumericalProperties numericalProperties;
	//
	// protected Map<Integer, String> scalingTextMap = new HashMap<>();

	protected final List<INumericalDataPropertiesWidgets> propertiesWidgets;

	/**
	 *
	 */
	public NumericalDataPropertiesCollectionWidget(Composite parent, Listener listener,
			final EnumSet<ENumericalDataProperties> properties) {
		this.parent = parent;
		List<INumericalDataPropertiesWidgets> propertiesWidgets = new ArrayList<>(properties.size());
		for (ENumericalDataProperties p : properties) {
			try {
				propertiesWidgets.add(p.getPropertiesWidgetClass().newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				Logger.log(new Status(IStatus.ERROR, "Numerical properties", "Could not instanciate widgets for "
						+ p.name()));
			}
		}
		this.propertiesWidgets = Collections.unmodifiableList(propertiesWidgets);

		createDataTypeGroup(parent);

		for (INumericalDataPropertiesWidgets w : propertiesWidgets) {
			w.create(parent, listener);
		}

		// if (visibleProperties.contains(ENumericalDataProperties.SCALING))
		// createScalingGroup(parent);
		//
		// if (visibleProperties.contains(ENumericalDataProperties.CLIPPING))
		// createClippingGroup(parent, listener);
		//
		// if (visibleProperties.contains(ENumericalDataProperties.DATA_CENTER))
		// createDataCenterGroup(parent, listener);
	}

	public void updateNumericalProperties(NumericalProperties numericalProperties) {
		this.numericalProperties = numericalProperties;

		for (INumericalDataPropertiesWidgets w : propertiesWidgets) {
			w.updateProperties(numericalProperties);
		}

		// if (visibleProperties.contains(ENumericalDataProperties.DATA_CENTER)) {
		// Double dataCenter = numericalProperties.getDataCenter();
		// useDataCenterButton.setSelection(dataCenter != null);
		// dataCenterTextField.setEnabled(dataCenter != null);
		// dataCenterTextField.setText(dataCenter == null ? "0" : dataCenter.toString());
		// }
		//
		// if (visibleProperties.contains(ENumericalDataProperties.CLIPPING)) {
		// boolean maxDefined = numericalProperties.getMax() != null;
		// maxButton.setEnabled(true);
		// maxButton.setSelection(false);
		// maxTextField.setEnabled(false);
		// if (maxDefined)
		// maxTextField.setText(numericalProperties.getMax().toString());
		// else
		// maxTextField.setText("");
		//
		// boolean minDefined = numericalProperties.getMin() != null;
		// minButton.setEnabled(true);
		// minButton.setSelection(false);
		// minTextField.setEnabled(false);
		// if (minDefined)
		// minTextField.setText(numericalProperties.getMin().toString());
		// else
		// minTextField.setText("");
		// }
		//
		// if (visibleProperties.contains(ENumericalDataProperties.SCALING)) {
		// String previousMathFiltermode = numericalProperties.getDataTransformation();
		// if (previousMathFiltermode.equals("Log10"))
		// scalingCombo.select(1);
		// else if (previousMathFiltermode.equals("Log2"))
		// scalingCombo.select(2);
		// else
		// scalingCombo.select(0);
		// }
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

	// private void createDataCenterGroup(Composite parent, Listener listener) {
	// dataCenterGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
	// dataCenterGroup.setText("Data Center");
	// dataCenterGroup.setLayout(new GridLayout(2, false));
	// dataCenterGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	//
	// Label dateCenterExplanationLabel = new Label(dataCenterGroup, SWT.WRAP);
	// dateCenterExplanationLabel
	// .setText("The data center is a value that, if set, determines a neutral center point of the data. A common example is that 0 is the neutral value, lower values are in the negative and larger values are in the positive range. If the data center is set it is assumed that the extend into both, positive and negative direction is the same. For example, for a dataset [-0.5, 0.7] with a center set at 0, the value range will be set to -0.7 to 0.7.");
	// GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
	// gridData.widthHint = 600;
	// dateCenterExplanationLabel.setLayoutData(gridData);
	// useDataCenterButton = new Button(dataCenterGroup, SWT.CHECK);
	// // useDataCenterButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
	// // true, true, 1,
	// // 1));
	// useDataCenterButton.setText("Use data center ");
	// useDataCenterButton.addListener(SWT.Selection, listener);
	// useDataCenterButton.addSelectionListener(new SelectionAdapter() {
	//
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// useDataCenterButtonSelected();
	// }
	// });
	// useDataCenterButton.setSelection(false);
	//
	// dataCenterTextField = new Text(dataCenterGroup, SWT.BORDER);
	// gridData = new GridData(SWT.LEFT, SWT.FILL, false, true);
	// gridData.widthHint = 70;
	// dataCenterTextField.setLayoutData(gridData);
	// dataCenterTextField.addListener(SWT.Modify, listener);
	// dataCenterTextField.setEnabled(false);
	// dataCenterGroup.layout(true, true);
	// }
	//
	// private void createClippingGroup(Composite parent, Listener listener) {
	//
	// clippingGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
	// clippingGroup.setText("Data Clipping");
	// clippingGroup.setLayout(new GridLayout(2, false));
	// clippingGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	//
	// Label clippingExplanationLabel = new Label(clippingGroup, SWT.WRAP);
	// clippingExplanationLabel
	// .setText("Specify the value range for the dataset. By default the value range is infered as the minimum and maximum of the dataset. This can be used to specify bounds in the data. For example, if you want to see your data relative to 0 but all data points are greater than 0 you should specify 0 here as the minimum.  You can also use this to clip your data. If you, for example, select '3' as the maximum every data point exceeding this range will be clipped to '3' for display.");
	// GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
	// gridData.widthHint = 200;
	// clippingExplanationLabel.setLayoutData(gridData);
	//
	// maxButton = new Button(clippingGroup, SWT.CHECK);
	// maxButton.setText("Max");
	// maxButton.addListener(SWT.Selection, listener);
	// maxButton.addSelectionListener(new SelectionAdapter() {
	//
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// maxButtonSelected();
	// }
	// });
	//
	// maxTextField = new Text(clippingGroup, SWT.BORDER);
	// maxTextField.addListener(SWT.Modify, listener);
	// maxTextField.setEnabled(false);
	// maxButton.setEnabled(true);
	//
	// minButton = new Button(clippingGroup, SWT.CHECK);
	// minButton.setText("Min");
	// minButton.addListener(SWT.Selection, listener);
	// minButton.addSelectionListener(new SelectionAdapter() {
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// minButtonSelected();
	// }
	// });
	//
	// minTextField = new Text(clippingGroup, SWT.BORDER);
	// minTextField.addListener(SWT.Modify, listener);
	// minTextField.setEnabled(false);
	// clippingGroup.layout(true, true);
	//
	// }
	//
	// private void createScalingGroup(Composite parent) {
	// scalingGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
	// scalingGroup.setText("Data Scale");
	// scalingGroup.setLayout(new GridLayout(2, false));
	// scalingGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	//
	// Label scalingExplanationLabel = new Label(scalingGroup, SWT.WRAP);
	// scalingExplanationLabel
	// .setText("Specify the way the data should be scaled. A linear scale is best if you are interested in absolute relationships within your data. A logarithmic scale is best if you are interested in the relative differences between data points and if your data is distributed inhomogeneously on a wide scale. Gene expression data, for example, is usually interpreted on a logarithmic scale.");
	// GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
	// gridData.widthHint = 200;
	// scalingExplanationLabel.setLayoutData(gridData);
	//
	// Label scalingMethodLabel = new Label(scalingGroup, SWT.NONE);
	// scalingMethodLabel.setText("Scaling Method");
	//
	// scalingCombo = new Combo(scalingGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
	// gridData = new GridData();
	// gridData.widthHint = 120;
	// scalingCombo.setLayoutData(gridData);
	//
	// String[] scalingOptions = { Transformation.LINEAR, NumericalTable.Transformation.LOG2,
	// NumericalTable.Transformation.LOG10 };
	// scalingTextMap.put(0, Transformation.LINEAR);
	// scalingTextMap.put(1, NumericalTable.Transformation.LOG2);
	// scalingTextMap.put(2, NumericalTable.Transformation.LOG10);
	// scalingCombo.setItems(scalingOptions);
	// scalingCombo.setEnabled(true);
	//
	// scalingCombo.select(0);
	// scalingGroup.layout(true, true);
	// }

	// /**
	// * Enables or disables maxTextField.
	// */
	// private void maxButtonSelected() {
	// maxTextField.setEnabled(maxButton.getSelection());
	// }
	//
	// /**
	// * Enables or disables minTextField.
	// */
	// private void minButtonSelected() {
	// minTextField.setEnabled(minButton.getSelection());
	// }
	//
	// private void useDataCenterButtonSelected() {
	// dataCenterTextField.setEnabled(useDataCenterButton.getSelection());
	// }

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

		// if (visibleProperties.contains(ENumericalDataProperties.CLIPPING)) {
		// if (minTextField.getEnabled() && !minTextField.getText().isEmpty()) {
		// float min = Float.parseFloat(minTextField.getText());
		// if (!Float.isNaN(min)) {
		// numericalProperties.setMin(min);
		// }
		// }
		// if (maxTextField.getEnabled() && !maxTextField.getText().isEmpty()) {
		// float max = Float.parseFloat(maxTextField.getText());
		// if (!Float.isNaN(max)) {
		// numericalProperties.setMax(max);
		// }
		// }
		// }
		//
		// if (visibleProperties.contains(ENumericalDataProperties.DATA_CENTER)) {
		// if (useDataCenterButton.getSelection()) {
		// numericalProperties.setDataCenter(Double.parseDouble(dataCenterTextField.getText()));
		// }
		// }
		//
		// if (visibleProperties.contains(ENumericalDataProperties.SCALING)) {
		// numericalProperties.setDataTransformation(scalingTextMap.get(scalingCombo.getSelectionIndex()));
		// }

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

		// if (visibleProperties.contains(ENumericalDataProperties.DATA_TYPE))
		// dataTypeGroup.dispose();
		// if (visibleProperties.contains(ENumericalDataProperties.DATA_CENTER))
		// dataCenterGroup.dispose();
		// if (visibleProperties.contains(ENumericalDataProperties.CLIPPING))
		// clippingGroup.dispose();
		// if (visibleProperties.contains(ENumericalDataProperties.SCALING))
		// scalingGroup.dispose();
	}
}
