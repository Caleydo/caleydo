/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import gleem.linalg.Vec2f;

import java.util.Arrays;
import java.util.List;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.spline.TesselatedPolygons;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Samuel Gratzl
 *
 */
public class SandboxWidgetTest {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite t = new Composite(shell, SWT.NONE);
		t.setLayout(new GridLayout(1, false));
		t.setBackground(display.getSystemColor(SWT.COLOR_CYAN));
		final Test root = new Test();
		GLSandBox sandbox = new GLSandBox(t, root, GLPadding.ZERO);
		Button b = new Button(t, SWT.PUSH);
		b.setText("Push Me");
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventPublisher.trigger(new PushButtonEvent().to(root));
			}
		});

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		sandbox.shutdown();
	}

	static class Test extends PickableGLElement {
		private Color color = Color.RED;
		private List<Vec2f> spline;
		/**
		 *
		 */
		public Test() {
			setTooltip("Click on me to change color");
			setVisibility(EVisibility.PICKABLE);
			setPicker(null); // avoids rendering a quad for picking
		}

		@Override
		protected void layoutImpl(int deltaTimeMs) {
			super.layoutImpl(deltaTimeMs);

			Vec2f size = getSize();

			spline = TesselatedPolygons.spline(
					Arrays.asList(new Vec2f(10, 10), new Vec2f(300, 140), new Vec2f(40, 200), size), 20);
		}

		@Override
		protected void onMouseReleased(Pick pick) {
			color = Color.GREEN;
			repaint();
			super.onMouseReleased(pick);
		}

		@ListenTo(sendToMe = true)
		private void onPushButtonEvent(PushButtonEvent event) {
			this.color = Color.BLUE;
			repaint();
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			g.color(color).fillRect(0, 0, w, h);
			g.color(Color.BLACK).incZ().drawPath(spline, false).decZ();
			super.renderImpl(g, w, h);
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			g.drawPath(spline, false);
			super.renderPickImpl(g, w, h);
		}
	}

	static class PushButtonEvent extends ADirectedEvent {

	}
}
