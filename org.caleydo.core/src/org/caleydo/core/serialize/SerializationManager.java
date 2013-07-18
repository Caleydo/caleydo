/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.serialize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.color.AlexColorPalette;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;
import org.caleydo.core.util.color.StyledColor;

/**
 * Central access point for xml-serialization related tasks.
 *
 * @author Werner Puff
 * @author Alexander Lex
 * @author Marc Streit
 */
public class SerializationManager {
	private static final String EXTENSION_POINT = "org.caleydo.serialize.addon";

	private static volatile SerializationManager instance = null;

	/** {link JAXBContext} for project (de-)serialization */
	private JAXBContext projectContext;

	private List<Class<?>> serializableTypes;

	private final Collection<ISerializationAddon> addons;

	private SerializationManager() {
		addons = ExtensionUtils.findImplementation(EXTENSION_POINT, "class", ISerializationAddon.class);

		serializableTypes = new ArrayList<Class<?>>();

		serializableTypes.add(ProjectMetaData.class);
		serializableTypes.add(SerializationData.class);
		serializableTypes.add(DataDomainSerializationData.class);
		serializableTypes.add(DataDomainList.class);
		serializableTypes.add(Color.class);
		serializableTypes.add(AlexColorPalette.AlexColorPaletteColor.class);
		serializableTypes.add(ColorBrewer.ColorBrewerColor.class);
		serializableTypes.add(StyledColor.class);

		for (ISerializationAddon addon : addons)
			serializableTypes.addAll(addon.getJAXBContextClasses());

		createNewProjectContext();
	}

	private void createNewProjectContext() {
		try {
			Class<?>[] projectClasses = new Class<?>[serializableTypes.size()];
			serializableTypes.toArray(projectClasses);
			projectContext = JAXBContext.newInstance(projectClasses);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}
	}

	public synchronized static SerializationManager get() {
		if (instance == null) {
			synchronized (SerializationManager.class) {
				if (instance == null)
					instance = new SerializationManager();
			}
		}
		return instance;
	}

	/**
	 * Gets the {@link JAXBContext} used during load/save caleydo projects.
	 *
	 * @return caleydo-project serialization {@link JAXBContext}.
	 */
	public JAXBContext getProjectContext() {
		return projectContext;
	}

	/**
	 * @return the addons
	 */
	public Collection<ISerializationAddon> getAddons() {
		return addons;
	}
}
