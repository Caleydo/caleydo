package org.caleydo.core.data.filter;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.filter.event.MoveStorageFilterEvent;
import org.caleydo.core.data.filter.event.MoveStorageFilterListener;
import org.caleydo.core.data.filter.event.NewStorageFilterEvent;
import org.caleydo.core.data.filter.event.NewStorageFilterListener;
import org.caleydo.core.data.filter.event.ReEvaluateStorageFilterListEvent;
import org.caleydo.core.data.filter.event.ReEvaluateStorageFilterListListener;
import org.caleydo.core.data.filter.event.RemoveStorageFilterEvent;
import org.caleydo.core.data.filter.event.RemoveStorageFilterListener;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.delta.StorageVADelta;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.event.data.ReplaceDimensionVAInUseCaseEvent;
import org.caleydo.core.manager.event.view.storagebased.StorageVAUpdateEvent;
import org.caleydo.core.view.opengl.canvas.listener.IStorageVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.StorageVAUpdateListener;

/**
 * Concrete implementation of {@link FilterManager} for {@link DimensionVirtualArray}s.
 * 
 * @author Alexander Lex
 */
public class StorageFilterManager
	extends FilterManager<StorageVADelta, StorageFilter, DimensionVirtualArray>
	implements IStorageVAUpdateHandler {

	private StorageVAUpdateListener storageVAUpdateListener;
	private RemoveStorageFilterListener removeStorageFilterListener;
	private MoveStorageFilterListener moveStorageFilterListener;
	private NewStorageFilterListener newStorageFilterListener;
	private ReEvaluateStorageFilterListListener reEvaluateStorageFilterListListener;

	public StorageFilterManager(ATableBasedDataDomain dataDomain) {
		super(dataDomain, dataDomain.getDataTable().getBaseStorageVA(), new StorageFilterFactory());

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
		storageVAUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(StorageVAUpdateEvent.class, storageVAUpdateListener);

		removeStorageFilterListener = new RemoveStorageFilterListener();
		removeStorageFilterListener.setHandler(this);
		removeStorageFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(RemoveStorageFilterEvent.class, removeStorageFilterListener);
		
		moveStorageFilterListener = new MoveStorageFilterListener();
		moveStorageFilterListener.setHandler(this);
		moveStorageFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(MoveStorageFilterEvent.class, moveStorageFilterListener);

		newStorageFilterListener = new NewStorageFilterListener();
		newStorageFilterListener.setHandler(this);
		newStorageFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(NewStorageFilterEvent.class, newStorageFilterListener);

		reEvaluateStorageFilterListListener = new ReEvaluateStorageFilterListListener();
		reEvaluateStorageFilterListListener.setHandler(this);
		reEvaluateStorageFilterListListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
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
		event.setDataDomainID(dataDomain.getDataDomainID());
		event.setVirtualArrayDelta(delta);
		eventPublisher.triggerEvent(event);
	}

	@Override
	protected void triggerReplaceVAEvent() {
		ReplaceDimensionVAInUseCaseEvent event = new ReplaceDimensionVAInUseCaseEvent();
		event.setVAType(DataTable.DIMENSION);
		event.setVirtualArray(currentVA);
		event.setSender(this);
		event.setDataDomainID(dataDomain.getDataDomainID());

		eventPublisher.triggerEvent(event);
	}

}
