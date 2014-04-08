/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.prefs;

import org.caleydo.view.tourguide.api.prefs.MyPreferences;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
/**
 * @author Samuel Gratzl
 *
 */
public class MyPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public MyPreferencePage() {
		super(GRID);
	}

	@Override
	public void createFieldEditors() {
		final Composite parent = getFieldEditorParent();

		addField(new IntegerFieldEditor(MyPreferences.MIN_CLUSTER_SIZE, "Default minimal set size", parent));
		addField(new BooleanFieldEditor(MyPreferences.JUMP_TO_SELECTED_ROW, "Jump to selected row", parent));
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(MyPreferences.prefs());
		setDescription("Tour Guide settings");
	}
}