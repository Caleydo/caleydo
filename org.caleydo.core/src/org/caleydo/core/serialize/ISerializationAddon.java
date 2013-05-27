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

import java.util.Collection;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.datadomain.IDataDomain;

/**
 * @author Samuel Gratzl
 *
 */
public interface ISerializationAddon {
	public Collection<? extends Class<?>> getJAXBContextClasses();

	/**
	 * @param dirName
	 * @param unmarshaller
	 */
	public void deserialize(String dirName, Unmarshaller unmarshaller);
	/**
	 * @param dirName
	 * @param unmarshaller
	 * @param serializationData
	 */
	public void deserialize(String dirName, Unmarshaller unmarshaller, SerializationData data);

	/**
	 * @param toSave
	 * @param marshaller
	 * @param dirName
	 */
	public void serialize(Collection<? extends IDataDomain> toSave, Marshaller marshaller, String dirName);

	/**
	 * @param serializationDataList
	 */
	public void load(SerializationData data);


}
