package org.caleydo.rcp.preferences;

import java.util.Collection;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.parcoords.GLParallelCoordinates;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for heat map specific settings
 * 
 * @author Alexander Lex
 */
public class ParCoordsPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage
{

	private IntegerFieldEditor numRandomSamplesFE;

	public ParCoordsPreferencePage()
	{
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
	public void createFieldEditors()
	{
		// Create the layout.
		RowLayout layout = new RowLayout();
		// Optionally set layout fields.
		layout.wrap = true;
		getFieldEditorParent().setLayout(layout);
		numRandomSamplesFE = new IntegerFieldEditor(
				PreferenceConstants.PC_NUM_RANDOM_SAMPLING_POINT, "Number of Random Samples:",
				getFieldEditorParent());
		numRandomSamplesFE.loadDefault();
		addField(numRandomSamplesFE);
		getFieldEditorParent().pack();
	}

	@Override
	protected void performDefaults()
	{

	}

	@Override
	public void init(IWorkbench workbench)
	{

	}

	@Override
	public boolean performOk()
	{

		boolean bReturn = super.performOk();

		Collection<AGLEventListener> eventListeners = GeneralManager.get()
				.getViewGLCanvasManager().getAllGLEventListeners();
		for (AGLEventListener eventListener : eventListeners)
		{
			if (eventListener instanceof GLParallelCoordinates)
			{
				GLParallelCoordinates parCoords = (GLParallelCoordinates) eventListener;
				// if(!heatMap.isRenderedRemote())
				// {
				parCoords.setNumberOfSamplesToShow(numRandomSamplesFE.getIntValue());
				// }
			}
		}

		return bReturn;
	}

}