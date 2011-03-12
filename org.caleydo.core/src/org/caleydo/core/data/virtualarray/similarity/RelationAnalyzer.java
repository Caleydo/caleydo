package org.caleydo.core.data.virtualarray.similarity;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.AEventHandler;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.data.RelationsUpdatedEvent;
import org.caleydo.core.manager.event.data.ReplaceContentVAEvent;
import org.caleydo.core.manager.event.view.storagebased.ContentVAUpdateEvent;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IContentVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceContentVAListener;

/**
 * Analyze the relations of groups (i.e. created through clustering) in multiple virtual arrays.
 * 
 * @author Alexander Lex
 */
public class RelationAnalyzer
	extends AEventHandler
	implements IContentVAUpdateHandler {

	/**
	 * The queue which holds the events
	 */

	private ContentVAUpdateListener contentVAUpdateListener;
	private ReplaceContentVAListener replaceContentVAListener;
	private EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

	private ASetBasedDataDomain dataDomain;

	private HashMap<Integer, SimilarityMap> hashSimilarityMaps;

	public RelationAnalyzer(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		hashSimilarityMaps = new HashMap<Integer, SimilarityMap>(20);

	}

	@Override
	public void registerEventListeners() {

		contentVAUpdateListener = new ContentVAUpdateListener();
		contentVAUpdateListener.setHandler(this);
		contentVAUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(ContentVAUpdateEvent.class, contentVAUpdateListener);

		replaceContentVAListener = new ReplaceContentVAListener();
		replaceContentVAListener.setHandler(this);
		replaceContentVAListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(ReplaceContentVAEvent.class, replaceContentVAListener);

	}

	@Override
	public void unregisterEventListeners() {
		if (contentVAUpdateListener != null) {
			eventPublisher.removeListener(contentVAUpdateListener);
			contentVAUpdateListener = null;
		}

		if (replaceContentVAListener != null) {
			eventPublisher.removeListener(replaceContentVAListener);
			replaceContentVAListener = null;
		}
	}

	@Override
	public void handleVAUpdate(ContentVADelta vaDelta, String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void replaceContentVA(int setID, String dataDomainType, String vaType) {

		ISet set = dataDomain.getSet(setID);
		ContentVirtualArray contentVA = set.getContentData(Set.CONTENT).getContentVA();

		// if this thing does not exist yet, we create it here, else we replace the pre-existing one
		SimilarityMap currentMap = new SimilarityMap(setID, contentVA);

		for (Entry<Integer, SimilarityMap> entry : hashSimilarityMaps.entrySet()) {
			if (entry.getKey() == setID)
				continue;
			VASimilarity<ContentVirtualArray, ContentGroupList> similarity =
				entry.getValue().calculateVASimilarity(setID, contentVA);
			currentMap.setVaSimilarity(similarity);
		}
		hashSimilarityMaps.put(setID, currentMap);
		RelationsUpdatedEvent event = new RelationsUpdatedEvent();
		event.setDataDomainType(dataDomain.getDataDomainType());
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	/**
	 * Returns the similarity map for a specific set.
	 * 
	 * @param setID
	 *            The id of the set
	 * @return the similarity map with info on all relations to other registered meta sets
	 */
	public synchronized SimilarityMap getSimilarityMap(Integer setID) {
		return hashSimilarityMaps.get(setID);
	}
}
