/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport.wizard;

import java.util.EnumSet;
import java.util.List;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.io.DataDescriptionUtil;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.io.gui.dataimport.widget.CategoricalDataPropertiesWidget;
import org.caleydo.core.io.gui.dataimport.widget.numerical.NumericalDataPropertiesCollectionWidget;
import org.caleydo.core.io.gui.dataimport.widget.numerical.NumericalDataPropertiesCollectionWidget.ENumericalDataProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog to define the data properties for a column of an inhomogeneous dataset.
 *
 * @author Christian Partl
 *
 */
public class ColumnDataPropertiesDialog extends Dialog implements Listener {

	private CategoricalClassDescription<String> categoricalClassDescription;

	private NumericalProperties numericalProperties;

	private boolean isNumericalData;

	private Button numericalDataButton;

	private Button categoricalDataButton;

	private NumericalDataPropertiesCollectionWidget numericalDataPropertiesWidget;

	private CategoricalDataPropertiesWidget categoricalDataPropertiesWidget;

	private Composite parentComposite;

	private List<List<String>> datasetMatrix;

	private int columnIndex;

	private EDataType dataType = EDataType.FLOAT;

	private ScrolledComposite scrolledComposite;

	private String columnCaption;

	/**
	 * @param parentShell
	 */
	public ColumnDataPropertiesDialog(Shell parentShell, NumericalProperties numericalProperties, EDataType dataType,
			List<List<String>> datasetMatrix, int columnIndex, String columnCaption) {
		super(parentShell);
		this.numericalProperties = numericalProperties;
		this.datasetMatrix = datasetMatrix;
		this.columnIndex = columnIndex;
		this.dataType = dataType;
		this.columnCaption = columnCaption;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	/**
	 * @param parentShell
	 */
	public ColumnDataPropertiesDialog(Shell parentShell,
			CategoricalClassDescription<String> categoricalClassDescription, List<List<String>> datasetMatrix,
			int columnIndex, String columnCaption) {
		super(parentShell);
		this.categoricalClassDescription = categoricalClassDescription;
		this.datasetMatrix = datasetMatrix;
		this.columnIndex = columnIndex;
		this.columnCaption = columnCaption;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Data Properties Of Column " + columnCaption);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		GridLayout l = new GridLayout(1, true);
		l.horizontalSpacing = 0;
		l.verticalSpacing = 0;
		l.marginHeight = 0;
		l.marginHeight = 0;
		scrolledComposite.setLayout(l);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		// this.parent = new Composite(scrolledComposite, SWT.NONE);
		//
		// this.parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// this.parent.setLayout(l);

		parentComposite = new Composite(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(parentComposite);
		parentComposite.setLayout(new GridLayout(1, false));
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// parentComposite.setLayoutData(new GridData(820, 660));

		Group group = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		group.setText("Data Type");
		group.setLayout(new GridLayout(1, true));
		numericalDataButton = new Button(group, SWT.RADIO);
		numericalDataButton.setText("Numerical");
		numericalDataButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showNumericalDataWidgets();
			}
		});

		categoricalDataButton = new Button(group, SWT.RADIO);
		categoricalDataButton.setText("Categorical");
		categoricalDataButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showCategoricalDataWidgets();
			}
		});

		if (numericalProperties != null) {
			numericalDataButton.setSelection(true);
			showNumericalDataWidgets();
		} else {
			categoricalDataButton.setSelection(true);
			showCategoricalDataWidgets();
		}

		scrolledComposite.setMinSize(850, 700);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 900;
		gd.heightHint = 800;
		parent.setLayoutData(gd);
		parent.layout(true, true);
		scrolledComposite.layout(true, true);
		//
		// parentComposite.layout(true);
		// parentComposite.pack();

		parent.pack();
		return parent;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		// TODO Auto-generated method stub
		return super.createButtonBar(parent);
	}

	private void showNumericalDataWidgets() {
		if (categoricalDataPropertiesWidget != null) {
			// temporarily save settings
			categoricalClassDescription = categoricalDataPropertiesWidget.getCategoricalClassDescription();
			categoricalDataPropertiesWidget.dispose();
			parentComposite.layout(true);
			categoricalDataPropertiesWidget = null;
		}

		if (numericalDataPropertiesWidget == null) {
			numericalDataButton.setSelection(true);
			numericalDataPropertiesWidget = new NumericalDataPropertiesCollectionWidget(parentComposite, this,
					EnumSet.of(ENumericalDataProperties.CLIPPING, ENumericalDataProperties.DATA_CENTER,
							ENumericalDataProperties.SCALING));
			if (numericalProperties != null) {
				numericalDataPropertiesWidget.updateNumericalProperties(numericalProperties);
				numericalDataPropertiesWidget.setDataType(dataType);
			}
			// numericalDataPropertiesWidget.
			parentComposite.pack();
			parentComposite.layout(true, true);
		}

	}

	@SuppressWarnings("unchecked")
	private void showCategoricalDataWidgets() {

		if (numericalDataPropertiesWidget != null) {
			// temporarily save settings
			dataType = numericalDataPropertiesWidget.getDataType();
			numericalProperties = numericalDataPropertiesWidget.getNumericalProperties();
			numericalDataPropertiesWidget.dispose();
			parentComposite.layout(true);
			numericalDataPropertiesWidget = null;
		}

		if (categoricalDataPropertiesWidget == null) {
			categoricalDataPropertiesWidget = new CategoricalDataPropertiesWidget(parentComposite);
			if (categoricalClassDescription == null) {
				categoricalClassDescription = (CategoricalClassDescription<String>) DataDescriptionUtil
						.createCategoricalDataDescription(datasetMatrix, columnIndex).getCategoricalClassDescription();
			}

			categoricalDataPropertiesWidget.updateCategories(datasetMatrix, columnIndex, categoricalClassDescription);

			parentComposite.layout(true);
		}
		// parentComposite.pack();
	}

	@Override
	protected void okPressed() {
		if (numericalDataButton.getSelection()) {
			isNumericalData = true;
			dataType = numericalDataPropertiesWidget.getDataType();
			numericalProperties = numericalDataPropertiesWidget.getNumericalProperties();
			categoricalClassDescription = null;
		} else {
			isNumericalData = false;
			categoricalClassDescription = categoricalDataPropertiesWidget.getCategoricalClassDescription();
			numericalProperties = null;
		}
		super.okPressed();
	}

	/**
	 * @return the dataType, see {@link #dataType}
	 */
	public EDataType getDataType() {
		return dataType;
	}

	/**
	 * @return the isNumericalData, see {@link #isNumericalData}
	 */
	public boolean isNumericalData() {
		return isNumericalData;
	}

	/**
	 * @return the categoricalClassDescription, see {@link #categoricalClassDescription}
	 */
	public CategoricalClassDescription<String> getCategoricalClassDescription() {
		return categoricalClassDescription;
	}

	/**
	 * @return the numericalProperties, see {@link #numericalProperties}
	 */
	public NumericalProperties getNumericalProperties() {
		return numericalProperties;
	}

	@Override
	public void handleEvent(Event event) {

	}

}
