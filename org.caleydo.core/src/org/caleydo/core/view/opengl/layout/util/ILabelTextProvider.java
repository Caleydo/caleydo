/**
 * 
 */
package org.caleydo.core.view.opengl.layout.util;

/**
 * Interface for classes that provide the text for a {@link LabelRenderer}.
 * 
 * @author Christian
 * 
 */
public interface ILabelTextProvider {

	/**
	 * @return The text that shall be displayed by a {@link LabelRenderer}.
	 */
	public String getLabelText();
}
