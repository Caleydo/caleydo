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
package org.caleydo.core.util.color.mapping;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.RedrawViewEvent;
import org.caleydo.core.io.gui.IDataOKListener;
import org.caleydo.core.manager.GeneralManager;
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
			if (colorScheme.getColorSchemeName().equals(
					dataDomain.getColorMapper().getColorSchemeName()))
			{
				button.setSelection(true);
			}
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					colorMapper = ColorMapper.createDefaultMapper(colorScheme);

					dataDomain.setColorMapper(colorMapper);

					EventPublisher eventPublisher = GeneralManager.get()
							.getEventPublisher();

					RedrawViewEvent redrawEvent = new RedrawViewEvent();
					redrawEvent.setEventSpace(dataDomain.getDataDomainID());
					eventPublisher.triggerEvent(redrawEvent);

					UpdateColorMappingEvent event = new UpdateColorMappingEvent();
					event.setEventSpace(dataDomain.getDataDomainID());
					// event.setSender(this);
					eventPublisher.triggerEvent(event);
				}
			});

			CLabel colorMappingPreview = new CLabel(colorSchemeGroup, SWT.SHADOW_IN
					| SWT.RADIO);
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.minimumWidth = 250;
			colorMappingPreview.setLayoutData(gridData);
			ColorMapper.createColorMappingPreview(
					ColorMapper.createDefaultMapper(colorScheme), colorMappingPreview);
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

	/**
	 * @return the colorMapper, see {@link #colorMapper}
	 */
	public ColorMapper getColorMapper() {
		return colorMapper;
	}

	@Override
	public void dataOK() {
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}
}
