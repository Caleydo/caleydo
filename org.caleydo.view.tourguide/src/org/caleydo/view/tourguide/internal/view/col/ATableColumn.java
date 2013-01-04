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

import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSpacer;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createYSpacer;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.wrap;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.COL_SPACING;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.LABEL_PADDING;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.LABEL_PADDING_HOR;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.LABEL_PADDING_VER;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.ROW_HEIGHT;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.ROW_SPACING;

import java.util.List;

import org.caleydo.core.util.base.ConstantLabelProvider;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.util.PickingRenderer;
import org.caleydo.core.view.opengl.layout.util.Renderers;
import org.caleydo.view.tourguide.api.query.ScoreQuery;
import org.caleydo.view.tourguide.api.query.ScoringElement;
import org.caleydo.view.tourguide.internal.view.ScoreQueryUI;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ATableColumn extends Column {
	protected final AGLView view;

	protected ElementLayout th, th2;
	private final ElementLayout rowSpacing = createYSpacer(ROW_SPACING);
	protected ElementLayout colSpacing = createXSpacer(COL_SPACING);

	public ATableColumn(AGLView view) {
		this.view = view;
	}

	protected abstract ElementLayout createHeader();

	protected ElementLayout createHeader2() {
		ElementLayout l = new ElementLayout();
		l.setGrabX(true);
		return l;
	}

	protected void init() {
		this.setYDynamic(false);
		this.th = createHeader();
		this.th.setPixelSizeY(ROW_HEIGHT);
		this.th2 = createHeader2();
		this.th2.setPixelSizeY(ROW_HEIGHT);
		this.setBottomUp(false);
		this.clearBody();
	}

	protected void clearBody() {
		this.clear();
		this.add(th);
		this.add(th2);
		this.add(createYSpacer(5));
		this.setPixelSizeY(ROW_HEIGHT * 2 + 5);
	}

	protected final void addTd(ElementLayout td, int i) {
		td.setGrabY(false);
		td.setGrabX(true);
		td.setPixelSizeY(ROW_HEIGHT);
		if (i >= 0)
			td.addBackgroundRenderer(new PickingRenderer(ScoreQueryUI.SELECT_ROW, i, this.view).moveBack());
		this.add(td).add(rowSpacing);
		this.setPixelSizeY(this.getPixelSizeY() + ROW_HEIGHT + ROW_SPACING);
	}


	public final ElementLayout getTd(int i) {
		List<ElementLayout> elem = this.getElements();
		int pos = 3 + i * 2;
		if (elem.size() <= pos)
			return null;
		return elem.get(pos);
	}

	protected final ElementLayout createLabel(String label, int width) {
		return createLabel(new ConstantLabelProvider(label), width);
	}

	protected final ElementLayout createLabel(ILabelProvider label, int width) {
		if (label == null)
			return createXSpacer(width);
		return wrap(Renderers.createLabel(label, view.getTextRenderer()).padding(LABEL_PADDING).build(), width);
	}

	protected final int getTextWidth(String text) {
		float height = ROW_HEIGHT - LABEL_PADDING_VER;
		int width = Math.round(this.view.getTextRenderer().getRequiredTextWidth(text,
				height));
		width += LABEL_PADDING_HOR + 3;
		return width;
	}

	protected final int getTextWidth(ILabelProvider text) {
		if (text == null)
			return 0;
		return getTextWidth(text.getLabel());
	}

	protected final ElementLayout createRightLabel(ILabelProvider label, int width) {
		return wrap(Renderers.createLabel(label, view.getTextRenderer()).padding(LABEL_PADDING).alignRight().build(),
				width);
	}

	/**
	 * @param data
	 * @param query
	 */
	public abstract void setData(List<ScoringElement> data, ScoreQuery query);
}
