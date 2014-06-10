/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * This event signals a view that a change has occurred in some part that affects the display of the view, outside of
 * other update events such as {@link SelectionUpdate}. An example is a change in color mapping.
 * 
 * @author Werner Puff
 * @deprecated Way to unspecific (alex)
 */
@XmlRootElement
@XmlType
@Deprecated
public class RedrawViewEvent extends AEvent {

	/**
	 *
	 */
	public RedrawViewEvent() {
	}

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
