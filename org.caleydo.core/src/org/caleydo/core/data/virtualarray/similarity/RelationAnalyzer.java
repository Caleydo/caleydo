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
package org.caleydo.core.data.virtualarray.similarity;

import java.util.HashMap;
import java.util.Map.Entry;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
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

	public synchronized void updateRelations(String perspectiveID, RecordVirtualArray recordVA) {

		// if this thing does not exist yet, we create it here, else we replace the pre-existing one
		SimilarityMap currentMap = new SimilarityMap(perspectiveID, recordVA);

		for (Entry<String, SimilarityMap> entry : hashSimilarityMaps.entrySet()) {
//			if (entry.getKey() == perspectiveID)
//				continue;
			VASimilarity<RecordVirtualArray, RecordGroupList> similarity =
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
