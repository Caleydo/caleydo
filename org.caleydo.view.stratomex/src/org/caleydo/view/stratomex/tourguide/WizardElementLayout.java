/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.stratomex.tourguide;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ElementLayouts;
import org.caleydo.view.stratomex.column.BrickColumn;

/**
 * @author Samuel Gratzl
 *
 */
public class WizardElementLayout extends Column {

	public WizardElementLayout(ElementLayout body) {
		this.add(ElementLayouts.create().height(BrickColumn.BETWEEN_BRICKS_SPACING * 2).build());
		this.add(body);
		this.add(ElementLayouts.create().height(BrickColumn.BETWEEN_BRICKS_SPACING).build());
		this.setPixelSizeX(body.getPixelSizeX());
	}

	public ElementLayout getBody() {
		return get(1);
	}
}

