/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage.model;

import java.beans.PropertyChangeListener;
import java.util.BitSet;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.basic.EButtonIcon;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.ui.GLPropertyChangeListeners;
import org.caleydo.vis.lineup.ui.detail.ValueElement;

/**
 * a special column model, showing a checkbox for the rows
 *
 * @author Samuel Gratzl
 *
 */
public class CheckColumnModel extends ARankColumnModel implements IGLRenderer {
	public static final String PROP_CHECKED = "checked";
	private final BitSet checked = new BitSet();

	public CheckColumnModel() {
		super(Color.GRAY, new Color(.95f, .95f, .95f));
		setWidth(20);
		setHeaderRenderer(this);
	}

	/**
	 * @param copy
	 */
	public CheckColumnModel(CheckColumnModel copy) {
		super(copy);
	}

	@Override
	public ARankColumnModel clone() {
		return new CheckColumnModel(this);
	}

	@Override
	public String getValue(IRow row) {
		return is(row) ? "Checked" : "Unchecked";
	}

	public boolean is(IRow row) {
		return checked.get(row.getIndex());
	}

	public void set(boolean value) {
		if (value)
			this.checked.set(0, getTable().getDataSize());
		else
			this.checked.clear();
		propertySupport.firePropertyChange(PROP_CHECKED, !value, value);
	}

	public void set(IRow row, boolean value) {
		boolean old = is(row);
		if (old == value)
			return;
		checked.set(row.getIndex(), value);
		propertySupport.fireIndexedPropertyChange(PROP_CHECKED, row.getIndex(), old, value);
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new Summary();
	}

	@Override
	public ValueElement createValue() {
		return new Value();
	}

	/**
	 * @return the checked, see {@link #checked}
	 */
	public BitSet getChecked() {
		return checked;
	}

	private class Summary extends GLElement {
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			String icon = EButtonIcon.CHECKBOX.get(true);
			g.fillImage(icon, 1, 1, h - 2, h - 2);
			super.renderImpl(g, w, h);
		}
	}

	@Override
	public String toString() {
		return "Selection Model";
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		String icon = EButtonIcon.CHECKBOX.get(true);
		g.fillImage(icon, 1, 1, h - 2, h - 2);
	}

	private class Value extends ValueElement implements IPickingListener {
		private int pickingId = -1;
		private final PropertyChangeListener repaintListner = GLPropertyChangeListeners.repaintOnEvent(this);

		/**
		 *
		 */
		public Value() {
			setVisibility(EVisibility.VISIBLE);
		}

		@Override
		protected void init(IGLElementContext context) {
			pickingId = context.registerPickingListener(this);
			super.init(context);
			addPropertyChangeListener(PROP_CHECKED, repaintListner);
		}

		@Override
		protected void takeDown() {
			context.unregisterPickingListener(pickingId);
			removePropertyChangeListener(PROP_CHECKED, repaintListner);
			super.takeDown();
		}

		@Override
		public void pick(Pick pick) {
			if (pick.getPickingMode() == PickingMode.CLICKED) {
				set(getRow(), !is(getRow()));
				repaint();
			}
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			String icon = EButtonIcon.CHECKBOX.get(is(getRow()));
			float hi = Math.min(h - 2, 16);
			g.fillImage(icon, (w - hi) * 0.5f, (h - hi) * 0.5f, hi, hi);
			super.renderImpl(g, w, h);
		}

		@Override
		protected boolean hasPickAbles() {
			return true;
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			g.pushName(pickingId);
			g.incZ();
			float hi = Math.min(h - 2, 16);
			g.fillRect((w - hi) * 0.5f, (h - hi) * 0.5f, hi, hi);
			g.decZ();
			g.popName();

			super.renderPickImpl(g, w, h);
		}
	}
}
