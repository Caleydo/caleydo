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

import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
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

	public static ElementLayout createXSpacer(int width) {
		ElementLayout x = new ElementLayout("xspace" + width);
		setWidth(x, width);
		return x;
	}

	public static ElementLayout createXSeparator(int width) {
		ElementLayout l = createXSpacer(width);
		l.setRenderer(new LineSeparatorRenderer(true));
		return l;
	}

	public static ElementLayout createYSpacer(int height) {
		ElementLayout x = new ElementLayout("yspace" + height);
		setHeight(x, height);
		return x;
	}

	public static ElementLayout createYSeparator(int width) {
		ElementLayout l = createYSpacer(width);
		l.setRenderer(new LineSeparatorRenderer(false));
		return l;
	}

	public static ElementLayout createLabel(AGLView view, String label, int width) {
		ElementLayout l = new ElementLayout("label" + label);
		setWidth(l, width);
		l.setRenderer(Renderers.createLabel(label, view));
		return l;
	}

	private static void setWidth(ElementLayout elem, int width) {
		if (width < 0)
			elem.setGrabX(true);
		else
			elem.setPixelSizeX(width);
	}

	private static void setHeight(ElementLayout elem, int height) {
		if (height < 0)
			elem.setGrabY(true);
		else
			elem.setPixelSizeY(height);
	}

	public static ElementLayout createLabel(AGLView view, ILabelProvider label, int width) {
		ElementLayout l = new ElementLayout("label" + label);
		setWidth(l, width);
		if (label != null)
			l.setRenderer(new LabelRenderer(view, label));
		return l;
	}

	public static ElementLayout createColor(Color color, int width) {
		ElementLayout l = new ElementLayout("color" + color);
		l.setRenderer(new ColorRenderer(color.getRGBA()));
		setWidth(l, width);
		return l;
	}

	public static ElementLayout createButton(AGLView view, Button button) {
		return createButton(view, button, 16, 16);
	}

	public static ElementLayout createButton(AGLView view, Button button, int width, int height) {
		ElementLayout b = new ElementLayout("button:" + button);
		setWidth(b, width);
		setHeight(b, height);
		b.setRenderer(new ButtonRenderer(button, view));
		return b;
	}

	public static ElementLayout wrap(LayoutRenderer renderer, int width) {
		ElementLayout b = new ElementLayout();
		setWidth(b, width);
		b.setRenderer(renderer);
		return b;
	}
}
