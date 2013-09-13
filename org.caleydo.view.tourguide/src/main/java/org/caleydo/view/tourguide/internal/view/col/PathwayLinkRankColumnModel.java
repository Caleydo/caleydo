/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.internal.view.col;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.system.BrowserUtils;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.internal.TourGuideRenderStyle;
import org.caleydo.view.tourguide.internal.model.PathwayPerspectiveRow;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.StringRankColumnModel;
import org.caleydo.vis.lineup.ui.detail.ValueElement;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class PathwayLinkRankColumnModel extends StringRankColumnModel {


	public PathwayLinkRankColumnModel(IGLRenderer header, Color color, Color bgColor) {
		super(header, new Function<IRow, String>() {
			@Override
			public String apply(IRow input) {
				assert input instanceof PathwayPerspectiveRow;
				return ((PathwayPerspectiveRow) input).getPathway().getName();
			}
		}, color, bgColor, FilterStrategy.SUBSTRING);
	}

	public PathwayLinkRankColumnModel(PathwayLinkRankColumnModel copy) {
		super(copy);
	}

	@Override
	public PathwayLinkRankColumnModel clone() {
		return new PathwayLinkRankColumnModel(this);
	}

	@Override
	public ValueElement createValue() {
		return new MyValueElement();
	}

	public String getLink(IRow row) {
		assert row instanceof PathwayPerspectiveRow;
		String l = ((PathwayPerspectiveRow) row).getPathway().getExternalLink();
		if (l == null || !l.startsWith("http"))
			return null;
		return l;
	}

	private class MyValueElement extends ValueElement {
		public MyValueElement() {
			setVisibility(EVisibility.PICKABLE);
			setPicker(null);
		}
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (h < 5)
				return;
			String link = getLink(getRow());
			if (getTable().getSelectedRow() != getRow())
				link = null;
			if (w > 30 || (link == null && w > 20)) {
				String value = getValue(getRow());
				if (value == null)
					return;
				float hi = Math.min(h, 19);
				g.drawText(value, 3, (h - hi) * 0.5f, w - (link != null ? 20 : 7), hi - 5);
			}
			if (link != null) {
				float hi = h - 2;
				g.fillImage(TourGuideRenderStyle.ICON_EXTERNAL, w - hi - 2, 1, hi, hi);
			}
		}

		@Override
		protected void onClicked(Pick pick) {
			String link = getLink(getRow());
			if (link != null) {
				BrowserUtils.openURL(link);
			}
			super.onClicked(pick);
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			if (getVisibility() == EVisibility.PICKABLE && h >= 5 && getTable().getSelectedRow() == getRow()) {
				float hi = h - 2;
				g.fillRect(w - hi - 2, 1, hi, hi);
			}
			super.renderPickImpl(g, w, h);
		}

		@Override
		public String getTooltip() {
			return getValue(getRow()) + "\n" + getLink(getRow());
		}
	}
}
