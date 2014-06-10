/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.toolbar;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.datadomain.pathway.listener.ESampleMappingMode;
import org.caleydo.datadomain.pathway.listener.SampleMappingModeEvent;
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
 extends ControlContribution {

	private ESampleMappingMode mappingMode = ESampleMappingMode.ALL;

	/**
	 * constructor as requested by ControlContribution
	 *
	 */
	public SampleSelectionMode(ESampleMappingMode sampleMappingMode) {
		super("");

		this.mappingMode = sampleMappingMode;
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
				
				EventPublisher.INSTANCE
						.triggerEvent(new SampleMappingModeEvent(ESampleMappingMode.ALL));
			}
		});

		selectedSampleModeRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mappingMode = ESampleMappingMode.SELECTED;
				
				EventPublisher.INSTANCE
						.triggerEvent(new SampleMappingModeEvent(ESampleMappingMode.SELECTED));
			}
		});

		return buttonGroup;

	}
}
