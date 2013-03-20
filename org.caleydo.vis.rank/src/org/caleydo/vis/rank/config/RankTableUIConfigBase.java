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
package org.caleydo.vis.rank.config;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.IScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * basic implementation of a {@link IRankTableUIConfig}
 *
 * @author Samuel Gratzl
 *
 */
public class RankTableUIConfigBase implements IRankTableUIConfig {
	private final boolean isInteractive;
	private final boolean isMoveAble;
	private final boolean canChangeWeights;

	public RankTableUIConfigBase(boolean isInteractive, boolean isMoveAble, boolean canChangeWeights) {
		this.isInteractive = isInteractive;
		this.isMoveAble = isMoveAble;
		this.canChangeWeights = canChangeWeights;
	}

	@Override
	public boolean isInteractive() {
		return isInteractive;
	}

	@Override
	public boolean isMoveAble() {
		return isMoveAble;
	}

	@Override
	public boolean canChangeWeights() {
		return canChangeWeights;
	}

	@Override
	public IScrollBar createScrollBar(boolean horizontal) {
		return new ScrollBar(horizontal);
	}

	@Override
	public void renderIsOrderByGlyph(GLGraphics g, float w, float h) {
		g.fillImage(RenderStyle.ICON_STAR, w - 16, -8, 16, 16);
	}

}
