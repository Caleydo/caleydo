package org.caleydo.core.view.opengl.canvas.radial;

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
