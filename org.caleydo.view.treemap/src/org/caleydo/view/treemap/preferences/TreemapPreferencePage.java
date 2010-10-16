package org.caleydo.view.treemap.preferences;

import java.util.ArrayList;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.util.vislink.EVisLinkStyleType;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference Page for Visual Links appearance and behavior.
 * 
 * @author Oliver Pimas
 * @author Alexander Lex
 */
public class TreemapPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	private boolean animation;
	private float width;
	private boolean animatedHalo;
	private boolean visLinksForMouseOver;
	private boolean visLinksForSelection;

	private Combo layoutAlgorithmCB;
	private Button frameButton;
	private Spinner maxDepthSp;
	
	private ArrayList<EVisLinkStyleType> styleTypes;

	private int iCurrentlyUsedStyle = 0;

	private PreferenceStore preferenceStore;

	private static final String LAYOUT_ALGORITHM_DISPLAYNAME[] = {"Simple Layout Algorithm", "Squarified Treemap Layout Algorithm"};
	
	public TreemapPreferencePage() {
		super(GRID);
		preferenceStore = GeneralManager.get().getPreferenceStore();
		setPreferenceStore(preferenceStore);
		// setDescription("Set visual link appearance");

		styleTypes = new ArrayList<EVisLinkStyleType>(3);
		animation = false;
		width = 2.0f;
		animatedHalo = false;
		// FIXME: add color
		// FIXME: add aa quality

		styleTypes.add(EVisLinkStyleType.STANDARD_VISLINK);
		styleTypes.add(EVisLinkStyleType.SHADOW_VISLINK);
		styleTypes.add(EVisLinkStyleType.HALO_VISLINK);
	}

	/**
	 * Creates the gui components which are initialized with default values or from the pref store.
	 */
	@Override
	public void createFieldEditors() {

		iCurrentlyUsedStyle = preferenceStore.getInt(PreferenceConstants.VISUAL_LINKS_STYLE);
		animation = preferenceStore.getBoolean(PreferenceConstants.VISUAL_LINKS_ANIMATION);
		width = GeneralManager.get().getPreferenceStore().getFloat(PreferenceConstants.VISUAL_LINKS_WIDTH);
		animatedHalo = preferenceStore.getBoolean(PreferenceConstants.VISUAL_LINKS_ANIMATED_HALO);
		visLinksForSelection = preferenceStore.getBoolean(PreferenceConstants.VISUAL_LINKS_FOR_SELECTIONS);
		visLinksForMouseOver = preferenceStore.getBoolean(PreferenceConstants.VISUAL_LINKS_FOR_MOUSE_OVER);

		Composite baseComposite = new Composite(getFieldEditorParent(), SWT.NULL);
		baseComposite.setLayout(new GridLayout(1, false));

		Label l= new Label(baseComposite, SWT.SHADOW_NONE);
		l.setText("The Algorithm used to layout the Treemap");
		layoutAlgorithmCB = new Combo(baseComposite, SWT.READ_ONLY);
		
		layoutAlgorithmCB.setItems(LAYOUT_ALGORITHM_DISPLAYNAME);
		int selectedLayout = preferenceStore.getInt(PreferenceConstants.TREEMAP_LAYOUT_ALGORITHM);
		layoutAlgorithmCB.setText(LAYOUT_ALGORITHM_DISPLAYNAME[selectedLayout]);

		frameButton = new Button(baseComposite, SWT.CHECK);
		boolean frameSelection=preferenceStore.getBoolean(PreferenceConstants.TREEMAP_DRAW_CLUSTER_FRAME);
		frameButton.setSelection(frameSelection);
		frameButton.setText("Draw frame for Clusters");
		
		int maxDepth=preferenceStore.getInt(PreferenceConstants.TREEMAP_MAX_DEPTH);
		if(maxDepth<1)
			maxDepth=10;
		maxDepthSp= new Spinner(baseComposite, SWT.BORDER);
		maxDepthSp.setDigits(0);
		maxDepthSp.setMaximum(999);
		maxDepthSp.setMinimum(1);
		maxDepthSp.setIncrement(1);
		maxDepthSp.setSelection(maxDepth);

		baseComposite.pack();
		
		
	}

	@Override
	protected void performDefaults() {

	}

	@Override
	public boolean performOk() {
		boolean bReturn = super.performOk();


		int selectedLayout = layoutAlgorithmCB.getSelectionIndex();
		preferenceStore.setValue(PreferenceConstants.TREEMAP_LAYOUT_ALGORITHM, selectedLayout);
		
		preferenceStore.setValue(PreferenceConstants.TREEMAP_DRAW_CLUSTER_FRAME, frameButton.getSelection());
		
		preferenceStore.setValue(PreferenceConstants.TREEMAP_MAX_DEPTH, maxDepthSp.getSelection());
		
		return bReturn;
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

}
