package org.caleydo.core.data.filter;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.filter.event.MoveDimensionFilterEvent;
import org.caleydo.core.data.filter.event.MoveDimensionFilterListener;
import org.caleydo.core.data.filter.event.NewDimensionFilterEvent;
import org.caleydo.core.data.filter.event.NewDimensionFilterListener;
import org.caleydo.core.data.filter.event.ReEvaluateDimensionFilterListEvent;
import org.caleydo.core.data.filter.event.ReEvaluateDimensionFilterListListener;
import org.caleydo.core.data.filter.event.RemoveDimensionFilterEvent;
import org.caleydo.core.data.filter.event.RemoveDimensionFilterListener;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.event.data.ReplaceDimensionVAInUseCaseEvent;
import org.caleydo.core.manager.event.view.tablebased.DimensionVAUpdateEvent;
import org.caleydo.core.view.opengl.canvas.listener.DimensionVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IDimensionVAUpdateHandler;

/**
 * Concrete implementation of {@link FilterManager} for {@link DimensionVirtualArray}s.
 * 
 * @author Alexander Lex
 */
public class DimensionFilterManager
	extends FilterManager<DimensionVADelta, DimensionFilter, DimensionVirtualArray>
	implements IDimensionVAUpdateHandler {

	private DimensionVAUpdateListener dimensionVAUpdateListener;
	private RemoveDimensionFilterListener removeDimensionFilterListener;
	private MoveDimensionFilterListener moveDimensionFilterListener;
	private NewDimensionFilterListener newDimensionFilterListener;
	private ReEvaluateDimensionFilterListListener reEvaluateDimensionFilterListListener;

	public DimensionFilterManager(ATableBasedDataDomain dataDomain) {
		super(dataDomain, dataDomain.getTable().getBaseDimensionVA(), new DimensionFilterFactory());

	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		//
		// dimensionVAUpdateListener = new DimensionVAUpdateListener();
		// dimensionVAUpdateListener.setHandler(this);
		// dimensionVAUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		// eventPublisher.addListener(DimensionVAUpdateEvent.class, dimensionVAUpdateListener);

		dimensionVAUpdateListener = new DimensionVAUpdateListener();
		dimensionVAUpdateListener.setHandler(this);
		dimensionVAUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(DimensionVAUpdateEvent.class, dimensionVAUpdateListener);

		removeDimensionFilterListener = new RemoveDimensionFilterListener();
		removeDimensionFilterListener.setHandler(this);
		removeDimensionFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(RemoveDimensionFilterEvent.class, removeDimensionFilterListener);
		
		moveDimensionFilterListener = new MoveDimensionFilterListener();
		moveDimensionFilterListener.setHandler(this);
		moveDimensionFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(MoveDimensionFilterEvent.class, moveDimensionFilterListener);

		newDimensionFilterListener = new NewDimensionFilterListener();
		newDimensionFilterListener.setHandler(this);
		newDimensionFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(NewDimensionFilterEvent.class, newDimensionFilterListener);

		reEvaluateDimensionFilterListListener = new ReEvaluateDimensionFilterListListener();
		reEvaluateDimensionFilterListListener.setHandler(this);
		reEvaluateDimensionFilterListListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(ReEvaluateDimensionFilterListEvent.class,
			reEvaluateDimensionFilterListListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (dimensionVAUpdateListener != null) {
			eventPublisher.removeListener(dimensionVAUpdateListener);
			dimensionVAUpdateListener = null;
		}

		if (removeDimensionFilterListener != null) {
			eventPublisher.removeListener(removeDimensionFilterListener);
			removeDimensionFilterListener = null;
		}

		if (newDimensionFilterListener != null) {
			eventPublisher.removeListener(newDimensionFilterListener);
			newDimensionFilterListener = null;
		}

		if (reEvaluateDimensionFilterListListener != null) {
			eventPublisher.removeListener(reEvaluateDimensionFilterListListener);
			reEvaluateDimensionFilterListListener = null;
		}
	}

	@Override
	public void replaceDimensionVA(String dataDomain, String vaType) {

	}

	@Override
	protected void triggerVAUpdateEvent(DimensionVADelta delta) {
		DimensionVAUpdateEvent event = new DimensionVAUpdateEvent();
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
