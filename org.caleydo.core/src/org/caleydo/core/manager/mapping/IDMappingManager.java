package org.caleydo.core.manager.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingDataType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.collection.MultiHashMap;
import org.eclipse.core.runtime.Status;

/**
 * Manages mapping tables.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class IDMappingManager
	implements IIDMappingManager {
	protected HashMap<EMappingType, Map<?, ?>> hashType2Mapping;

	private IGeneralManager generalManager = GeneralManager.get();

	/**
	 * Constructor.
	 */
	public IDMappingManager() {
		hashType2Mapping = new HashMap<EMappingType, Map<?, ?>>();
	}

	@Override
	public void createMap(EMappingType type, EMappingDataType dataType) {
		generalManager.getLogger().log(new Status(Status.INFO, GeneralManager.PLUGIN_ID,
			"Create lookup table for type=" + type));

		switch (dataType) {
			case INT2INT:
				hashType2Mapping.put(type, new HashMap<Integer, Integer>());
				break;
			case INT2STRING:
				hashType2Mapping.put(type, new HashMap<Integer, String>());
				break;
			case STRING2INT:
				hashType2Mapping.put(type, new HashMap<String, Integer>());
				break;
			case STRING2STRING:
				hashType2Mapping.put(type, new HashMap<String, String>());
				break;
			case MULTI_STRING2STRING:
				hashType2Mapping.put(type, new MultiHashMap<String, String>());
				break;
			case MULTI_INT2STRING:
				hashType2Mapping.put(type, new MultiHashMap<Integer, String>());
				break;
			case MULTI_STRING2INT:
				hashType2Mapping.put(type, new MultiHashMap<String, Integer>());
				break;
			case MULTI_INT2INT:
				hashType2Mapping.put(type, new MultiHashMap<Integer, Integer>());
				break;
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
	}

	/**
	 * Method takes a map that contains identifier codes and creates a new resolved codes. Resolving means
	 * mapping from code to internal ID.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <KeyType, ValueType> void createCodeResolvedMap(EMappingType mappingType,
		EMappingType destMappingType) {
		Map codeResolvedMap = null;
		int iMappingErrors = 0;

		EIDType originKeyType = mappingType.getTypeOrigin();
		EIDType originValueType = mappingType.getTypeTarget();
		EIDType destKeyType = destMappingType.getTypeOrigin();
		EIDType destValueType = destMappingType.getTypeTarget();

		Map<KeyType, ValueType> srcMap = (Map<KeyType, ValueType>) hashType2Mapping.get(mappingType);

		if (mappingType != EMappingType.REFSEQ_MRNA_2_DAVID) {
			// Remove old unresolved map
			hashType2Mapping.remove(mappingType);
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
								conversionType, srcMap.get(key)));
						}
					}
					else {
						codeResolvedMap = new MultiHashMap<Integer, Integer>();
						MultiHashMap<Integer, String> srcMultiMap = (MultiHashMap<Integer, String>) srcMap;
						Integer iID = 0;

						for (KeyType key : srcMap.keySet()) {
							for (String sID : srcMultiMap.getAll(key)) {
								iID = generalManager.getIDMappingManager().getID(conversionType, sID);

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
								conversionType, srcMap.get(key)));
						}
					}
					else {
						codeResolvedMap = new MultiHashMap<String, Integer>();
						MultiHashMap<String, String> srcMultiMap = (MultiHashMap<String, String>) srcMap;
						Integer iID = 0;

						for (KeyType key : srcMap.keySet()) {
							for (String sID : srcMultiMap.getAll(key)) {
								iID = generalManager.getIDMappingManager().getID(conversionType, sID);

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
							codeResolvedMap.put(generalManager.getIDMappingManager().getID(conversionType,
								key), srcMap.get(key));
						}
					}
					else {
						codeResolvedMap = new MultiHashMap<Integer, Integer>();
						MultiHashMap<String, Integer> srcMultiMap = (MultiHashMap<String, Integer>) srcMap;
						Integer iResolvedID = 0;

						for (KeyType key : srcMap.keySet()) {
							iResolvedID = generalManager.getIDMappingManager().getID(conversionType, key);

							if (iResolvedID == null) {
								generalManager.getLogger().log(new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
									iMappingErrors++ + ": No DAVID mapping for RefSeq " + key));
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
						codeResolvedMap.put(generalManager.getIDMappingManager().getID(conversionType, key),
							srcMap.get(key));
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
	}

	@Override
	@SuppressWarnings("unchecked")
	public <KeyType, ValueType> Map<KeyType, ValueType> getMapping(EMappingType type) {
		return (Map<KeyType, ValueType>) hashType2Mapping.get(type);
	}

	@Override
	public final boolean hasMapping(EMappingType type) {
		return hashType2Mapping.containsKey(type);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <KeyType, ValueType> ValueType getID(EMappingType type, KeyType key) {
		Map<KeyType, ValueType> tmpMap;
		tmpMap = (HashMap<KeyType, ValueType>) hashType2Mapping.get(type);
		return tmpMap.get(key);
	}

	@SuppressWarnings("unchecked")
	public <KeyType, ValueType> Set<ValueType> getMultiID(EMappingType type, KeyType key) {
		MultiHashMap<KeyType, ValueType> tmpMap;
		tmpMap = (MultiHashMap<KeyType, ValueType>) hashType2Mapping.get(type);
		return tmpMap.getAll(key);
	}
}
