/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui;

import org.caleydo.view.tourguide.internal.model.ADataDomainQuery;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

/**
 * base class for a filter dialog of a {@link ADataDomainQuery}
 *
 * @author Samuel Gratzl
 *
 */
public class AFilterDialog extends Dialog {
	protected Spinner minSizeUI;
	private final ADataDomainQuery model;

	public AFilterDialog(Shell shell, ADataDomainQuery model) {
		super(shell);
		this.model = model;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Edit Filter of " + model.getDataDomain().getLabel());
		this.setBlockOnOpen(false);
	}

	@Override
	protected final Control createDialogArea(Composite parent) {
		parent = (Composite) super.createDialogArea(parent);

		createSpecificContent(parent);

		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		final GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		c.setLayout(layout);

		Label l = new Label(c, SWT.CENTER);
		l.setText("Minimum cluster size");
		l.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		minSizeUI = new Spinner(c, SWT.BORDER);
		minSizeUI.setValues(model.getMinSize(), 0, 10000, 0, 1, 10);
		minSizeUI.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// Create the decoration for the text UI component
		final ControlDecoration deco = new ControlDecoration(minSizeUI, SWT.TOP | SWT.LEFT);

		// Re-use an existing image
		Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
				.getImage();
		// Set description and image
		deco.setDescriptionText("Specifies the minimum size that a cluster must have to be used during query computation\nA default value can be set within the Caleydo preferences");
		deco.setImage(image);
		deco.setShowOnlyOnFocus(false);

		applyDialogFont(parent);
		return parent;
	}

	/**
	 * @param parent
	 */
	protected void createSpecificContent(Composite parent) {

	}
}