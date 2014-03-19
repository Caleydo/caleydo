/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.color;

import java.util.List;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.GLComboBox;
import org.caleydo.core.view.opengl.layout2.basic.GLComboBox.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.dnd.IDnDItem;
import org.caleydo.core.view.opengl.layout2.dnd.IDragGLSource;
import org.caleydo.core.view.opengl.layout2.dnd.IDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.TextDragInfo;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.Pick;

import com.google.common.collect.ImmutableList;

/**
 * This product includes color specifications and designs developed by Cynthia Brewer (http://org/).
 *
 * @author Samuel Gratzl
 *
 */
public class ColorPalettePicker extends GLElementContainer implements ISelectionCallback<IColorPalette> {
	private final GLComboBox<IColorPalette> selector;
	private final GLElementContainer body;

	public ColorPalettePicker() {
		setLayout(GLLayouts.flowVertical(2));
		ImmutableList.Builder<IColorPalette> b = ImmutableList.builder();
		b.add(ColorBrewer.values());
		b.add(AlexColorPalette.values());

		this.selector = new GLComboBox<IColorPalette>(b.build(), GLComboBox.DEFAULT,
				GLRenderers.fillRect(Color.WHITE));
		selector.setSize(-1, 14);
		this.selector.setCallback(this);
		this.add(selector);

		this.body = new GLElementContainer(GLLayouts.flowHorizontal(1));
		this.add(body);
		selector.setSelected(0);
	}

	@Override
	public void onSelectionChanged(GLComboBox<? extends IColorPalette> widget, IColorPalette item) {
		body.clear();
		if (item == null)
			return;
		List<Color> colors = item.get(item.getSizes().last().intValue());
		for (Color c : colors)
			body.add(new ColorPicker("#" + c.getHEX(), c));
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
			return new TextDragInfo(label);
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
