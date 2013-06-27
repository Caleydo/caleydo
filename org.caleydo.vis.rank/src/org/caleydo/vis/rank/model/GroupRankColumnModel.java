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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.model.mixin.IExplodeableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankColumnModel;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.detail.ValueElement;

/**
 * @author Samuel Gratzl
 *
 */
public class GroupRankColumnModel extends ACompositeRankColumnModel implements IHideableColumnMixin,
		IExplodeableColumnMixin,
		IGLRenderer {
	private String title = null;

	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case PROP_WIDTH:
				onWeightChanged((ARankColumnModel) evt.getSource(), (float) evt.getOldValue(),
						(float) evt.getNewValue());
				break;
			case IFilterColumnMixin.PROP_FILTER:
			case IMappedColumnMixin.PROP_MAPPING:
				propertySupport.firePropertyChange(evt);
				break;
			}
		}
	};

	public GroupRankColumnModel(String title, Color color, Color bgColor) {
		super(color, bgColor);
		setHeaderRenderer(this);
		this.title = title;
		width = +RenderStyle.STACKED_COLUMN_PADDING * 2;
	}

	public GroupRankColumnModel(GroupRankColumnModel copy) {
		super(copy);
		setHeaderRenderer(this);
		this.title = copy.title;
		width = RenderStyle.STACKED_COLUMN_PADDING * 2;
		cloneInitChildren();
	}

	@Override
	public ColumnRanker getMyRanker(IRankColumnModel model) {
		return getMyRanker();
	}

	/**
	 * @return the title, see {@link #title}
	 */
	@Override
	public String getTitle() {
		return title;
	}

	@Override
	protected void init(ARankColumnModel model) {
		super.init(model);
		model.addPropertyChangeListener(PROP_WIDTH, listener);
		model.addPropertyChangeListener(IFilterColumnMixin.PROP_FILTER, listener);
		model.addPropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, listener);
		float oldWidth = size() == 1 ? (getSpaces() - RenderStyle.COLUMN_SPACE) : width;
		super.setWidth(oldWidth + model.getWidth() + RenderStyle.COLUMN_SPACE);
	}

	@Override
	protected void takeDown(ARankColumnModel model) {
		super.takeDown(model);
		model.removePropertyChangeListener(PROP_WIDTH, listener);
		model.removePropertyChangeListener(IFilterColumnMixin.PROP_FILTER, listener);
		model.removePropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, listener);
		super.setWidth(width - model.getWidth() - RenderStyle.COLUMN_SPACE);
	}

	@Override
	public ARankColumnModel setWidth(float width) {
		float shift = getSpaces();
		float factor = (width - shift) / (this.width - shift); // new / old
		for (ARankColumnModel col : this) {
			float wi = col.getWidth() * factor;
			col.removePropertyChangeListener(PROP_WIDTH, listener);
			col.setWidth(wi);
			col.addPropertyChangeListener(PROP_WIDTH, listener);
		}
		return super.setWidth(width);
	}

	/**
	 * @return
	 */
	private float getSpaces() {
		return RenderStyle.STACKED_COLUMN_PADDING * 2 + RenderStyle.COLUMN_SPACE * size();
	}

	protected void onWeightChanged(ARankColumnModel child, float oldValue, float newValue) {
		super.setWidth(width + (newValue - oldValue));
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		g.drawText(title, 0, 0, w, h, VAlign.CENTER);
	}

	@Override
	public void orderBy(IRankableColumnMixin model) {
		getParent().orderBy(model);
	}

	@Override
	public boolean isFlatAdding(ACompositeRankColumnModel model) {
		return model instanceof GroupRankColumnModel;
	}

	@Override
	public ARankColumnModel clone() {
		return new GroupRankColumnModel(this);
	}

	@Override
	public String getValue(IRow row) {
		return null;
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new GLElement();
	}

	@Override
	public ValueElement createValue() {
		return new ValueElement();
	}

	@Override
	public void explode() {
		parent.explode(this);
	}
}