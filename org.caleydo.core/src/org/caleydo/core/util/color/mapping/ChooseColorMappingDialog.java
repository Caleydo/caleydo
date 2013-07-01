/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.color.mapping;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.io.gui.IDataOKListener;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class ChooseColorMappingDialog extends Dialog implements IDataOKListener {

	private ColorMapper colorMapper;

	private ATableBasedDataDomain dataDomain;

	private Group colorSchemeGroup;

	public ChooseColorMappingDialog(Shell parent, ATableBasedDataDomain dataDomain) {
		super(parent);
		this.dataDomain = dataDomain;
		colorMapper = dataDomain.getColorMapper();
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Choose Color Map for " + dataDomain.getLabel() + " Data");
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		colorSchemeGroup = new Group(parent, SWT.NONE);
		colorSchemeGroup.setLayout(new GridLayout(2, false));
		colorSchemeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		for (final EDefaultColorSchemes colorScheme : EDefaultColorSchemes.values()) {

			Button button = new Button(colorSchemeGroup, SWT.RADIO);
			button.setText(colorScheme.getColorSchemeName());
			if (colorScheme.getColorSchemeName().equals(dataDomain.getColorMapper().getColorSchemeName())) {
				button.setSelection(true);
			}
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					dataDomain.setColorMapper(ColorMapper.createDefaultMapper(colorScheme));

					UpdateColorMappingEvent event = new UpdateColorMappingEvent();
					event.setEventSpace(dataDomain.getDataDomainID());
					EventPublisher.trigger(event);
				}
			});

			CLabel colorMappingPreview = new CLabel(colorSchemeGroup, SWT.SHADOW_IN | SWT.RADIO);
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.minimumWidth = 250;
			colorMappingPreview.setLayoutData(gridData);
			ColorMapper.createColorMappingPreview(ColorMapper.createDefaultMapper(colorScheme), colorMappingPreview);
		}

		return parent;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		// if (!dataChooserComposite.isOK())
		// getButton(IDialogConstants.OK_ID).setEnabled(false);
		return control;
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		// resetting color mapper
		dataDomain.setColorMapper(colorMapper);
		UpdateColorMappingEvent event = new UpdateColorMappingEvent();
		event.setEventSpace(dataDomain.getDataDomainID());
		EventPublisher.trigger(event);
		super.cancelPressed();
	}

	@Override
	public void dataOK() {
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}
}
