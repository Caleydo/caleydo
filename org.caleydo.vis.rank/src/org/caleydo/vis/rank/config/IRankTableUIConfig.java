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
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankTableModel;

/**
 * config interface describing the visual representation of a {@link RankTableModel}
 *
 * @author Samuel Gratzl
 *
 */
public interface IRankTableUIConfig {

	boolean isSmallHeaderByDefault();

	/**
	 * should the whole visualization be interactive, e.g button to hide, collapse,...
	 *
	 * @return
	 */
	boolean isInteractive();

	/**
	 * should the columns be move able
	 *
	 * @return
	 */
	boolean isMoveAble();

	/**
	 * is it allowed to change the weights / width of a column
	 *
	 * @return
	 */
	boolean canChangeWeights();

	/**
	 * factory method for creating a new {@link IScrollBar} implementation, as this is picking specific
	 *
	 * @param horizontal
	 * @return
	 */
	IScrollBar createScrollBar(boolean horizontal);

	/**
	 * renderes the glyph to indicate that this column is the current ranking criteria
	 *
	 * @param g
	 * @param w
	 * @param h
	 * @param orderByIt TODO
	 */
	void renderIsOrderByGlyph(GLGraphics g, float w, float h, boolean orderByIt);

	void renderHeaderBackground(GLGraphics g, float w, float h, float labelHeight, ARankColumnModel model);

	/**
	 * @return
	 */
	boolean isShowColumnPool();

	public enum EButtonBarPositionMode {
		AT_THE_BOTTOM, OVER_LABEL, UNDER_LABEL, ABOVE_LABEL, BELOW_HIST
	}

	public EButtonBarPositionMode getButtonBarPosition();

	void renderRowBackground(GLGraphics g, float x, float y, float w, float h, boolean even, IRow row, IRow selected);

	boolean canEditValues();

}
