package org.caleydo.rcp.preferences;

import static org.caleydo.rcp.preferences.PreferenceConstants.COLOR_MARKER_POINT_COLOR;
import static org.caleydo.rcp.preferences.PreferenceConstants.COLOR_MARKER_POINT_VALUE;
import static org.caleydo.rcp.preferences.PreferenceConstants.NUMBER_OF_COLOR_MARKER_POINTS;

import java.util.ArrayList;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.rcp.Activator;
import org.caleydo.rcp.Application;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class ColorMappingPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage
{

	private ArrayList<ColorFieldEditor> alColorFieldEditors;
	private ArrayList<Spinner> alColorMarkerPointSpinners;

	private int iNumberOfColorPoints = 0;
	private Spinner numColorPointsSpinner;

	private class MyModifyListener
		implements ModifyListener
	{

		private Spinner spinner;

		public void setSpinner(Spinner spinner)
		{
			this.spinner = spinner;
		}

		@Override
		public void modifyText(ModifyEvent e)
		{
			setNumberOfColorPoints(spinner.getSelection());

			for (int iCount = 0; iCount < alColorFieldEditors.size(); iCount++)
			{
				if (iCount < spinner.getSelection())
				{
					alColorFieldEditors.get(iCount).setEnabled(true, getFieldEditorParent());
					alColorMarkerPointSpinners.get(iCount).setEnabled(true);
				}
				else
				{
					alColorFieldEditors.get(iCount).setEnabled(false, getFieldEditorParent());
					alColorMarkerPointSpinners.get(iCount).setEnabled(false);
				}
			}
		}

	}

	public ColorMappingPreferencePage()
	{
		super(GRID);
		// setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setPreferenceStore(GeneralManager.get().getPreferenceStore());
		setDescription("Set color mapping for different use cases");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors()
	{
		// // String[][] sar = {{ "2", "2" }, { "3", "3" }, {"4", "4" }};
		// numColorPointsFE = new IntegerFieldEditor("NumberOfColorPoints",
		// "Number of Color Points", getFieldEditorParent(), 2);
		//		
		// RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		// layout.wrap = true;
		// layout.fill = false;
		// layout.justify = true;
		// getFieldEditorParent().setLayout(layout);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		getFieldEditorParent().setLayout(gridLayout);

		GridData gridData1 = new GridData();
		Label numColorPointsLabel = new Label(getFieldEditorParent(), SWT.LEFT);
		numColorPointsLabel.setText("Number of Color Points:");
		numColorPointsLabel.setLayoutData(gridData1);

		numColorPointsSpinner = new Spinner(getFieldEditorParent(), SWT.BORDER);
		numColorPointsSpinner.setMinimum(2);
		numColorPointsSpinner.setMaximum(5);
		numColorPointsSpinner.setIncrement(1);
		numColorPointsSpinner.setLayoutData(gridData1);

		iNumberOfColorPoints = getPreferenceStore().getInt(NUMBER_OF_COLOR_MARKER_POINTS);
		if (iNumberOfColorPoints == 0)
		{
			iNumberOfColorPoints = getPreferenceStore().getDefaultInt(
					NUMBER_OF_COLOR_MARKER_POINTS);
		}

		numColorPointsSpinner.setSelection(iNumberOfColorPoints);

		MyModifyListener listener = new MyModifyListener();
		listener.setSpinner(numColorPointsSpinner);
		numColorPointsSpinner.addModifyListener(listener);

		alColorFieldEditors = new ArrayList<ColorFieldEditor>();
		alColorMarkerPointSpinners = new ArrayList<Spinner>();
		for (int iCount = 1; iCount <= 5; iCount++)
		{
			ColorFieldEditor colorFieldEditor = new ColorFieldEditor(COLOR_MARKER_POINT_COLOR
					+ iCount, "Color " + iCount, getFieldEditorParent());
			colorFieldEditor.load();
			colorFieldEditor.fillIntoGrid(getFieldEditorParent(), 3);

			alColorFieldEditors.add(colorFieldEditor);
			addField(colorFieldEditor);

			Spinner markerPointSpinner = new Spinner(getFieldEditorParent(), SWT.BORDER);

			markerPointSpinner.setMinimum(0);
			markerPointSpinner.setMaximum(100);

			markerPointSpinner.setSelection((int) (getPreferenceStore().getDouble(
					COLOR_MARKER_POINT_VALUE + iCount) * 100));

			if (iCount == 1)
			{
				markerPointSpinner.setSelection(0);
				markerPointSpinner.setEnabled(false);
			}
			else if (iCount == iNumberOfColorPoints)
			{
				markerPointSpinner.setSelection(100);
				markerPointSpinner.setEnabled(false);
			}

			if (iCount <= iNumberOfColorPoints)
			{
				colorFieldEditor.setEnabled(true, getFieldEditorParent());
				markerPointSpinner.setEnabled(true);
			}
			else
			{
				colorFieldEditor.setEnabled(false, getFieldEditorParent());
				markerPointSpinner.setEnabled(false);
			}
			alColorMarkerPointSpinners.add(markerPointSpinner);
			markerPointSpinner.update();

		}
	}

	private void setNumberOfColorPoints(int iNumberOfColorPoints)
	{
		this.iNumberOfColorPoints = iNumberOfColorPoints;
	}

	@Override
	protected void performDefaults()
	{

		// super.performDefaults();

		iNumberOfColorPoints = getPreferenceStore().getDefaultInt(
				NUMBER_OF_COLOR_MARKER_POINTS);

		numColorPointsSpinner.setSelection(iNumberOfColorPoints);

		alColorMarkerPointSpinners.get(0)
				.setSelection(
						(int) (getPreferenceStore().getDefaultDouble(
								COLOR_MARKER_POINT_VALUE + "1") * 100));
		alColorMarkerPointSpinners.get(1)
				.setSelection(
						(int) (getPreferenceStore().getDefaultDouble(
								COLOR_MARKER_POINT_VALUE + "2") * 100));
		alColorMarkerPointSpinners.get(2)
				.setSelection(
						(int) (getPreferenceStore().getDefaultDouble(
								COLOR_MARKER_POINT_VALUE + "3") * 100));

		// getPreferenceStore().setValue("ColorPoint1", "0,255,0");
		// getPreferenceStore().setValue("ColorPoint2", "0,0,0");
		// getPreferenceStore().setValue("ColorPoint3", "255,0,1");
		alColorFieldEditors.get(0).loadDefault();
		alColorFieldEditors.get(1).loadDefault();
		alColorFieldEditors.get(2).loadDefault();
	}

	@Override
	public void init(IWorkbench workbench)
	{
	}

	public boolean performOk()
	{
		boolean bReturn = super.performOk();

		getPreferenceStore().setValue("NumberOfColorPoints", iNumberOfColorPoints);

		for (int iCount = 0; iCount < iNumberOfColorPoints; iCount++)
		{
			getPreferenceStore().setValue(COLOR_MARKER_POINT_VALUE + (iCount + 1),
					(float) alColorMarkerPointSpinners.get(iCount).getSelection() / 100);

			alColorFieldEditors.get(iCount).store();
		}

		ColorMappingManager.get().initiFromPreferenceStore();
		Application.initializeColorMapping();
		return bReturn;
	}
}