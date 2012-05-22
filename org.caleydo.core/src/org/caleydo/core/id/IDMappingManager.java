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
package org.caleydo.core.id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.collection.EColumnType;
import org.caleydo.core.util.collection.MultiHashMap;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

/**
 * Provides methods for mapping different IDTypes using a graph of Maps.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Christian Partl
 */
public class IDMappingManager {

	private IDCategory idCategory;

	/**
	 * HashMap that contains all mappings identified by their MappingType.
	 */
	private HashMap<MappingType, Map<?, ?>> hashMappingType2Map;

	private HashMap<String, MappingType> hashMappingTypeString2MappingType;

	/**
	 * Graph of mappings. IDTypes are the vertices of the graph, edges represent a mapping from one IDType to
	 * another that is backed up by a corresponding map in hashType2Mapping.
	 */
	private DefaultDirectedWeightedGraph<IDType, MappingType> mappingGraph;

	/**
	 * Constructor.
	 */
	IDMappingManager(IDCategory idCategory) {
		this.idCategory = idCategory;
		hashMappingType2Map = new HashMap<MappingType, Map<?, ?>>();
		hashMappingTypeString2MappingType = new HashMap<String, MappingType>();
		mappingGraph = new DefaultDirectedWeightedGraph<IDType, MappingType>(MappingType.class);
	}

	/**
	 * Adds a new map for the specified mapping type. To fill that map with elements, use the method
	 * {@link #getMap(MappingType)} after calling this one.
	 * 
	 * @param <K>
	 *            Type of Keys of the map
	 * @param <V>
	 *            Type of Values of the map
	 * @param fromIDType
	 *            Specifies the source ID type.
	 * @param toIDType
	 *            Specifies the target ID type.
	 * @param isMultiMap
	 *            Determines if a multi map will be created.
	 */
	public <K, V> MappingType createMap(IDType fromIDType, IDType toIDType, boolean isMultiMap) {

		if (hashMappingTypeString2MappingType.containsKey(fromIDType.getTypeName() + "_2_"
			+ toIDType.getTypeName()))
			return hashMappingTypeString2MappingType.get(fromIDType.getTypeName() + "_2_"
				+ toIDType.getTypeName());

		if (!mappingGraph.containsVertex(fromIDType))
			mappingGraph.addVertex(fromIDType);
		if (!mappingGraph.containsVertex(toIDType))
			mappingGraph.addVertex(toIDType);

		MappingType mappingType = MappingType.registerMappingType(fromIDType, toIDType, isMultiMap);
		hashMappingTypeString2MappingType.put(mappingType.toString(), mappingType);
		mappingGraph.addEdge(fromIDType, toIDType, mappingType);
		if (mappingType.isMultiMap()) {
			hashMappingType2Map.put(mappingType, new MultiHashMap<K, V>());
			mappingGraph.setEdgeWeight(mappingType, Double.MAX_VALUE);
		}
		else {
			hashMappingType2Map.put(mappingType, new HashMap<K, V>());
			mappingGraph.setEdgeWeight(mappingType, 1);
		}

		return mappingType;
	}

	/**
	 * Creates a reverse map to an already existent map.
	 * 
	 * @param <SrcType>
	 * @param <DestType>
	 * @param sourceMappingType
	 *            Mapping type the reverse map shall be created for.
	 */
	@SuppressWarnings("unchecked")
	public <SrcType, DestType> void createReverseMap(MappingType srcMappingType) {

		MappingType reverseType =
			MappingType.registerMappingType(srcMappingType.getToIDType(), srcMappingType.getFromIDType(),
				srcMappingType.isMultiMap());
		hashMappingTypeString2MappingType.put(reverseType.toString(), reverseType);

		Map<DestType, SrcType> reverseMap;

		if (srcMappingType.isMultiMap()) {
			MultiHashMap<SrcType, DestType> sourceMap =
				(MultiHashMap<SrcType, DestType>) hashMappingType2Map.get(srcMappingType);

			if (reverseType.isMultiMap()) {
				hashMappingType2Map.put(reverseType, new MultiHashMap<DestType, SrcType>());

				reverseMap = (MultiHashMap<DestType, SrcType>) hashMappingType2Map.get(reverseType);
			}
			else {
				hashMappingType2Map.put(reverseType, new HashMap<DestType, SrcType>());

				reverseMap = (HashMap<DestType, SrcType>) hashMappingType2Map.get(reverseType);
			}

			for (SrcType key : sourceMap.keySet()) {
				for (DestType value : sourceMap.getAll(key)) {
					reverseMap.put(value, key);
				}
			}
		}
		else {
			hashMappingType2Map.put(reverseType, new HashMap<DestType, SrcType>());

			reverseMap = (HashMap<DestType, SrcType>) hashMappingType2Map.get(reverseType);
			Map<SrcType, DestType> sourceMap =
				(HashMap<SrcType, DestType>) hashMappingType2Map.get(srcMappingType);

			for (SrcType key : sourceMap.keySet()) {
				reverseMap.put(sourceMap.get(key), key);
			}
		}

		mappingGraph.addEdge(reverseType.getFromIDType(), reverseType.getToIDType(), reverseType);

		if (reverseType.isMultiMap()) {
			mappingGraph.setEdgeWeight(reverseType, Double.MAX_VALUE);
		}
		else {
			mappingGraph.setEdgeWeight(reverseType, 1);
		}
	}

	/**
	 * Method takes a map that contains identifier codes and creates a new resolved codes. Resolving means
	 * mapping from code to internal ID.
	 * 
	 * @param <KeyType>
	 * @param <ValueType>
	 * @param mappingType
	 *            Mapping type that specifies the already existent map which is used for creating the code
	 *            resolved map.
	 */
	@SuppressWarnings("unchecked")
	public <KeyType, ValueType> void createCodeResolvedMap(MappingType mappingType,
		IDType codeResolvedFromType, IDType codeResolvedToType) {

		Map codeResolvedMap = null;

		IDType originKeyType = mappingType.getFromIDType();
		IDType originValueType = mappingType.getToIDType();
		IDType destKeyType = codeResolvedFromType;
		IDType destValueType = codeResolvedToType;

		MappingType destMappingType =
			MappingType.registerMappingType(codeResolvedFromType, codeResolvedToType, false); // MULTI??
		hashMappingTypeString2MappingType.put(mappingType.toString(), mappingType);

		Map<KeyType, ValueType> srcMap = (Map<KeyType, ValueType>) hashMappingType2Map.get(mappingType);

		// FIXME: IS THIS NEEDED ANYMORE?
		// if (mappingType != MappingType.REFSEQ_MRNA_2_DAVID) {
		// // Remove old unresolved map
		// hashMappingType2Map.remove(mappingType);
		// mappingGraph.removeEdge(mappingType.getFromIDType(), mappingType.getToIDType());
		// }

		if (originKeyType == destKeyType) {
			if (originValueType != destValueType) {
				if (originKeyType.getColumnType() == EColumnType.INT
					&& destValueType.getColumnType() == EColumnType.INT) {
					codeResolvedMap = new HashMap<Integer, Integer>();

					if (!mappingType.isMultiMap()) {
						codeResolvedMap = new HashMap<Integer, Integer>();

						for (KeyType key : srcMap.keySet()) {
							codeResolvedMap.put(key, getID(originValueType, destValueType, srcMap.get(key)));
						}
					}
					else {
						codeResolvedMap = new MultiHashMap<Integer, Integer>();
						MultiHashMap<Integer, String> srcMultiMap = (MultiHashMap<Integer, String>) srcMap;
						Integer iID = 0;

						for (KeyType key : srcMap.keySet()) {
							for (String sID : srcMultiMap.getAll(key)) {
								iID = getID(originValueType, destValueType, sID);

								if (iID == null || iID == -1) {
									continue;
									// throw new
									// IllegalStateException("No DAVID mapping for RefSeq "
									// +key);
								}

								codeResolvedMap.put(key, iID);
							}
						}
					}
				}
				else if (originKeyType.getColumnType() == EColumnType.INT
					&& destValueType.getColumnType() == EColumnType.STRING) {
					codeResolvedMap = new HashMap<Integer, String>();

					throw new RuntimeException("Not implemented!");
				}
				else if (originKeyType.getColumnType() == EColumnType.STRING
					&& destValueType.getColumnType() == EColumnType.STRING) {
					codeResolvedMap = new HashMap<String, String>();

					throw new RuntimeException("Not implemented!");
				}
				else if (originKeyType.getColumnType() == EColumnType.STRING
					&& destValueType.getColumnType() == EColumnType.INT) {

					if (!mappingType.isMultiMap()) {
						codeResolvedMap = new HashMap<String, Integer>();

						for (KeyType key : srcMap.keySet()) {
							codeResolvedMap.put(key, getID(originValueType, destValueType, srcMap.get(key)));
						}
					}
					else {
						codeResolvedMap = new MultiHashMap<String, Integer>();
						MultiHashMap<String, String> srcMultiMap = (MultiHashMap<String, String>) srcMap;
						Integer iID = 0;

						for (KeyType key : srcMap.keySet()) {
							for (String sID : srcMultiMap.getAll(key)) {
								iID = getID(originValueType, destValueType, sID);

								if (iID == null || iID == -1) {
									continue;
									// throw new
									// IllegalStateException("No DAVID mapping for RefSeq "
									// +key);
								}

								codeResolvedMap.put(key, iID);
							}
						}
					}
				}
			}
		}
		else {
			if (originValueType == destValueType) {
				if (destKeyType.getColumnType() == EColumnType.INT
					&& destValueType.getColumnType() == EColumnType.INT) {
					codeResolvedMap = new HashMap<Integer, Integer>();

					MappingType conversionType = mappingType;// MappingType.valueOf(originKeyType + "_2_" +
																// destKeyType);

					if (!mappingType.isMultiMap()) {
						codeResolvedMap = new HashMap<Integer, Integer>();

						for (KeyType key : srcMap.keySet()) {
							codeResolvedMap.put(
								getID(conversionType.getFromIDType(), conversionType.getToIDType(), key),
								srcMap.get(key));
						}
					}
					else {
						codeResolvedMap = new MultiHashMap<Integer, Integer>();
						MultiHashMap<String, Integer> srcMultiMap = (MultiHashMap<String, Integer>) srcMap;
						Integer iResolvedID = 0;

						for (KeyType key : srcMap.keySet()) {
							iResolvedID =
								getID(conversionType.getFromIDType(), conversionType.getToIDType(), key);

							if (iResolvedID == null) {
								// generalManager.getLogger().log(new Status(Status.WARNING,
								// GeneralManager.PLUGIN_ID,
								// iMappingErrors++ + ": No DAVID mapping for RefSeq " + key));
								continue;
							}

							for (Integer iID : srcMultiMap.getAll(key)) {
								if (iID == null) {
									continue;
								}

								codeResolvedMap.put(iResolvedID, iID);
							}
						}
					}
				}
				else if (destKeyType.getColumnType() == EColumnType.INT
					&& destValueType.getColumnType() == EColumnType.STRING) {
					codeResolvedMap = new HashMap<Integer, String>();

					MappingType conversionType = mappingType;// MappingType.valueOf(originKeyType + "_2_" +
																// destKeyType);

					for (KeyType key : srcMap.keySet()) {
						codeResolvedMap.put(
							getID(conversionType.getFromIDType(), conversionType.getToIDType(), key),
							srcMap.get(key));
					}
				}
				else if (destKeyType.getColumnType() == EColumnType.STRING
					&& destValueType.getColumnType() == EColumnType.STRING) {
					codeResolvedMap = new HashMap<String, String>();

					throw new RuntimeException("Not implemented!");
				}
				else if (destKeyType.getColumnType() == EColumnType.STRING
					&& destValueType.getColumnType() == EColumnType.INT) {
					codeResolvedMap = new HashMap<String, Integer>();

					throw new RuntimeException("Not implemented!");
				}
			}
		}

		// Add new code resolved map
		hashMappingType2Map.put(destMappingType, codeResolvedMap);
		mappingGraph.addEdge(destMappingType.getFromIDType(), destMappingType.getToIDType(), mappingType);
		if (destMappingType.isMultiMap()) {
			mappingGraph.setEdgeWeight(mappingType, Double.MAX_VALUE);
		}
		else {
			mappingGraph.setEdgeWeight(mappingType, 1);
		}
	}

	/**
	 * Gets the map of the specified mapping type for manipulation.
	 * 
	 * @param <KeyType>
	 * @param <ValueType>
	 * @param type
	 *            Mapping type that identifies the map.
	 * @return Map that corresponds to the specified mapping type. If no such map exists, null is returned.
	 */
	@SuppressWarnings("unchecked")
	public <KeyType, ValueType> Map<KeyType, ValueType> getMap(MappingType type) {
		return (Map<KeyType, ValueType>) hashMappingType2Map.get(type);
	}

	/**
	 * Returns, whether a mapping is possible from the specified source IDType to the destination IDType.
	 * 
	 * @param source
	 *            Source IDType of the mapping.
	 * @param destination
	 *            Destination IDType of the mapping.
	 * @return True, if a mapping is possible, false otherwise.
	 */
	public final boolean hasMapping(IDType source, IDType destination) {

		if (source.equals(destination))
			return true;
		try {
			return (DijkstraShortestPath.findPathBetween(mappingGraph, source, destination) != null);
		}
		catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * Tries to find the mapping from the source IDType to the destination IDType of the specified sourceID
	 * along a path of IDTypes where mappings exist. If no such path is found, null is returned. If the path
	 * includes multimappings, a Set of values is returned. Note that there will always be chosen a path that
	 * does not include multimappings over paths that include multimappings if more than one path exists.
	 * 
	 * @param <K>
	 *            Type of the sourceID
	 * @param <V>
	 *            Type of the expected result of the mapping
	 * @param source
	 *            IDType of the source data
	 * @param destination
	 *            IDType of the destination data
	 * @param sourceID
	 *            ID for which the mapping shall be found
	 * @return If no mapping is found, null, otherwise the corresponding ID, or Set of IDs.
	 * @deprecated use {@link #getIDAsSet(IDType, IDType, Object)} instead
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public <K, V> V getID(IDType source, IDType destination, K sourceID) {

		if (source.equals(destination))
			return (V) sourceID;

		List<MappingType> path;

		try {
			path = DijkstraShortestPath.findPathBetween(mappingGraph, source, destination);
		}
		catch (IllegalArgumentException e) {
			Logger.log(new Status(Status.ERROR, this.toString(), "One of the data types " + source + " and "
				+ destination + " is not registered with this IDMappingManager."));

			// data type is not in the mapping
			return null;
		}

		if (path == null) {
			Logger.log(new Status(Status.ERROR, this.toString(), "No mapping path found between " + source
				+ " and " + destination));
			return null;
		}

		Object currentID = sourceID;

		Set<Object> keys = null;
		Collection<Object> values = new ArrayList<Object>();

		for (MappingType edge : path) {
			Map<?, ?> currentMap = hashMappingType2Map.get(edge);

			if (keys != null) {
				for (Object key : keys) {
					if (edge.isMultiMap()) {
						Set<Object> temp = (Set<Object>) ((MultiHashMap<?, ?>) (currentMap)).getAll(key);
						if (temp != null)
							values.addAll(temp);
					}
					else {
						Object value = currentMap.get(key);
						if (value != null)
							values.add(value);
					}
				}
				if (values.isEmpty())
					return null;

				keys = new HashSet<Object>();
				for (Object value : values) {
					keys.add(value);
				}
				values.clear();
			}
			else {
				if (edge.isMultiMap()) {
					keys = (Set<Object>) ((MultiHashMap<?, ?>) (currentMap)).getAll(currentID);
					if ((keys == null) || (keys.isEmpty()))
						return null;
				}
				else {
					currentID = currentMap.get(currentID);
					if (currentID == null)
						return null;
				}

			}
		}
		if (keys != null)
			return (V) keys;
		return (V) currentID;
	}

	/**
	 * Tries to find the mapping from the source IDType to the destination IDType of the specified sourceID
	 * along a path of IDTypes where mappings exist. If no such path is found, null is returned. The result
	 * will always be a Set of the found mappings.
	 * 
	 * @param <K>
	 *            Type of the sourceID
	 * @param <V>
	 *            Type of the expected result of the mapping
	 * @param source
	 *            IDType of the source data
	 * @param destination
	 *            IDType of the destination data
	 * @param sourceID
	 *            ID for which the mapping shall be found
	 * @return If no mapping is found, null, otherwise the Set containing the corresponding ID(s).
	 */
	@SuppressWarnings("unchecked")
	public <K, V> Set<V> getIDAsSet(IDType source, IDType destination, K sourceID) {

		Set<V> setResult = new HashSet<V>();

		if (source.equals(destination)) {
			setResult.add((V) sourceID);
			return setResult;
		}
		List<MappingType> path;
		try {
			path = DijkstraShortestPath.findPathBetween(mappingGraph, source, destination);
		}
		catch (IllegalArgumentException e) {
			Logger.log(new Status(IStatus.INFO, toString(), "No mapping found between " + source + " and "
				+ destination + " for: " + sourceID));
			return null;
		}
		Object currentID = sourceID;

		if (path == null)
			return null;

		Set<Object> keys = null;
		Collection<Object> values = new ArrayList<Object>();

		for (MappingType edge : path) {
			Map<?, ?> currentMap = hashMappingType2Map.get(edge);

			if (keys != null) {
				for (Object key : keys) {
					if (edge.isMultiMap()) {
						Set<Object> temp = (Set<Object>) ((MultiHashMap<?, ?>) (currentMap)).getAll(key);
						if (temp != null)
							values.addAll(temp);
					}
					else {
						Object value = currentMap.get(key);
						if (value != null)
							values.add(value);
					}
				}
				if (values.isEmpty())
					return null;

				keys = new HashSet<Object>();
				for (Object value : values) {
					keys.add(value);
				}
				values.clear();
			}
			else {
				if (edge.isMultiMap()) {
					keys = (Set<Object>) ((MultiHashMap<?, ?>) (currentMap)).getAll(currentID);
					if ((keys == null) || (keys.isEmpty()))
						return null;
				}
				else {
					currentID = currentMap.get(currentID);
					if (currentID == null)
						return null;
				}

			}
		}
		if (keys != null)
			return (Set<V>) keys;

		setResult.add((V) currentID);

		return setResult;
	}

	// @Override
	// public List<IDType> getIDTypes(EIDCategory category) {
	// ArrayList<IDType> idTypes = new ArrayList<IDType>();
	//
	// for (IDType idType : mappingGraph.vertexSet()) {
	// if (idType.getCategory().equals(category))
	// idTypes.add(idType);
	// }
	// return idTypes;
	// }

	// public void printGraph() {
	// System.out.println(mappingGraph.toString());
	// }

	/**
	 * Determines whether the IDMappingManager holds a map that contains the specified element of the
	 * specified type.
	 * 
	 * @param <T>
	 * @param idType
	 *            IDType of the element.
	 * @param element
	 *            Element to be found.
	 * @return True, if such an element is fund, false otherwise.
	 */
	public <T> boolean doesElementExist(IDType idType, T element) {
		Set<MappingType> edges = mappingGraph.edgesOf(idType);

		for (MappingType edge : edges) {
			Map<?, ?> currentMap = hashMappingType2Map.get(edge);
			if (currentMap != null) {
				if (currentMap.containsKey(element))
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns all id types registered in this ID Mapping Manager
	 * 
	 * @return
	 */
	public HashSet<IDType> getIDTypes() {
		HashSet<IDType> idTypes = new HashSet<IDType>();
		for (MappingType mappingType : hashMappingType2Map.keySet()) {
			idTypes.add(mappingType.getFromIDType());
			idTypes.add(mappingType.getToIDType());
		}

		return idTypes;
	}

	/**
	 * Returns all mapping types of currently loaded mappings.
	 * 
	 * @return
	 */
	public MappingType getMappingType(String mappingTypeString) {
		return hashMappingTypeString2MappingType.get(mappingTypeString);
	}

	@Override
	public String toString() {
		return "IDMappingManager for " + idCategory + " with registered id types: "
			+ hashMappingType2Map.keySet();
	}
}
