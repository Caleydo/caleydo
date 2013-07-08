/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray.similarity;

import java.util.HashMap;
import java.util.Map.Entry;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.RelationsUpdatedEvent;
import org.caleydo.core.manager.GeneralManager;

/**
 * Analyze the relations of groups (i.e. created through clustering) in multiple virtual arrays.
 *
 * @author Alexander Lex
 */
public class RelationAnalyzer {

	private EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

	private HashMap<String, SimilarityMap> hashSimilarityMaps;

	public RelationAnalyzer() {
		hashSimilarityMaps = new HashMap<String, SimilarityMap>(20);

	}

	public synchronized void updateRelations(String perspectiveID, VirtualArray recordVA) {

		// if this thing does not exist yet, we create it here, else we replace the pre-existing one
		SimilarityMap currentMap = new SimilarityMap(perspectiveID, recordVA);

		for (Entry<String, SimilarityMap> entry : hashSimilarityMaps.entrySet()) {
//			if (entry.getKey() == perspectiveID)
//				continue;
			VASimilarity similarity =
				entry.getValue().calculateVASimilarity(perspectiveID, recordVA);
			currentMap.setVaSimilarity(similarity);
		}
		hashSimilarityMaps.put(perspectiveID, currentMap);
		RelationsUpdatedEvent event = new RelationsUpdatedEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	/**
	 * Returns the similarity map for a specific table.
	 *
	 * @param tableID
	 *            The id of the set
	 * @return the similarity map with info on all relations to other registered meta sets
	 */
	public synchronized SimilarityMap getSimilarityMap(String perspectiveID) {
		return hashSimilarityMaps.get(perspectiveID);
	}
}
