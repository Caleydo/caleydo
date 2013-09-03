/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray.similarity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.RelationsUpdatedEvent;

/**
 * Analyze the relations of groups (i.e. created through clustering) in multiple virtual arrays.
 *
 * @author Alexander Lex
 */
public class RelationAnalyzer {
	private final Map<String, SimilarityMap> maps = new HashMap<String, SimilarityMap>(20);

	public synchronized void updateRelations(Perspective recordPerspective) {

		final String perspectiveID = recordPerspective.getPerspectiveID();
		final VirtualArray recordVA = recordPerspective.getVirtualArray();

		// if this thing does not exist yet, we create it here, else we replace the pre-existing one
		SimilarityMap currentMap = new SimilarityMap(perspectiveID, recordVA);

		for (Entry<String, SimilarityMap> entry : maps.entrySet()) {
//			if (entry.getKey() == perspectiveID)
//				continue;
			VASimilarity similarity =
				entry.getValue().calculateVASimilarity(perspectiveID, recordVA);
			currentMap.setVaSimilarity(similarity);
		}
		maps.put(perspectiveID, currentMap);
		RelationsUpdatedEvent event = new RelationsUpdatedEvent();
		event.setSender(this);
		EventPublisher.trigger(event);
	}

	/**
	 * remove all stored relationships about the given {@link Perspective}
	 *
	 * @param recordPerspective
	 */
	public synchronized void removeAll(Perspective recordPerspective) {
		final String perspectiveID = recordPerspective.getPerspectiveID();

		// remove id vs *
		maps.remove(perspectiveID);
		// remove * vs id
		for (SimilarityMap map : maps.values()) {
			map.removeVASimilarity(perspectiveID);
		}
	}

	/**
	 * Returns the similarity map for a specific table.
	 *
	 * @param tableID
	 *            The id of the set
	 * @return the similarity map with info on all relations to other registered meta sets
	 */
	public synchronized SimilarityMap getSimilarityMap(String perspectiveID) {
		return maps.get(perspectiveID);
	}
}
