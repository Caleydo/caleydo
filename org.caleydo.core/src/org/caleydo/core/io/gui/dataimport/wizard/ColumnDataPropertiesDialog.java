/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.io.gui.dataimport.wizard;

import java.util.List;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.io.gui.dataimport.widget.CategoricalDataPropertiesWidget;
import org.caleydo.core.io.gui.dataimport.widget.NumericalDataPropertiesWidget;
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

	private NumericalDataPropertiesWidget numericalDataPropertiesWidget;

	private CategoricalDataPropertiesWidget categoricalDataPropertiesWidget;

	private Composite parentComposite;

	private List<List<String>> datasetMatrix;

	private int columnIndex;

	private EDataType dataType = EDataType.FLOAT;

	private ScrolledComposite scrolledComposite;

	/**
	 * @param parentShell
	 */
	protected ColumnDataPropertiesDialog(Shell parentShell, NumericalProperties numericalProperties,
			EDataType dataType, List<List<String>> datasetMatrix, int columnIndex) {
		super(parentShell);
		this.numericalProperties = numericalProperties;
		this.datasetMatrix = datasetMatrix;
		this.columnIndex = columnIndex;
		this.dataType = dataType;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	/**
	 * @param parentShell
	 */
	protected ColumnDataPropertiesDialog(Shell parentShell,
			CategoricalClassDescription<String> categoricalClassDescription, List<List<String>> datasetMatrix,
			int columnIndex) {
		super(parentShell);
		this.categoricalClassDescription = categoricalClassDescription;
		this.datasetMatrix = datasetMatrix;
		this.columnIndex = columnIndex;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Column Data Properties");
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


		scrolledComposite.setMinSize(820, 660);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 825;
		gd.heightHint = 760;
		parent.setLayoutData(gd);
		// parent.layout(true, true);
		// parent.pack(true);
		//
		// parentComposite.layout(true);
		// parentComposite.pack();


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
			numericalDataPropertiesWidget = new NumericalDataPropertiesWidget(parentComposite, this);
			if (numericalProperties != null) {
				numericalDataPropertiesWidget.updateNumericalProperties(numericalProperties);
				numericalDataPropertiesWidget.setDataType(dataType);
			}
			parentComposite.layout(true);
		}
		// parentComposite.pack();
	}

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
				categoricalDataPropertiesWidget.updateCategories(datasetMatrix, columnIndex);
			} else {
				categoricalDataPropertiesWidget.updateCategories(datasetMatrix, columnIndex,
						categoricalClassDescription);
			}
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
