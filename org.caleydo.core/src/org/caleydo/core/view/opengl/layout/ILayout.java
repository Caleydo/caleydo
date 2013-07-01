/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout;

/**
 * @author Samuel Gratzl
 *
 */
public interface ILayout {

	float getUnscalableElementWidth(ALayoutContainer container);

	float getUnscalableElementHeight(ALayoutContainer container);

	void calculateTransforms(ALayoutContainer container, float bottom, float left, float top, float right);

	void calculateScales(ALayoutContainer container, float totalWidth, float totalHeight,
			Integer numberOfDynamicSizeUnitsX, Integer numberOfDynamicSizeUnitsY);

	int getDynamicSizeUnitsX(ALayoutContainer container);

	int getDynamicSizeUnitsY(ALayoutContainer container);
}
