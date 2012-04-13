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
package org.caleydo.core.serialize;

import java.util.HashMap;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;

/**
 * Bean that holds the initialization data for new started Caleydo application. Used to store and restore
 * project or to sync remote clients.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
public class DataDomainSerializationData {

	/** defines the type of usage of the application */
	private ATableBasedDataDomain dataDomain;

	/** content of the set file the application is based on, only used to sync remote clients */
	private byte[] setFileContent;

	/** virtual arrays of this application stored in relation with their their-key */
	private HashMap<String, RecordPerspective> recordPerspectiveMap;
	private HashMap<String, DimensionPerspective> dimensionPerspectiveMap;

	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public byte[] getTableFileContent() {
		return setFileContent;
	}

	public void setTableFileContent(byte[] setFileContent) {
		this.setFileContent = setFileContent;
	}

	public HashMap<String, RecordPerspective> getRecordPerspectiveMap() {
		return recordPerspectiveMap;
	}

	public void setRecordPerspectiveMap(HashMap<String, RecordPerspective> recordPerspectiveMap) {
		this.recordPerspectiveMap = recordPerspectiveMap;
	}

	public HashMap<String, DimensionPerspective> getDimensionPerspectiveMap() {
		return dimensionPerspectiveMap;
	}

	public void setDimensionPerspectiveMap(HashMap<String, DimensionPerspective> dimensionDataMap) {
		this.dimensionPerspectiveMap = dimensionDataMap;
	}
}
