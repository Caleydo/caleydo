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
