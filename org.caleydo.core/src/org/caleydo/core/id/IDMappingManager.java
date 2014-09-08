/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * <p>
 * Handles the mapping between IDs of different {@link IDType}s that share a common {@link IDCategory}.
 * </p>
 * <p>
 * Mappings between several different types of IDs make the relationships between multiple datasets possible. The
 * <code>IDMappingManager</code> provides utilities for such a mapping for identifiers of a common family. This family
 * relationship is expressed by the {@link IDCategory} of this manager. All {@link IDType}s that can be mapped to one an
 * other have to belong to the same <code>IDCategory</code>.
 * </p>
 * <p>
 * The mapping manager contains a list of maps which are defined through their {@link MappingType}, which basically
 * contains a two id types, the "fromIDType" which corresponds to the key of a map, and the "toIDType" which is the
 * value of the map.
 * </p>
 * <p>
 * Every <code>IDCategory</code> contains a <b>primary mapping type</b>. This primary mapping type can be <b>externally
 * provided</b>, in which case the manager assumes that all maps are somehow connected through this external type, or it
 * can be <b>automatically created</b>, in which case the mapping manager creates a mapping for every type where
 * {@link IDType#isInternalType()} is false. The primary mapping type must be of data type Integer, which guarantees
 * that for every IDCategory there is an Identifier that can be used with Classes such as {@link VirtualArray}s and
 * {@link SelectionManager}s.
 * </p>
 * <p>
 * Maps can either contain only unique mappings (for every key ID exactly one value ID), or accommodate multi-mappings
 * (for every key ID multiple value IDs.
 * </p>
 * <p>
 * Maps can also be specified to be backed by <b>reverse maps</b> where the key and value are exchanged. If a reverse
 * map is desired it is created and maintained automatically.
 * </p>
 *
 * <h2>Known Issues</h2>
 * <p>
 * Multi-mapping works in general, but it's not clear how it is preserved e.g. in reverse maps
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
	 * Registered {@link IDMappingDescription}s for mappings loaded by the user.
	 */
	private static List<IDMappingDescription> idMappingDescriptions = new ArrayList<>();

	/**
	 * The {@link IDCategory} for which this mapping manager provides the mapping
	 */
	private final IDCategory idCategory;

	/** A counter for dynamically created primary IDs */
	private int primaryTypeCounter = 0;

	/**
	 * HashMap that contains all mappings identified by their MappingType.
	 */
	private final Map<MappingType, Map<?, ?>> hashMappingType2Map;

	/**
	 * Graph of mappings. IDTypes are the vertices of the graph, edges represent a mapping from one IDType to another
	 * that is backed up by a corresponding map in hashType2Mapping.
	 */
	private final DefaultDirectedWeightedGraph<IDType, MappingType> mappingGraph;

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
	 * Adds a new map for the specified mapping type. To fill that map with elements, use the
	 * {@link #addMapping(MappingType, Object, Object)} method.
	 * </p>
	 * <p>
	 * </p>
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
	 *            If true, a multi-map will be created (i.e,. multiple values of toIDType can map to one key), else only
	 *            a single key-value relationship is allowed
	 * @param createReverseMap
	 *            Flag that determines whether the resolution of fromIDType to toIDType should also be created from
	 *            toIDType to fromIDType
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
		} else {
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
	 * @param sourceMappingType
	 *            Mapping type the reverse map shall be created for.
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
			} else {
				hashMappingType2Map.put(reverseType, new HashMap<DestType, SrcType>());

				reverseMap = (HashMap<DestType, SrcType>) hashMappingType2Map.get(reverseType);
			}

			for (SrcType key : sourceMap.keySet()) {
				for (DestType value : sourceMap.getAll(key)) {
					reverseMap.put(value, key);
				}
			}
		} else {
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
		} else {
			mappingGraph.setEdgeWeight(reverseType, 1);
		}
	}

	/**
	 * Method takes a map that contains identifier codes and creates a new resolved codes. Resolving means mapping from
	 * code to internal ID.
	 *
	 * TODO: several cases are not handled
	 *
	 * @param <K>
	 * @param <V>
	 * @param originalMappingType
	 *            Mapping type that specifies the already existent map which is used for creating the code resolved map.
	 */
	@SuppressWarnings("unchecked")
	public <K, V> void createCodeResolvedMap(MappingType originalMappingType, IDType codeResolvedFromType,
			IDType codeResolvedToType) {

		@SuppressWarnings("rawtypes")
		Map codeResolvedMap = null;

		IDType originKeyType = originalMappingType.getFromIDType();
		IDType originValueType = originalMappingType.getToIDType();
		IDType destKeyType = codeResolvedFromType;
		IDType destValueType = codeResolvedToType;

		MappingType destMappingType = MappingType.registerMappingType(codeResolvedFromType, codeResolvedToType, false,
				false); // MULTI??

		Map<K, V> srcMap = (Map<K, V>) hashMappingType2Map.get(originalMappingType);

		if (originKeyType == destKeyType) {
			if (originValueType != destValueType) {
				if (originKeyType.getDataType() == EDataType.INTEGER
						&& destValueType.getDataType() == EDataType.INTEGER) {
					codeResolvedMap = new HashMap<Integer, Integer>();

					if (!originalMappingType.isMultiMap()) {

						codeResolvedMap = new HashMap<Integer, Integer>();

						IIDTypeMapper<K, Integer> mapper = getIDTypeMapper(originValueType, destValueType);
						for (K key : srcMap.keySet()) {
							Set<Integer> resolvedIDs = mapper.apply(key);

							if (resolvedIDs == null) {
								continue;
							}
							for (Integer resolvedID : resolvedIDs) {
								codeResolvedMap.put(key, resolvedID);
							}
						}
					} else {
						codeResolvedMap = new MultiHashMap<Integer, Integer>();
						MultiHashMap<Integer, String> srcMultiMap = (MultiHashMap<Integer, String>) srcMap;
						IIDTypeMapper<String, Integer> mapper = getIDTypeMapper(originValueType, destValueType);
						for (K key : srcMap.keySet()) {
							for (String sID : srcMultiMap.getAll(key)) {
								Set<Integer> resolvedIDs = mapper.apply(sID);

								if (resolvedIDs == null) {
									continue;
								}
								for (Integer resolvedID : resolvedIDs) {
									codeResolvedMap.put(key, resolvedID);
								}
							}
						}
					}
				} else if (originKeyType.getDataType() == EDataType.INTEGER
						&& destValueType.getDataType() == EDataType.STRING) {
					codeResolvedMap = new HashMap<Integer, String>();

					throw new RuntimeException("Not implemented!");
				} else if (originKeyType.getDataType() == EDataType.STRING
						&& destValueType.getDataType() == EDataType.STRING) {
					codeResolvedMap = new HashMap<String, String>();

					throw new RuntimeException("Not implemented!");
				} else if (originKeyType.getDataType() == EDataType.STRING
						&& destValueType.getDataType() == EDataType.INTEGER) {

					if (!originalMappingType.isMultiMap()) {
						codeResolvedMap = new HashMap<String, Integer>();

						IIDTypeMapper<V, String> mapper = getIDTypeMapper(originValueType, destValueType);
						for (K key : srcMap.keySet()) {
							Set<String> resolvedIDs = mapper.apply(srcMap.get(key));
							if (resolvedIDs == null)
								continue;
							for (String resolvedID : resolvedIDs) {
								codeResolvedMap.put(key, resolvedID);
							}
						}
					} else {
						codeResolvedMap = new MultiHashMap<String, Integer>();
						MultiHashMap<String, String> srcMultiMap = (MultiHashMap<String, String>) srcMap;

						IIDTypeMapper<String, Integer> mapper = getIDTypeMapper(originValueType, destValueType);
						for (K key : srcMap.keySet()) {
							for (String sID : srcMultiMap.getAll(key)) {
								Set<Integer> resolvedIDs = mapper.apply(sID);

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
		} else {
			if (originValueType == destValueType) {
				if (destKeyType.getDataType() == EDataType.INTEGER && destValueType.getDataType() == EDataType.INTEGER) {
					codeResolvedMap = new HashMap<Integer, Integer>();

					MappingType conversionType = originalMappingType;// MappingType.valueOf(originKeyType
					// + "_2_" +
					// destKeyType);

					if (!originalMappingType.isMultiMap()) {
						codeResolvedMap = new HashMap<Integer, Integer>();

						for (K key : srcMap.keySet()) {
							codeResolvedMap.put(
									getID(conversionType.getFromIDType(), conversionType.getToIDType(), key),
									srcMap.get(key));
						}
					} else {
						codeResolvedMap = new MultiHashMap<Integer, Integer>();
						MultiHashMap<String, Integer> srcMultiMap = (MultiHashMap<String, Integer>) srcMap;
						Integer iResolvedID = 0;

						for (K key : srcMap.keySet()) {
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
				} else if (destKeyType.getDataType() == EDataType.INTEGER
						&& destValueType.getDataType() == EDataType.STRING) {
					codeResolvedMap = new HashMap<Integer, String>();

					MappingType conversionType = originalMappingType;

					for (K key : srcMap.keySet()) {
						codeResolvedMap.put(getID(conversionType.getFromIDType(), conversionType.getToIDType(), key),
								srcMap.get(key));
					}
				} else if (destKeyType.getDataType() == EDataType.STRING
						&& destValueType.getDataType() == EDataType.STRING) {
					codeResolvedMap = new HashMap<String, String>();

					throw new RuntimeException("Not implemented!");
				} else if (destKeyType.getDataType() == EDataType.STRING
						&& destValueType.getDataType() == EDataType.INTEGER) {
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
		} else {
			mappingGraph.setEdgeWeight(originalMappingType, 1);
		}
	}

	/**
	 * Adds a mapping from fromID to toID to the map identified through {@link MappingType}. If
	 * {@link MappingType#isHasReverseMap()} is true, a corresponding entry to the reverse map is automatically added.
	 *
	 * @param mappingType
	 *            the mapping type identifying the map for the supplied ids
	 * @param fromID
	 *            the source ID
	 * @param toID
	 *            the target ID
	 */
	public synchronized <KeyType, ValueType> void addMapping(MappingType mappingType, KeyType fromID, ValueType toID) {
		@SuppressWarnings("unchecked")
		Map<KeyType, ValueType> map = (Map<KeyType, ValueType>) hashMappingType2Map.get(mappingType);
		map.put(fromID, toID);

		if (mappingType.isHasReverseMap()) {
			MappingType reverseMappingType = MappingType
					.getType(mappingType.getToIDType(), mappingType.getFromIDType());
			@SuppressWarnings("unchecked")
			Map<ValueType, KeyType> reverseMap = (Map<ValueType, KeyType>) hashMappingType2Map.get(reverseMappingType);
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
	 * Convenience wrapper of {@link #addMapping(MappingType, Object, Object)} containing {@link IDType}s instead of a
	 * {@link MappingType}
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
	 * Checks whether a mapping is possible from the specified source IDType to the destination IDType.
	 *
	 * @param source
	 *            Source IDType of the mapping.
	 * @param destination
	 *            Destination IDType of the mapping.
	 * @return true, if a mapping is possible, false otherwise.
	 */
	public synchronized final boolean hasMapping(IDType source, IDType destination) {

		if (source.equals(destination))
			return true;
		try {
			return (DijkstraShortestPath.findPathBetween(mappingGraph, source, destination) != null);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * Convenience method for getting human readable ids.
	 *
	 * @param source
	 * @param sourceID
	 * @return
	 */
	public synchronized <K> Set<String> getHumanReadableIDs(IDType source, K sourceID) {
		return getIDAsSet(source, source.getIDCategory().getHumanReadableIDType(), sourceID);
	}

	/**
	 * Convenience method for getting the id type mapper for human readable ids.
	 *
	 * @param source
	 * @return
	 */
	public synchronized <K> IIDTypeMapper<K, String> getHumanReadableIDTypeMapper(IDType source) {
		return getIDTypeMapper(source, source.getIDCategory().getHumanReadableIDType());
	}

	/**
	 * <p>
	 * Tries to find the mapping from the source IDType to the destination IDType of the specified sourceID along a path
	 * of IDTypes where mappings exist. If no such path is found, null is returned. If the path includes multi-mappings,
	 * a Set of values is returned.
	 * </p>
	 * <p>
	 * Note that this method always tries to choose a path without multi-mappings if possible.
	 * </p>
	 *
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
	 */

	@SuppressWarnings("unchecked")
	public synchronized <K, V> V getID(IDType source, IDType destination, K sourceID) {

		if (source.equals(destination))
			return (V) sourceID;

		List<MappingType> path;

		try {
			path = DijkstraShortestPath.findPathBetween(mappingGraph, source, destination);
		} catch (IllegalArgumentException e) {
			Logger.log(new Status(IStatus.ERROR, this.toString(), "One of the data types " + source + " and "
					+ destination + " is not registered with this IDMappingManager (" + toString() + ")."));

			// data type is not in the mapping
			return null;
		}

		if (path == null) {
			Logger.log(new Status(IStatus.ERROR, this.toString(), "No mapping path found between " + source + " and "
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
					} else {
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
			} else {
				if (edge.isMultiMap()) {
					keys = (Set<Object>) ((MultiHashMap<?, ?>) (currentMap)).getAll(currentID);
					if ((keys == null) || (keys.isEmpty()))
						return null;
				} else {
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
	 * Tries to find the mapping from the source IDType to the destination IDType of the specified sourceID along a path
	 * of IDTypes where mappings exist. If no such path is found, null is returned. The result will always be a Set of
	 * the found mappings.
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
	public synchronized <K, V> Set<V> getIDAsSet(IDType source, IDType destination, K sourceID) {
		Function<K, Set<V>> fun = getIDTypeMapper(source, destination);
		if (fun == null)
			return null;
		return fun.apply(sourceID);
	}

	/**
	 * see {@link #getIDAsSet(IDType, IDType, Object)} but returns a function that can be used to map multiple ids at
	 * once
	 *
	 * @param source
	 * @param destination
	 * @return
	 */
	public synchronized <K, V> IIDTypeMapper<K, V> getIDTypeMapper(IDType source, IDType destination) {
		if (source.equals(destination)) { // both are the same so an identity mapping
			// but for getting all the possible values, lets use the primary value
			return new IdentityIDTypeMapper<K, V>(source);
		}

		// first resolve path
		List<MappingType> path = findPath(source, destination);
		if (path == null)
			return null;

		return new IDTypeMapper<>(path, source, destination);
	}

	protected List<MappingType> findPath(IDType source, IDType destination) {
		MappingType r = mappingGraph.getEdge(source, destination);
		if (r != null) { // direct edge
			return Collections.singletonList(r);
		}
		try {
			return DijkstraShortestPath.findPathBetween(mappingGraph, source, destination);
		} catch (IllegalArgumentException e) {
			Logger.log(new Status(IStatus.INFO, toString(), "No mapping found between " + source + " and "
					+ destination));
			return null;
		}
	}

	public class IDTypeMapper<K, V> implements IIDTypeMapper<K, V> {
		private final List<MappingType> path;
		private final IDType source;
		private final IDType target;

		private IDTypeMapper(List<MappingType> path, IDType source, IDType target) {
			this.path = path;
			this.source = source;
			this.target = target;
		}

		/**
		 * @return the source, see {@link #source}
		 */
		@Override
		public IDType getSource() {
			return source;
		}

		/**
		 * @return the target, see {@link #target}
		 */
		@Override
		public IDType getTarget() {
			return target;
		}

		@Override
		public boolean isOne2OneMapping() {
			for (MappingType p : path)
				if (p.isMultiMap())
					return false;
			return true;
		}

		@Override
		public Set<V> apply(K sourceID) {
			// resolve ids
			Object currentID = sourceID;

			Set<Object> keys = null;
			Collection<Object> values = new ArrayList<Object>();

			for (MappingType edge : path) {
				Map<?, ?> currentMap = hashMappingType2Map.get(edge);

				if (keys == null) { // first edge or a single id
					if (edge.isMultiMap()) {
						@SuppressWarnings("unchecked")
						Set<Object> tmp = (Set<Object>) ((MultiHashMap<?, ?>) (currentMap)).getAll(currentID);
						keys = tmp;
						if ((keys == null) || (keys.isEmpty()))
							return null;
					} else {
						currentID = currentMap.get(currentID);
						if (currentID == null)
							return null;
					}
				} else {
					for (Object key : keys) {
						if (edge.isMultiMap()) {
							@SuppressWarnings("unchecked")
							Set<Object> temp = (Set<Object>) ((MultiHashMap<?, ?>) (currentMap)).getAll(key);
							if (temp != null)
								values.addAll(temp);
						} else {
							Object value = currentMap.get(key);
							if (value != null)
								values.add(value);
						}
					}
					if (values.isEmpty())
						return null;

					keys = new HashSet<Object>(values);
					values.clear();
				}
			}
			if (keys != null) // multiple keys found
				return (Set<V>) keys;

			return Collections.singleton((V) currentID);
		}

		@Override
		public Set<V> apply(Iterable<K> sourceIds) {
			Set<Object> ping = Sets.<Object> newHashSet(sourceIds); // current
			Set<Object> pong = new HashSet<>(ping.size()); // next
			Set<Object> tmp;

			for (MappingType edge : path) {
				Map<?, ?> currentMap = hashMappingType2Map.get(edge);
				for (Object key : ping) {
					if (edge.isMultiMap()) {
						tmp = (Set<Object>) ((MultiHashMap<?, ?>) (currentMap)).getAll(key);
						if (tmp != null)
							pong.addAll(tmp);
					} else {
						Object value = currentMap.get(key);
						if (value != null)
							pong.add(value);
					}
				}
				if (pong.isEmpty()) // no next at all found, abort
					return Collections.emptySet();

				// swap current and next
				tmp = ping;
				ping = pong;
				pong = tmp;
				// prepare for next round
				pong.clear();
			}
			return (Set<V>) ping;
		}

		@Override
		public Collection<Set<V>> applySeq(Collection<K> sourceIDs) {
			List<Set<V>> r = new ArrayList<>(sourceIDs.size());
			for (K sourceID : sourceIDs) {
				Set<V> ri = apply(sourceID);
				if (ri == null)
					ri = Collections.emptySet();
				r.add(ri);
			}
			return ImmutableList.copyOf(r);
		}

		@Override
		public boolean isMapAble(K sourceId) {
			return !apply(Collections.singleton(sourceId)).isEmpty();
		}

		@Override
		public int hashCode() {
			return Objects.hash(getOuterType(), source, target);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("rawtypes")
			IDTypeMapper other = (IDTypeMapper) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			return Objects.equals(source, other.source) && Objects.equals(target, other.target);
		}

		private IDMappingManager getOuterType() {
			return IDMappingManager.this;
		}

		@Override
		public List<MappingType> getPath() {
			return path;
		}
	}

	// public void printGraph() {
	// System.out.println(mappingGraph.toString());
	// }

	/**
	 * Determines whether the IDMappingManager holds a map that contains the specified element of the specified type.
	 *
	 * @param <T>
	 * @param idType
	 *            IDType of the element.
	 * @param element
	 *            Element to be found.
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
	 * returns for the given {@link IDType} all ids that are the starting point of any mapping, i.e. the ids of the
	 * outgoing edges of the mapping graph
	 *
	 * @param idType
	 * @return an unmodifiable set of ids
	 */
	public Set<?> getAllMappedIDs(IDType idType) {
		if (!mappingGraph.containsVertex(idType))
			return Collections.emptySet();
		Set<MappingType> edges = mappingGraph.outgoingEdgesOf(idType);

		Set<Object> ids = new HashSet<>();
		for (MappingType edge : edges) {
			Map<?, ?> currentMap = hashMappingType2Map.get(edge);
			if (currentMap != null) {
				ids.addAll(currentMap.keySet());
			}
		}
		return Collections.unmodifiableSet(ids);
	}

	@Override
	public String toString() {
		// StringBuilder b = new StringBuilder();
		// for (IDType v : mappingGraph.vertexSet()) {
		// b.append(v).append(": \n");
		// for (MappingType e : mappingGraph.edgesOf(v)) {
		// b.append(e).append("\n");
		// }
		// }
		// return b.toString();
		return "IDMappingManager for " + idCategory;// + " with registered id types: " +
		// hashMappingType2Map.keySet();
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

	public static void addIDMappingDescription(IDMappingDescription desc) {
		idMappingDescriptions.add(desc);
	}

	/**
	 * @return the idMappingDescriptions, see {@link #idMappingDescriptions}
	 */
	public static List<IDMappingDescription> getIdMappingDescriptions() {
		return idMappingDescriptions;
	}
}
