/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.info.selection.external;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ExternalPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public ExternalPreferencePage() {
		super(GRID);
	}

	@Override
	public void createFieldEditors() {
		final Composite parent = getFieldEditorParent();

		addField(new ExternalFieldEditor("external.idcategory", "External Patterns", parent));
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(org.caleydo.view.info.selection.Activator.getDefault().getPreferenceStore());
		setDescription("Defines Open Externally Settings");
	}
}
