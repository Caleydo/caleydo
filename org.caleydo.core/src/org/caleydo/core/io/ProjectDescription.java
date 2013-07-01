/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
