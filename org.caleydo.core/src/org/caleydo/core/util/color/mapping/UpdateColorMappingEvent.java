/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.color.mapping;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * Event that signals a permanent change to the color mapping. Usually, changes (especially interactive changes when a
 * user drags the color values) in color mapping are propagated by reference in the {@link ColorMapper} of the
 * dataDomain. This event signals that a new color has been set permanently and that expensive updates of the color
 * (e.g. recalculating textures) should be done.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class UpdateColorMappingEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
