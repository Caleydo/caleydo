package org.caleydo.core.gui.preferences;

import java.util.ArrayList;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
import org.caleydo.core.util.conversion.ConversionTools;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
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
	implements IWorkbenchPreferencePage {

	private ArrayList<String> sAlTargetColors;

	private ArrayList<ArrayList<String>> colorMappings;

	int iCurrentlyUsedMapping = 0;

	public ColorMappingPreferencePage() {
		super(GRID);
		setPreferenceStore(GeneralManager.get().getPreferenceStore());
		setDescription("Set color mapping");

		colorMappings = new ArrayList<ArrayList<String>>(2);

		ArrayList<String> sAlGBR = new ArrayList<String>(3);
		sAlGBR.add("0,255,0");
		sAlGBR.add("0,0,0");
		sAlGBR.add("255,0,0");
		colorMappings.add(sAlGBR);

		ArrayList<String> sAlBLBY = new ArrayList<String>(3);
		sAlBLBY.add("0,0,255");
		sAlBLBY.add("0,0,0");
		sAlBLBY.add("255,255,0");
		colorMappings.add(sAlBLBY);
	}

	/**
	 * Creates the gui components which are initialized with default values or from the pref store.
	 */
	@Override
	public void createFieldEditors() {

		iCurrentlyUsedMapping =
			GeneralManager.get().getPreferenceStore().getInt(PreferenceConstants.COLOR_MAPPING_USED);
		sAlTargetColors = colorMappings.get(iCurrentlyUsedMapping);

		Composite baseComposite = new Composite(getFieldEditorParent(), SWT.NULL);
		baseComposite.setLayout(new GridLayout(1, false));

		Group group = new Group(baseComposite, SWT.SHADOW_IN);
		group.setText("Choose the desired color mapping");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(2, true));
		Button gbr = new Button(group, SWT.RADIO);
		gbr.setText("Green, Black, Red");
		if (iCurrentlyUsedMapping == 0)
			gbr.setSelection(true);

		gbr.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				sAlTargetColors = colorMappings.get(0);
				iCurrentlyUsedMapping = 0;
			}
		});

		CLabel colorMappingPreviewLabel = new CLabel(group, SWT.SHADOW_IN);
		updateColorLabel(colorMappingPreviewLabel, colorMappings.get(0));
		colorMappingPreviewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button blby = new Button(group, SWT.RADIO);
		blby.setText("Blue, Black, Yellow");
		if (iCurrentlyUsedMapping == 1)
			blby.setSelection(true);

		blby.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				sAlTargetColors = colorMappings.get(1);
				iCurrentlyUsedMapping = 1;
			}
		});

		colorMappingPreviewLabel = new CLabel(group, SWT.SHADOW_IN);
		updateColorLabel(colorMappingPreviewLabel, colorMappings.get(1));
		colorMappingPreviewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		baseComposite.pack();
	}

	@Override
	protected void performDefaults() {

	}

	@Override
	public boolean performOk() {
		boolean bReturn = super.performOk();

		PreferenceStore store = GeneralManager.get().getPreferenceStore();

		store.setValue(PreferenceConstants.COLOR_MAPPING_USED, iCurrentlyUsedMapping);
		int iCount = 1;
		for (String color : sAlTargetColors) {
			store.setValue(PreferenceConstants.GENE_EXPRESSION_PREFIX
				+ PreferenceConstants.COLOR_MARKER_POINT_COLOR + iCount, color);
			iCount++;
		}
		ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION)
			.initiFromPreferenceStore();

		EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
		RedrawViewEvent redrawEvent = new RedrawViewEvent();
		redrawEvent.setSender(this);
		eventPublisher.triggerEvent(redrawEvent);

		UpdateViewEvent event = new UpdateViewEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);

		return bReturn;
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	/**
	 * Sets a color label to the values specified in alColor. The spreading between the colors is even.
	 * 
	 * @param label
	 *            the color label
	 * @param alColor
	 *            the list of colors
	 */
	private void updateColorLabel(CLabel label, ArrayList<String> alColor) {

		Color[] arColor = new Color[alColor.size()];
		int[] iArColorMarkerPoints = new int[alColor.size() - 1];
		int iCount = 0;
		for (String color : alColor) {

			int[] iArColor = ConversionTools.getIntColorFromString(color);
			if (iCount != 0)
				iArColorMarkerPoints[iCount - 1] = 100 / (alColor.size() - 1) * iCount;

			arColor[iCount] =
				new Color(PlatformUI.getWorkbench().getDisplay(), iArColor[0], iArColor[1], iArColor[2]);
			iCount++;
		}

		label.setBackground(arColor, iArColorMarkerPoints);
		label.update();
	}
}