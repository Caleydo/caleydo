/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.text;

import java.awt.Font;

/**
 * different text styles
 *
 * @author Samuel Gratzl
 *
 */
public enum ETextStyle {
	PLAIN, BOLD, ITALIC;

	public int toAWTFontStyle() {
		switch (this) {
		case BOLD:
			return Font.BOLD;
		case ITALIC:
			return Font.ITALIC;
		default:
			return Font.PLAIN;
		}
	}
}
