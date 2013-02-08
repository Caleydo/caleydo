package org.caleydo.core.view.opengl.layout2.renderer;

import org.caleydo.core.view.opengl.layout2.Element;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * abstraction of a rendering element,
 *
 * the idea is that renderers are constant and don't change over time
 *
 * @author Samuel Gratzl
 *
 */
public interface IRenderer {
	/**
	 * performs the actual rendering using the given data
	 */
	void render(GLGraphics g, float w, float h, Element parent);
}

