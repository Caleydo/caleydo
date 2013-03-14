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

import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
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
	protected GLElementContainer createButtons() {
		GLElementContainer buttons = super.createButtons();

		{
			final StackedRankColumnModel m = (StackedRankColumnModel) model;
			final GLButton b = new GLButton(EButtonMode.CHECKBOX);
			b.setSize(RenderStyle.BUTTON_WIDTH, -1);
			b.setRenderer(GLRenderers.fillImage(RenderStyle.ICON_ALIGN_CLASSIC));
			b.setSelectedRenderer(GLRenderers.fillImage(RenderStyle.ICON_ALIGN_STACKED));
			b.setSelected(m.isAlignAll());
			b.setTooltip("Toggle classic multi-col score bar table and stacked one");
			final ISelectionCallback callback = new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.setAlignAll(!m.isAlignAll());
				}
			};
			b.setCallback(callback);
			onAlignmentChange = new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					b.setCallback(null);
					b.setSelected(m.isAlignAll());
					b.setCallback(callback);
				}
			};
			m.addPropertyChangeListener(StackedRankColumnModel.PROP_ALIGNMENT, onAlignmentChange);
			buttons.add(0,b);
		}
		return buttons;
	}
}
