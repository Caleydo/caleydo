/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Event that signals that a view has been activated. the command holds a list of view-ids as payload.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class ViewActivationEvent
	extends ViewEvent {

}
