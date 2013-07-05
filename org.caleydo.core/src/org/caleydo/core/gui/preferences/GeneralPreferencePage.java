/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for heat map specific settings
 * 
 * @author Alexander Lex
 */
public class GeneralPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	// private RadioGroupFieldEditor performanceLevelFE;

	public GeneralPreferencePage() {
		super(GRID);
		setDescription("General Preferences.");
	}

	@Override
	public void createFieldEditors() {

	}

	@Override
	public void init(IWorkbench workbench) {

	}


}
