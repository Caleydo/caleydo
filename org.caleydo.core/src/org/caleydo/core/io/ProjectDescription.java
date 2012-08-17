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
/**
 * 
 */
package org.caleydo.core.io;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A description of a project, containing mainly a collection of all @DataTypeSet
 * objects that are needed for loading multiple data sets.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public class ProjectDescription {

	/** The name of the project */
	String projectName;

	/**
	 * A list of {@link DataSetDescription}s that specify the datasets to be
	 * loaded for a project
	 */
	ArrayList<DataSetDescription> dataSetDescriptionCollection = new ArrayList<DataSetDescription>();

	/**
	 * Default constructor
	 */
	public ProjectDescription() {
	}

	/**
	 * @param projectName
	 *            setter, see {@link #projectName}
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the projectName, see {@link #projectName}
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Add a {@link DataSetDescription} to the collection.
	 * 
	 * @param dataSetDescription
	 */
	public void add(DataSetDescription dataSetDescription) {
		dataSetDescriptionCollection.add(dataSetDescription);
	}

	/**
	 * @param dataSetDescriptionCollection
	 *            setter, see {@link #dataSetDescriptionCollection}
	 */
	public void setDataSetDescriptionCollection(
			ArrayList<DataSetDescription> dataSetDescriptionCollection) {
		this.dataSetDescriptionCollection = dataSetDescriptionCollection;
	}

	/**
	 * @return the dataSetDescriptionCollection, see
	 *         {@link #dataSetDescriptionCollection}
	 */
	public ArrayList<DataSetDescription> getDataSetDescriptionCollection() {
		return dataSetDescriptionCollection;
	}

	@Override
	public String toString() {
		return dataSetDescriptionCollection.toString();
	}
}
