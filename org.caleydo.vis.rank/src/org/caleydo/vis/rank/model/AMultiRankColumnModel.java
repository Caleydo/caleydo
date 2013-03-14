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
package org.caleydo.vis.rank.model;

import java.awt.Color;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.internal.event.AnnotationEditEvent;
import org.caleydo.vis.rank.internal.ui.TitleDescriptionDialog;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IExplodeableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMultiColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AMultiRankColumnModel extends ACompositeRankColumnModel implements IMultiColumnMixin,
		IExplodeableColumnMixin, IHideableColumnMixin, ICollapseableColumnMixin, IGLRenderer {
	private SimpleHistogram cacheHist = null;
	private final String prefix;
	private String title = null;
	private String description = "";

	public AMultiRankColumnModel(Color color, Color bgColor, String prefix) {
		super(color, bgColor);
		this.prefix = prefix;
		setHeaderRenderer(this);
	}

	public AMultiRankColumnModel(AMultiRankColumnModel copy) {
		super(copy);
		this.prefix = copy.prefix;
		setHeaderRenderer(this);
		this.title = copy.title;
		this.description = copy.description;
	}

	@Override
	public boolean canAdd(ARankColumnModel model) {
		return model instanceof IRankableColumnMixin && super.canAdd(model);
	}

	@Override
	public final Float apply(IRow row) {
		return applyPrimitive(row);
	}

	@Override
	public void explode() {
		parent.explode(this);
	}

	@Override
	public final SimpleHistogram getHist(int bins) {
		if (cacheHist != null && cacheHist.size() == bins)
			return cacheHist;
		return cacheHist = DataUtils.getHist(bins, getMyRanker().iterator(), this);
	}

	@Override
	public final Color[] getColors() {
		Color[] colors = new Color[size()];
		int i = 0;
		for (ARankColumnModel child : this)
			colors[i++] = child.getColor();
		return colors;
	}

	@Override
	public void onRankingInvalid() {
		cacheHist = null;
		super.onRankingInvalid();
	}

	@Override
	public final boolean[] isValueInferreds(IRow row) {
		boolean[] r = new boolean[size()];
		int i = 0;
		for(IRankableColumnMixin child : Iterables.filter(this, IRankableColumnMixin.class))
			r[i++] = child.isValueInferred(row);
		return r;
	}

	@Override
	public final SimpleHistogram[] getHists(int bins) {
		SimpleHistogram[] hists = new SimpleHistogram[size()];
		int i = 0;
		for (IRankableColumnMixin child : Iterables.filter(this, IRankableColumnMixin.class))
			hists[i++] = child.getHist(bins);
		return hists;
	}

	@Override
	public ColumnRanker getMyRanker(ARankColumnModel model) {
		return getMyRanker();
	}

	/**
	 * @return the description, see {@link #description}
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * @param title
	 *            setter, see {@link title}
	 */
	@Override
	public void setTitle(String title) {
		if (title == null || title.isEmpty())
			title = "";
		propertySupport.firePropertyChange(PROP_TITLE, this.title, this.title = title);
	}

	/**
	 * @param description
	 *            setter, see {@link description}
	 */
	@Override
	public void setDescription(String description) {
		propertySupport.firePropertyChange(PROP_DESCRIPTION, this.description, this.description = description);
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		g.drawText(getTitle(), 0, 0, w, h, VAlign.CENTER);
	}

	@Override
	public String getTitle() {
		if (title == null || title.isEmpty()) {
			StringBuilder b = new StringBuilder(prefix).append(" (");
			for (ARankColumnModel r : this) {
				b.append(r.getTitle()).append(", ");
			}
			b.setLength(b.length() - 2);
			b.append(")");
			return b.toString();
		}
		return title;
	}

	@Override
	public void editAnnotation(final GLElement summary) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				TitleDescriptionDialog d = new TitleDescriptionDialog(null, "Edit Annotation of: " + getTitle(),
						"Edit Annotation", title, description);
				if (d.open() == Window.OK) {
					String t = d.getTitle().trim();
					String desc = d.getDescription().trim();
					EventPublisher.publishEvent(new AnnotationEditEvent(t, desc).to(summary));
				}
			}
		});
	}

}