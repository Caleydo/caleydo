/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui;

import gleem.linalg.Vec2f;

import java.util.List;

import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.layout.IRowHeightLayout;
import org.caleydo.vis.lineup.layout.RowHeightLayouts;
import org.caleydo.vis.lineup.model.RankTableModel;

/**
 * basic ui widget for a {@link RankTableModel}
 *
 * @author Samuel Gratzl
 *
 */
public class TableUI extends GLElementContainer implements IGLLayout, IPickingListener {
	public TableUI(RankTableModel table, IRankTableUIConfig config, IRowHeightLayout... layouts) {
		setLayout(this);
		this.add(new TableHeaderUI(table, config));
		this.add(new TableBodyUI(table, layouts.length == 0 ? RowHeightLayouts.UNIFORM : layouts[0], config));
		setVisibility(EVisibility.PICKABLE);
		onPick(this);
		setPicker(new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				g.decZ().decZ().fillRect(0, 0, w, h).incZ().incZ();
			}
		});
	}

	public TableBodyUI getBody() {
		return (TableBodyUI) get(1);
	}

	public TableHeaderUI getHeader() {
		return (TableHeaderUI) get(0);
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement header = children.get(0);
		IGLLayoutElement body = children.get(1);
		float hi = header.getSetHeight();
		header.setBounds(0, 0, w, hi);
		body.setBounds(0, hi, w, h - hi);
		Vec2f old = getLayoutDataAs(Vec2f.class, new Vec2f(0, 0));
		Vec2f new_ = body.getLayoutDataAs(Vec2f.class, new Vec2f(0, 0));
		if (!old.equals(new_)) {
			setLayoutData(new_.copy());
			relayoutParent();
		}
	}

	@Override
	public void pick(Pick pick) {
		if (pick.getPickingMode() == PickingMode.MOUSE_WHEEL) {
			int r = ((IMouseEvent) pick).getWheelRotation();
			if (r == 0)
				return;
			getBody().scroll(-r);
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		// push my resource locator to find the icons
		g.pushResourceLocator(ResourceLocators.classLoader(this.getClass().getClassLoader()));

		super.renderImpl(g, w, h);

		g.popResourceLocator();
	}
}
