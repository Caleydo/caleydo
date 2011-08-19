package org.caleydo.core.data.filter;

import org.caleydo.core.data.collection.table.RecordPerspective;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.filter.event.CombineRecordFilterEvent;
import org.caleydo.core.data.filter.event.CombineRecordFilterListener;
import org.caleydo.core.data.filter.event.MoveRecordFilterListener;
import org.caleydo.core.data.filter.event.MoveRecordtFilterEvent;
import org.caleydo.core.data.filter.event.NewRecordFilterEvent;
import org.caleydo.core.data.filter.event.NewRecordFilterListener;
import org.caleydo.core.data.filter.event.ReEvaluateRecordFilterListEvent;
import org.caleydo.core.data.filter.event.ReEvaluateRecordFilterListListener;
import org.caleydo.core.data.filter.event.RemoveRecordFilterEvent;
import org.caleydo.core.data.filter.event.RemoveRecordFilterListener;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.events.RecordReplaceVAEvent;
import org.caleydo.core.data.virtualarray.events.RecordVADeltaEvent;

/**
 * Concrete implementation of {@link FilterManager} for {@link RecordVirtualArray}s.
 * 
 * @author Alexander Lex
 */
public class RecordFilterManager
	extends FilterManager<RecordPerspective, RecordVADelta, RecordFilter, RecordVirtualArray> {

	// private RecordVADeltaListener recordVAUpdateListener;
	private RemoveRecordFilterListener removeContentFilterListener;
	private MoveRecordFilterListener moveContentFilterListener;
	private CombineRecordFilterListener combineContentFilterListener;
	private NewRecordFilterListener newContentFilterListener;
	private ReEvaluateRecordFilterListListener reEvaluateContentFilterListListener;

	public RecordFilterManager(ATableBasedDataDomain dataDomain, RecordPerspective perspective) {
		super(dataDomain, perspective);
	}

	@Override
	public void registerEventListeners() {
		removeContentFilterListener = new RemoveRecordFilterListener();
		removeContentFilterListener.setHandler(this);
		removeContentFilterListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(RemoveRecordFilterEvent.class, removeContentFilterListener);

		moveContentFilterListener = new MoveRecordFilterListener();
		moveContentFilterListener.setHandler(this);
		moveContentFilterListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(MoveRecordtFilterEvent.class, moveContentFilterListener);

		combineContentFilterListener = new CombineRecordFilterListener();
		combineContentFilterListener.setHandler(this);
		combineContentFilterListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(CombineRecordFilterEvent.class, combineContentFilterListener);

		newContentFilterListener = new NewRecordFilterListener();
		newContentFilterListener.setHandler(this);
		newContentFilterListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(NewRecordFilterEvent.class, newContentFilterListener);

		reEvaluateContentFilterListListener = new ReEvaluateRecordFilterListListener();
		reEvaluateContentFilterListListener.setHandler(this);
		reEvaluateContentFilterListListener.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher
			.addListener(ReEvaluateRecordFilterListEvent.class, reEvaluateContentFilterListListener);

	}

	@Override
	public void unregisterEventListeners() {
		if (removeContentFilterListener != null) {
			eventPublisher.removeListener(removeContentFilterListener);
			removeContentFilterListener = null;
		}

		if (moveContentFilterListener != null) {
			eventPublisher.removeListener(moveContentFilterListener);
			moveContentFilterListener = null;
		}

		if (combineContentFilterListener != null) {
			eventPublisher.removeListener(combineContentFilterListener);
			combineContentFilterListener = null;
		}

		if (newContentFilterListener != null) {
			eventPublisher.removeListener(newContentFilterListener);
			newContentFilterListener = null;
		}

		if (reEvaluateContentFilterListListener != null) {
			eventPublisher.removeListener(reEvaluateContentFilterListListener);
			reEvaluateContentFilterListListener = null;
		}
	}

	@Override
	protected void triggerReplaceVAEvent() {
		RecordReplaceVAEvent event = new RecordReplaceVAEvent();
		event.setVAType(perspective.getPerspectiveID());
		event.setVirtualArray(perspective.getVA());
		event.setSender(this);
		event.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.triggerEvent(event);
	}

	@Override
	protected void triggerVADeltaEvent(RecordVADelta delta) {
		RecordVADeltaEvent event = new RecordVADeltaEvent();
		event.setSender(this);
		event.setDataDomainID(dataDomain.getDataDomainID());
		event.setVirtualArrayDelta(delta);
		eventPublisher.triggerEvent(event);
	}

	@Override
	protected void resetVA() {
		perspective.setVA(dataDomain.getTable().getBaseRecordVA(perspective.getPerspectiveID()));
	}

}
