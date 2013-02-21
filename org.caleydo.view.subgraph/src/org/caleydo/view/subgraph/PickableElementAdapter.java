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
package org.caleydo.view.subgraph;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout2.GLElementAdapter;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * @author Christian
 *
 */
public class PickableElementAdapter extends GLElementAdapter {

	// private GLPathwayBackground background;
	// private AGLView view;
	protected boolean hovered = false;

	/**
	 *
	 */
	public PickableElementAdapter(AGLView view, ALayoutRenderer renderer) {
		super(view, renderer);
		// final ColorRenderer c = new ColorRenderer(new float[] { 1, 0, 0, 1 }, view);
		// setRenderer(c);
		// c.addPickingID("BG", hashCode());
		// view.addIDPickingListener(new APickingListener() {
		//
		// @Override
		// protected void mouseOver(Pick pick) {
		// c.setColor(new float[] { 0, 1, 0, 1 });
		// }
		//
		// @Override
		// protected void mouseOut(Pick pick) {
		// c.setColor(new float[] { 1, 0, 0, 1 });
		// }
		// }, "BG", hashCode());

		// setVisibility(EVisibility.PICKABLE);
		// this.background = background;
		// this.view = view;

		// onPick(new APickingListener() {
		//
		// @Override
		// protected void mouseOver(Pick pick) {
		// hovered = true;
		// repaint();
		// System.out.println("BGover");
		// repaintPick();
		// }
		//
		// @Override
		// protected void mouseOut(Pick pick) {
		// hovered = false;
		// repaint();
		// System.out.println("BGout");
		// repaintPick();
		// }
		// });
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (hovered)
			g.color(0, 0, 0, 1).drawRoundedRect(0, 0, w, h, 10);

		super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		// g.pushName(view.getPickingManager().getPickingID(view.getID(), "BG", background.hashCode()));
		// g.incZ(-0.2f);
		// g.fillRect(0, 0, w, h);
		// g.incZ(0.2f);
		super.renderPickImpl(g, w, h);
		// renderAdapter(g, w, h);
		// repaintPick();

		// g.popName();
	}

}
