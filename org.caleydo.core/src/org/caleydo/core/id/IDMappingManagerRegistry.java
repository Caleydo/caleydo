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
/**
 * 
 */
package org.caleydo.core.id;

import java.util.Collection;
import java.util.HashMap;

/**
 * Registry for {@link IDMappingManagers}. Each {@link IDMappingManager} is
 * associated with an {@link IDCategory}, which summarizes which {@link IDType}s
 * can be mapped to each other.For each {@link IDCategory} there is exactly one
 * {@link IDMappingManager} which may only be accessed through this Registry.
 * 
 * @author Alexander Lex
 */
public class IDMappingManagerRegistry {

	private volatile static IDMappingManagerRegistry instance;

	HashMap<IDCategory, IDMappingManager> hashIDMappingManagers = new HashMap<IDCategory, IDMappingManager>(
			5);

	private IDMappingManagerRegistry() {

	}

	public static IDMappingManagerRegistry get() {
		if (instance == null) {
			synchronized (IDMappingManagerRegistry.class) {
				if (instance == null)
					instance = new IDMappingManagerRegistry();
			}
		}
		return instance;
	}

	public boolean hasIDMappingManager(IDCategory idCategory) {
		return hashIDMappingManagers.containsKey(idCategory);
	}

	/**
	 * Returns the {@link IDMappingManager} for the {@link IDCategory}
	 * specified. If no such {@link IDMappingManager} exists, a new one is
	 * created and registered.
	 * 
	 * @param idCategory
	 * @return
	 */
	public IDMappingManager getIDMappingManager(IDCategory idCategory) {
		if (idCategory == null)
			throw new IllegalArgumentException("idCategory was null");
		if (!hashIDMappingManagers.containsKey(idCategory)) {
			hashIDMappingManagers.put(idCategory, new IDMappingManager(idCategory));
		}
		return hashIDMappingManagers.get(idCategory);
	}

	/**
	 * Same as {@link #getIDMappingManager(IDCategory)} but for IDType.
	 * 
	 * @param idType
	 * @return
	 */
	public IDMappingManager getIDMappingManager(IDType idType) {
		return getIDMappingManager(idType.getIDCategory());
	}

	@Override
	public String toString() {
		return "Registered IDMappingManagers: " + hashIDMappingManagers.keySet();
	}

	/**
	 * Returns all mapping managers that are currently registered.
	 * 
	 * @return collection of mapping managers
	 */
	public Collection<IDMappingManager> getAllIDMappingManager() {
		return hashIDMappingManagers.values();
	}
}
