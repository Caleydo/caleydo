/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.data.configuration;

import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.variable.Perspective;
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
 * Composite that lets a user determine which instance of
 * {@link ATableBasedDataDomain} and the respective {@link Perspective}
 * s to use.
 *
 * @author Alexander Lex
 */
public class DataChooserComposite extends Composite {

	public static int DATA_READY_EVENT = 1986;

	/** The data domain chosen by the user */
	private ATableBasedDataDomain dataDomain;
	/** The record perspective chosen by the user */
	private Perspective recordPerspective;
	/** The dimension perspective chosen by the user */
	private Perspective dimensionPerspective;

	/**
	 * All datadomains that can be chosen from.
	 */
	private List<ATableBasedDataDomain> supportedDataDomains;

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
	 * @param supportedDataDomains
	 *            All datadomains that shall be chosen from. If null, all
	 *            tablebased datadomains are available.
	 * @param style
	 *            the SWT style
	 *
	 */
	public DataChooserComposite(IDataOKListener dataOKListener, Composite parent,
			List<ATableBasedDataDomain> supportedDataDomains, int style) {
		super(parent, style);
		if (supportedDataDomains == null) {
			this.supportedDataDomains = DataDomainManager.get().getDataDomainsByType(
					ATableBasedDataDomain.class);
		} else {
			this.supportedDataDomains = supportedDataDomains;
		}
		this.dataOKListener = dataOKListener;
		this.parent = parent;
		initGui();
	}

	/** Creates the GUI for this composite */
	public void initGui() {
		GridLayout layout = new GridLayout(2, false);
		this.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		this.setLayoutData(gridData);

		Label dataDomainLabel = new Label(this, SWT.NONE);
		dataDomainLabel.setText("Data Set:");
		// dataDomainGroup.setLayout(new GridLayout(1, false));

		dataDomainChooser = new Combo(this, SWT.NONE | SWT.READ_ONLY);
		dataDomainChooser.setText("Choose data set");
		dataDomainChooser.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		possibleDataDomains = new String[supportedDataDomains.size()];
		for (int count = 0; count < supportedDataDomains.size(); count++) {
			possibleDataDomains[count] = supportedDataDomains.get(count)
					.getDataDomainID();
			// String possibleDataDomain = possibleDataDomains[count];
			dataDomainChooser.add(supportedDataDomains.get(count).getLabel(), count);
		}
		if (possibleDataDomains.length == 1) {
			dataDomainChooser.select(0);
			dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
					.getDataDomainByID(possibleDataDomains[0]);
		}

		dataDomainChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				String dataDomainID = possibleDataDomains[dataDomainChooser
						.getSelectionIndex()];
				System.out.println(dataDomainID);
				dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
						.getDataDomainByID(dataDomainID);
				initDataPerspectiveChoosers(parent);
				checkOK();
			}
		});

		Label recordPerspectiveLabel = new Label(this, SWT.NONE);
		recordPerspectiveLabel.setText("Rows:");
		// recordPerspectiveLabel.setLayout(new GridLayout(1, false));

		recordPerspectiveChooser = new Combo(this, SWT.DROP_DOWN | SWT.BORDER
				| SWT.READ_ONLY);
		recordPerspectiveChooser.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false));
		recordPerspectiveChooser.setText("Choose record perspective");

		// data = new GridData(GridData.FILL_HORIZONTAL);
		// data.horizontalSpan = 1;
		// data.minimumWidth = 400;
		// recordPerspectiveChooser.setLayoutData(data);

		recordPerspectiveChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectRecordPerspective(recordPerspectiveChooser.getSelectionIndex());
				checkOK();
			}
		});

		// Label dimensionPerspectiveLabel = new Label(this, SWT.BORDER);
		// dimensionPerspectiveLabel.setText("Columns: ");
		// data = new GridData(GridData.FILL_HORIZONTAL);
		// data.horizontalSpan = 1;
		// data.minimumWidth = labelWidth;
		// dimensionPerspectiveLabel.setLayoutData(data);

		Label dimensionPerspectiveLabel = new Label(this, SWT.NONE);
		dimensionPerspectiveLabel.setText("Columns:");
		// dimensionPerspectiveLabel.setLayout(new GridLayout(1, false));

		dimensionPerspectiveChooser = new Combo(this, SWT.DROP_DOWN | SWT.BORDER
				| SWT.READ_ONLY);
		dimensionPerspectiveChooser.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false));
		dimensionPerspectiveChooser.setText("Choose columns:");
		//
		// data = new GridData(GridData.FILL_HORIZONTAL);
		// data.horizontalSpan = 1;
		// data.minimumWidth = 400;
		// dimensionPerspectiveChooser.setLayoutData(data);

		dimensionPerspectiveChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectDimensionPerspective(dimensionPerspectiveChooser
						.getSelectionIndex());
				checkOK();
			}
		});

		initDataPerspectiveChoosers(parent);
	}

	private final void initDataPerspectiveChoosers(Composite parent) {
		if (dataDomain != null) {
			possibleRecordPerspectives = dataDomain.getRecordPerspectiveIDs().toArray(
					new String[0]);
			possibleDimensionPerspectives = dataDomain.getDimensionPerspectiveIDs()
					.toArray(new String[0]);
		} else {
			possibleRecordPerspectives = new String[] { "Choose data set first!" };
			possibleDimensionPerspectives = new String[] { "Choose data set first!" };
			return;
		}

		recordPerspectiveChooser.removeAll();
		for (int index = 0; index < possibleRecordPerspectives.length; index++) {
			String possibleDataPerspective = possibleRecordPerspectives[index];
			String recordPerspectiveLabel = dataDomain.getTable()
					.getRecordPerspective(possibleDataPerspective).getLabel();
			recordPerspectiveChooser.add(recordPerspectiveLabel, index);
		}

		dimensionPerspectiveChooser.removeAll();
		for (int index = 0; index < possibleDimensionPerspectives.length; index++) {
			String possibleDataPerspective = possibleDimensionPerspectives[index];
			String dimensionPerspectiveLabel = dataDomain.getTable()
					.getDimensionPerspective(possibleDataPerspective).getLabel();
			dimensionPerspectiveChooser.add(dimensionPerspectiveLabel, index);
		}

		if (dataDomain == null) {
			recordPerspectiveChooser.setEnabled(false);
			dimensionPerspectiveChooser.setEnabled(false);
		} else {
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
		dimensionPerspective = dataDomain.getTable().getDimensionPerspective(
				dimensionPerspectiveID);
	}

	private final void selectRecordPerspective(int index) {
		String recordPerspectiveID = possibleRecordPerspectives[index];
		recordPerspective = dataDomain.getTable().getRecordPerspective(
				recordPerspectiveID);
	}

	private final boolean checkOK() {
		if (dataDomain == null || recordPerspective == null
				|| dimensionPerspective == null) {
			return false;
		}
		dataOKListener.dataOK();
		return true;
	}

	/**
	 * Tells the caller whether all the data has been correctly chosen by the
	 * user and is ready to be accessed
	 *
	 * @return true if the data is ready, else false.
	 */
	public final boolean isOK() {
		if (dataDomain == null || recordPerspective == null
				|| dimensionPerspective == null) {
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
	public Perspective getRecordPerspective() {
		return recordPerspective;
	}

	/**
	 * @return the dimensionPerspective, see {@link #dimensionPerspective}
	 */
	public Perspective getDimensionPerspective() {
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
