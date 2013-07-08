/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.generic.internal;

import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.serialize.ISerializationAddon;
import org.caleydo.core.serialize.SerializationData;
import org.caleydo.datadomain.generic.GenericDataDomain;

/**
 * simple addon to register the {@link GenericDataDomain}
 * 
 * @author Samuel Gratzl
 * 
 */
public class GenericSerializationAddon implements ISerializationAddon {

	@Override
	public Collection<? extends Class<?>> getJAXBContextClasses() {
		return Collections.singleton(GenericDataDomain.class);
	}

	@Override
	public void deserialize(String dirName, Unmarshaller unmarshaller) {

	}

	@Override
	public void deserialize(String dirName, Unmarshaller unmarshaller, SerializationData data) {

	}

	@Override
	public void serialize(Collection<? extends IDataDomain> toSave, Marshaller marshaller, String dirName) {

	}

	@Override
	public void load(SerializationData data) {

	}

}
