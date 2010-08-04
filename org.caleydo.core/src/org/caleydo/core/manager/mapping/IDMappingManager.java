package org.caleydo.core.manager.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.mapping.IDType;
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
	protected HashMap<MappingType, Map<?, ?>> hashMappingType2Map;

	/**
	 * Graph of mappings. IDTypes are the vertices of the graph, edges represent a mapping from one IDType to
	 * another that is backed up by a corresponding map in hashType2Mapping.
	 */
	private DefaultDirectedWeightedGraph<IDType, MappingType> mappingGraph;

	private IGeneralManager generalManager = GeneralManager.get();

	/**
	 * Constructor.
	 */
	public IDMappingManager() {
		hashMappingType2Map = new HashMap<MappingType, Map<?, ?>>();
		mappingGraph = new DefaultDirectedWeightedGraph<IDType, MappingType>(MappingType.class);
	}

	@Override
	public <K, V> void createMap(IDType fromIDType, IDType toIDType, boolean isMultiMap) {

		if (!mappingGraph.containsVertex(fromIDType))
			mappingGraph.addVertex(fromIDType);
		if (!mappingGraph.containsVertex(toIDType))
			mappingGraph.addVertex(toIDType);

		MappingType mappingType = new MappingType(fromIDType, toIDType, isMultiMap);
		mappingGraph.addEdge(fromIDType, toIDType, mappingType);
		if (mappingType.isMultiMap()) {
			// hashType2Mapping.put(mappingType, new MultiHashMap<K, V>());
			mappingGraph.setEdgeWeight(mappingType, Double.MAX_VALUE);
		}
		else {
			hashMappingType2Map.put(mappingType, new HashMap<K, V>());
			mappingGraph.setEdgeWeight(mappingType, 1);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <SrcType, DestType> void createReverseMap(MappingType srcType, MappingType reverseType) {
		Map<DestType, SrcType> reverseMap;

		if (srcType.isMultiMap()) {
			MultiHashMap<SrcType, DestType> sourceMap =
				(MultiHashMap<SrcType, DestType>) hashMappingType2Map.get(srcType);

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
			Map<SrcType, DestType> sourceMap = (HashMap<SrcType, DestType>) hashMappingType2Map.get(srcType);

			for (SrcType key : sourceMap.keySet()) {
				reverseMap.put(sourceMap.get(key), key);
			}
		}

		MappingType edge =
			new MappingType(reverseType.getFromIDType(), reverseType.getToIDType(), reverseType.isMultiMap());
		mappingGraph.addEdge(reverseType.getFromIDType(), reverseType.getToIDType(), edge);
		if (reverseType.isMultiMap()) {
			mappingGraph.setEdgeWeight(edge, Double.MAX_VALUE);
		}
		else {
			mappingGraph.setEdgeWeight(edge, 1);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <KeyType, ValueType> void createCodeResolvedMap(MappingType mappingType,
		MappingType destMappingType) {
		Map codeResolvedMap = null;
		// int iMappingErrors = 0;

		IDType originKeyType = mappingType.getFromIDType();
		IDType originValueType = mappingType.getToIDType();
		IDType destKeyType = destMappingType.getFromIDType();
		IDType destValueType = destMappingType.getToIDType();

		Map<KeyType, ValueType> srcMap = (Map<KeyType, ValueType>) hashMappingType2Map.get(mappingType);

		// FIXME: IS THIS NEEDED ANYMORE?
		// if (mappingType != MappingType.REFSEQ_MRNA_2_DAVID) {
		// // Remove old unresolved map
		// hashMappingType2Map.remove(mappingType);
		// mappingGraph.removeEdge(mappingType.getFromIDType(), mappingType.getToIDType());
		// }

		if (originKeyType == destKeyType) {
			if (originValueType != destValueType) {
				if (originKeyType.getStorageType() == EStorageType.INT
					&& destValueType.getStorageType() == EStorageType.INT) {
					codeResolvedMap = new HashMap<Integer, Integer>();
					MappingType conversionType = mappingType;//MappingType.valueOf(originValueType + "_2_" + destValueType);

					if (!mappingType.isMultiMap()) {
						codeResolvedMap = new HashMap<Integer, Integer>();

						for (KeyType key : srcMap.keySet()) {
							codeResolvedMap.put(
								key,
								generalManager.getIDMappingManager().getID(conversionType.getFromIDType(),
									conversionType.getToIDType(), srcMap.get(key)));
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
										conversionType.getFromIDType(), conversionType.getToIDType(), sID);

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
					MappingType conversionType = mappingType;//MappingType.valueOf(originValueType + "_2_" + destValueType);

					if (!mappingType.isMultiMap()) {
						codeResolvedMap = new HashMap<String, Integer>();

						for (KeyType key : srcMap.keySet()) {
							codeResolvedMap.put(
								key,
								generalManager.getIDMappingManager().getID(conversionType.getFromIDType(),
									conversionType.getToIDType(), srcMap.get(key)));
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
										conversionType.getFromIDType(), conversionType.getToIDType(), sID);

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

					MappingType conversionType = mappingType;//MappingType.valueOf(originKeyType + "_2_" + destKeyType);

					if (!mappingType.isMultiMap()) {
						codeResolvedMap = new HashMap<Integer, Integer>();

						for (KeyType key : srcMap.keySet()) {
							codeResolvedMap.put(
								generalManager.getIDMappingManager().getID(conversionType.getFromIDType(),
									conversionType.getToIDType(), key), srcMap.get(key));
						}
					}
					else {
						codeResolvedMap = new MultiHashMap<Integer, Integer>();
						MultiHashMap<String, Integer> srcMultiMap = (MultiHashMap<String, Integer>) srcMap;
						Integer iResolvedID = 0;

						for (KeyType key : srcMap.keySet()) {
							iResolvedID =
								generalManager.getIDMappingManager().getID(conversionType.getFromIDType(),
									conversionType.getToIDType(), key);

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

					MappingType conversionType = mappingType;//MappingType.valueOf(originKeyType + "_2_" + destKeyType);

					for (KeyType key : srcMap.keySet()) {
						codeResolvedMap.put(
							generalManager.getIDMappingManager().getID(conversionType.getFromIDType(),
								conversionType.getToIDType(), key), srcMap.get(key));
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
		hashMappingType2Map.put(destMappingType, codeResolvedMap);

		mappingGraph.addEdge(destMappingType.getFromIDType(), destMappingType.getToIDType(), mappingType);
		if (destMappingType.isMultiMap()) {
			mappingGraph.setEdgeWeight(mappingType, Double.MAX_VALUE);
		}
		else {
			mappingGraph.setEdgeWeight(mappingType, 1);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <KeyType, ValueType> Map<KeyType, ValueType> getMap(MappingType type) {
		return (Map<KeyType, ValueType>) hashMappingType2Map.get(type);
	}

	@Override
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

	@Override
	@SuppressWarnings("unchecked")
	public <K, V> V getID(IDType source, IDType destination, K sourceID) {

		if (source.equals(destination))
			return (V) sourceID;

		List<MappingType> path;

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

	@SuppressWarnings("unchecked")
	@Override
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
			// data type is not in the mapping
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

	@Override
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
}
