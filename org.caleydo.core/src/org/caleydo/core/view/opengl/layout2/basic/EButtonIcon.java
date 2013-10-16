/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.basic;

/**
 * @author Samuel Gratzl
 *
 */
public enum EButtonIcon {
	RADIO, CHECKBOX;

	public String get(boolean selected) {
		return String.format("resources/icons/general/%s_%sselected.png", this.name().toLowerCase(), (selected ? ""
				: "not_"));
	}
}
