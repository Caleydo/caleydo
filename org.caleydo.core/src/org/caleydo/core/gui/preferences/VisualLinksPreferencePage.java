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
package org.caleydo.core.gui.preferences;

import java.util.ArrayList;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.tablebased.RedrawViewEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.util.vislink.EVisLinkStyleType;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference Page for Visual Links appearance and behavior.
 * 
 * @author Oliver Pimas
 * @author Alexander Lex
 */
public class VisualLinksPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	private boolean animation;
	private float width;
	private boolean animatedHalo;
	private boolean visLinksForMouseOver;
	private boolean visLinksForSelection;

	private ArrayList<EVisLinkStyleType> styleTypes;

	private int iCurrentlyUsedStyle = 0;

	private PreferenceStore preferenceStore;

	public VisualLinksPreferencePage() {
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

		Group highlightGroup = new Group(baseComposite, SWT.SHADOW_IN);
		highlightGroup.setText("Highlight modes for visual links");
		highlightGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		highlightGroup.setLayout(new GridLayout(1, true));

		final Button noHighlighting = new Button(highlightGroup, SWT.RADIO);
		noHighlighting.setText("No highlighting");
		if (iCurrentlyUsedStyle == 0)
			noHighlighting.setSelection(true);

		noHighlighting.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				iCurrentlyUsedStyle = 0;
			}
		});

		final Button shadow = new Button(highlightGroup, SWT.RADIO);
		shadow.setText("Shadow");
		if (iCurrentlyUsedStyle == 1)
			shadow.setSelection(true);

		shadow.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				iCurrentlyUsedStyle = 1;
			}
		});

		final Button halo = new Button(highlightGroup, SWT.RADIO);
		halo.setText("Halo");
		if (iCurrentlyUsedStyle == 2)
			halo.setSelection(true);

		halo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				iCurrentlyUsedStyle = 2;
			}
		});

		Group animationGroup = new Group(baseComposite, SWT.SHADOW_IN);
		animationGroup.setText("Visual Links Animation");
		animationGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		animationGroup.setLayout(new GridLayout(1, true));

		final Button animationBox = new Button(animationGroup, SWT.CHECK);
		animationBox.setText("Animation");
		if (animation == true)
			animationBox.setSelection(true);

		animationBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				animation = !animation;
			}
		});

		final Button animatedHaloBox = new Button(animationGroup, SWT.CHECK);
		animatedHaloBox.setText("Animate Halo (overwrites other selections)");
		if (animatedHalo == true) {
			animatedHaloBox.setSelection(true);
			animationBox.setEnabled(!animatedHalo);
			noHighlighting.setEnabled(!animatedHalo);
			shadow.setEnabled(!animatedHalo);
			halo.setEnabled(!animatedHalo);
		}

		animatedHaloBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				animatedHalo = !animatedHalo;
				if (animatedHalo) {
					noHighlighting.setSelection(false);
					shadow.setSelection(false);
					halo.setSelection(true);
					iCurrentlyUsedStyle = 2;
					animationBox.setSelection(true);
					animation = true;
				}
				animationBox.setEnabled(!animatedHalo);
				noHighlighting.setEnabled(!animatedHalo);
				shadow.setEnabled(!animatedHalo);
				halo.setEnabled(!animatedHalo);
			}
		});

		Group appearenceGroup = new Group(baseComposite, SWT.SHADOW_IN);
		appearenceGroup.setText("Appearence");
		appearenceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		appearenceGroup.setLayout(new GridLayout(2, true));

		final Label widthCaption = new Label(appearenceGroup, SWT.NONE);
		widthCaption.setText("Width:");
		widthCaption.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		final Slider widthSlider = new Slider(appearenceGroup, SWT.HORIZONTAL);
		widthSlider.setMinimum(10);
		widthSlider.setMaximum(40);
		widthSlider.setIncrement(5);
		widthSlider.setPageIncrement(5);
		int currentWidth =
			(int) (GeneralManager.get().getPreferenceStore().getFloat(PreferenceConstants.VISUAL_LINKS_WIDTH) * 10);
		widthSlider.setSelection(currentWidth);
		widthSlider.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		widthSlider.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				width = (widthSlider.getSelection() / 10.0f);
				if (width < 1.0f || width > 4.0f)
					width = 2.0f;
			}
		});

		Group selectionGroup = new Group(baseComposite, SWT.SHADOW_IN);
		selectionGroup.setText("Show visual links for");
		selectionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectionGroup.setLayout(new GridLayout(1, true));

		final Button selectionBox = new Button(selectionGroup, SWT.RADIO);
		selectionBox.setText("Selections");
		if (visLinksForSelection == true)
			selectionBox.setSelection(true);

		selectionBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				visLinksForSelection = !visLinksForSelection;
			}
		});

		final Button mouseOverBox = new Button(selectionGroup, SWT.RADIO);
		mouseOverBox.setText("Mouse Hover");
		if (visLinksForMouseOver == true) {
			mouseOverBox.setSelection(true);
		}

		mouseOverBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				visLinksForMouseOver = !visLinksForMouseOver;
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

		preferenceStore.setValue(PreferenceConstants.VISUAL_LINKS_STYLE, iCurrentlyUsedStyle);
		preferenceStore.setValue(PreferenceConstants.VISUAL_LINKS_ANIMATION, animation);
		preferenceStore.setValue(PreferenceConstants.VISUAL_LINKS_WIDTH, width);
		preferenceStore.setValue(PreferenceConstants.VISUAL_LINKS_ANIMATED_HALO, animatedHalo);
		preferenceStore.setValue(PreferenceConstants.VISUAL_LINKS_FOR_SELECTIONS, visLinksForSelection);
		preferenceStore.setValue(PreferenceConstants.VISUAL_LINKS_FOR_MOUSE_OVER, visLinksForMouseOver);

		EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
		RedrawViewEvent redrawEvent = new RedrawViewEvent();
		redrawEvent.setSender(this);
		eventPublisher.triggerEvent(redrawEvent);

		return bReturn;
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

}
