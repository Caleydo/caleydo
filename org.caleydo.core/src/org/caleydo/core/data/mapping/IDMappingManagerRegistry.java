/**
 * 
 */
package org.caleydo.core.data.mapping;

import java.util.HashMap;

import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;

/**
 * Registry for {@link IDMappingManagers} . Each {@link IDMappingManager} is associated with an
 * {@link IDCategory}, which summarizes which {@link IDType}s can be mapped to each other.For each
 * {@link IDCategory} there is exactly one {@link IDMappingManager} which may only be accessed through this
 * Registry.
 * 
 * @author Alexander Lex
 */
public class IDMappingManagerRegistry {

	private volatile static IDMappingManagerRegistry instance;

	HashMap<IDCategory, IDMappingManager> hashIDMappingManagers =
		new HashMap<IDCategory, IDMappingManager>(5);

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

	/**
	 * Returns the {@link IDMappingManager} for the {@link IDCategory} specified. If no such
	 * {@link IDMappingManager} exists, a new one is created and registered.
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

	@Override
	public String toString() {
		return "Registered IDMappingManagers: " + hashIDMappingManagers.keySet();
	}

}
