/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi.score;

/**
 * marker interface whether the score should be registered or not
 *
 * @author Samuel Gratzl
 *
 */
public interface IRegisteredScore extends IScore {
	void onRegistered();
}
