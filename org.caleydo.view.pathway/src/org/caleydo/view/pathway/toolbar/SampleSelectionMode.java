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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Drop down to select the dataset to be mapped in the pathway. Uses the default perspective.
 * 
 * @author Alexander Lex
 */
public class SampleSelectionMode
	extends ControlContribution
	implements IToolBarItem {

	private ESampleMappingMode mappingMode;

	/** mediator to handle actions triggered by the contributed element */
	private PathwayToolBarMediator pathwayToolBarMediator;

	/**
	 * constructor as requested by ControlContribution
	 * 
	 * @param str
	 */
	public SampleSelectionMode(String str, ESampleMappingMode mappingMode) {
		super(str);
		if(mappingMode == null) {
			this.mappingMode = ESampleMappingMode.ALL;
		}
		this.mappingMode = mappingMode;
	}

	@Override
	protected Control createControl(Composite parent) {
		Composite buttonGroup = new Composite(parent, SWT.NULL);
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);

		buttonGroup.setLayout(rowLayout);

		Button allSamplesModeRadio = new Button(buttonGroup, SWT.RADIO);
		allSamplesModeRadio.setText("Map all Samples");
		allSamplesModeRadio
				.setToolTipText("If selected an average of all samples of the chosen dataset is mapped onto the pathway nodes.");
		
		Button selectedSampleModeRadio = new Button(buttonGroup, SWT.RADIO);
		selectedSampleModeRadio.setText("Map selected Samples");
		selectedSampleModeRadio
				.setToolTipText("If selected an average of only the selected samples of the chosen dataset is mapped onto the pathway nodes.");

		if(mappingMode == ESampleMappingMode.ALL) {
			allSamplesModeRadio.setSelection(true);
		} else {
			selectedSampleModeRadio.setSelection(true);
		}
		
		allSamplesModeRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mappingMode = ESampleMappingMode.ALL;
				GeneralManager.get().getEventPublisher()
						.triggerEvent(new SampleMappingModeEvent(ESampleMappingMode.ALL));
			}
		});

		selectedSampleModeRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mappingMode = ESampleMappingMode.SELECTED;
				GeneralManager.get().getEventPublisher()
						.triggerEvent(new SampleMappingModeEvent(ESampleMappingMode.SELECTED));
			}
		});

		return buttonGroup;

	}

	public PathwayToolBarMediator getPathwayToolBarMediator() {
		return pathwayToolBarMediator;
	}

	public void setPathwayToolBarMediator(PathwayToolBarMediator pathwayToolBarMediator) {
		this.pathwayToolBarMediator = pathwayToolBarMediator;
	}
}
