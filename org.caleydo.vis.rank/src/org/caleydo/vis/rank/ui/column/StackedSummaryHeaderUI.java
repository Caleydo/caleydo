/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.ui.column;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.contextmenu.item.SeparatorMenuItem;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.internal.event.OrderByMeEvent;
import org.caleydo.vis.rank.internal.ui.ButtonBar;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * @author Samuel Gratzl
 *
 */
public class StackedSummaryHeaderUI extends AColumnHeaderUI {
	private PropertyChangeListener onAlignmentChange;

	public StackedSummaryHeaderUI(final StackedRankColumnModel model, IRankTableUIConfig config) {
		super(model, config, true, true);
	}

	@Override
	protected void takeDown() {
		super.takeDown();
		model.removePropertyChangeListener(StackedRankColumnModel.PROP_ALIGNMENT, onAlignmentChange);
	}

	@Override
	protected void renderOrderGlyph(GLGraphics g, float w, float h) {
		StackedRankColumnModel stacked = (StackedRankColumnModel)model;
		if (model.isCollapsed() || stacked.isCompressed())
			super.renderOrderGlyph(g, w, h);
		//else handled by my parent
	}

	@Override
	protected ButtonBar createButtons() {
		ButtonBar buttons = super.createButtons();

		{
			final StackedRankColumnModel m = (StackedRankColumnModel) model;
			final GLButton b = new GLButton(EButtonMode.BUTTON);
			b.setSize(RenderStyle.BUTTON_WIDTH, -1);
			final ISelectionCallback callback = new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.switchToNextAlignment();
				}
			};
			b.setCallback(callback);
			onAlignmentChange = new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					b.repaint();
				}
			};
			m.addPropertyChangeListener(StackedRankColumnModel.PROP_ALIGNMENT, onAlignmentChange);
			buttons.addButton(0, b, "Toggle different column alignemnts (stacked, classic and ordered stacked)",
					RenderStyle.ICON_ALIGN_CLASSIC, RenderStyle.ICON_ALIGN_STACKED);
			b.setRenderer(new  IGLRenderer() {
				@Override
				public void render(GLGraphics g, float w, float h, GLElement parent) {
					switch(m.getAlignment()) {
					case ALL:
						g.fillImage(RenderStyle.ICON_ALIGN_CLASSIC, 0, 0, w, h);
						break;
					case ORDERED:
						g.fillImage(RenderStyle.ICON_ALIGN_STACKED_ORDERED, 0, 0, w, h);
						break;
					case SINGLE:
						g.fillImage(RenderStyle.ICON_ALIGN_STACKED, 0, 0, w, h);
						break;
					}
				}
			});
		}
		{
			final StackedRankColumnModel m = (StackedRankColumnModel) model;
			final GLButton b = new GLButton(EButtonMode.BUTTON);
			b.setSize(RenderStyle.BUTTON_WIDTH, -1);
			final ISelectionCallback callback = new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.sortByWeights();
				}
			};
			b.setCallback(callback);
			buttons.addButton(1, b, "Sort members by weight", RenderStyle.ICON_SORT_BY_WEIGHT,
					RenderStyle.ICON_SORT_BY_WEIGHT);
		}

		return buttons;
	}

	@Override
	protected void showContextMenu(List<AContextMenuItem> items) {
		items.add(SeparatorMenuItem.INSTANCE);
		GenericContextMenuItem editWeights = new GenericContextMenuItem("Edit weights",
				new OpenEditWeightsEvent().to(this));
		items.add(editWeights);
		items.add(0, new GenericContextMenuItem("Order by this attribute", new OrderByMeEvent().to(this)));
		context.getSWTLayer().showContextMenu(items);
	}

	@ListenTo(sendToMe = true)
	private void onEditWeights(OpenEditWeightsEvent event) {
		EditWeightsDialog.show((StackedRankColumnModel) this.model, getParent());
	}

	@ListenTo(sendToMe = true)
	private void onSortByWeights(SortByWeightsEvent event) {
		((StackedRankColumnModel) model).sortByWeights();
	}

	public static class OpenEditWeightsEvent extends ADirectedEvent {

		@Override
		public boolean checkIntegrity() {
			return true;
		}

	}

	public static class SortByWeightsEvent extends ADirectedEvent {

		@Override
		public boolean checkIntegrity() {
			return true;
		}

	}
}

