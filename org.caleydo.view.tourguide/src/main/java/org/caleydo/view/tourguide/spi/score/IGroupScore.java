/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi.score;

import org.caleydo.core.data.virtualarray.group.Group;

/**
 * a kind of a score that is based on a stratification and a group
 *
 * @author Samuel Gratzl
 *
 */
public interface IGroupScore extends IStratificationScore, IGroupBasedScore {
	Group getGroup();
}
