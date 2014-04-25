/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.util.svg;

import java.awt.Dimension;
import java.io.File;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLElementDecorator;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.IGLGraphicsTracer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Samuel Gratzl
 *
 */
public class Test extends GLSandBox {

	private boolean screen = false;
	private SVGGraphicsTracer g;

	public Test(Shell parentShell) {
		super(parentShell, "Test", new GLElementDecorator(), GLPadding.ZERO, new Dimension(800, 600));

		((GLElementDecorator) getRoot()).setContent(createScene());
	}

	/**
	 * @return
	 */
	private GLElement createScene() {
		GLElementContainer c = new GLElementContainer(GLLayouts.flowVertical(2));
		c.add(new GLButton().setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				screen = true;
				repaint();
			}
		}).setSize(-1, 20));

		c.add(new GLElement(new IGLRenderer() {

			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				g.color(Color.BLUE).fillRect(10, 10, 100, 100);
				g.color(Color.RED).drawRect(30, 30, 50, 50);
			}
		}));
		return c;
	}

	@Override
	protected IGLGraphicsTracer createTracer() {
		if (screen) {
			screen = false;
			return new SVGGraphicsTracer(new File("out" + System.currentTimeMillis() + ".svg"));
		}
		return super.createTracer();
	}

	public static void main(String[] args) {
		GLSandBox.main(args, Test.class);
	}
}
