/**
 * 
 */
package org.caleydo.core.view.opengl.layout.util;

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
	public float[] getColor();

}
