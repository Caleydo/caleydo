/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.text;

import java.util.List;

/**
 * @author Samuel Gratzl
 *
 */
public interface IWrappingTextRenderer extends ITextRenderer {

	/**
	 * @param text
	 * @param width
	 * @param lineHeight
	 * @return
	 */
	List<String> wrap(String text, float width, float lineHeight);

}
