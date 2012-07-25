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
package org.caleydo.view.heatmap.preferences;

import java.util.Collection;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.heatmap.hierarchical.GLHierarchicalHeatMap;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for heat map specific settings
 * 
 * @author Alexander Lex
 * @deprecated STILL IN USE?
 */
@Deprecated
public class HeatMapPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private IntegerFieldEditor numRandomSamplesFE;
	private IntegerFieldEditor numSamplesPerTexture;
	private IntegerFieldEditor numSamplesPerHeatmap;
	private BooleanFieldEditor limitRemoteToContext;

	public HeatMapPreferencePage() {
		super(GRID);
		// setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setPreferenceStore(GeneralManager.get().getPreferenceStore());
		setDescription("Preferences for the Heat Map view.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		// Create the layout.
		RowLayout layout = new RowLayout();
		// Optionally set layout fields.
		layout.wrap = true;
		getFieldEditorParent().setLayout(layout);
		numRandomSamplesFE = new IntegerFieldEditor(
				PreferenceConstants.HM_NUM_RANDOM_SAMPLING_POINT,
				"Number of Random Samples:", getFieldEditorParent());
		numRandomSamplesFE.loadDefault();
		addField(numRandomSamplesFE);

		// numSamplesPerTexture = new IntegerFieldEditor(
		// PreferenceConstants.HM_NUM_SAMPLES_PER_TEXTURE,
		// "Number of Samples per Texture:", getFieldEditorParent());
		// numSamplesPerTexture.loadDefault();
		// addField(numSamplesPerTexture);
		//
		// numSamplesPerHeatmap = new IntegerFieldEditor(
		// PreferenceConstants.HM_NUM_SAMPLES_PER_HEATMAP,
		// "Number of Samples per Heatmap:", getFieldEditorParent());
		// numSamplesPerHeatmap.loadDefault();
		// addField(numSamplesPerHeatmap);

		limitRemoteToContext = new BooleanFieldEditor(
				PreferenceConstants.HM_LIMIT_REMOTE_TO_CONTEXT,
				"Limit remote views to show contextual information only",
				getFieldEditorParent());
		limitRemoteToContext.loadDefault();
		addField(limitRemoteToContext);

		getFieldEditorParent().pack();
	}

	@Override
	protected void performDefaults() {

	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	public boolean performOk() {

		boolean bReturn = super.performOk();

		Collection<AGLView> eventListeners = GeneralManager.get().getViewManager()
				.getAllGLViews();
		for (AGLView glView : eventListeners) {
			if (glView instanceof GLHierarchicalHeatMap) {
				GLHierarchicalHeatMap heatMap = (GLHierarchicalHeatMap) glView;
				// if(!heatMap.isRenderedRemote())
				// {
				heatMap.setNumberOfSamplesToShow(numRandomSamplesFE.getIntValue());
				heatMap.setNumberOfSamplesPerTexture(numSamplesPerTexture.getIntValue());
				heatMap.setNumberOfSamplesPerHeatmap(numSamplesPerHeatmap.getIntValue());
				// }
			}
		}

		return bReturn;
	}

}