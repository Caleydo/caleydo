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
package org.caleydo.view.tourguide.internal.view.col;

import java.util.Collection;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.tourguide.internal.model.AScoreRow;
import org.caleydo.view.tourguide.internal.model.GroupInfo;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.ui.detail.ValueElement;

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
		b.append("total:\t").append(sum);
		for (GroupInfo info : infos)
			b.append('\n').append(info.getLabel()).append('\t').append(info.getSize())
					.append(String.format("(%.2f%%)", info.getSize() / (float) sum));
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
