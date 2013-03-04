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
package org.caleydo.vis.rank.model;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.BitSet;
import java.util.Iterator;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * the stacked column
 *
 * @author Samuel Gratzl
 *
 */
public class ContainerRankColumnModel extends ACompositeRankColumnModel {

	private final PropertyChangeListener weightChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onWeightChanged((float) evt.getNewValue() - (float) evt.getOldValue());
		}
	};

	public ContainerRankColumnModel(String title) {
		super(Color.GRAY, new Color(0.90f, .90f, .90f));
		setHeaderRenderer(GLRenderers.drawText(title, VAlign.CENTER));
		setWeight(0);
	}

	public ContainerRankColumnModel(ContainerRankColumnModel copy) {
		super(copy);
	}

	@Override
	public ContainerRankColumnModel clone() {
		return new ContainerRankColumnModel(this);
	}

	protected void onWeightChanged(float delta) {
		addWeight(delta);
	}

	@Override
	public float getPreferredWidth() {
		return getWeight() + RenderStyle.COLUMN_SPACE * size() + 6;
	}

	@Override
	protected void init(ARankColumnModel model) {
		super.init(model);
		model.addPropertyChangeListener(PROP_WEIGHT, weightChanged);
		addWeight(model.getWeight());
	}

	@Override
	protected void takeDown(ARankColumnModel model) {
		super.takeDown(model);
		model.removePropertyChangeListener(PROP_WEIGHT, weightChanged);
		addWeight(-model.getWeight());
	}

	@Override
	protected boolean canAdd(ARankColumnModel model) {
		return model instanceof IRankableColumnMixin;
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new GLElement();
	}

	@Override
	public GLElement createValue() {
		return new GLElement();
	}

	@Override
	public Iterator<IRow> getCurrentOrder() {
		return parent.getCurrentOrder();
	}

	@Override
	public BitSet getCurrentFilter() {
		return parent.getCurrentFilter();
	}

	@Override
	public int getCurrentSize() {
		return parent.getCurrentSize();
	}

	@Override
	public IRow getCurrent(int rank) {
		return parent.getCurrent(rank);
	}
}
