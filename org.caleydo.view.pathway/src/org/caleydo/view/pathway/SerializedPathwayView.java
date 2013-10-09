/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.pathway;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedMultiTablePerspectiveBasedView;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.datadomain.pathway.listener.ESampleMappingMode;

/**
 * Serialized form of a pathway-view.
 *
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedPathwayView
 extends ASerializedMultiTablePerspectiveBasedView {

	/**
	 * id of the pathway in caleydo's pathway library, -1 for unknown pathway FIXME: this needs to be checked
	 */
	private int pathwayID;

	/**
	 * The id of the pathway data domain. The id of the underlying mapped data datadomain is stored in
	 * {@link ASerializedSingleTablePerspectiveBasedView}
	 */
	private String pathwayDataDomainID;

	/**
	 * Determines whether the path selection mode is active.
	 */
	private boolean isPathSelectionMode;

	/**
	 * Mapping mode determining, e.g., whether all samples or selected samples shall be mapped onto the pathway.
	 */
	private ESampleMappingMode mappingMode;

	/**
	 * Default constructor with default initialization
	 */
	public SerializedPathwayView() {
		isPathSelectionMode = false;
	}

	public SerializedPathwayView(String pathwayDataDomainID, IMultiTablePerspectiveBasedView view) {
		super(view);
		pathwayID = -1;
		isPathSelectionMode = false;
		this.pathwayDataDomainID = pathwayDataDomainID;
	}

	/**
	 * @return the pathwayDataDomainID, see {@link #pathwayDataDomainID}
	 */
	public String getPathwayDataDomainID() {
		return pathwayDataDomainID;
	}

	/**
	 * @param pathwayDataDomainID setter, see {@link #pathwayDataDomainID}
	 */
	public void setPathwayDataDomainID(String pathwayDataDomainID) {
		this.pathwayDataDomainID = pathwayDataDomainID;
	}

	/**
	 * Gets the pathwayId of this SerializedPathwayView
	 *
	 * @return pathwayId
	 */
	public int getPathwayID() {
		return pathwayID;
	}

	/**
	 * Sets the pathwayId of this SerlializedPathwayView
	 *
	 * @param pathwayId a valid pathwayId as in Caleydo's pathway library or -1 for an unknown or uninitialized pathway
	 */
	public void setPathwayID(int pathwayId) {
		this.pathwayID = pathwayId;
	}

	@Override
	public String getViewType() {
		return GLPathway.VIEW_TYPE;
	}

	/**
	 * @param isPathSelectionMode setter, see {@link #isPathSelectionMode}
	 */
	public void setPathSelectionMode(boolean isPathSelectionMode) {
		this.isPathSelectionMode = isPathSelectionMode;
	}

	/**
	 * @return the isPathSelectionMode, see {@link #isPathSelectionMode}
	 */
	public boolean isPathSelectionMode() {
		return isPathSelectionMode;
	}

	/**
	 * @param mappingMode setter, see {@link #mappingMode}
	 */
	public void setMappingMode(ESampleMappingMode mappingMode) {
		this.mappingMode = mappingMode;
	}

	/**
	 * @return the mappingMode, see {@link #mappingMode}
	 */
	public ESampleMappingMode getMappingMode() {
		return mappingMode;
	}

}
