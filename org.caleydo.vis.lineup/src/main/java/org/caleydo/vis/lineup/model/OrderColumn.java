/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.lineup.internal.event.AnnotationEditEvent;
import org.caleydo.vis.lineup.internal.ui.TitleDescriptionDialog;
import org.caleydo.vis.lineup.model.mixin.IAnnotatedColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.lineup.ui.detail.ValueElement;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * a column that orders it right elements
 *
 * @author Samuel Gratzl
 *
 */
public final class OrderColumn extends ARankColumnModel implements IAnnotatedColumnMixin, IHideableColumnMixin,
		ICollapseableColumnMixin, IGLRenderer, Cloneable {
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
				String tori = getTitle();
				TitleDescriptionDialog d = new TitleDescriptionDialog(null, "Edit Label of: " + tori, tori, description);
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
