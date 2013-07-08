/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.genetic.internal;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.serialize.ISerializationAddon;
import org.caleydo.core.serialize.SerializationData;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.datadomain.genetic.Activator;
import org.caleydo.datadomain.genetic.GeneticDataDomain;

/**
 * {@link ISerializationAddon} to register on the one hand the {@link GeneticDataDomain} and on the other hand to
 * deserialize the old {@link BasicInformation}
 * 
 * @author Samuel Gratzl
 * 
 */
public class GeneticSerializationAddon implements ISerializationAddon {
	private static final Logger log = Logger.create(GeneticSerializationAddon.class);

	/** file name of the datadomain-file in project-folders */
	private static final String BASIC_INFORMATION_FILE = "basic_information.xml";

	@Override
	public List<Class<? extends Object>> getJAXBContextClasses() {
		return Arrays.asList(GeneticDataDomain.class, BasicInformation.class);
	}


	@Override
	public void deserialize(String dirName, Unmarshaller unmarshaller) {
		File f = new File(dirName, BASIC_INFORMATION_FILE);
		if (!f.exists())
			return;
		BasicInformation infos;
		try {
			infos = (BasicInformation) unmarshaller.unmarshal(f);
			if (infos != null)
				Activator.setOrganism(infos.getOrganism());
		} catch (JAXBException e) {
			log.error("can't deserialize basic information", e);
		}
	}

	@Override
	public void serialize(Collection<? extends IDataDomain> toSave, Marshaller marshaller, String dirName) {
		// as now part of the project meta data no need to save it anymore
	}

	@Override
	public void load(SerializationData data) {
	}

	@Override
	public void deserialize(String dirName, Unmarshaller unmarshaller, SerializationData data) {

	}



}
