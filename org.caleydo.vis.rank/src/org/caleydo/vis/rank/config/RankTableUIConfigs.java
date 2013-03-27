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
import org.caleydo.vis.rank.model.IRow;

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
		public void renderRowBackground(GLGraphics g, float x, float y, float w, float h, boolean even, IRow row,
				IRow selected) {
			wrappee.renderRowBackground(g, x, y, w, h, even, row, selected);
		}
	}

}

