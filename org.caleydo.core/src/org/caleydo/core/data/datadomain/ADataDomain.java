/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.datadomain.event.AskRemoveDataDomainEvent;
import org.caleydo.core.data.datadomain.listener.AskRemoveDataDomainEventListener;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventHandler;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.data.RemoveDataDomainEvent;
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
	 * determines whether the label of this datadomain is the default label.
	 */
	protected boolean isLabelDefault = false;

	@XmlTransient
	protected final EventListenerManager listeners = EventListenerManagers.wrap(this);


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
	@Override
	public boolean isSerializeable() {
		return isSerializeable;
	}

	@Override
	public String getProviderName() {
		return "Dataset";
	}


	@Override
	public void registerEventListeners() {
		AEventListener<ADataDomain> removeListener = new AEventListener<ADataDomain>() {
			@Override
			public void handleEvent(AEvent event) {
				if (event instanceof RemoveDataDomainEvent && event.getEventSpace().equals(this.getEventSpace()))
					removeMe();
			}
		}.setExclusiveEventSpace(dataDomainID).setHandler(this);
		listeners.register(RemoveDataDomainEvent.class, removeListener);
		listeners.register(AskRemoveDataDomainEvent.class, new AskRemoveDataDomainEventListener(this));
	}

	protected void removeMe() {
		// ok we are in the event listener thread for this data domain -> interrupt me
		Thread.currentThread().interrupt();
	}

	@Override
	public void unregisterEventListeners() {
		listeners.unregisterAll();
	}

}
