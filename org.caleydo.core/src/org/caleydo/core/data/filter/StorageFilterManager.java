package org.caleydo.core.data.filter;

import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.filter.event.NewStorageFilterEvent;
import org.caleydo.core.data.filter.event.NewStorageFilterListener;
import org.caleydo.core.data.filter.event.ReEvaluateStorageFilterListEvent;
import org.caleydo.core.data.filter.event.ReEvaluateStorageFilterListListener;
import org.caleydo.core.data.filter.event.RemoveStorageFilterEvent;
import org.caleydo.core.data.filter.event.RemoveStorageFilterListener;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.delta.StorageVADelta;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.data.ReplaceStorageVAInUseCaseEvent;
import org.caleydo.core.manager.event.view.storagebased.StorageVAUpdateEvent;
import org.caleydo.core.view.opengl.canvas.listener.IStorageVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.StorageVAUpdateListener;

/**
 * Concrete implementation of {@link FilterManager} for {@link StorageVirtualArray}s.
 * 
 * @author Alexander Lex
 */
public class StorageFilterManager
	extends FilterManager<StorageVADelta, StorageFilter, StorageVirtualArray>
	implements IStorageVAUpdateHandler {

	private StorageVAUpdateListener storageVAUpdateListener;
	private RemoveStorageFilterListener removeStorageFilterListener;
	private NewStorageFilterListener newStorageFilterListener;
	private ReEvaluateStorageFilterListListener reEvaluateStorageFilterListListener;

	public StorageFilterManager(ASetBasedDataDomain dataDomain) {
		super(dataDomain, dataDomain.getSet().getBaseStorageVA(), new StorageFilterFactory());

	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		//
		// storageVAUpdateListener = new StorageVAUpdateListener();
		// storageVAUpdateListener.setHandler(this);
		// storageVAUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		// eventPublisher.addListener(StorageVAUpdateEvent.class, storageVAUpdateListener);

		storageVAUpdateListener = new StorageVAUpdateListener();
		storageVAUpdateListener.setHandler(this);
		storageVAUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(StorageVAUpdateEvent.class, storageVAUpdateListener);

		removeStorageFilterListener = new RemoveStorageFilterListener();
		removeStorageFilterListener.setHandler(this);
		removeStorageFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(RemoveStorageFilterEvent.class, removeStorageFilterListener);

		newStorageFilterListener = new NewStorageFilterListener();
		newStorageFilterListener.setHandler(this);
		newStorageFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(NewStorageFilterEvent.class, newStorageFilterListener);

		reEvaluateStorageFilterListListener = new ReEvaluateStorageFilterListListener();
		reEvaluateStorageFilterListListener.setHandler(this);
		reEvaluateStorageFilterListListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(ReEvaluateStorageFilterListEvent.class,
			reEvaluateStorageFilterListListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (storageVAUpdateListener != null) {
			eventPublisher.removeListener(storageVAUpdateListener);
			storageVAUpdateListener = null;
		}

		if (removeStorageFilterListener != null) {
			eventPublisher.removeListener(removeStorageFilterListener);
			removeStorageFilterListener = null;
		}

		if (newStorageFilterListener != null) {
			eventPublisher.removeListener(newStorageFilterListener);
			newStorageFilterListener = null;
		}

		if (reEvaluateStorageFilterListListener != null) {
			eventPublisher.removeListener(reEvaluateStorageFilterListListener);
			reEvaluateStorageFilterListListener = null;
		}
	}

	@Override
	public void replaceStorageVA(String dataDomain, String vaType) {

	}

	@Override
	protected void triggerVAUpdateEvent(StorageVADelta delta) {
		StorageVAUpdateEvent event = new StorageVAUpdateEvent();
		event.setSender(this);
		event.setDataDomainType(dataDomain.getDataDomainType());
		event.setVirtualArrayDelta(delta);
		eventPublisher.triggerEvent(event);
	}

	@Override
	protected void triggerReplaceVAEvent() {
		ReplaceStorageVAInUseCaseEvent event = new ReplaceStorageVAInUseCaseEvent();
		event.setVAType(Set.STORAGE);
		event.setVirtualArray(currentVA);
		event.setSender(this);
		event.setDataDomainType(dataDomain.getDataDomainType());

		eventPublisher.triggerEvent(event);
	}

}
