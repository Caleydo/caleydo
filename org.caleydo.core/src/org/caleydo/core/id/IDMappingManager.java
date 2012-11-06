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

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.collection.MultiHashMap;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

/**
 * <p>
 * Handles the mapping between IDs of different {@link IDType}s that share a
 * common {@link IDCategory}.
 * </p>
 * <p>
 * Mappings between several different types of IDs make the relationships
 * between multiple datasets possible. The <code>IDMappingManager</code>
 * provides utilities for such a mapping for identifiers of a common family.
 * This family relationship is expressed by the {@link IDCategory} of this
 * manager. All {@link IDType}s that can be mapped to one an other have to
 * belong to the same <code>IDCategory</code>.
 * </p>
 * <p>
 * The mapping manager contains a list of maps which are defined through their
 * {@link MappingType}, which basically contains a two id types, the
 * "fromIDType" which corresponds to the key of a map, and the "toIDType" which
 * is the value of the map.
 * </p>
 * <p>
 * Every <code>IDCategory</code> contains a <b>primary mapping type</b>. This
 * primary mapping type can be <b>externally provided</b>, in which case the
 * manager assumes that all maps are somehow connected through this external
 * type, or it can be <b>automatically created</b>, in which case the mapping
 * manager creates a mapping for every type where
 * {@link IDType#isInternalType()} is false. The primary mapping type must be of
 * data type Integer, which guarantees that for every IDCategory there is an
 * Identifier that can be used with Classes such as {@link VirtualArray}s and
 * {@link SelectionManager}s.
 * </p>
 * <p>
 * Maps can either contain only unique mappings (for every key ID exactly one
 * value ID), or accommodate multi-mappings (for every key ID multiple value
 * IDs.
 * </p>
 * <p>
 * Maps can also be specified to be backed by <b>reverse maps</b> where the key
 * and value are exchanged. If a reverse map is desired it is created and
 * maintained automatically.
 * </p>
 *
 * <h2>Known Issues</h2>
 * <p>
 * Multi-mapping works in general, but it's not clear how it is preserved e.g.
 * in reverse maps
 * </p>
 * <p>
 * Code-resolved maps only work for special cases
 * </p>
 *
 * <p>
 * TODO: implement search feature
 * </p>
 *
 * @author Marc Streit
 * @author Alexander Lex
 * @author Christian Partl
 *
 */
public class IDMappingManager {

	/**
	 * The {@link IDCategory} for which this mapping manager provides the
	 * mapping
	 */
	private IDCategory idCategory;

	/** A counter for dynamically created primary IDs */
	private int primaryTypeCounter = 0;

	/**
	 * HashMap that contains all mappings identified by their MappingType.
	 */
	private HashMap<MappingType, Map<?, ?>> hashMappingType2Map;

	/**
	 * Graph of mappings. IDTypes are the vertices of the graph, edges represent
	 * a mapping from one IDType to another that is backed up by a corresponding
	 * map in hashType2Mapping.
	 */
	private DefaultDirectedWeightedGraph<IDType, MappingType> mappingGraph;

	/**
	 * Constructor.
	 */
	IDMappingManager(IDCategory idCategory) {
		this.idCategory = idCategory;
		idCategory.initialize();
		hashMappingType2Map = new HashMap<MappingType, Map<?, ?>>();
		mappingGraph = new DefaultDirectedWeightedGraph<IDType, MappingType>(MappingType.class);
	}

	/**
	 * <p>
	 * Adds a new map for the specified mapping type. To fill that map with
	 * elements, use the {@link #addMapping(MappingType, Object, Object)}
	 * method.
	 * </p>
	 * <p>
	 * </p>
	 *
	 * @param <K> Type of Keys of the map
	 * @param <V> Type of Values of the map
	 * @param fromIDType Specifies the source ID type.
	 * @param toIDType Specifies the target ID type.
	 * @param isMultiMap If true, a multi-map will be created (i.e,. multiple
	 *            values of toIDType can map to one key), else only a single
	 *            key-value relationship is allowed
	 * @param createReverseMap Flag that determines whether the resolution of
	 *            fromIDType to toIDType should also be created from toIDType to
	 *            fromIDType
	 */
	public synchronized <K, V> MappingType createMap(IDType fromIDType, IDType toIDType, boolean isMultiMap,
			boolean createReverseMap) {

		MappingType mappingType = MappingType.registerMappingType(fromIDType, toIDType, isMultiMap, createReverseMap);
		if (hashMappingType2Map.containsKey(mappingType))
			return mappingType;

		if (!mappingGraph.containsVertex(fromIDType))
			mappingGraph.addVertex(fromIDType);
		if (!mappingGraph.containsVertex(toIDType))
			mappingGraph.addVertex(toIDType);

		mappingGraph.addEdge(fromIDType, toIDType, mappingType);

		if (mappingType.isMultiMap()) {
			hashMappingType2Map.put(mappingType, new MultiHashMap<K, V>());
			mappingGraph.setEdgeWeight(mappingType, Double.MAX_VALUE);
		}
		else {
			hashMappingType2Map.put(mappingType, new HashMap<K, V>());
			mappingGraph.setEdgeWeight(mappingType, 1);
		}

		if (createReverseMap) {
			createReverseMap(mappingType);
		}

		if (idCategory.isPrimaryMappingTypeDefault()) {
			// in this case we need to create a mapping for the primary type to
			// non-internal types
			if (!fromIDType.isInternalType() && !fromIDType.equals(idCategory.getPrimaryMappingType())
					&& MappingType.getType(fromIDType, idCategory.getPrimaryMappingType()) == null) {
				createMap(fromIDType, idCategory.getPrimaryMappingType(), false, true);
			}

			if (!toIDType.isInternalType() && !toIDType.equals(idCategory.getPrimaryMappingType())
					&& MappingType.getType(toIDType, idCategory.getPrimaryMappingType()) == null) {
				createMap(toIDType, idCategory.getPrimaryMappingType(), false, true);
			}
		}

		return mappingType;
	}

	/**
	 * Creates a reverse map to an already existing map.
	 *
	 * TODO: Check how multi-mapping is handled.
	 *
	 * @param <SrcType>
	 * @param <DestType>
	 * @param sourceMappingType Mapping type the reverse map shall be created
	 *            for.
	 */
	@SuppressWarnings("unchecked")
	private <SrcType, DestType> void createReverseMap(MappingType srcMappingType) {

		MappingType reverseType = MappingType.registerMappingType(srcMappingType.getToIDType(),
				srcMappingType.getFromIDType(), srcMappingType.isMultiMap(), true);

		Map<DestType, SrcType> reverseMap;

		if (srcMappingType.isMultiMap()) {
			MultiHashMap<SrcType, DestType> sourceMap = (MultiHashMap<SrcType, DestType>) hashMappingType2Map
					.get(srcMappingType);

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
			Map<SrcType, DestType> sourceMap = (HashMap<SrcType, DestType>) hashMappingType2Map.get(srcMappingType);

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
	 * Method takes a map that contains identifier codes and creates a new
	 * resolved codes. Resolving means mapping from code to internal ID.
	 *
	 * TODO: several cases are not handled
	 *
	 * @param <KeyType>
	 * @param <ValueType>
	 * @param originalMappingType Mapping type that specifies the already
	 *            existent map which is used for creating the code resolved map.
	 */
	@SuppressWarnings("unchecked")
	public <KeyType, ValueType> void createCodeResolvedMap(MappingType originalMappingType,
			IDType codeResolvedFromType, IDType codeResolvedToType) {

		@SuppressWarnings("rawtypes") Map codeResolvedMap = null;

		IDType originKeyType = originalMappingType.getFromIDType();
		IDType originValueType = originalMappingType.getToIDType();
		IDType destKeyType = codeResolvedFromType;
		IDType destValueType = codeResolvedToType;

		MappingType destMappingType = MappingType.registerMappingType(codeResolvedFromType, codeResolvedToType, false,
				false); // MULTI??

		Map<KeyType, ValueType> srcMap = (Map<KeyType, ValueType>) hashMappingType2Map.get(originalMappingType);

		if (originKeyType == destKeyType) {
			if (originValueType != destValueType) {
				if (originKeyType.getColumnType() == EDataType.INT && destValueType.getColumnType() == EDataType.INT) {
					codeResolvedMap = new HashMap<Integer, Integer>();

					if (!originalMappingType.isMultiMap()) {

						codeResolvedMap = new HashMap<Integer, Integer>();

						for (KeyType key : srcMap.keySet()) {
							Set<Integer> resolvedIDs = getIDAsSet(originValueType, destValueType, key);

							if (resolvedIDs == null) {
								continue;
							}
							for (Integer resolvedID : resolvedIDs) {
								codeResolvedMap.put(key, resolvedID);
							}
						}
					}
					else {
						codeResolvedMap = new MultiHashMap<Integer, Integer>();
						MultiHashMap<Integer, String> srcMultiMap = (MultiHashMap<Integer, String>) srcMap;

						for (KeyType key : srcMap.keySet()) {
							for (String sID : srcMultiMap.getAll(key)) {
								Set<Integer> resolvedIDs = getIDAsSet(originValueType, destValueType, sID);

								if (resolvedIDs == null) {
									continue;
								}
								for (Integer resolvedID : resolvedIDs) {
									codeResolvedMap.put(key, resolvedID);
								}
							}
						}
					}
				}
				else if (originKeyType.getColumnType() == EDataType.INT
						&& destValueType.getColumnType() == EDataType.STRING) {
					codeResolvedMap = new HashMap<Integer, String>();

					throw new RuntimeException("Not implemented!");
				}
				else if (originKeyType.getColumnType() == EDataType.STRING
						&& destValueType.getColumnType() == EDataType.STRING) {
					codeResolvedMap = new HashMap<String, String>();

					throw new RuntimeException("Not implemented!");
				}
				else if (originKeyType.getColumnType() == EDataType.STRING
						&& destValueType.getColumnType() == EDataType.INT) {

					if (!originalMappingType.isMultiMap()) {
						codeResolvedMap = new HashMap<String, Integer>();

						for (KeyType key : srcMap.keySet()) {
							Set<String> resolvedIDs = getIDAsSet(originValueType, destValueType, srcMap.get(key));
							if (resolvedIDs == null)
								continue;
							for (String resolvedID : resolvedIDs) {
								codeResolvedMap.put(key, resolvedID);
							}
						}
					}
					else {
						codeResolvedMap = new MultiHashMap<String, Integer>();
						MultiHashMap<String, String> srcMultiMap = (MultiHashMap<String, String>) srcMap;

						for (KeyType key : srcMap.keySet()) {
							for (String sID : srcMultiMap.getAll(key)) {
								Set<Integer> resolvedIDs = getIDAsSet(originValueType, destValueType, sID);

								if (resolvedIDs == null) {
									continue;
								}
								for (Integer resolvedID : resolvedIDs) {
									codeResolvedMap.put(key, resolvedID);
								}
							}
						}
					}
				}
			}
		}
		else {
			if (originValueType == destValueType) {
				if (destKeyType.getColumnType() == EDataType.INT && destValueType.getColumnType() == EDataType.INT) {
					codeResolvedMap = new HashMap<Integer, Integer>();

					MappingType conversionType = originalMappingType;// MappingType.valueOf(originKeyType
					// + "_2_" +
					// destKeyType);

					if (!originalMappingType.isMultiMap()) {
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
							iResolvedID = getID(conversionType.getFromIDType(), conversionType.getToIDType(), key);

							if (iResolvedID == null) {

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
				else if (destKeyType.getColumnType() == EDataType.INT
						&& destValueType.getColumnType() == EDataType.STRING) {
					codeResolvedMap = new HashMap<Integer, String>();

					MappingType conversionType = originalMappingType;

					for (KeyType key : srcMap.keySet()) {
						codeResolvedMap.put(getID(conversionType.getFromIDType(), conversionType.getToIDType(), key),
								srcMap.get(key));
					}
				}
				else if (destKeyType.getColumnType() == EDataType.STRING
						&& destValueType.getColumnType() == EDataType.STRING) {
					codeResolvedMap = new HashMap<String, String>();

					throw new RuntimeException("Not implemented!");
				}
				else if (destKeyType.getColumnType() == EDataType.STRING
						&& destValueType.getColumnType() == EDataType.INT) {
					codeResolvedMap = new HashMap<String, Integer>();

					throw new RuntimeException("Not implemented!");
				}
			}
		}

		// Add new code resolved map
		hashMappingType2Map.put(destMappingType, codeResolvedMap);
		mappingGraph.addEdge(destMappingType.getFromIDType(), destMappingType.getToIDType(), originalMappingType);
		if (destMappingType.isMultiMap()) {
			mappingGraph.setEdgeWeight(originalMappingType, Double.MAX_VALUE);
		}
		else {
			mappingGraph.setEdgeWeight(originalMappingType, 1);
		}
	}

	/**
	 * Gets the map of the specified mapping type for manipulation.
	 *
	 * @param <KeyType>
	 * @param <ValueType>
	 * @param type Mapping type that identifies the map.
	 * @return Map that corresponds to the specified mapping type. If no such
	 *         map exists, null is returned.
	 * @deprecated replace with search feature, maps shouldn't be exposed
	 *             externally
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public <KeyType, ValueType> Map<KeyType, ValueType> getMap(MappingType type) {
		return (Map<KeyType, ValueType>) hashMappingType2Map.get(type);
	}

	/**
	 * Adds a mapping from fromID to toID to the map identified through
	 * {@link MappingType}. If {@link MappingType#isHasReverseMap()} is true, a
	 * corresponding entry to the reverse map is automatically added.
	 *
	 * @param mappingType the mapping type identifying the map for the supplied
	 *            ids
	 * @param fromID the source ID
	 * @param toID the target ID
	 */
	public synchronized <KeyType, ValueType> void addMapping(MappingType mappingType, KeyType fromID, ValueType toID) {
		@SuppressWarnings("unchecked") Map<KeyType, ValueType> map = (Map<KeyType, ValueType>) hashMappingType2Map
				.get(mappingType);
		map.put(fromID, toID);

		if (mappingType.isHasReverseMap()) {
			MappingType reverseMappingType = MappingType
					.getType(mappingType.getToIDType(), mappingType.getFromIDType());
			@SuppressWarnings("unchecked") Map<ValueType, KeyType> reverseMap = (Map<ValueType, KeyType>) hashMappingType2Map
					.get(reverseMappingType);
			reverseMap.put(toID, fromID);
		}

		if (idCategory.isPrimaryMappingTypeDefault()) {
			// in this case we need to create a mapping for the primary type to
			// non-internal types
			IDType fromIDType = mappingType.getFromIDType();
			IDType toIDType = mappingType.getToIDType();

			Integer primaryTypeID = null;

			if (!fromIDType.isInternalType() && !fromIDType.equals(idCategory.getPrimaryMappingType())) {
				MappingType fromToPrimaryMapping = MappingType.getType(fromIDType, idCategory.getPrimaryMappingType());
				if (getID(fromIDType, idCategory.getPrimaryMappingType(), fromID) == null) {
					primaryTypeID = primaryTypeCounter++;
					addMapping(fromToPrimaryMapping, fromID, primaryTypeID);
				}
			}

			if (!toIDType.isInternalType() && !toIDType.equals(idCategory.getPrimaryMappingType())) {
				MappingType toToPrimaryMapping = MappingType.getType(toIDType, idCategory.getPrimaryMappingType());
				if (getID(toIDType, idCategory.getPrimaryMappingType(), toID) == null) {
					if (primaryTypeID == null)
						primaryTypeID = primaryTypeCounter++;
					addMapping(toToPrimaryMapping, toID, primaryTypeID);
				}
			}
		}
	}

	/**
	 * @return the primaryTypeCounter, see {@link #primaryTypeCounter}
	 */
	public int getPrimaryTypeCounter() {
		return primaryTypeCounter;
	}

	/**
	 * Convenience wrapper of {@link #addMapping(MappingType, Object, Object)}
	 * containing {@link IDType}s instead of a {@link MappingType}
	 *
	 * @param fromIDType
	 * @param fromID
	 * @param toIDType
	 * @param toID
	 */
	public <KeyType, ValueType> void addMapping(IDType fromIDType, KeyType fromID, IDType toIDType, ValueType toID) {
		addMapping(MappingType.getType(fromIDType, toIDType), fromID, toID);
	}

	/**
	 * Checks whether a mapping is possible from the specified source IDType to
	 * the destination IDType.
	 *
	 * @param source Source IDType of the mapping.
	 * @param destination Destination IDType of the mapping.
	 * @return true, if a mapping is possible, false otherwise.
	 */
	public synchronized final boolean hasMapping(IDType source, IDType destination) {

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
	 * <p>
	 * Tries to find the mapping from the source IDType to the destination
	 * IDType of the specified sourceID along a path of IDTypes where mappings
	 * exist. If no such path is found, null is returned. If the path includes
	 * multi-mappings, a Set of values is returned.
	 * </p>
	 * <p>
	 * Note that this method always tries to choose a path without
	 * multi-mappings if possible.
	 * </p>
	 *
	 *
	 * @param <K> Type of the sourceID
	 * @param <V> Type of the expected result of the mapping
	 * @param source IDType of the source data
	 * @param destination IDType of the destination data
	 * @param sourceID ID for which the mapping shall be found
	 * @return If no mapping is found, null, otherwise the corresponding ID, or
	 *         Set of IDs.
	 * @deprecated Use {@link #getIDAsSet(IDType, IDType, Object)} instead as it
	 *             is safer.
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public synchronized <K, V> V getID(IDType source, IDType destination, K sourceID) {

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
			Logger.log(new Status(Status.ERROR, this.toString(), "No mapping path found between " + source + " and "
					+ destination));
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
	 * Tries to find the mapping from the source IDType to the destination
	 * IDType of the specified sourceID along a path of IDTypes where mappings
	 * exist. If no such path is found, null is returned. The result will always
	 * be a Set of the found mappings.
	 *
	 * @param <K> Type of the sourceID
	 * @param <V> Type of the expected result of the mapping
	 * @param source IDType of the source data
	 * @param destination IDType of the destination data
	 * @param sourceID ID for which the mapping shall be found
	 * @return If no mapping is found, null, otherwise the Set containing the
	 *         corresponding ID(s).
	 */
	@SuppressWarnings("unchecked")
	public synchronized <K, V> Set<V> getIDAsSet(IDType source, IDType destination, K sourceID) {

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

	// public void printGraph() {
	// System.out.println(mappingGraph.toString());
	// }

	/**
	 * Determines whether the IDMappingManager holds a map that contains the
	 * specified element of the specified type.
	 *
	 * @param <T>
	 * @param idType IDType of the element.
	 * @param element Element to be found.
	 * @return True, if such an element is fund, false otherwise.
	 */
	public synchronized <T> boolean doesElementExist(IDType idType, T element) {
		if (!mappingGraph.containsVertex(idType))
			return false;
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
	 * Returns all id types registered in this ID Mapping Manager. Consider
	 * using {@link IDCategory#getIdTypes()} for a list of all registered
	 * {@link IDType}s of a category instead.
	 *
	 * @return
	 */
	public synchronized HashSet<IDType> getIDTypes() {
		HashSet<IDType> idTypes = new HashSet<IDType>();
		for (MappingType mappingType : hashMappingType2Map.keySet()) {
			idTypes.add(mappingType.getFromIDType());
			idTypes.add(mappingType.getToIDType());
		}
		return idTypes;
	}

	@Override
	public String toString() {
		return "IDMappingManager for " + idCategory + " with registered id types: " + hashMappingType2Map.keySet();
	}

	/**
	 * Clear all mapping types that have a from- or to-type that is internal.
	 */
	public synchronized void clearInternalMappingsAndIDTypes() {

		Object[] mappingTypes = hashMappingType2Map.keySet().toArray();
		for (int i = 0; i < mappingTypes.length; i++) {
			MappingType mappingType = (MappingType) mappingTypes[i];

			if (mappingType.getFromIDType().getTypeName().contains("org.caleydo.")
					|| mappingType.getToIDType().getTypeName().contains("org.caleydo.")) {

				hashMappingType2Map.get(mappingType).clear();
				hashMappingType2Map.remove(mappingType);
				mappingGraph.removeEdge(mappingType);

				if (mappingType.getFromIDType().getTypeName().contains("org.caleydo."))
					mappingGraph.removeVertex(mappingType.getFromIDType());

				if (mappingType.getToIDType().getTypeName().contains("org.caleydo."))
					mappingGraph.removeVertex(mappingType.getToIDType());
			}
		}
	}
}
