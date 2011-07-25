package org.caleydo.core.data.virtualarray.similarity;

import java.util.HashMap;
import java.util.Map.Entry;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.data.RelationsUpdatedEvent;

/**
 * Analyze the relations of groups (i.e. created through clustering) in multiple virtual arrays.
 * 
 * @author Alexander Lex
 */
public class RelationAnalyzer {

	/**
	 * The queue which holds the events
	 */

//	private RecordVAUpdateListener recordVAUpdateListener;
//	private ReplaceRecordVAListener replaceRecordVAListener;
	private EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

//	private ASetBasedDataDomain dataDomain;

	private HashMap<Integer, SimilarityMap> hashSimilarityMaps;

	public RelationAnalyzer() {
//		this.dataDomain = dataDomain;
		hashSimilarityMaps = new HashMap<Integer, SimilarityMap>(20);

	}

//	@Override
//	public void registerEventListeners() {
//
//		recordVAUpdateListener = new RecordVAUpdateListener();
//		recordVAUpdateListener.setHandler(this);
//		recordVAUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
//		eventPublisher.addListener(RecordVAUpdateEvent.class, recordVAUpdateListener);
//
//		replaceRecordVAListener = new ReplaceRecordVAListener();
//		replaceRecordVAListener.setHandler(this);
//		replaceRecordVAListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
//		eventPublisher.addListener(ReplaceRecordVAEvent.class, replaceRecordVAListener);
//
//	}
//
//	@Override
//	public void unregisterEventListeners() {
//		if (recordVAUpdateListener != null) {
//			eventPublisher.removeListener(recordVAUpdateListener);
//			recordVAUpdateListener = null;
//		}
//
//		if (replaceRecordVAListener != null) {
//			eventPublisher.removeListener(replaceRecordVAListener);
//			replaceRecordVAListener = null;
//		}
//	}
//
//	@Override
//	public void handleVAUpdate(RecordVADelta vaDelta, String info) {
//		// TODO Auto-generated method stub
//
//	}

	
	public synchronized void updateRelations(int tableID, RecordVirtualArray recordVA) {

//		try {
//			Thread.sleep(1000);
//		}
//		catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		DataTable set = dataDomain.getTable(tableID);
//		ContentVirtualArray recordVA = table.getContentData(Set.CONTENT).getRecordVA();

		// if this thing does not exist yet, we create it here, else we replace the pre-existing one
		SimilarityMap currentMap = new SimilarityMap(tableID, recordVA);

		for (Entry<Integer, SimilarityMap> entry : hashSimilarityMaps.entrySet()) {
			if (entry.getKey() == tableID)
				continue;
			VASimilarity<RecordVirtualArray, RecordGroupList> similarity =
				entry.getValue().calculateVASimilarity(tableID, recordVA);
			currentMap.setVaSimilarity(similarity);
		}
		hashSimilarityMaps.put(tableID, currentMap);
		RelationsUpdatedEvent event = new RelationsUpdatedEvent();
//		event.setDataDomainType(dataDomain.getDataDomainType());
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
	public synchronized SimilarityMap getSimilarityMap(Integer tableID) {
		return hashSimilarityMaps.get(tableID);
	}
}
