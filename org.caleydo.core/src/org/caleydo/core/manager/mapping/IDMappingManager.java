package org.caleydo.core.manager.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.collection.MultiHashMap;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

/**
 * Provides methods for mapping different IDTypes using a graph of Maps.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Christian Partl
 */
public class IDMappingManager
	implements IIDMappingManager {
	/**
	 * HashMap that contains all mappings identified by their MappingType.
	 */
	protected HashMap<EMappingType, Map<?, ?>> hashType2Mapping;
	/**
	 * Graph of mappings. IDTypes are the vertices of the graph, edges represent a mapping from one IDType to
	 * another that is backed up by a corresponding map in hashType2Mapping.
	 */
	private DefaultDirectedWeightedGraph<EIDType, MappingEdge> mappingGraph;

	private IGeneralManager generalManager = GeneralManager.get();

	/**
	 * Constructor.
	 */
	public IDMappingManager() {
		hashType2Mapping = new HashMap<EMappingType, Map<?, ?>>();
		mappingGraph = new DefaultDirectedWeightedGraph<EIDType, MappingEdge>(MappingEdge.class);
	}

	@Override
	public <K, V> void createMap(EMappingType mappingType) {

		EIDType fromIDType = mappingType.getTypeOrigin();
		EIDType toIDType = mappingType.getTypeTarget();

		if (!mappingGraph.containsVertex(fromIDType))
			mappingGraph.addVertex(fromIDType);
		if (!mappingGraph.containsVertex(toIDType))
			mappingGraph.addVertex(toIDType);

		MappingEdge edge = new MappingEdge(mappingType);
		mappingGraph.addEdge(fromIDType, toIDType, edge);
		if (mappingType.isMultiMap()) {
			hashType2Mapping.put(mappingType, new MultiHashMap<K, V>());
			mappingGraph.setEdgeWeight(edge, Double.MAX_VALUE);
		}
		else {
			hashType2Mapping.put(mappingType, new HashMap<K, V>());
			mappingGraph.setEdgeWeight(edge, 1);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <SrcType, DestType> void createReverseMap(EMappingType srcType, EMappingType reverseType) {
		Map<DestType, SrcType> reverseMap;

		if (srcType.isMultiMap()) {
			MultiHashMap<SrcType, DestType> sourceMap =
				(MultiHashMap<SrcType, DestType>) hashType2Mapping.get(srcType);

			if (reverseType.isMultiMap()) {
				hashType2Mapping.put(reverseType, new MultiHashMap<DestType, SrcType>());

				reverseMap = (MultiHashMap<DestType, SrcType>) hashType2Mapping.get(reverseType);
			}
			else {
				hashType2Mapping.put(reverseType, new HashMap<DestType, SrcType>());

				reverseMap = (HashMap<DestType, SrcType>) hashType2Mapping.get(reverseType);
			}

			for (SrcType key : sourceMap.keySet()) {
				for (DestType value : sourceMap.getAll(key)) {
					reverseMap.put(value, key);
				}
			}
		}
		else {
			hashType2Mapping.put(reverseType, new HashMap<DestType, SrcType>());

			reverseMap = (HashMap<DestType, SrcType>) hashType2Mapping.get(reverseType);
			Map<SrcType, DestType> sourceMap = (HashMap<SrcType, DestType>) hashType2Mapping.get(srcType);

			for (SrcType key : sourceMap.keySet()) {
				reverseMap.put(sourceMap.get(key), key);
			}
		}

		MappingEdge edge = new MappingEdge(reverseType);
		mappingGraph.addEdge(reverseType.getTypeOrigin(), reverseType.getTypeTarget(), edge);
		if (reverseType.isMultiMap()) {
			mappingGraph.setEdgeWeight(edge, Double.MAX_VALUE);
		}
		else {
			mappingGraph.setEdgeWeight(edge, 1);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <KeyType, ValueType> void createCodeResolvedMap(EMappingType mappingType,
		EMappingType destMappingType) {
		Map codeResolvedMap = null;
		// int iMappingErrors = 0;

		EIDType originKeyType = mappingType.getTypeOrigin();
		EIDType originValueType = mappingType.getTypeTarget();
		EIDType destKeyType = destMappingType.getTypeOrigin();
		EIDType destValueType = destMappingType.getTypeTarget();

		Map<KeyType, ValueType> srcMap = (Map<KeyType, ValueType>) hashType2Mapping.get(mappingType);

		if (mappingType != EMappingType.REFSEQ_MRNA_2_DAVID) {
			// Remove old unresolved map
			hashType2Mapping.remove(mappingType);
			mappingGraph.removeEdge(mappingType.getTypeOrigin(), mappingType.getTypeTarget());
		}

		if (originKeyType == destKeyType) {
			if (originValueType != destValueType) {
				if (originKeyType.getStorageType() == EStorageType.INT
					&& destValueType.getStorageType() == EStorageType.INT) {
					codeResolvedMap = new HashMap<Integer, Integer>();
					EMappingType conversionType =
						EMappingType.valueOf(originValueType + "_2_" + destValueType);

					if (!mappingType.isMultiMap()) {
						codeResolvedMap = new HashMap<Integer, Integer>();

						for (KeyType key : srcMap.keySet()) {
							codeResolvedMap.put(key, generalManager.getIDMappingManager().getID(
								conversionType.getTypeOrigin(), conversionType.getTypeTarget(),
								srcMap.get(key)));
						}
					}
					else {
						codeResolvedMap = new MultiHashMap<Integer, Integer>();
						MultiHashMap<Integer, String> srcMultiMap = (MultiHashMap<Integer, String>) srcMap;
						Integer iID = 0;

						for (KeyType key : srcMap.keySet()) {
							for (String sID : srcMultiMap.getAll(key)) {
								iID =
									generalManager.getIDMappingManager().getID(
										conversionType.getTypeOrigin(), conversionType.getTypeTarget(), sID);

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
				else if (originKeyType.getStorageType() == EStorageType.INT
					&& destValueType.getStorageType() == EStorageType.STRING) {
					codeResolvedMap = new HashMap<Integer, String>();

					throw new RuntimeException("Not implemented!");
				}
				else if (originKeyType.getStorageType() == EStorageType.STRING
					&& destValueType.getStorageType() == EStorageType.STRING) {
					codeResolvedMap = new HashMap<String, String>();

					throw new RuntimeException("Not implemented!");
				}
				else if (originKeyType.getStorageType() == EStorageType.STRING
					&& destValueType.getStorageType() == EStorageType.INT) {
					EMappingType conversionType =
						EMappingType.valueOf(originValueType + "_2_" + destValueType);

					if (!mappingType.isMultiMap()) {
						codeResolvedMap = new HashMap<String, Integer>();

						for (KeyType key : srcMap.keySet()) {
							codeResolvedMap.put(key, generalManager.getIDMappingManager().getID(
								conversionType.getTypeOrigin(), conversionType.getTypeTarget(),
								srcMap.get(key)));
						}
					}
					else {
						codeResolvedMap = new MultiHashMap<String, Integer>();
						MultiHashMap<String, String> srcMultiMap = (MultiHashMap<String, String>) srcMap;
						Integer iID = 0;

						for (KeyType key : srcMap.keySet()) {
							for (String sID : srcMultiMap.getAll(key)) {
								iID =
									generalManager.getIDMappingManager().getID(
										conversionType.getTypeOrigin(), conversionType.getTypeTarget(), sID);

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
				if (destKeyType.getStorageType() == EStorageType.INT
					&& destValueType.getStorageType() == EStorageType.INT) {
					codeResolvedMap = new HashMap<Integer, Integer>();

					EMappingType conversionType = EMappingType.valueOf(originKeyType + "_2_" + destKeyType);

					if (!mappingType.isMultiMap()) {
						codeResolvedMap = new HashMap<Integer, Integer>();

						for (KeyType key : srcMap.keySet()) {
							codeResolvedMap.put(generalManager.getIDMappingManager().getID(
								conversionType.getTypeOrigin(), conversionType.getTypeTarget(), key), srcMap
								.get(key));
						}
					}
					else {
						codeResolvedMap = new MultiHashMap<Integer, Integer>();
						MultiHashMap<String, Integer> srcMultiMap = (MultiHashMap<String, Integer>) srcMap;
						Integer iResolvedID = 0;

						for (KeyType key : srcMap.keySet()) {
							iResolvedID =
								generalManager.getIDMappingManager().getID(conversionType.getTypeOrigin(),
									conversionType.getTypeTarget(), key);

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
				else if (destKeyType.getStorageType() == EStorageType.INT
					&& destValueType.getStorageType() == EStorageType.STRING) {
					codeResolvedMap = new HashMap<Integer, String>();

					EMappingType conversionType = EMappingType.valueOf(originKeyType + "_2_" + destKeyType);

					for (KeyType key : srcMap.keySet()) {
						codeResolvedMap.put(generalManager.getIDMappingManager().getID(
							conversionType.getTypeOrigin(), conversionType.getTypeTarget(), key), srcMap
							.get(key));
					}
				}
				else if (destKeyType.getStorageType() == EStorageType.STRING
					&& destValueType.getStorageType() == EStorageType.STRING) {
					codeResolvedMap = new HashMap<String, String>();

					throw new RuntimeException("Not implemented!");
				}
				else if (destKeyType.getStorageType() == EStorageType.STRING
					&& destValueType.getStorageType() == EStorageType.INT) {
					codeResolvedMap = new HashMap<String, Integer>();

					throw new RuntimeException("Not implemented!");
				}
			}
		}

		// Add new code resolved map
		hashType2Mapping.put(destMappingType, codeResolvedMap);

		MappingEdge edge = new MappingEdge(destMappingType);
		mappingGraph.addEdge(destMappingType.getTypeOrigin(), destMappingType.getTypeTarget(), edge);
		if (destMappingType.isMultiMap()) {
			mappingGraph.setEdgeWeight(edge, Double.MAX_VALUE);
		}
		else {
			mappingGraph.setEdgeWeight(edge, 1);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <KeyType, ValueType> Map<KeyType, ValueType> getMap(EMappingType type) {
		return (Map<KeyType, ValueType>) hashType2Mapping.get(type);
	}

	@Override
	public final boolean hasMapping(EIDType source, EIDType destination) {

		if (source.equals(destination))
			return true;
		return (DijkstraShortestPath.findPathBetween(mappingGraph, source, destination) != null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <K, V> V getID(EIDType source, EIDType destination, K sourceID) {

		if (source.equals(destination))
			return (V) sourceID;

		List<MappingEdge> path;

		try {
			path = DijkstraShortestPath.findPathBetween(mappingGraph, source, destination);
		}
		catch (IllegalArgumentException e) {
			// data type is not in the mapping
			return null;
		}

		Object currentID = sourceID;

		if (path == null)
			return null;

		Set<Object> keys = null;
		Collection<Object> values = new ArrayList<Object>();

		for (MappingEdge edge : path) {
			Map<?, ?> currentMap = hashType2Mapping.get(edge.getMappingType());

			if (keys != null) {
				for (Object key : keys) {
					if (edge.getMappingType().isMultiMap()) {
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
				if (edge.getMappingType().isMultiMap()) {
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

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> Set<V> getIDAsSet(EIDType source, EIDType destination, K sourceID) {

		Set<V> setResult = new HashSet<V>();

		if (source.equals(destination)) {
			setResult.add((V) sourceID);
			return setResult;
		}
		List<MappingEdge> path;
		try {
			path = DijkstraShortestPath.findPathBetween(mappingGraph, source, destination);
		}
		catch (IllegalArgumentException e) {
			// data type is not in the mapping
			return null;
		}
		Object currentID = sourceID;

		if (path == null)
			return null;

		Set<Object> keys = null;
		Collection<Object> values = new ArrayList<Object>();

		for (MappingEdge edge : path) {
			Map<?, ?> currentMap = hashType2Mapping.get(edge.getMappingType());

			if (keys != null) {
				for (Object key : keys) {
					if (edge.getMappingType().isMultiMap()) {
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
				if (edge.getMappingType().isMultiMap()) {
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

	@Override
	public List<EIDType> getIDTypes(EIDCategory category) {
		ArrayList<EIDType> idTypes = new ArrayList<EIDType>();

		for (EIDType idType : mappingGraph.vertexSet()) {
			if (idType.getCategory().equals(category))
				idTypes.add(idType);
		}
		return idTypes;
	}

	// public void printGraph() {
	// System.out.println(mappingGraph.toString());
	// }

	@Override
	public <T> boolean doesElementExist(EIDType idType, T element) {
		Set<MappingEdge> edges = mappingGraph.edgesOf(idType);

		for (MappingEdge edge : edges) {
			Map<?, ?> currentMap = hashType2Mapping.get(edge.getMappingType());
			if (currentMap != null) {
				if (currentMap.containsKey(element))
					return true;
			}
		}
		return false;
	}
}
