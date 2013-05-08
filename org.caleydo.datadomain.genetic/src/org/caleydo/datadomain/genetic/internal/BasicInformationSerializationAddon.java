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
import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.serialize.ISerializationAddon;
import org.caleydo.core.serialize.SerializationData;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.datadomain.genetic.Activator;
import org.caleydo.datadomain.genetic.GeneticMetaData;

public class BasicInformationSerializationAddon implements ISerializationAddon {
	private static final Logger log = Logger.create(BasicInformationSerializationAddon.class);

	/** file name of the datadomain-file in project-folders */
	private static final String BASIC_INFORMATION_FILE = "basic_information.xml";

	@Override
	public Collection<Class<BasicInformation>> getJAXBContextClasses() {
		return Collections.singleton(BasicInformation.class);
	}


	@Override
	public void deserialize(String dirName, Unmarshaller unmarshaller) {
		BasicInformation infos;
		try {
			infos = (BasicInformation) unmarshaller.unmarshal(new File(dirName, BASIC_INFORMATION_FILE));
			if (infos != null)
				Activator.setOrganism(infos.getOrganism());
		} catch (JAXBException e) {
			log.error("can't deserialize basic information", e);
		}
	}

	@Override
	public void serialize(Collection<? extends IDataDomain> toSave, Marshaller marshaller, String dirName) {
		BasicInformation infos = new BasicInformation();
		infos.setOrganism(GeneticMetaData.getOrganism());
		try {
			marshaller.marshal(infos, new File(dirName, BASIC_INFORMATION_FILE));
		} catch (JAXBException e) {
			log.error("can't store organism", e);
		}
	}

	@Override
	public void load(SerializationData data) {
	}

	@Override
	public void deserialize(String dirName, Unmarshaller unmarshaller, SerializationData data) {

	}


}
