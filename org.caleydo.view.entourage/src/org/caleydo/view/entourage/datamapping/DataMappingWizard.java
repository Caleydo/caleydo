/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.entourage.datamapping;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
import org.caleydo.datadomain.pathway.toolbar.SelectPathAction;
import org.caleydo.view.entourage.GLEntourage;
import org.eclipse.swt.widgets.Display;

/**
 * Wizard to help selecting a path and showing experimental data in enroute.
 *
 * @author Christian
 *
 */
public class DataMappingWizard extends GLElementContainer {

	private class TextLineRenderer extends GLElement {
		private String text;

		public TextLineRenderer(String text) {
			this.text = text;
			setSize(Float.NaN, 20);
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			// g.incZ(0.5f);
			g.drawText(text, 0, 0, w, 17, VAlign.CENTER);
			// g.incZ(-0.5f);
		}
	}

	private GLEntourage entourage;

	public DataMappingWizard(GLEntourage entourage) {
		this.entourage = entourage;
		entourage.getEventListenerManager().register(this, entourage.getPathEventSpace());

		setLayout(new GLSizeRestrictiveFlowLayout(false, 1, GLPadding.ZERO));

		GLElementContainer selectPathButtonContainer = createButtonLayout("resources/icons/icon_32.png",
				new IPickingListener() {
					@Override
					public void pick(Pick pick) {
						if (pick.getPickingMode() == PickingMode.CLICKED) {
							EnablePathSelectionEvent event = new EnablePathSelectionEvent(true);
							event.setEventSpace(DataMappingWizard.this.entourage.getPathEventSpace());
							GeneralManager.get().getEventPublisher().triggerEvent(event);
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									SelectPathAction selectPathAction = DataMappingWizard.this.entourage
											.getSelectPathAction();
									if (selectPathAction != null) {
										selectPathAction.setChecked(true);
									}
								}
							});
						}
					}
				});

		GLElementContainer openDataMapperButtonContainer = createButtonLayout(
				"resources/icons/view/pathway/data_mapping.png", new IPickingListener() {
					@Override
					public void pick(Pick pick) {
						if (pick.getPickingMode() == PickingMode.CLICKED) {
							DataMappers.getDataMapper().show();
						}
					}
				});

		add(createSpacing());
		add(new TextLineRenderer("To visualize"));
		add(new TextLineRenderer("experimental data"));
		add(new TextLineRenderer("select a path"));
		add(new TextLineRenderer("in a pathway"));
		add(selectPathButtonContainer);
		add(new TextLineRenderer("and use LineUp"));
		add(openDataMapperButtonContainer);
		add(new TextLineRenderer("to specify"));
		add(new TextLineRenderer("which data to show."));
		add(createSpacing());
	}

	private GLElementContainer createButtonLayout(String iconPath, IPickingListener pickingListener) {

		GLButton button = new GLButton(EButtonMode.BUTTON);
		button.setSize(32, 32);
		button.setRenderer(GLRenderers.fillImage(iconPath));
		button.onPick(pickingListener);

		GLElementContainer row = new GLElementContainer(new GLSizeRestrictiveFlowLayout(true, 1, new GLPadding(2)));
		row.setSize(Float.NaN, 32);
		row.add(createSpacing());
		row.add(button);
		row.add(createSpacing());

		return row;
	}

	private GLElement createSpacing() {
		GLElement spacing = new GLElement();
		spacing.setLayoutData(0.5f);
		return spacing;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.incZ(0.5f);
		super.renderImpl(g, w, h);
		g.incZ(-0.5f);
		// g.incZ(0.5f);
		// g.drawText("To visualize\nexperimental data\nselect a path\nin a pathway\n"
		// + "([PATH])\n and use LineUp\n[LineUp]\nto specify\nwhich data to show", 0, 0, w, 120, VAlign.CENTER);
		// List<String> lines = new ArrayList<>();
		// // lines.add("To visualize")
		// //
		// // g.drawText(lines, x, y, w, h, lineSpace, valign, style)
		// g.incZ(-0.5f);
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onPathwayPathSelected(PathwayPathSelectionEvent e) {

		if (!e.getPathSegments().isEmpty()) {
			setVisibility(EVisibility.NONE);
		} else {
			setVisibility(EVisibility.PICKABLE);
		}
	}
}
