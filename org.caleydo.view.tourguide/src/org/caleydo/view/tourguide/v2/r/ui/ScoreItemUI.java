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
package org.caleydo.view.tourguide.v2.r.ui;

import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.view.tourguide.v2.r.model.IValue;
import org.caleydo.view.tourguide.v2.r.model.ScoreColumn;
import org.caleydo.view.tourguide.v2.r.model.ScoreTable;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreItemUI extends GLElement {
	public static final float RENDER_TEXT = 6;
	private final ScoreColumn column;
	private final int row;
	private final ScoreTable table;

	public ScoreItemUI(ScoreTable table, ScoreColumn column, int row) {
		this.column = column;
		this.table = table;
		this.row = row;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);

		g.color(column.getColor());

		IValue v = table.getNormalized(column, row);
		if (v.hasMultiple()) {
			float[] vs = v.asFloats();
			float hi = h * 0.8f - vs.length - 1;
			if (hi < vs.length) { // just the maximum
				g.fillRect(0, h * 0.1f, w, h * 0.8f);
			} else {
				float hd = hi / (vs.length + 1);
				float scale = w / v.asFloat();
				float y = h * 0.1f;
				for (int i = 0; i < vs.length; ++i) {
					float hvi = (i == v.getRepr()) ? hd * 2 : hd;
					g.fillRect(0, y, scale * vs[i], hvi);
					y += hvi + 1;
				}
			}
			// if (h > RENDER_TEXT) {
			// float raw = table.getRaw(column, row).asFloat();
			// g.drawText(Formatter.formatNumber(raw), 0, h * 0.1f, w, h * 0.8f, VAlign.LEFT);
			// }
		} else {
			g.fillRect(0, h * 0.1f, w, h * 0.8f);
			if (h > RENDER_TEXT) {
				float raw = table.getRaw(column, row).asFloat();
				g.drawText(Formatter.formatNumber(raw), 0, h * 0.1f, w, h * 0.8f, VAlign.LEFT);
			}
		}



	}
}