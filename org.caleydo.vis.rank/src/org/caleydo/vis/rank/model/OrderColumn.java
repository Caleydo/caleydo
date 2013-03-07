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
import java.util.Objects;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.internal.event.FilterEvent;
import org.caleydo.vis.rank.internal.ui.MultiLineInputDialog;
import org.caleydo.vis.rank.model.mixin.IAnnotatedColumnMixin;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * a column that orders it right elements
 *
 * @author Samuel Gratzl
 *
 */
public class OrderColumn extends ARankColumnModel implements IAnnotatedColumnMixin, IHideableColumnMixin,
		ICollapseableColumnMixin {
	private String annotation = "";
	private final ColumnRanker ranker;

	public OrderColumn(ColumnRanker ranker) {
		super(Color.LIGHT_GRAY, new Color(0.9f, .9f, .9f));
		this.ranker = ranker;
		setHeaderRenderer(GLRenderers.drawText("Order Adapter", VAlign.CENTER));
	}

	private OrderColumn(OrderColumn copy) {
		super(copy);
		setHeaderRenderer(copy.getHeaderRenderer());
		this.ranker = copy.ranker.clone(getTable()); // FIXME not working
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
	public GLElement createValue() {
		return null;
	}

	/**
	 * @return the ranker, see {@link #ranker}
	 */
	public ColumnRanker getRanker() {
		return ranker;
	}

	protected void setAnnotation(String annotation) {
		propertySupport.firePropertyChange(PROP_ANNOTATION, this.annotation, this.annotation = annotation);
	}

	@Override
	public void editAnnotation(final GLElement summary) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				InputDialog d = new MultiLineInputDialog(null, "Edit Annotation of: " + getTooltip(),
						"Edit Annotation", annotation, null);
				if (d.open() == Window.OK) {
					String v = d.getValue().trim();
					if (v.length() == 0)
						v = null;
					EventPublisher.publishEvent(new FilterEvent(v).to(summary));
				}
			}
		});
	}
	@Override
	public String getAnnotation() {
		return annotation;
	}

	class MyElement extends GLElement {
		@ListenTo(sendToMe = true)
		private void onSetAnnotation(FilterEvent event) {
			setAnnotation(Objects.toString(event.getFilter(), null));
		}
	}
}
