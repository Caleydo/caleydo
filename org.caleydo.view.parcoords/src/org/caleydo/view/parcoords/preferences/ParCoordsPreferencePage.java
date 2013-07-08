/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords.preferences;

import static org.caleydo.view.parcoords.preferences.MyPreferences.NUM_RANDOM_SAMPLING_POINT;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.parcoords.Activator;
import org.caleydo.view.parcoords.GLParallelCoordinates;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for heat map specific settings
 *
 * @author Alexander Lex
 * @deprecated STILL IN USE????
 */
@Deprecated
public class ParCoordsPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public ParCoordsPreferencePage() {
		super(GRID);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		final Composite parent = getFieldEditorParent();
		addField(new IntegerFieldEditor(
				NUM_RANDOM_SAMPLING_POINT,
				"Number of Random Samples:", parent));
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Preferences for the Parallel Coordinates view.");
	}

	@Override
	public boolean performOk() {
		boolean bReturn = super.performOk();

		for (AGLView glView : GeneralManager.get().getViewManager().getAllGLViews()) {
			if (glView.getViewType().equals(GLParallelCoordinates.VIEW_TYPE)) {
				GLParallelCoordinates parCoords = (GLParallelCoordinates) glView;
				parCoords.setNumberOfSamplesToShow(MyPreferences.getNumRandomSamplePoint());
			}
		}

		return bReturn;
	}

}
