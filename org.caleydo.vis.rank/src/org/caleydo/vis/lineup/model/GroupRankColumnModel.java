/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.lineup.model.mixin.IExplodeableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IRankColumnModel;
import org.caleydo.vis.lineup.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.lineup.ui.RenderStyle;
import org.caleydo.vis.lineup.ui.detail.ValueElement;

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
