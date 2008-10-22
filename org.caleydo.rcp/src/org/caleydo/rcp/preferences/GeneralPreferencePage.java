package org.caleydo.rcp.preferences;

import java.awt.Label;
import java.util.Collection;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for heat map specific settings
 * 
 * @author Alexander Lex
 */
public class GeneralPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage
{

	private RadioGroupFieldEditor dataFilterLevelFE;
	private BooleanFieldEditor enableXPClassicStyleMode;

	public GeneralPreferencePage()
	{
		super(GRID);
		// setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setPreferenceStore(GeneralManager.get().getPreferenceStore());
		setDescription("General Preferences.");
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
		// numRandomSamplesFE = new IntegerFieldEditor(
		// PreferenceConstants.HM_NUM_RANDOM_SAMPLING_POINT,
		// "Number of Random Samples:",
		// getFieldEditorParent());
		// numRandomSamplesFE.loadDefault();
		// addField(numRandomSamplesFE);

		enableXPClassicStyleMode = new BooleanFieldEditor(
				PreferenceConstants.XP_CLASSIC_STYLE_MODE,
				"Use Windows XP classic style mode", getFieldEditorParent());
		enableXPClassicStyleMode.loadDefault();
		addField(enableXPClassicStyleMode);

		dataFilterLevelFE = new RadioGroupFieldEditor(PreferenceConstants.DATA_FILTER_LEVEL,
				"Filter level for gene expression data.", 1, new String[][] {
						{ "No Filtering", "complete" },
						{ "Use only values that have a DAVID ID Mapping", "only_mapping" },
						{ "Use only values that occur in pathways", "only_context" } },
				getFieldEditorParent());
		dataFilterLevelFE.loadDefault();
		addField(dataFilterLevelFE);

		org.eclipse.swt.widgets.Label label = new org.eclipse.swt.widgets.Label(
				getFieldEditorParent(), SWT.NONE);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		label
				.setText("Note that this only applies for standalone views. \nViews in bucket show only elements that occur in pathways \nby default (You will have to restart for this to take effect)");

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

		return bReturn;
	}

}