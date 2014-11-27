/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.dnd;

import org.apache.commons.lang.StringUtils;

/**
 * a special {@link IDragInfo} for transferring text
 * 
 * @author Samuel Gratzl
 * 
 */
public class TextDragInfo implements IDragInfo {
	private final String text;

	public TextDragInfo(String text) {
		this.text = text;
	}

	/**
	 * @return the text, see {@link #text}
	 */
	public String getText() {
		return text;
	}

	@Override
	public String getLabel() {
		return StringUtils.abbreviate(text, 0, 20);
	}
}
