/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDnDItem;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragEvent;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragGLSource;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragInfo;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDropGLTarget;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IMultiViewDragInfo;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * @author Samuel Gratzl
 *
 */
public class Test extends PickableGLElement implements IDragGLSource {

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(Color.BLUE).fillRect(0, 0, w, h);
		super.renderImpl(g, w, h);
	}

	@Override
	protected void onMouseOver(Pick pick) {
		System.out.println("mouse in");
		context.getMouseLayer().addDragSource(this);
		super.onMouseOver(pick);
	}

	@Override
	protected void onMouseOut(Pick pick) {
		System.out.println("mouse out");
		context.getMouseLayer().removeDragSource(this);
		super.onMouseOut(pick);
	}

	@Override
	public GLElement createUI(IDragInfo info) {
		return new GLElement(GLRenderers.fillRect(Color.RED)).setSize(20, 20);
	}

	@Override
	public void onDropped(IDnDItem info) {
		//
	}

	@Override
	public IDragInfo startDrag(IDragEvent event) {
		return new TransferAbleData();
	}

	public static void main(String[] args) {
		GLElementContainer c = new GLElementContainer(GLLayouts.flowHorizontal(10));
		c.add(new Test());
		c.add(new TestDrop());
		GLSandBox.main(args, c);
	}

	private static class TestDrop extends PickableGLElement implements IDropGLTarget {
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			g.color(Color.RED).fillRect(0, 0, w, h);
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
		public boolean canDrop(IDnDItem input) {
			return true;
		}

		@Override
		public void onDrop(IDnDItem input) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onItemChanged(IDnDItem input) {
			System.out.println(input.getType());
		}

	}
	@XmlRootElement
	public static class TransferAbleData implements IMultiViewDragInfo {

		@Override
		public String getLabel() {
			return "Test";
		}

	}

}
