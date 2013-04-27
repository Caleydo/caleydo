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
package org.caleydo.data.importer.setupgenerator;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.ProjectDescription;

/**
 * Base class for setting up and serializing (to xml) a set of
 * {@link DataSetDescription}s which can then be used to create a caleydo
 * project with the importer plug-in.
 * 
 * @author Alexander Lex
 * 
 */
public abstract class DataSetDescriptionSerializer {

	protected ProjectDescription projectDescription = new ProjectDescription();

	protected String outputXMLFilePath = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "caleydo_data.xml";

	/**
	 * Constructor including command line arguments which may be used to specify
	 * the output file path.
	 * 
	 * @param arguments
	 *            the array may contain exactly one string specifying the path
	 *            where the file is saved. Alternatively, the array may be empty
	 *            or null, which writes the file to the default location (see
	 *            {@link #outputXMLFilePath}).
	 */
	public DataSetDescriptionSerializer(String[] arguments) {
		if (arguments != null && arguments.length == 1) {
			outputXMLFilePath = arguments[0];
		}
	}

	/**
	 * Triggers the loading of the <code>DataSetDescriptions</code> and the
	 * serialization.
	 */
	public void run() {
		setUpDataSetDescriptions();
		serialize();
	}
	
	
	/**
	 * The <code>DataSetDescription</code> creation is triggered and all of them
	 * are written into the {@link #projectDescription}
	 */
	protected abstract void setUpDataSetDescriptions();

	/**
	 * Serializes the elements in {@link #projectDescription} to the
	 * {@link #outputXMLFilePath}.
	 */
	public void serialize() {
		JAXBContext context = null;
		try {
			Class<?>[] serializableClasses = new Class<?>[2];
			serializableClasses[0] = DataSetDescription.class;
			serializableClasses[1] = ProjectDescription.class;

			context = JAXBContext.newInstance(serializableClasses);

			Marshaller marshaller;
			marshaller = context.createMarshaller();
			marshaller.marshal(projectDescription, new File(outputXMLFilePath));
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
			System.out.println("Created configuration for "
					+ projectDescription.getDataSetDescriptionCollection()
							.size() + " datasets: " + projectDescription);
			System.out.println("Written to: " + outputXMLFilePath);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}
	}

}
