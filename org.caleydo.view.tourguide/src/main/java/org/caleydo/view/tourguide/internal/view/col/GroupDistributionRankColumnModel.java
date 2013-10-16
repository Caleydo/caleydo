/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.col;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.GroupInfo;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.ui.detail.ValueElement;

/**
 * a column showing the group distributions
 *
 * @author Samuel Gratzl
 *
 */
public class GroupDistributionRankColumnModel extends ARankColumnModel {

	public GroupDistributionRankColumnModel() {
		super(Color.GRAY, new Color(0.95f, .95f, .95f));
		setHeaderRenderer(GLRenderers.drawText("Distribution", VAlign.CENTER));
		setWidth(50);
	}

	public GroupDistributionRankColumnModel(GroupDistributionRankColumnModel copy) {
		super(copy);
	}

	@Override
	public ARankColumnModel clone() {
		return new GroupDistributionRankColumnModel(this);
	}

	@Override
	public String getValue(IRow row) {
		return getLabel((AScoreRow) row, "");
	}

	/**
	 * @param row
	 * @param string
	 * @return
	 */
	private static String getLabel(AScoreRow r, String default_) {
		Collection<GroupInfo> infos = r.getGroupInfos();
		int sum = 0;
		for (GroupInfo info : infos)
			sum += info.getSize();
		if (sum == 0)
			return default_;
		StringBuilder b = new StringBuilder();
		int maxSize = "Total".length();
		for(GroupInfo info : infos)
			maxSize = Math.max(info.getLabel().length(),maxSize);
		// maxSize = (int) (maxSize * 1.2);
		b.append("Total").append(StringUtils.repeat(" ", maxSize - "Total".length())).append('\t').append(sum);
		final float factor = 100.f / sum;
		for (GroupInfo info : infos) {
			b.append('\n').append(info.getLabel()).append(StringUtils.repeat(" ", maxSize - info.getLabel().length()));
			b.append('\t').append(info.getSize());
			b.append(String.format("(%.2f%%)", info.getSize() * factor));
		}
		return b.toString();
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new GLElement();
	}

	@Override
	public ValueElement createValue() {
		return new DistributionValueElement();
	}


	private static class DistributionValueElement extends ValueElement {
		@Override
		public String getTooltip() {
			AScoreRow r = this.getLayoutDataAs(AScoreRow.class, null);
			return getLabel(r, super.getTooltip());
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (h < 1)
				return;
			AScoreRow r = this.getLayoutDataAs(AScoreRow.class, null);
			Collection<GroupInfo> infos = r.getGroupInfos();
			int sum = 0;
			for (GroupInfo info : infos)
				sum += info.getSize();
			if (sum == 0)
				return;
			float factor = w / sum;

			Color ping = new Color(0.85f, 0.85f, 0.85f);
			Color pong = Color.LIGHT_GRAY;
			Color tmp;
			float xi = 0;
			for (GroupInfo info : infos) {
				float wi = info.getSize() * factor;
				if (wi <= 0)
					continue;
				if (info.getColor() != null)
					g.color(info.getColor());
				else {
					g.color(ping);
					tmp = ping;
					ping = pong;
					pong = tmp;
				}
				g.fillRect(xi, 1, wi, h - 2);
				xi += wi;
			}
			if (getRenderInfo().getBarOutlineColor() != null) {
				// outline
				g.color(getRenderInfo().getBarOutlineColor()).drawRect(0, 1, w, h - 2);
			}
		}
	}
}
