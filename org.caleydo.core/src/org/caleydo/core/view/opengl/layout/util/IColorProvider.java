/**
 *
 */
package org.caleydo.core.view.opengl.layout.util;

import org.caleydo.core.util.color.Color;

/**
 * Interface for classes that provide the the color for {@link ColorRenderer}.
 *
 * @author Christian
 *
 */
public interface IColorProvider {

	/**
	 * @return The RGBA color that shall be used by the {@link ColorRenderer}.
	 */
	public Color getColor();

	/**
	 * @return The second color that is used to display a gradient.
	 */
	public Color getGradientColor();

	/**
	 * @return True, if a gradient color shall be used, false otherwise.
	 */
	public boolean useGradient();

	/**
	 * @return True, if the gradient is horizontal, false otherwise.
	 */
	public boolean isHorizontalGradient();

}
