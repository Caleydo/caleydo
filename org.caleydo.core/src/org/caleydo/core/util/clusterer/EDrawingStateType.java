/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer;

import javax.xml.bind.annotation.XmlType;

/**
 * List of all types of drawing states
 * 
 * @author Christian Partl
 */

@XmlType
public enum EDrawingStateType {
	DRAWING_STATE_FULL_HIERARCHY,
	DRAWING_STATE_DETAIL_OUTSIDE,
	ANIMATION_NEW_ROOT_ELEMENT,
	ANIMATION_PARENT_ROOT_ELEMENT,
	ANIMATION_POP_OUT_DETAIL_OUTSIDE,
	ANIMATION_PULL_IN_DETAIL_OUTSIDE
}
