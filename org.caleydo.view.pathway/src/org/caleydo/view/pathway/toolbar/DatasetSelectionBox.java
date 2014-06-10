/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.pathway.toolbar;

import java.util.List;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.listener.PathwayMappingEvent;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Drop down to select the dataset to be mapped in the pathway. Uses the default perspective.
 *
 * @author Alexander Lex
 */
public class DatasetSelectionBox extends ControlContribution {

	public static final int TOOLBAR_WIDTH = 300;

	private Combo dataSetChooser;

	private IDataDomain mappingDataDomain;

	private final String eventSpace;

	/**
	 * constructor as requested by ControlContribution
	 *
	 */
	public DatasetSelectionBox(IDataDomain mappingDataDomain, String eventSpace) {
		super("Select Data");
		this.mappingDataDomain = mappingDataDomain;
		this.eventSpace = eventSpace;
	}

	@Override
	protected Control createControl(Composite parent) {

		dataSetChooser = new Combo(parent, SWT.BORDER);
		dataSetChooser.setText("Choose data set");
		dataSetChooser
				.setToolTipText("Select which dataset should be used for mapping experimental data onto the nodes of the pathway.");
		GridData gd = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		gd.widthHint = 100;
		dataSetChooser.setLayoutData(gd);

		final List<GeneticDataDomain> candidateDataDomains = DataDomainManager.get().getDataDomainsByType(
				GeneticDataDomain.class);
		String[] datasetNames = new String[candidateDataDomains.size() + 1];
		datasetNames[0] = "No mapping Dataset";
		int selectionIndex = 0;
		for (int datasetCount = 1; datasetCount <= candidateDataDomains.size(); datasetCount++) {
			datasetNames[datasetCount] = candidateDataDomains.get(datasetCount - 1).getDataSetDescription()
					.getDataSetName();
			if (candidateDataDomains.get(datasetCount - 1) == mappingDataDomain) {
				selectionIndex = datasetCount;
			}
		}

		dataSetChooser.setItems(datasetNames);
		dataSetChooser.select(selectionIndex);

		dataSetChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				// for (AGLView view : ViewManager.get().getAllGLViews()) {
				// if (view instanceof GLPathway) {
				// GLPathway pwView = (GLPathway) view;
				AddTablePerspectivesEvent addTablePerspectivesEvent = new AddTablePerspectivesEvent();
				addTablePerspectivesEvent.setEventSpace(eventSpace);
				// addTablePerspectivesEvent.to(receiver);
				TablePerspective tablePerspective = null;
				if (dataSetChooser.getSelectionIndex() != 0) {
					GeneticDataDomain dataDomain = candidateDataDomains.get(dataSetChooser.getSelectionIndex() - 1);
					addTablePerspectivesEvent.setEventSpace(dataDomain.getDataDomainID());
					tablePerspective = dataDomain.getDefaultTablePerspective();
					tablePerspective.setPrivate(false);

					addTablePerspectivesEvent.addTablePerspective(tablePerspective);
					
					EventPublisher.trigger(addTablePerspectivesEvent);
					PathwayMappingEvent event = new PathwayMappingEvent(tablePerspective);
					event.setSender(this);
					event.setEventSpace(eventSpace);
					// event.to(receiver);
					EventPublisher.trigger(event);

				} else {
					PathwayMappingEvent event = new PathwayMappingEvent();
					event.setSender(this);
					event.setEventSpace(eventSpace);
					
					// event.to(receiver);
					EventPublisher.trigger(event);
				}

			}
			// }
			// }
		});

		return dataSetChooser;

	}

	@Override
	protected int computeWidth(Control control) {
		return TOOLBAR_WIDTH;
	}
}
