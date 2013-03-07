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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragInfo;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ARankColumnModel implements IDragInfo, IRankColumnModel {
	public static final String PROP_WEIGHT = "weight";

	protected final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);

	private float weight = 100;

	private IGLRenderer header = GLRenderers.DUMMY;
	protected final Color color;
	protected final Color bgColor;
	private boolean collapsed = false;

	protected IRankColumnParent parent;

	public ARankColumnModel(Color color, Color bgColor) {
		this.color = color;
		this.bgColor = bgColor;
	}

	public ARankColumnModel(ARankColumnModel copy) {
		this.color = copy.color;
		this.bgColor = copy.bgColor;
		this.weight = copy.weight;
		this.parent = copy.parent;
		this.collapsed = copy.collapsed;
		this.header = copy.header;
	}

	@Override
	public abstract ARankColumnModel clone();

	protected final void setHeaderRenderer(IGLRenderer header) {
		this.header = header;
	}

	protected void init(IRankColumnParent parent) {
		this.parent = parent;
	}

	protected void takeDown() {
		this.parent = null;
	}

	/**
	 * @return the parent, see {@link #parent}
	 */
	@Override
	public IRankColumnParent getParent() {
		return parent;
	}

	/**
	 * @param weight
	 *            setter, see {@link weight}
	 */
	public ARankColumnModel setWeight(float weight) {
		propertySupport.firePropertyChange(PROP_WEIGHT, this.weight, this.weight = weight);
		return this;
	}

	public ARankColumnModel addWeight(float delta) {
		setWeight(weight + delta);
		return this;
	}

	public float getWeight() {
		return weight;
	}

	public abstract GLElement createSummary(boolean interactive);

	public abstract GLElement createValue();

	public final IGLRenderer getHeaderRenderer() {
		return header;
	}

	@Override
	public String getTooltip() {
		return header.toString();
	}


	public final void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(listener);
	}

	public final void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(propertyName, listener);
	}

	public final void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(listener);
	}

	public final void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * @return the bgColor, see {@link #bgColor}
	 */
	public Color getBgColor() {
		return bgColor;
	}

	/**
	 * @return the color, see {@link #color}
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @return the table, see {@link #table}
	 */
	@Override
	public RankTableModel getTable() {
		return parent.getTable();
	}

	public final boolean isCombineAble(ARankColumnModel with) {
		return getTable().isCombineAble(this, with);
	}

	public final boolean combine(ARankColumnModel with) {
		return combine(this, with);
	}

	private static boolean combine(ARankColumnModel model, ARankColumnModel with) {
		IRankColumnParent base = model.getParent();
		uncollapse(model);
		uncollapse(with);
		boolean isModelComposite = model instanceof ACompositeRankColumnModel;
		boolean isWithComposite = with instanceof ACompositeRankColumnModel;
		with.getParent().detach(with);
		if (isModelComposite) {
			ACompositeRankColumnModel t = (ACompositeRankColumnModel) model;
			if (isWithComposite && t.isFlatAdding(t)) {
				ACompositeRankColumnModel w = (ACompositeRankColumnModel) with;
				Collection<ARankColumnModel> tmp = new ArrayList<>(w.getChildren());
				for (ARankColumnModel wi : tmp) {
					w.detach(wi);
					t.add(wi);
				}
				base.getTable().destroy(w);
			} else {
				t.add(with);
			}
		} else {
			if (isWithComposite) {
				ACompositeRankColumnModel w = (ACompositeRankColumnModel) with;
				base.replace(model, w);
				w.add(0, model);
			} else {
				ACompositeRankColumnModel new_ = base.getTable().createCombined();
				new_.setWeight(model.getWeight());
				base.replace(model, new_);
				new_.add(model);
				new_.add(with);
			}
		}
		return true;
	}

	static void uncollapse(ARankColumnModel model) {
		if (model instanceof ICollapseableColumnMixin)
			((ICollapseableColumnMixin) model).setCollapsed(false);
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public float getPreferredWidth() {
		if (isCollapsed())
			return ICollapseableColumnMixin.COLLAPSED_WIDTH;
		return getWeight();
	}

	public final boolean isCollapseAble() {
		return parent.isCollapseAble(this);
	}

	public void setCollapsed(boolean collapsed) {
		if (this.collapsed == collapsed)
			return;
		if (collapsed && !parent.isCollapseAble(this))
			return;
		propertySupport.firePropertyChange(ICollapseableColumnMixin.PROP_COLLAPSED, this.collapsed,
				this.collapsed = collapsed);
	}

	public boolean hide() {
		return parent.hide(this);
	}

	public boolean isHideAble() {
		return parent.isHideAble(this);
	}

	public boolean isHidden() {
		return parent.isHidden(this);
	}

	public boolean isDestroyAble() {
		return parent.isDestroyAble(this);
	}

	public boolean destroy() {
		if (!isDestroyAble())
			return false;
		return getTable().destroy(this);
	}

	public ColumnRanker getMyRanker() {
		return parent.getMyRanker(this);
	}

	public void onRankingInvalid() {

	}

}
