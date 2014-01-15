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
 * Utility methods for creating a different versions of a {@link IRankTableUIConfig}
 *
 * @author Samuel Gratzl
 *
 */
public class RankTableUIConfigs {

	public static final IRankTableUIConfig DEFAULT = new RankTableUIConfigBase(true, true, true);

	public static IRankTableUIConfig nonInteractive(IRankTableUIConfig config) {
		return new WrappedRankTableUIConfig(config, Boolean.FALSE, null, null);
	}

	/**
	 * wrapper around another {@link IRankTableUIConfig} with one or more attributes changes
	 *
	 * @author Samuel Gratzl
	 *
	 */
	private static class WrappedRankTableUIConfig implements IRankTableUIConfig {
		private final IRankTableUIConfig wrappee;
		// null -> use default, else use the value
		private final Boolean isInteractive;
		private final Boolean isMoveAble;
		private final Boolean canChangeWeights;

		public WrappedRankTableUIConfig(IRankTableUIConfig wrappee, Boolean isInteractive, Boolean isMoveAble,
				Boolean canChangeWeights) {
			this.wrappee = wrappee;
			this.isInteractive = isInteractive;
			this.isMoveAble = isMoveAble;
			this.canChangeWeights = canChangeWeights;
		}

		@Override
		public boolean isSmallHeaderByDefault() {
			return wrappee.isSmallHeaderByDefault();
		}

		@Override
		public boolean isFastFiltering() {
			return wrappee.isFastFiltering();
		}

		@Override
		public boolean isInteractive() {
			if (isInteractive != null)
				return isInteractive.booleanValue();
			return wrappee.isInteractive();
		}

		@Override
		public boolean isMoveAble() {
			if (isMoveAble != null)
				return isMoveAble.booleanValue();
			return wrappee.isMoveAble();
		}

		@Override
		public boolean canChangeWeights() {
			if (canChangeWeights != null)
				return canChangeWeights.booleanValue();
			return wrappee.canChangeWeights();
		}

		@Override
		public IScrollBar createScrollBar(boolean horizontal) {
			return wrappee.createScrollBar(horizontal);
		}

		@Override
		public void renderIsOrderByGlyph(GLGraphics g, float w, float h, boolean orderByIt) {
			wrappee.renderIsOrderByGlyph(g, w, h, orderByIt);
		}

		@Override
		public boolean isShowColumnPool() {
			return wrappee.isShowColumnPool();
		}

		@Override
		public EButtonBarPositionMode getButtonBarPosition() {
			return wrappee.getButtonBarPosition();
		}

		@Override
		public void renderRowBackground(GLGraphics g, Rect rect, boolean even, IRow row,
				IRow selected) {
			wrappee.renderRowBackground(g, rect, even, row, selected);
		}

		@Override
		public void renderHeaderBackground(GLGraphics g, float w, float h, float labelHeight, ARankColumnModel model) {
			wrappee.renderHeaderBackground(g, w, h, labelHeight, model);
		}

		@Override
		public boolean canEditValues() {
			return wrappee.canEditValues();
		}

		@Override
		public Color getBarOutlineColor() {
			return wrappee.getBarOutlineColor();
		}

		@Override
		public void onRowClick(RankTableModel table, Pick pick, IRow row, boolean isSelected,
				IGLElementContext context) {
			wrappee.onRowClick(table, pick, row, isSelected, context);
		}
	}

}

