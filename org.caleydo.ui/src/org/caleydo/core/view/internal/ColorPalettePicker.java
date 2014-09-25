/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.internal;

import java.util.Comparator;
import java.util.Locale;

import org.caleydo.core.util.color.AlexColorPalette;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;
import org.caleydo.core.util.color.IColorPalette;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.dnd.IDnDItem;
import org.caleydo.core.view.opengl.layout2.dnd.IDragGLSource;
import org.caleydo.core.view.opengl.layout2.dnd.IDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.TextDragInfo;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.Pick;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;


public class ColorPalettePicker extends GLElementContainer {
	public ColorPalettePicker() {
		setLayout(GLLayouts.flowVertical(10));
		ImmutableSortedSet.Builder<IColorPalette> b = ImmutableSortedSet.orderedBy(new Comparator<IColorPalette>() {
			@Override
			public int compare(IColorPalette o1, IColorPalette o2) {
				int c;
				if ((c = o1.getType().compareTo(o2.getType())) != 0)
					return c;
				c = o1.getSizes().last() - o2.getSizes().last();
				if (c != 0)
					return -c;
				return String.CASE_INSENSITIVE_ORDER.compare(o1.getLabel(), o2.getLabel());
			}
		});
		b.add(ColorBrewer.values());
		b.add(AlexColorPalette.values());

		final ImmutableList<IColorPalette> l = b.build().asList();
		GLElementContainer c = new GLElementContainer(GLLayouts.flowHorizontal(2));
		for (IColorPalette p : l.subList(0, l.size() / 2))
			c.add(new ColorPalette(p));
		this.add(c);
		c = new GLElementContainer(GLLayouts.flowHorizontal(2));
		for (IColorPalette p : l.subList(l.size() / 2, l.size()))
			c.add(new ColorPalette(p));
		this.add(c);

	}

	private static class ColorPalette extends GLElementContainer {

		/**
		 * @param p
		 */
		public ColorPalette(IColorPalette p) {
			setLayout(GLLayouts.flowVertical(1));
			add(new GLElement(GLRenderers.drawText(p.getLabel(),VAlign.CENTER)).setSize(-1, 10));
			for (Color c : p.get(p.getSizes().last())) {
				this.add(new ColorPicker("#" + c.getHEX(), c).setSize(-1, 25));
			}
		}

	}

	private static class ColorPicker extends PickableGLElement implements IDragGLSource {
		private final String label;
		private final Color color;
		private boolean hover;

		public ColorPicker(String label, Color color) {
			this.label = label;
			this.color = color;
		}

		@Override
		protected void onMouseOver(Pick pick) {
			hover = true;
			context.getMouseLayer().addDragSource(this);
			super.onMouseOver(pick);
		}

		@Override
		protected void takeDown() {
			context.getMouseLayer().removeDragSource(this);
			super.takeDown();
		}

		@Override
		protected void onMouseOut(Pick pick) {
			context.getMouseLayer().removeDragSource(this);
			hover = false;
			repaint();
			super.onMouseOut(pick);
		}

		@Override
		public GLElement createUI(IDragInfo info) {
			return null;
		}

		@Override
		public void onDropped(IDnDItem info) {

		}

		@Override
		public IDragInfo startSWTDrag(IDragEvent event) {
			Locale.setDefault(Locale.ENGLISH);
			return new TextDragInfo(String.format("<r>%f</r><g>%f</g><b>%f</b><a>%f</a>", color.r, color.g, color.b,
					color.a)); // color.toString());
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			g.color(color).fillRect(0, 0, w, h);
			g.drawText(label, 0, 0 + (h - 10) * 0.5f, w, 10, VAlign.CENTER);
			if (hover)
				g.color(Color.BLACK).drawRect(0, 0, w, h);
			super.renderImpl(g, w, h);
		}
	}

	public static void main(String[] args) {
		GLSandBox.main(args, new ColorPalettePicker());
	}
}
