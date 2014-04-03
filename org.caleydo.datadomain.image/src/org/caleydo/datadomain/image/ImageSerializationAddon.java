/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.image;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.serialize.ISerializationAddon;
import org.caleydo.core.serialize.SerializationData;

import com.google.common.io.Files;

/**
 * @author Thomas Geymayer
 *
 */
public class ImageSerializationAddon implements ISerializationAddon {

	@Override
	public Collection<? extends Class<?>> getJAXBContextClasses() {
		return Collections.singleton(ImageDataDomain.class);
	}

	@Override
	public void deserialize(String dirName, Unmarshaller unmarshaller) {

	}

	@Override
	public void deserialize( String dirName,
							 Unmarshaller unmarshaller,
							 SerializationData data) {

		// Add all files from the matching data directory
		List<IDataDomain> domains = DataDomainManager.get()
				.getDataDomainsByType(ImageDataDomain.DATA_DOMAIN_TYPE);
		for(IDataDomain dataDomain: domains) {
			ImageSet imageSet = ((ImageDataDomain) dataDomain).getImageSet();
			imageSet.add(new File(dirName, dataDomain.getDataDomainID()));
			imageSet.refreshGroups();
		}
	}

	@Override
	public void serialize( Collection<? extends IDataDomain> toSave,
						   Marshaller marshaller,
						   String dirName ) throws IOException {
		for(IDataDomain dataDomain: toSave)
			if( dataDomain instanceof ImageDataDomain) {
				File savePath = new File(dirName, dataDomain.getDataDomainID());
				savePath.mkdirs();
				for(File img: ((ImageDataDomain)dataDomain).getImageSet()
														   .getFiles()
														   .values()) {
					Files.copy(img, new File(savePath, img.getName()));
				}
			}
	}

	@Override
	public void load(SerializationData data) {

	}

}
