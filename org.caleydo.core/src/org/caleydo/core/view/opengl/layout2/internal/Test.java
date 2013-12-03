/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import java.io.Serializable;
import java.util.Random;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.dnd.EDnDType;
import org.caleydo.core.view.opengl.layout2.dnd.IDnDItem;
import org.caleydo.core.view.opengl.layout2.dnd.IDragGLSource;
import org.caleydo.core.view.opengl.layout2.dnd.IDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.IDropGLTarget;
import org.caleydo.core.view.opengl.layout2.dnd.IUIDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.TextDragInfo;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * @author Samuel Gratzl
 *
 */
public class Test extends PickableGLElement implements IDragGLSource {
	private Color color = Color.BLUE;
	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(color).fillRect(0, 0, w, h);
		super.renderImpl(g, w, h);
	}

	@Override
	protected void onMouseOver(Pick pick) {
		context.getMouseLayer().addDragSource(this);
		super.onMouseOver(pick);
	}

	@Override
	protected void onMouseOut(Pick pick) {
		context.getMouseLayer().removeDragSource(this);
		super.onMouseOut(pick);
	}

	@Override
	public GLElement createUI(IDragInfo info) {
		return ((TransferAbleData) info).createUI();
	}

	@Override
	public void onDropped(IDnDItem info) {
		if (info.getType() == EDnDType.COPY || info.getType() == EDnDType.NONE)
			return;
		ColorBrewer s = ColorBrewer.Set3;
		Integer max = s.getSizes().last();

		color = s.get(max, new Random().nextInt(max));
		repaint();
	}

	@Override
	public IDragInfo startSWTDrag(IDragEvent event) {
		return new TransferAbleData(color);
	}

	public static void main(String[] args) {
		GLElementContainer c = new GLElementContainer(GLLayouts.flowHorizontal(10));
		c.add(new Test());
		c.add(new TestDrop());
		GLSandBox.main(args, c);
	}

	private static class TestDrop extends PickableGLElement implements IDropGLTarget {
		private Color color = Color.RED;

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			g.color(color).fillRect(0, 0, w, h);
			super.renderImpl(g, w, h);
		}

		@Override
		protected void onMouseOver(Pick pick) {
			context.getMouseLayer().addDropTarget(this);
			super.onMouseOver(pick);
		}

		@Override
		protected void onMouseOut(Pick pick) {
			context.getMouseLayer().removeDropTarget(this);
			super.onMouseOut(pick);
		}

		@Override
		public boolean canSWTDrop(IDnDItem input) {
			return input.getInfo() instanceof TransferAbleData || input.getInfo() instanceof TextDragInfo;
		}

		@Override
		public void onDrop(IDnDItem input) {
			IDragInfo info = input.getInfo();
			if (info instanceof TextDragInfo) {
				System.out.println(((TextDragInfo) info).getText());
				color = Color.WHITE;
			} else if (info instanceof TransferAbleData)
				color = ((TransferAbleData) info).getColor();
			repaint();
		}

		@Override
		public EDnDType defaultSWTDnDType(IDnDItem item) {
			return EDnDType.MOVE;
		}

		@Override
		public void onItemChanged(IDnDItem input) {
		}

	}

	public static class TransferAbleData implements IUIDragInfo, Serializable {
		private static final long serialVersionUID = 231153137513449700L;
		private Color color;

		public TransferAbleData() {
		}

		/**
		 * @return the color, see {@link #color}
		 */
		public Color getColor() {
			return color;
		}

		/**
		 * @param color
		 *            setter, see {@link color}
		 */
		public void setColor(Color color) {
			this.color = color;
		}

		public TransferAbleData(Color color) {
			this.color = color;
		}

		@Override
		public String getLabel() {
			return color.getHEX();
		}

		@Override
		public GLElement createUI() {
			return new GLElement(GLRenderers.fillRect(color.darker())).setSize(20, 20);
		}
	}

}
