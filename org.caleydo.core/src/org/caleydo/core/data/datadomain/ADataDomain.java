/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.datadomain;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEventHandler;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Abstract class that implements data and view management.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public abstract class ADataDomain extends AEventHandler implements IDataDomain {

	protected String dataDomainType;

	/**
	 * All {@link IDCategory}s that are used in this data container. Used to
	 * define associations between DataDomains
	 */
	@XmlTransient
	protected Set<IDCategory> idCategories = new HashSet<IDCategory>();

	// /**
	// * This mode determines whether the user can load and work with gene
	// expression data or otherwise if an
	// * not further specified data set is loaded. In the case of the
	// unspecified data set some specialized gene
	// * expression features are not available.
	// */

	protected String dataDomainID = "unspecified";

	protected EIconTextures icon = EIconTextures.NO_ICON_AVAILABLE;

	/** parameters for loading the data-{@link set} */
	protected DataSetDescription dataSetDescription;

	/** determines which view will be opened after a DataDomain is created */
	protected String defaultStartViewType;

	/** determines whether a data domain should be serialized **/
	protected boolean isSerializeable = true;

	/**
	 * DO NOT CALL THIS CONSTRUCTOR! ONLY USED FOR DESERIALIZATION.
	 */
	public ADataDomain() {
		System.out.println("Creating dataDomain " + this);
	}

	public ADataDomain(String dataDomainType, String dataDomainID) {
		this.dataDomainType = dataDomainType;
		this.dataDomainID = dataDomainID;
		System.out.println("Creating dataDomain " + this);
	}

	/**
	 * <p>
	 * All initialization of the ADataDomain must be done in here instead of in
	 * the constructor. This is called when the ADataDomain is created in the
	 * {@link DataDomainManager}.
	 * </p>
	 */
	public void init() {

	}

	@Override
	public String getDataDomainID() {
		return dataDomainID;
	}

	@Override
	public void setDataDomainID(String dataDomainType) {
		this.dataDomainID = dataDomainType;
	}

	@Override
	public String getDataDomainType() {
		return dataDomainType;
	}

	@Override
	public void setDataDomainType(String dataDomainType) {
		this.dataDomainType = dataDomainType;
	}

	@Override
	public EIconTextures getIcon() {
		return icon;
	}

	@Override
	public DataSetDescription getDataSetDescription() {
		return dataSetDescription;
	}

	@Override
	public void setDataSetDescription(DataSetDescription dataSetDescription) {
		this.dataSetDescription = dataSetDescription;
	}

	@Override
	public String toString() {
		return getLabel();
	}

	// @Override
	// @XmlTransient
	// public List<ADimensionGroupData> getDimensionGroups() {
	// return dimensionGroups;
	// }

	// @Override
	// public void setDimensionGroups(List<ADimensionGroupData> dimensionGroups)
	// {
	// this.dimensionGroups = dimensionGroups;
	// DimensionGroupsChangedEvent event = new
	// DimensionGroupsChangedEvent(this);
	// event.setSender(this);
	// GeneralManager.get().getEventPublisher().triggerEvent(event);
	// }
	//
	// @Override
	// public void addDimensionGroup(ADimensionGroupData dimensionGroup) {
	// dimensionGroups.add(dimensionGroup);
	// DimensionGroupsChangedEvent event = new
	// DimensionGroupsChangedEvent(this);
	// event.setSender(this);
	// GeneralManager.get().getEventPublisher().triggerEvent(event);
	// }

	@Override
	public Color getColor() {
		return dataSetDescription.getColor();
	}

	@Override
	public void setLabel(String label) {
		dataSetDescription.setDataSetName(label);
	}

	@Override
	public String getLabel() {
		if (dataSetDescription == null || dataSetDescription.getDataSetName() == null)
			return dataDomainID;
		return dataSetDescription.getDataSetName();
	}

	/**
	 * @return the defaultStartViewType, see {@link #defaultStartViewType}
	 */
	public String getDefaultStartViewType() {
		return defaultStartViewType;
	}

	@Override
	public Set<IDCategory> getIDCategories() {
		return idCategories;
	}

	@Override
	public void addIDCategory(IDCategory category) {
		idCategories.add(category);
	}

	/**
	 * @return the isSerializeable, see {@link #isSerializeable}
	 */
	public boolean isSerializeable() {
		return isSerializeable;
	}
}
