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
package org.caleydo.core.view.opengl.layout;

import org.caleydo.core.util.base.ConstantLabelProvider;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.color.IColor;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.builder.ElementLayoutBuilder;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.LineSeparatorRenderer;
import org.caleydo.core.view.opengl.layout.util.Renderers;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;

/**
 * @author Samuel Gratzl
 *
 */
public final class ElementLayouts {
	private ElementLayouts() {

	}

	public static ElementLayoutBuilder create() {
		return new ElementLayoutBuilder();
	}

	public static ElementLayout createXSpacer(int width) {
		return create().width(width).build();
	}

	public static ElementLayout createXSeparator(int width) {
		return wrap(new LineSeparatorRenderer(true), width);
	}

	public static ElementLayout createYSpacer(int height) {
		return create().height(height).build();
	}

	public static ElementLayout createYSeparator(int width) {
		ElementLayout l = createYSpacer(width);
		l.setRenderer(new LineSeparatorRenderer(false));
		return l;
	}

	public static ElementLayout createLabel(AGLView view, String label, int width) {
		return createLabel(view, new ConstantLabelProvider(label), width);
	}

	public static ElementLayout createLabel(AGLView view, ILabelProvider label, int width) {
		return create().width(width).render(Renderers.createLabel(label, view.getTextRenderer())).build();
	}

	public static ElementLayout createColor(IColor color, int width) {
		return wrap(new ColorRenderer(color.getRGBA()), width);
	}

	public static ElementLayout createButton(AGLView view, Button button) {
		return createButton(view, button, 16, 16);
	}

	public static ElementLayout createButton(AGLView view, Button button, int width, int height) {
		return create().width(width).height(height).render(new ButtonRenderer(button, view)).build();
	}

	public static ElementLayout wrap(LayoutRenderer renderer, int width) {
		return create().width(width).render(renderer).build();
	}
}
