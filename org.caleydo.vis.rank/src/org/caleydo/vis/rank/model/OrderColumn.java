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

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.internal.event.AnnotationEditEvent;
import org.caleydo.vis.rank.internal.ui.TitleDescriptionDialog;
import org.caleydo.vis.rank.model.mixin.IAnnotatedColumnMixin;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.rank.ui.detail.ValueElement;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * a column that orders it right elements
 *
 * @author Samuel Gratzl
 *
 */
public class OrderColumn extends ARankColumnModel implements IAnnotatedColumnMixin, IHideableColumnMixin,
		ICollapseableColumnMixin, IGLRenderer {
	private String description = "";
	private String title = "Separator";
	private final ColumnRanker ranker;

	public OrderColumn() {
		super(Color.LIGHT_GRAY, new Color(0.9f, .9f, .9f));
		this.ranker = new ColumnRanker(this);
		setHeaderRenderer(this);
	}

	private OrderColumn(OrderColumn copy) {
		super(copy);
		setHeaderRenderer(this);
		this.title = copy.title;
		this.description = copy.description;
		this.ranker = copy.ranker.clone(this);
	}

	@Override
	public OrderColumn clone() {
		return new OrderColumn(this);
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new MyElement();
	}

	@Override
	public ValueElement createValue() {
		return null;
	}

	/**
	 * @return the ranker, see {@link #ranker}
	 */
	public ColumnRanker getRanker() {
		return ranker;
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
		if (title == null || title.length() == 0)
			title = "Separator";
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
		g.drawText(title, 0, 0, w, h, VAlign.CENTER);
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getValue(IRow row) {
		return null;
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
					String desc =d.getDescription().trim();
					EventPublisher.trigger(new AnnotationEditEvent(t,desc).to(summary));
				}
			}
		});
	}

	class MyElement extends GLElement {
		@ListenTo(sendToMe = true)
		private void onSetAnnotation(AnnotationEditEvent event) {
			setTitle(event.getTitle());
			setDescription(event.getDescription());
		}
	}
}
