/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.serialize;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.id.IDMappingDescription;

/**
 * List for serializing {@link IDMappingDescription}s.
 *
 * @author Christian
 *
 */
@XmlType
@XmlRootElement
public class IDMappingList {

	/** list of all data domains to (re-)store */
	@XmlElement
	private List<? extends IDMappingDescription> mappingDescriptions;

	public List<? extends IDMappingDescription> getIDMappingDescriptions() {
		return mappingDescriptions;
	}

	public void setMappingDescriptions(List<? extends IDMappingDescription> mappingDescriptions) {
		this.mappingDescriptions = mappingDescriptions;
	}
}
