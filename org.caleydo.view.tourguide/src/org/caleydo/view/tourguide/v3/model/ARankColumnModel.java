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
package org.caleydo.view.tourguide.v3.model;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragInfo;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.tourguide.v3.model.mixin.ICollapseableColumnMixin;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ARankColumnModel implements IDragInfo {
	public static final String PROP_WEIGHT = "weight";

	protected final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);

	private float weight = 100;

	private IGLRenderer header = GLRenderers.DUMMY;
	private IGLRenderer value = GLRenderers.DUMMY;
	protected final Color color;
	protected final Color bgColor;

	protected IRankColumnParent parent;

	public ARankColumnModel(Color color, Color bgColor) {
		this.color = color;
		this.bgColor = bgColor;
	}

	protected final void setValueRenderer(IGLRenderer value) {
		this.value = value;
	}

	protected final void setHeaderRenderer(IGLRenderer header) {
		this.header = header;
	}

	/**
	 * @param parent
	 *            setter, see {@link parent}
	 */
	void setParent(IRankColumnParent parent) {
		this.parent = parent;
	}

	/**
	 * @return the parent, see {@link #parent}
	 */
	IRankColumnParent getParent() {
		return parent;
	}

	/**
	 * @param weight
	 *            setter, see {@link weight}
	 */
	public void setWeight(float weight) {
		propertySupport.firePropertyChange(PROP_WEIGHT, this.weight, this.weight = weight);
	}

	public void addWeight(float delta) {
		setWeight(weight + delta);
	}

	public float getWeight() {
		return weight;
	}

	public float getPreferredWidth() {
		return weight;
	}

	public abstract GLElement createSummary();

	public final IGLRenderer getHeaderRenderer() {
		return header;
	}

	public final IGLRenderer getValueRenderer() {
		return value;
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
			if (isWithComposite) {
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

}
