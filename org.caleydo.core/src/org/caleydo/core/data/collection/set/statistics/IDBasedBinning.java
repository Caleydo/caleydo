package org.caleydo.core.data.collection.set.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.mapping.IDMappingManager;

public class IDBasedBinning {
	IDMappingManager mappingManager;

	HashMap<String, ArrayList<Integer>> bin = new HashMap<String, ArrayList<Integer>>();

	public IDBasedBinning() {
		mappingManager = GeneralManager.get().getIDMappingManager();
	}

	public HashMap<String, ArrayList<Integer>> getBinning(IDType sourceType, IDType targetType,
		ContentVirtualArray contenVA) {
		if (bin.isEmpty()) {
			ArrayList<Integer> nonMappingIDs = new ArrayList<Integer>();
			for (Integer contentID : contenVA) {
				
				Set<String> refSeqIDs = mappingManager.getIDAsSet(sourceType, IDType.getIDType("REFSEQ_MRNA"), contentID);
				
				for (String refSeq : refSeqIDs) {
					Set<String> mappedIDs = mappingManager.getIDAsSet(IDType.getIDType("REFSEQ_MRNA"), targetType, refSeq);
					if (mappedIDs == null) {
						nonMappingIDs.add(contentID);
						bin.put("Other", nonMappingIDs);
						continue;
					}
					for (String id : mappedIDs) {
						if (!bin.containsKey(id)) {
							ArrayList<Integer> ids = new ArrayList<Integer>();
							ids.add(contentID);
							bin.put(id, ids);
						}
						else {
							ArrayList<Integer> ids = bin.get(id);
							ids.add(contentID);
							bin.put(id, ids);
						}
					}					
				}
			}
		}
		return bin;
	}
}
