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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.manager.BasicInformation;
import org.caleydo.core.util.ExtensionUtils;

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

	/** {@link JAXBContext} for event (de-)serialization */
	private JAXBContext eventContext;

	/** {link JAXBContext} for project (de-)serialization */
	private JAXBContext projectContext;

	private ArrayList<Class<?>> serializableTypes;

	private Collection<ISerializationAddon> addons;

	private SerializationManager() {
		addons = ExtensionUtils.findImplementation(EXTENSION_POINT, "class", ISerializationAddon.class);
		try {
			Collection<Class<? extends AEvent>> eventTypes = getSerializeableEventTypes();
			Class<?>[] classes = new Class<?>[eventTypes.size()];
			classes = eventTypes.toArray(classes);
			eventContext = JAXBContext.newInstance(classes);

			serializableTypes = new ArrayList<Class<?>>();
			serializableTypes.add(ProjectMetaData.class);
			serializableTypes.add(SerializationData.class);
			serializableTypes.add(DataDomainSerializationData.class);
			serializableTypes.add(DataDomainList.class);
			serializableTypes.add(BasicInformation.class);
			for (ISerializationAddon addon : addons)
				serializableTypes.addAll(addon.getJAXBContextClasses());

			createNewProjectContext();
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}
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
	 * Gets the {@link JAXBContext} used to serialize events.
	 *
	 * @return events-serialization {@link JAXBContext}.
	 */
	public JAXBContext getEventContext() {
		return eventContext;
	}

	/**
	 * Gets the {@link JAXBContext} used during load/save caleydo projects.
	 *
	 * @return caleydo-project serialization {@link JAXBContext}.
	 */
	public JAXBContext getProjectContext() {
		return projectContext;
	}

	public void registerSerializableTypes(Class<?>... serializableClasses) {
		for (Class<?> serializableClass : serializableClasses) {
			serializableTypes.add(serializableClass);
		}
		createNewProjectContext();
	}

	/**
	 * Generates and returns a {@link Collection} of all events to serialize
	 *
	 * @return {@link Collection} of event-classes to transmit over the network
	 */
	public static Collection<Class<? extends AEvent>> getSerializeableEventTypes() {
		Collection<Class<? extends AEvent>> eventTypes = new ArrayList<Class<? extends AEvent>>();

		// FIXME: check if the list of individual events needs to be provided
		// here
		eventTypes.add(AEvent.class);

		return eventTypes;
	}

	/**
	 * @return the addons
	 */
	public Collection<ISerializationAddon> getAddons() {
		return addons;
	}
}
