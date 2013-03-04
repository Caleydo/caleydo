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
package org.caleydo.vis.rank.ui.column;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.animation.Durations;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions;
import org.caleydo.core.view.opengl.layout2.animation.Transitions;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.model.mixin.ISnapshotableColumnMixin;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * @author Samuel Gratzl
 *
 */
public class TableFreezedSummaryHeaderUI extends AnimatedGLElementContainer implements IGLLayout {
	private final static int HIST = 0;
	private final static int BUTTONS = 1;

	private final boolean interactive;
	private boolean hovered = false;

	private final StackedRankColumnModel model;
	private PropertyChangeListener onAlignmentChange;

	public TableFreezedSummaryHeaderUI(final StackedRankColumnModel model, boolean interactive) {
		this.model = model;
		this.interactive = interactive;
		setLayout(this);
		setLayoutData(model);
		this.add(model.createSummary(interactive).setLayoutData(Durations.NO), 0);
		if (interactive) {
			this.add(createButtons().setLayoutData(
							new MoveTransitions.MoveTransitionBase(Transitions.NO, Transitions.NO, Transitions.NO,
									Transitions.LINEAR)), 0);
			this.setVisibility(EVisibility.PICKABLE);
			this.onPick(new IPickingListener() {
				@Override
				public void pick(Pick pick) {
					onMainPick(pick);
				}
			});
		}
	}

	@Override
	protected void takeDown() {
		super.takeDown();
		model.removePropertyChangeListener(StackedRankColumnModel.PROP_ALIGNMENT, onAlignmentChange);
	}

	private GLElement createButtons() {
		GLElementContainer buttons = new GLElementContainer(GLLayouts.flowHorizontal(2));
		buttons.setzDelta(.5f);

		final int button_width = 12;
		{
			final GLButton b = new GLButton(EButtonMode.CHECKBOX);
			b.setSize(button_width, -1);
			b.setRenderer(GLRenderers.fillImage(RenderStyle.ICON_ALIGN_CLASSIC));
			b.setSelectedRenderer(GLRenderers.fillImage(RenderStyle.ICON_ALIGN_STACKED));
			b.setSelected(model.isAlignAll());
			b.setTooltip("Toggle classic multi-col score bar table and stacked one");
			final ISelectionCallback callback = new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					model.setAlignAll(!model.isAlignAll());
				}
			};
			b.setCallback(callback);
			onAlignmentChange = new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					b.setCallback(null);
					b.setSelected(model.isAlignAll());
					b.setCallback(callback);
				}
			};
			model.addPropertyChangeListener(StackedRankColumnModel.PROP_ALIGNMENT, onAlignmentChange);
			buttons.add(b);
		}
		if (model instanceof ISnapshotableColumnMixin) {
			final GLButton b = new GLButton();
			b.setSize(button_width, -1);
			b.setRenderer(GLRenderers.fillImage(RenderStyle.ICON_FREEZE));
			b.setTooltip("Take a snapshot of the current state");
			final ISelectionCallback callback = new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					if (model.canTakeSnapshot())
						model.takeSnapshot();
				}
			};
			b.setCallback(callback);
			buttons.add(b);
		}
		return buttons;
	}


	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement hist = children.get(HIST);
		hist.setBounds(1, 1, w - 2, h - 2);

		if (interactive) {
			IGLLayoutElement buttons = children.get(BUTTONS);
			buttons.setBounds(2, 2, w - 4, hovered ? 12 : 0);
		}
	}

	protected void onMainPick(Pick pick) {
		if (pick.isAnyDragging())
			return;
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			this.hovered = true;
			this.relayout();
			break;
		case MOUSE_OUT:
			if (this.hovered) {
				this.hovered = false;
				this.relayout();
			}
			break;
		default:
			break;
		}
	}
}
