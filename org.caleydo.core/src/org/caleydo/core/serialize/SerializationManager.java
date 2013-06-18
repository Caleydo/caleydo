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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.color.AlexColorPalette;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;

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
