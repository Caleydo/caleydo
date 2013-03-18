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
package org.caleydo.view.subgraph;

import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;

/**
 * Bar with text on it.
 *
 * @author Christian Partl
 *
 */
public class GLTitleBar extends GLElementContainer {

	protected final ILabelProvider labelProvider;
	protected final GLButton closeButton;

	public GLTitleBar(String text) {
		this(new DefaultLabelProvider(text));
	}

	public GLTitleBar(ILabelProvider labelProvider) {
		super();
		this.labelProvider = labelProvider;
		setSize(Float.NaN, 20);
		setLayout(new GLSizeRestrictiveFlowLayout(true, 2, new GLPadding(3, 2)));
		add(new GLElement() {

			@Override
			protected void renderImpl(GLGraphics g, float w, float h) {
				g.drawText(GLTitleBar.this.labelProvider, 0, -2, w, h);
			}
		});
		closeButton = new GLButton(EButtonMode.BUTTON);
		closeButton.setSize(16, 16);
		closeButton.setTooltip("Close");
		closeButton.setRenderer(GLRenderers.fillImage("resources/icons/general/remove.png"));
		closeButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
			}
		});

		add(closeButton);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(0.6f, 0.6f, 0.6f, 1f).fillRoundedRect(0, 0, w, h, 7);
		super.renderImpl(g, w, h);
	}

}
