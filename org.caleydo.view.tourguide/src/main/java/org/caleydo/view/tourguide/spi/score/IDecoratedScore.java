/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi.score;

/**
 * special kind of {@link IScore} that wraps / transforms another one
 * 
 * @author Samuel Gratzl
 * 
 */
public interface IDecoratedScore extends IScore{
	IScore getUnderlying();
}
