package org.caleydo.rcp.preferences;

import java.util.ArrayList;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.conversion.ConversionTools;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.util.vislink.EVisLinkStyleType;
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
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class VisualLinksPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	private ArrayList<String> sAlTargetColors; //FIXME
	private EVisLinkStyleType style;
	private boolean animation;
	private float width;

	private ArrayList<ArrayList<String>> colorMappings; //FIXME
	private ArrayList<EVisLinkStyleType> styleTypes;

	int iCurrentlyUsedMapping = 0; //FIXME
	int iCurrentlyUsedStyle = 0;

	public VisualLinksPreferencePage() {
		super(GRID);
		setPreferenceStore(GeneralManager.get().getPreferenceStore());
		setDescription("Set visual link appearance");

		colorMappings = new ArrayList<ArrayList<String>>(2); //FIXME
		styleTypes = new ArrayList<EVisLinkStyleType>(3);
		animation = false;
		width = 2.0f;
		//FIXME: add color
		
		styleTypes.add(EVisLinkStyleType.STANDARD_VISLINK);
		styleTypes.add(EVisLinkStyleType.SHADOW_VISLINK);
		styleTypes.add(EVisLinkStyleType.HALO_VISLINK);

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

		iCurrentlyUsedStyle = GeneralManager.get().getPreferenceStore().getInt(PreferenceConstants.VISUAL_LINKS_STYLE);
		animation = GeneralManager.get().getPreferenceStore().getBoolean(PreferenceConstants.VISUAL_LINKS_ANIMATION);
		width = GeneralManager.get().getPreferenceStore().getFloat(PreferenceConstants.VISUAL_LINKS_WIDTH);
		
		style = styleTypes.get(iCurrentlyUsedStyle);

		Composite baseComposite = new Composite(getFieldEditorParent(), SWT.NULL);
		baseComposite.setLayout(new GridLayout(1, false));

		Group group = new Group(baseComposite, SWT.SHADOW_IN);
		group.setText("Choose the desired highlighting-mode of visual links");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(2, true));
		Button standard = new Button(group, SWT.RADIO);
		standard.setText("No highlighting");
		if (iCurrentlyUsedStyle == 0)
			standard.setSelection(true);

		standard.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				style = styleTypes.get(0);
				iCurrentlyUsedStyle = 0;
			}
		});

//		CLabel colorMappingPreviewLabel = new CLabel(group, SWT.SHADOW_IN);
//		updateColorLabel(colorMappingPreviewLabel, colorMappings.get(0));
//		colorMappingPreviewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button shadow = new Button(group, SWT.RADIO);
		shadow.setText("Shadow");
		if (iCurrentlyUsedStyle == 1)
			shadow.setSelection(true);

		shadow.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				style = styleTypes.get(1);
				iCurrentlyUsedStyle = 1;
			}
		});

//		colorMappingPreviewLabel = new CLabel(group, SWT.SHADOW_IN);
//		updateColorLabel(colorMappingPreviewLabel, colorMappings.get(1));
//		colorMappingPreviewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button halo = new Button(group, SWT.RADIO);
		halo.setText("Halo");
		if (iCurrentlyUsedStyle == 2)
			halo.setSelection(true);

		halo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				style = styleTypes.get(2);
				iCurrentlyUsedStyle = 2;
			}
		});
		
		Button animationBox = new Button(baseComposite, SWT.CHECK);
		animationBox.setText("Animation");
		if (animation == true)
			animationBox.setSelection(true);
		
		animationBox.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				animation = !animation;
			}
		});
		
		final Slider widthSlider = new Slider(baseComposite, SWT.HORIZONTAL);
		widthSlider.setMinimum(10);
		widthSlider.setMaximum(40);
		widthSlider.setIncrement(5);
		widthSlider.setPageIncrement(5);
		widthSlider.setSelection(20);
		
		widthSlider.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				width = (float) (widthSlider.getSelection() / 10.0f);
				if(width < 1.0f || width > 4.0f)
					width = 2.0f;
			}
		});

		baseComposite.pack();
	}

	@Override
	protected void performDefaults() {

	}

	@Override
	public boolean performOk() {
		boolean bReturn = super.performOk();

		PreferenceStore store = GeneralManager.get().getPreferenceStore();

		store.setValue(PreferenceConstants.VISUAL_LINKS_STYLE, iCurrentlyUsedStyle);	
		store.setValue(PreferenceConstants.VISUAL_LINKS_ANIMATION, animation);
		store.setValue(PreferenceConstants.VISUAL_LINKS_WIDTH, width);

		IEventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
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
	
}
