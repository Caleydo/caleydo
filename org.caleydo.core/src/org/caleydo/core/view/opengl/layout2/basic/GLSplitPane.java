/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.basic;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.util.PickingPool;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.eclipse.swt.SWT;

/**
 * a split pane similar to <a
 * href="http://da2i.univ-lille1.fr/doc/tutorial-java/uiswing/components/splitpane.html">JSplitPane</a>
 *
 * @author Samuel Gratzl
 *
 */
public class GLSplitPane extends AnimatedGLElementContainer {

	private PickingPool movers;
	private int hovered = -1;

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		movers = new PickingPool(context, new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onMoverPick(pick);
			}
		});
	}

	@Override
	protected void takeDown() {
		movers.clear();
		movers = null;
		super.takeDown();
	}

	/**
	 * @param pick
	 */
	protected void onMoverPick(Pick pick) {
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			hovered = pick.getObjectID();
			repaint();
			break;
		case DRAGGED:
			context.getSWTLayer().setCursor(SWT.CURSOR_HAND);
			float dx = pick.getDx();
			float dy = pick.getDy();
			move(pick.getObjectID(), dx, dy);
			break;
		case MOUSE_OUT:
			hovered = -1;
			repaint();
			break;
		case MOUSE_RELEASED:
			context.getSWTLayer().setCursor(-1);
			break;
		default:
			break;
		}
	}

	/**
	 * @param objectID
	 * @param dx
	 * @param dy
	 */
	private void move(int objectID, float dx, float dy) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);

	}

	private void renderMover(boolean hor, GLGraphics g, float width, float total, boolean hovered, boolean dragged) {
		final float w = hor ? total : width;
		final float h = hor ? width : total;

		g.color(Color.LIGHT_GRAY).fillRect(0, 0, w, h);

		g.color(Color.DARK_GRAY).drawRect(0, 0, w, h);
	}
}
