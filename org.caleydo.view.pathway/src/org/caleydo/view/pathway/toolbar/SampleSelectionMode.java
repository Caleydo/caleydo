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

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.pathway.ESampleMappingMode;
import org.caleydo.view.pathway.event.SampleMappingModeEvent;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

/**
 * Drop down to select the dataset to be mapped in the pathway. Uses the default perspective.
 * 
 * @author Alexander Lex
 */
public class SampleSelectionMode
	extends ControlContribution
	implements IToolBarItem {

	public static final int TOOLBAR_WIDTH = 280;

	/** mediator to handle actions triggered by the contributed element */
	private PathwayToolBarMediator pathwayToolBarMediator;

	/**
	 * constructor as requested by ControlContribution
	 * 
	 * @param str
	 */
	public SampleSelectionMode(String str) {
		super(str);
	}

	@Override
	protected Control createControl(Composite parent) {

		Group buttonGroup = new Group(parent, SWT.NONE);
		FillLayout rowLayout = new FillLayout(SWT.HORIZONTAL);
		buttonGroup.setLayout(rowLayout);

		Button allSamplesModeRadio = new Button(buttonGroup, SWT.RADIO);
		allSamplesModeRadio.setText("Map all Samples");
		allSamplesModeRadio
				.setToolTipText("If selected an average of all samples of the chosen dataset is mapped onto the pathway nodes.");
		allSamplesModeRadio.setSelection(true);

		Button selectedSampleModeRadio = new Button(buttonGroup, SWT.RADIO);
		selectedSampleModeRadio.setText("Map selected Samples");
		selectedSampleModeRadio
				.setToolTipText("If selected an average of only the selected samples of the chosen dataset is mapped onto the pathway nodes.");

		allSamplesModeRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GeneralManager.get().getEventPublisher()
						.triggerEvent(new SampleMappingModeEvent(ESampleMappingMode.ALL));
			}
		});

		selectedSampleModeRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GeneralManager.get().getEventPublisher()
						.triggerEvent(new SampleMappingModeEvent(ESampleMappingMode.SELECTED));
			}
		});

		// GridData gd = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		// useAllButton.setLayoutData(gd);

		// final List<GeneticDataDomain> candidateDataDomains =
		// DataDomainManager.get().getDataDomainsByType(
		// GeneticDataDomain.class);
		// String[] datasetNames = new String[candidateDataDomains.size() + 1];
		// datasetNames[0] = "No mapping Dataset";
		// for (int datasetCount = 1; datasetCount <=
		// candidateDataDomains.size(); datasetCount++) {
		// datasetNames[datasetCount] = candidateDataDomains.get(datasetCount -
		// 1).getDataSetDescription()
		// .getDataSetName();
		// }

		// dataSetChooser.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		//
		// for (AGLView view : ViewManager.get().getAllGLViews()) {
		// if (view instanceof GLPathway) {
		// GLPathway pwView = (GLPathway) view;
		// AddTablePerspectivesEvent addTablePerspectivesEvent = new
		// AddTablePerspectivesEvent();
		// addTablePerspectivesEvent.setReceiver(pwView);
		// GeneticDataDomain dataDomain =
		// candidateDataDomains.get(dataSetChooser.getSelectionIndex() - 1);
		//
		// addTablePerspectivesEvent.setDataDomainID(dataDomain.getDataDomainID());
		// addTablePerspectivesEvent.addTablePerspecitve(dataDomain.getDefaultTablePerspective());
		//
		// GeneralManager.get().getEventPublisher().triggerEvent(addTablePerspectivesEvent);
		// }
		// }
		// }
		// });

		return buttonGroup;

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
