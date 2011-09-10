package org.caleydo.core.data.filter;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.filter.event.MoveDimensionFilterEvent;
import org.caleydo.core.data.filter.event.MoveDimensionFilterListener;
import org.caleydo.core.data.filter.event.NewDimensionFilterEvent;
import org.caleydo.core.data.filter.event.NewDimensionFilterListener;
import org.caleydo.core.data.filter.event.ReEvaluateDimensionFilterListEvent;
import org.caleydo.core.data.filter.event.ReEvaluateDimensionFilterListListener;
import org.caleydo.core.data.filter.event.RemoveDimensionFilterEvent;
import org.caleydo.core.data.filter.event.RemoveDimensionFilterListener;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.events.ReplaceDimensionPerspectiveEvent;
import org.caleydo.core.data.virtualarray.events.DimensionVADeltaEvent;

/**
 * Concrete implementation of {@link FilterManager} for {@link DimensionVirtualArray}s.
 * 
 * @author Alexander Lex
 */
public class DimensionFilterManager
	extends FilterManager<DimensionPerspective, DimensionVADelta, DimensionFilter, DimensionVirtualArray> {

	private RemoveDimensionFilterListener removeDimensionFilterListener;
	private MoveDimensionFilterListener moveDimensionFilterListener;
	private NewDimensionFilterListener newDimensionFilterListener;
	private ReEvaluateDimensionFilterListListener reEvaluateDimensionFilterListListener;

	public DimensionFilterManager(ATableBasedDataDomain dataDomain, DimensionPerspective perspective) {
		super(dataDomain, perspective);

	}

	@Override
	public void registerEventListeners() {

		removeDimensionFilterListener = new RemoveDimensionFilterListener();
		removeDimensionFilterListener.setHandler(this);
		removeDimensionFilterListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(RemoveDimensionFilterEvent.class, removeDimensionFilterListener);

		moveDimensionFilterListener = new MoveDimensionFilterListener();
		moveDimensionFilterListener.setHandler(this);
		moveDimensionFilterListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(MoveDimensionFilterEvent.class, moveDimensionFilterListener);

		newDimensionFilterListener = new NewDimensionFilterListener();
		newDimensionFilterListener.setHandler(this);
		newDimensionFilterListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(NewDimensionFilterEvent.class, newDimensionFilterListener);

		reEvaluateDimensionFilterListListener = new ReEvaluateDimensionFilterListListener();
		reEvaluateDimensionFilterListListener.setHandler(this);
		reEvaluateDimensionFilterListListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(ReEvaluateDimensionFilterListEvent.class,
			reEvaluateDimensionFilterListListener);

	}

	@Override
	public void unregisterEventListeners() {

		if (removeDimensionFilterListener != null) {
			eventPublisher.removeListener(removeDimensionFilterListener);
			removeDimensionFilterListener = null;
		}

		if (moveDimensionFilterListener != null) {
			eventPublisher.removeListener(moveDimensionFilterListener);
			moveDimensionFilterListener = null;
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
	protected void triggerVADeltaEvent(DimensionVADelta delta) {
		DimensionVADeltaEvent event = new DimensionVADeltaEvent();
		event.setSender(this);
		event.setDataDomainID(dataDomain.getDataDomainID());
		event.setVirtualArrayDelta(delta);
		eventPublisher.triggerEvent(event);
	}

	// @Override
	// protected void triggerReplaceVAEvent() {
	// DimensionReplaceVAEvent event = new DimensionReplaceVAEvent(dataDomain.getDataDomainID(),
	// perspective.getPerspectiveID(), perspective.getVirtualArray());
	// event.s(perspective.getPerspectiveID());
	// event.setVirtualArray(perspective.getVirtualArray());
	// event.setSender(this);
	// event.setDataDomainID(dataDomain.getDataDomainID());
	// eventPublisher.triggerEvent(event);
	// }

	@Override
	protected void resetVA() {
		perspective.reset();
	}

}
