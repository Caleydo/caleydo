/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.serialize;

import java.util.HashMap;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;

/**
 * Bean that holds the initialization data for new started Caleydo application.
 * Used to store and restore project or to sync remote clients.
 *
 * @author Werner Puff
 * @author Alexander Lex
 */
public class DataDomainSerializationData {

	/** defines the type of usage of the application */
	private ATableBasedDataDomain dataDomain;

	/**
	 * content of the set file the application is based on, only used to sync
	 * remote clients
	 */
	private byte[] dataTableContent;

	/**
	 * {@link Perspective}s of this DataDomain and their keys
	 */
	private HashMap<String, Perspective> recordPerspectiveMap;
	/**
	 * {@link DimensionPerspective}s of this DataDomain and their keys
	 */
	private HashMap<String, Perspective> dimensionPerspectiveMap;

	/**
	 * Default Constructor
	 */
	public DataDomainSerializationData() {

	}

	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public byte[] getTableFileContent() {
		return dataTableContent;
	}

	public void setTableFileContent(byte[] dataTableContent) {
		this.dataTableContent = dataTableContent;
	}

	public HashMap<String, Perspective> getRecordPerspectiveMap() {
		return recordPerspectiveMap;
	}

	public void setRecordPerspectiveMap(
HashMap<String, Perspective> recordPerspectiveMap) {
		this.recordPerspectiveMap = recordPerspectiveMap;
	}

	public HashMap<String, Perspective> getDimensionPerspectiveMap() {
		return dimensionPerspectiveMap;
	}

	public void setDimensionPerspectiveMap(
HashMap<String, Perspective> dimensionDataMap) {
		this.dimensionPerspectiveMap = dimensionDataMap;
	}
}
