/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.serialize;

import java.util.Collection;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.datadomain.IDataDomain;

/**
 * addon specification to load and store a project
 * 
 * @author Samuel Gratzl
 * 
 */
public interface ISerializationAddon {
	/**
	 * the additional jaxb classes to register to the context
	 * 
	 * @return
	 */
	public Collection<? extends Class<?>> getJAXBContextClasses();

	/**
	 * triggers to deserialize data from the given project directory using the given {@link Unmarshaller}
	 * 
	 * @param dirName
	 * @param unmarshaller
	 */
	public void deserialize(String dirName, Unmarshaller unmarshaller);

	/**
	 * triggers for a delayed project deserialization where the intermediate descriptions are stored within the given
	 * {@link SerializationData} object. The actual loading of the data should take place in
	 * {@link #load(SerializationData)}
	 * 
	 * @param dirName
	 * @param unmarshaller
	 * @param serializationData
	 */
	public void deserialize(String dirName, Unmarshaller unmarshaller, SerializationData data);

	/**
	 * triggers to persist the custom data using the given data
	 * 
	 * @param toSave
	 *            the data domain, that are going to be stored
	 * @param marshaller
	 * @param dirName
	 */
	public void serialize(Collection<? extends IDataDomain> toSave, Marshaller marshaller, String dirName);

	/**
	 * see {@link #deserialize(String, Unmarshaller, SerializationData)} to actually load the data
	 * 
	 * @param serializationDataList
	 */
	public void load(SerializationData data);


}
