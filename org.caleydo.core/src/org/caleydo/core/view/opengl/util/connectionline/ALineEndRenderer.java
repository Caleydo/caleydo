/**
 * 
 */
package org.caleydo.core.view.opengl.util.connectionline;

/**
 * Abstract base class for all {@link IConnectionLineAttributeRenderer} that are
 * supposed to be rendered at one end of the connection line. Specifies, which
 * line end to use.
 * 
 * @author Christian
 * 
 */
public abstract class ALineEndRenderer implements IConnectionLineAttributeRenderer {

	/**
	 * Specifies whether the attribute shall be rendered at the first or last
	 * line point.
	 */
	protected boolean isLineEnd1;

	/**
	 * @param isLineEnd1
	 *            see {@link #isLineEnd1}
	 */
	public ALineEndRenderer(boolean isLineEnd1) {
		this.isLineEnd1 = isLineEnd1;
	}

	/**
	 * @param isLineEnd1
	 *            setter, see {@link #isLineEnd1}
	 */
	public void setLineEnd1(boolean isLineEnd1) {
		this.isLineEnd1 = isLineEnd1;
	}

	/**
	 * @return the isLineEnd1, see {@link #isLineEnd1}
	 */
	public boolean isLineEnd1() {
		return isLineEnd1;
	}

}
