/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.pathway.toolbar;

import java.util.List;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.event.ClearMappingEvent;
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
public class DatasetSelectionBox
	extends ControlContribution
	implements IToolBarItem {

	public static final int TOOLBAR_WIDTH = 300;

	/** mediator to handle actions triggered by the contributed element */
	private PathwayToolBarMediator pathwayToolBarMediator;

	private Combo dataSetChooser;

	private IDataDomain mappingDataDomain;

	/**
	 * constructor as requested by ControlContribution
	 * 
	 * @param str
	 */
	public DatasetSelectionBox(String str, IDataDomain mappingDataDomain) {
		super(str);
		this.mappingDataDomain = mappingDataDomain;
	}

	@Override
	protected Control createControl(Composite parent) {

		dataSetChooser = new Combo(parent, SWT.BORDER);
		dataSetChooser.setText("Choose data set");
		dataSetChooser
				.setToolTipText("Select which dataset should be used for mapping experimental data onto the nodes of the pathway.");
		GridData gd = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		gd.widthHint = 120;
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

				for (AGLView view : ViewManager.get().getAllGLViews()) {
					if (view instanceof GLPathway) {
						GLPathway pwView = (GLPathway) view;
						AddTablePerspectivesEvent addTablePerspectivesEvent = new AddTablePerspectivesEvent();
						addTablePerspectivesEvent.setReceiver(pwView);
						TablePerspective tablePerspective = null;
						if (dataSetChooser.getSelectionIndex() != 0) {
							GeneticDataDomain dataDomain = candidateDataDomains.get(dataSetChooser.getSelectionIndex() - 1);
							addTablePerspectivesEvent.setDataDomainID(dataDomain.getDataDomainID());
							tablePerspective = dataDomain.getDefaultTablePerspective();
							tablePerspective.setPrivate(false);

							addTablePerspectivesEvent.addTablePerspective(tablePerspective);
							GeneralManager.get().getEventPublisher().triggerEvent(addTablePerspectivesEvent);
						}
						else {
							ClearMappingEvent event = new ClearMappingEvent(pwView);
							event.setSender(this);
							GeneralManager.get().getEventPublisher().triggerEvent(event);
						}

					}
				}
			}
		});

		return dataSetChooser;

	}

	@Override
	protected int computeWidth(Control control) {
		return TOOLBAR_WIDTH;
	}

	public PathwayToolBarMediator getPathwayToolBarMediator() {
		return pathwayToolBarMediator;
	}

	public void setPathwayToolBarMediator(PathwayToolBarMediator pathwayToolBarMediator) {
		this.pathwayToolBarMediator = pathwayToolBarMediator;
	}
}
