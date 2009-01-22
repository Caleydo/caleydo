package org.caleydo.rcp.preferences;

import static org.caleydo.core.util.preferences.PreferenceConstants.COLOR_MARKER_POINT_COLOR;
import static org.caleydo.core.util.preferences.PreferenceConstants.COLOR_MARKER_POINT_VALUE;
import static org.caleydo.core.util.preferences.PreferenceConstants.NAN_COLOR;
import static org.caleydo.core.util.preferences.PreferenceConstants.NUMBER_OF_COLOR_MARKER_POINTS;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.rcp.Activator;
import org.caleydo.rcp.Application;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * Preference page for color mapping settings
 * 
 * @author Alexander Lex
 */

public class ColorMappingPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage
{
	private ArrayList<ColorFieldEditor> alColorFieldEditors;
	private ArrayList<Spinner> alColorMarkerPointSpinners;
	private ArrayList<Label> alColorMarkerPointLabels;

	private int iNumberOfColorPoints = 0;
	private Spinner numColorPointsSpinner;

	private CLabel colorMappingPreviewLabel;

	private ColorFieldEditor nanColorFE;

	private ISet set;

	private class NumColorPointsModifyListener
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
			setFirstAndLastColorMarkerPointSpinner();

			updateColorMappingPreview();
		}
	}

	private class ColorChangedListener
		implements IPropertyChangeListener
	{
		@Override
		public void propertyChange(PropertyChangeEvent event)
		{
			updateColorMappingPreview();
		}
	}

	private class MarkerPointValueChangedModifyListener
		implements ModifyListener
	{
		@Override
		public void modifyText(ModifyEvent e)
		{
			updateColorMappingPreview();
		}

	}

	public ColorMappingPreferencePage()
	{
		super(GRID);
		// setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setPreferenceStore(GeneralManager.get().getPreferenceStore());
		setDescription("Set color mapping for different use cases, the values in the spinners are % of your data range.");

		for (ISet set : GeneralManager.get().getSetManager().getAllItems())
		{
			if (set.getSetType() == ESetType.GENE_EXPRESSION_DATA)
			{
				this.set = set;
			}
		}
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors()
	{

		// Composite numColorComposite = new Composite(, SWT.NONE);
		// GridData gridData1 = new GridData(SWT.FILL, SWT.FILL, false, false);
		// gridData1.horizontalSpan = 2;

		// getFieldEditorParent().setLayoutData(gridData1);
		// GridLayout gridLayout = new GridLayout(2, false);
		// numColorComposite.setLayout(gridLayout);
		// gridLayout.makeColumnsEqualWidth = true;

		// GridData gridData1 = new GridData(SWT.FILL, SWT.FILL, false, false);
		//	
		// gridData1.horizontalSpan = 2;

		Label numColorPointsLabel = new Label(getFieldEditorParent(), SWT.LEFT);
		numColorPointsLabel.setText("Number of Color Points:");
		// numColorPointsLabel.setLayoutData(gridData1);

		numColorPointsSpinner = new Spinner(getFieldEditorParent(), SWT.BORDER);
		numColorPointsSpinner.setMinimum(2);
		numColorPointsSpinner.setMaximum(5);
		numColorPointsSpinner.setIncrement(1);
		// gridData1 = new GridData();
		// gridData1.horizontalSpan = 2;
		// numColorPointsSpinner.setLayoutData(gridData1);

		iNumberOfColorPoints = getPreferenceStore().getInt(NUMBER_OF_COLOR_MARKER_POINTS);
		if (iNumberOfColorPoints == 0)
		{
			iNumberOfColorPoints = getPreferenceStore().getDefaultInt(
					NUMBER_OF_COLOR_MARKER_POINTS);
		}

		numColorPointsSpinner.setSelection(iNumberOfColorPoints);

		NumColorPointsModifyListener listener = new NumColorPointsModifyListener();
		listener.setSpinner(numColorPointsSpinner);
		numColorPointsSpinner.addModifyListener(listener);

		// Composite colorPointsComposite = new
		// Composite(getFieldEditorParent(), SWT.NONE);
		// gridLayout = new GridLayout(3, false);
		// colorPointsComposite.setLayout(gridLayout);

		alColorFieldEditors = new ArrayList<ColorFieldEditor>();
		alColorMarkerPointSpinners = new ArrayList<Spinner>();
		alColorMarkerPointLabels = new ArrayList<Label>();

		IPropertyChangeListener changeListener = new ColorChangedListener();
		ModifyListener markerPointValueChangedListener = new MarkerPointValueChangedModifyListener();
		for (int iCount = 1; iCount <= 5; iCount++)
		{
			// gridData1 = new GridData();
			// Group group = new Group(getFieldEditorParent(),
			// SWT.SHADOW_ETCHED_IN);
			//			
			ColorFieldEditor colorFieldEditor = new ColorFieldEditor(COLOR_MARKER_POINT_COLOR
					+ iCount, "Color " + iCount, getFieldEditorParent());
			colorFieldEditor.load();
			colorFieldEditor.getColorSelector().addListener(changeListener);
			alColorFieldEditors.add(colorFieldEditor);
			// colorFieldEditor.fillIntoGrid(composite, 1);
			addField(colorFieldEditor);
			//		
			Label markerPointLabel = new Label(getFieldEditorParent(), SWT.RIGHT);
			alColorMarkerPointLabels.add(markerPointLabel);
			Spinner markerPointSpinner = new Spinner(getFieldEditorParent(), SWT.BORDER);
			markerPointSpinner.setDigits(2);

			markerPointSpinner.addModifyListener(markerPointValueChangedListener);

			getFieldEditorParent().addMouseMoveListener(new MouseMoveListener()
			{
				@Override
				public void mouseMove(org.eclipse.swt.events.MouseEvent e)
				{
					setFirstAndLastColorMarkerPointSpinner();
				}
			});

			markerPointSpinner.setMinimum((int) (set.getMin() * 100));
			markerPointSpinner.setMaximum((int) (set.getMax() * 100));

			double dSpinnerValue = getPreferenceStore().getDouble(
					COLOR_MARKER_POINT_VALUE + iCount);
			markerPointLabel.setText(dSpinnerValue * 100 + "%");

			dSpinnerValue = set.getRawForNormalized(dSpinnerValue);

			markerPointSpinner.setSelection((int) (dSpinnerValue * 100));

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
			// markerPointSpinner.update();
		}
		// Composite colorMappingPreviewComposite = new
		// Composite(getFieldEditorParent(), SWT.NONE);
		// getFieldEditorParent() = new GridLayout(1, false);
		// colorMappingPreviewComposite.setLayout(gridLayout);
		//
		Label label = new Label(getFieldEditorParent(), SWT.RIGHT);
		label.setText("Preview:");
		colorMappingPreviewLabel = new CLabel(getFieldEditorParent(), SWT.SHADOW_IN);
		colorMappingPreviewLabel.setBounds(10, 10, 300, 100);
		colorMappingPreviewLabel.setText("                          ");

		Composite nanColorComposite = new Composite(getFieldEditorParent(), SWT.NONE);
		// gridLayout = new GridLayout(1, false);
		// nanColorComposite.setLayout(gridLayout);

		nanColorFE = new ColorFieldEditor(NAN_COLOR, "Color for NAN values:",
				nanColorComposite);
		nanColorFE.load();
		addField(nanColorFE);

		initialColorMappingPreview();

		// // composite.pack();
		// getFieldEditorParent().pack();
	}

	private void setNumberOfColorPoints(int iNumberOfColorPoints)
	{
		this.iNumberOfColorPoints = iNumberOfColorPoints;
	}

	@Override
	protected void performDefaults()
	{
		iNumberOfColorPoints = getPreferenceStore().getDefaultInt(
				NUMBER_OF_COLOR_MARKER_POINTS);

		numColorPointsSpinner.setSelection(iNumberOfColorPoints);

		double dDefaultValue;
		int iDefaultValue;

		dDefaultValue = getPreferenceStore().getDefaultDouble(COLOR_MARKER_POINT_VALUE + "1");
		dDefaultValue = set.getRawForNormalized(dDefaultValue);
		iDefaultValue = (int) (dDefaultValue * 100);
		alColorMarkerPointSpinners.get(0).setSelection(iDefaultValue);

		dDefaultValue = getPreferenceStore().getDefaultDouble(COLOR_MARKER_POINT_VALUE + "2");
		dDefaultValue = set.getRawForNormalized(dDefaultValue);
		iDefaultValue = (int) (dDefaultValue * 100);
		alColorMarkerPointSpinners.get(1).setSelection(iDefaultValue);

		dDefaultValue = getPreferenceStore().getDefaultDouble(COLOR_MARKER_POINT_VALUE + "3");
		dDefaultValue = set.getRawForNormalized(dDefaultValue);
		iDefaultValue = (int) (dDefaultValue * 100);
		alColorMarkerPointSpinners.get(2).setSelection(iDefaultValue);

		alColorFieldEditors.get(0).loadDefault();
		alColorFieldEditors.get(1).loadDefault();
		alColorFieldEditors.get(2).loadDefault();

	}

	@Override
	public boolean performOk()
	{
		boolean bReturn = super.performOk();

		getPreferenceStore().setValue(NUMBER_OF_COLOR_MARKER_POINTS, iNumberOfColorPoints);

		for (int iCount = 0; iCount < iNumberOfColorPoints; iCount++)
		{
			double dRaw = (double) alColorMarkerPointSpinners.get(iCount).getSelection() / 100;
			// get normalized value for real value
			double dNormalized = set.getNormalizedForRaw(dRaw);
			getPreferenceStore().setValue(COLOR_MARKER_POINT_VALUE + (iCount + 1),
					((float) Math.round(dNormalized * 100)) / 100);

			alColorFieldEditors.get(iCount).store();
		}

		// ColorMappingManager.get().initiFromPreferenceStore();
		Application.initializeColorMapping();
		return bReturn;
	}

	private void setFirstAndLastColorMarkerPointSpinner()
	{
		alColorMarkerPointSpinners.get(0).setSelection((int) (set.getMin() * 100));
		alColorMarkerPointSpinners.get(0).setEnabled(false);

		alColorMarkerPointSpinners.get(iNumberOfColorPoints - 1).setSelection(
				(int) (set.getMax() * 100));
		alColorMarkerPointSpinners.get(iNumberOfColorPoints - 1).setEnabled(false);
	}

	// not to nice, since it would be good to get the values with
	// updateColorMapping
	// but that doesn't work since it's initialized to late
	private void initialColorMappingPreview()
	{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.getInt("");
		store = GeneralManager.get().getPreferenceStore();
		int iNumberOfMarkerPoints = store
				.getInt(PreferenceConstants.NUMBER_OF_COLOR_MARKER_POINTS);

		Color[] alColor = new Color[iNumberOfMarkerPoints];
		int[] iArColorMarkerPoints = new int[iNumberOfMarkerPoints - 1];
		for (int iCount = 1; iCount <= iNumberOfMarkerPoints; iCount++)
		{
			int iColorMarkerPoint = (int) (100 * Activator.getDefault().getPreferenceStore()
					.getFloat(PreferenceConstants.COLOR_MARKER_POINT_VALUE + iCount));

			// Gradient label does not need the 0 point
			if (iColorMarkerPoint != 0)
			{
				iArColorMarkerPoints[iCount - 2] = iColorMarkerPoint;
			}

			String color = Activator.getDefault().getPreferenceStore().getString(
					PreferenceConstants.COLOR_MARKER_POINT_COLOR + iCount);

			int[] iArColor = new int[3];
			if (color.isEmpty())
			{
				iArColor[0] = 0;
				iArColor[1] = 0;
				iArColor[2] = 0;
			}
			else
			{
				StringTokenizer tokenizer = new StringTokenizer(color, ",", false);
				int iInnerCount = 0;
				while (tokenizer.hasMoreTokens())
				{
					try
					{
						String token = tokenizer.nextToken();
						iArColor[iInnerCount] = Integer.parseInt(token);
					}
					catch (Exception e)
					{

					}
					iInnerCount++;
				}
			}
			alColor[iCount - 1] = new Color(PlatformUI.getWorkbench().getDisplay(),
					iArColor[0], iArColor[1], iArColor[2]);
		}

		colorMappingPreviewLabel.setBackground(alColor, iArColorMarkerPoints);
		colorMappingPreviewLabel.update();
	}

	private void updateColorMappingPreview()
	{
		Color[] alColor = new Color[iNumberOfColorPoints];
		int[] iArColorMarkerPoints = new int[iNumberOfColorPoints - 1];

		for (int iCount = 0; iCount < iNumberOfColorPoints; iCount++)
		{
			RGB rgb = alColorFieldEditors.get(iCount).getColorSelector().getColorValue();
			if (rgb == null)
				return;
			alColor[iCount] = new Color(getFieldEditorParent().getDisplay(), rgb);

			if (iCount != 0)
			{
				double dRaw = ((double) alColorMarkerPointSpinners.get(iCount).getSelection()) / 100;
				int iNormalizedTo100 = (int) Math.round((set.getNormalizedForRaw(dRaw) * 100));
				alColorMarkerPointLabels.get(iCount).setText(iNormalizedTo100 + "%");
				iArColorMarkerPoints[iCount - 1] = iNormalizedTo100;

			}
		}
		colorMappingPreviewLabel.setBackground(alColor, iArColorMarkerPoints);
		colorMappingPreviewLabel.update();
	}

	@Override
	public void init(IWorkbench workbench)
	{
		// TODO Auto-generated method stub

	}
} // @jve:decl-index=0:visual-constraint="115,105"