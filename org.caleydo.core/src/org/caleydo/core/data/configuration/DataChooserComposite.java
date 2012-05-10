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
/**
 * 
 */
package org.caleydo.core.data.configuration;

import java.util.ArrayList;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.io.gui.IDataOKListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Composite that lets a user determine which instance of {@link ATableBasedDataDomain} and the respective
 * {@link ADataPerspective}s to use.
 * 
 * @author Alexander Lex
 */
public class DataChooserComposite
	extends Composite {

	public static int DATA_READY_EVENT = 1986;

	/** The data domain chosen by the user */
	private ATableBasedDataDomain dataDomain;
	/** The record perspective chosen by the user */
	private RecordPerspective recordPerspective;
	/** The dimension perspective chosen by the user */
	private DimensionPerspective dimensionPerspective;

	/** The parent of this widget which is notified when the data is ready */
	IDataOKListener dataOKListener;

	private Combo dataDomainChooser;
	private Combo recordPerspectiveChooser;
	private Combo dimensionPerspectiveChooser;

	private String[] possibleDataDomains;
	private String[] possibleRecordPerspectives;
	private String[] possibleDimensionPerspectives;

	private Composite parent;

	/**
	 * Constructor
	 * 
	 * @param dataOKListener
	 *            see {@link #dataOKListener}
	 * @param parent
	 *            the parent composite
	 * @param style
	 *            the SWT style
	 */
	public DataChooserComposite(IDataOKListener dataOKListener, Composite parent, int style) {
		super(parent, style);
		this.dataOKListener = dataOKListener;
		this.parent = parent;
	}

	/** Creates the GUI for this composite */
	public void initGui() {
		// Composite composite = new Composite(parent, SWT.BORDER_DASH);
		GridLayout layout = new GridLayout(2, false);
		this.setLayout(layout);
		// composite.setLayout(layout);

		int labelWidth = 150;
		Label dataDomainLabel = new Label(this, SWT.BORDER);
		dataDomainLabel.setText("Data set:");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.minimumWidth = labelWidth;
		dataDomainLabel.setLayoutData(data);

		dataDomainChooser = new Combo(this, SWT.DROP_DOWN | SWT.BORDER);
		dataDomainChooser.setText("Choose data set");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.minimumWidth = 400;
		dataDomainChooser.setLayoutData(data);

		ArrayList<ATableBasedDataDomain> tDataDomains =
			DataDomainManager.get().getDataDomainsByType(ATableBasedDataDomain.class);
		possibleDataDomains = new String[tDataDomains.size()];
		for (int count = 0; count < tDataDomains.size(); count++) {
			possibleDataDomains[count] = tDataDomains.get(count).getDataDomainID();
			// String possibleDataDomain = possibleDataDomains[count];
			dataDomainChooser.add(tDataDomains.get(count).getLabel(), count);
		}
		if (possibleDataDomains.length == 1) {
			dataDomainChooser.select(0);
			dataDomain =
				(ATableBasedDataDomain) DataDomainManager.get().getDataDomainByID(possibleDataDomains[0]);
		}

		dataDomainChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				String dataDomainID = possibleDataDomains[dataDomainChooser.getSelectionIndex()];
				System.out.println(dataDomainID);
				dataDomain = (ATableBasedDataDomain) DataDomainManager.get().getDataDomainByID(dataDomainID);
				initDataPerspectiveChoosers(parent);
				checkOK();
			}
		});

		Label recordPerspectiveLabel = new Label(this, SWT.BORDER);
		recordPerspectiveLabel.setText("Rows: ");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.minimumWidth = labelWidth;
		recordPerspectiveLabel.setLayoutData(data);

		recordPerspectiveChooser = new Combo(this, SWT.DROP_DOWN | SWT.BORDER);
		recordPerspectiveChooser.setText("Choose record perspective");

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.minimumWidth = 400;
		recordPerspectiveChooser.setLayoutData(data);

		recordPerspectiveChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectRecordPerspective(recordPerspectiveChooser.getSelectionIndex());
				checkOK();
			}
		});

		Label dimensionPerspectiveLabel = new Label(this, SWT.BORDER);
		dimensionPerspectiveLabel.setText("Columns: ");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.minimumWidth = labelWidth;
		dimensionPerspectiveLabel.setLayoutData(data);

		dimensionPerspectiveChooser = new Combo(this, SWT.DROP_DOWN | SWT.BORDER);
		dimensionPerspectiveChooser.setText("Choose dimension perspective");

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.minimumWidth = 400;
		dimensionPerspectiveChooser.setLayoutData(data);

		dimensionPerspectiveChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectDimensionPerspective(dimensionPerspectiveChooser.getSelectionIndex());
				checkOK();
			}
		});

		initDataPerspectiveChoosers(parent);
	}

	private final void initDataPerspectiveChoosers(Composite parent) {
		if (dataDomain != null) {
			possibleRecordPerspectives = dataDomain.getRecordPerspectiveIDs().toArray(new String[0]);
			possibleDimensionPerspectives = dataDomain.getDimensionPerspectiveIDs().toArray(new String[0]);
		}
		else {
			possibleRecordPerspectives = new String[] { "Choose data set first!" };
			possibleDimensionPerspectives = new String[] { "Choose data set first!" };
			return;
		}

		recordPerspectiveChooser.removeAll();
		for (int index = 0; index < possibleRecordPerspectives.length; index++) {
			String possibleDataPerspective = possibleRecordPerspectives[index];
			String recordPerspectiveLabel =
				dataDomain.getTable().getRecordPerspective(possibleDataPerspective).getLabel();
			recordPerspectiveChooser.add(recordPerspectiveLabel, index);
		}

		dimensionPerspectiveChooser.removeAll();
		for (int index = 0; index < possibleDimensionPerspectives.length; index++) {
			String possibleDataPerspective = possibleDimensionPerspectives[index];
			String dimensionPerspectiveLabel =
				dataDomain.getTable().getDimensionPerspective(possibleDataPerspective).getLabel();
			dimensionPerspectiveChooser.add(dimensionPerspectiveLabel, index);
		}

		if (dataDomain == null) {
			recordPerspectiveChooser.setEnabled(false);
			dimensionPerspectiveChooser.setEnabled(false);
		}
		else {
			selectRecordPerspective(0);
			recordPerspectiveChooser.select(0);
			selectDimensionPerspective(0);
			dimensionPerspectiveChooser.select(0);

			recordPerspectiveChooser.setEnabled(true);
			dimensionPerspectiveChooser.setEnabled(true);
		}
	}

	private final void selectDimensionPerspective(int index) {
		String dimensionPerspectiveID = possibleDimensionPerspectives[index];
		dimensionPerspective = dataDomain.getTable().getDimensionPerspective(dimensionPerspectiveID);
	}

	private final void selectRecordPerspective(int index) {
		String recordPerspectiveID = possibleRecordPerspectives[index];
		recordPerspective = dataDomain.getTable().getRecordPerspective(recordPerspectiveID);
	}

	private final boolean checkOK() {
		if (dataDomain == null || recordPerspective == null || dimensionPerspective == null) {
			return false;
		}
		dataOKListener.dataOK();
		return true;
	}

	/**
	 * Tells the caller whether all the data has been correctly chosen by the user and is ready to be accessed
	 * 
	 * @return true if the data is ready, else false.
	 */
	public final boolean isOK() {
		if (dataDomain == null || recordPerspective == null || dimensionPerspective == null) {
			return false;
		}
		return true;
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * @return the recordPerspective, see {@link #recordPerspective}
	 */
	public RecordPerspective getRecordPerspective() {
		return recordPerspective;
	}

	/**
	 * @return the dimensionPerspective, see {@link #dimensionPerspective}
	 */
	public DimensionPerspective getDimensionPerspective() {
		return dimensionPerspective;
	}

	public DataConfiguration getDataConfiguration() {
		DataConfiguration config = new DataConfiguration();
		config.setDataDomain(dataDomain);
		config.setDimensionPerspective(dimensionPerspective);
		config.setRecordPerspective(recordPerspective);
		return config;
	}

}
