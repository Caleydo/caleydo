/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.config;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.basic.IScrollBar;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.RankTableModel;

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

	void renderRowBackground(GLGraphics g, Rect bounds, boolean even, IRow row, IRow selected);

	boolean canEditValues();

	/**
	 * @return
	 */
	Color getBarOutlineColor();

	/**
	 * @param table
	 * @param pickingMode
	 * @param row
	 * @param isSelected
	 * @param pick
	 */
	void onRowClick(RankTableModel table, Pick pick, IRow row, boolean isSelected,
			IGLElementContext context);

	/**
	 * @return
	 */
	boolean isFastFiltering();

}
