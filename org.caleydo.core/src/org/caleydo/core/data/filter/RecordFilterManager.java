package org.caleydo.core.data.filter;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.filter.event.CombineRecordFilterEvent;
import org.caleydo.core.data.filter.event.CombineRecordFilterListener;
import org.caleydo.core.data.filter.event.MoveRecordtFilterEvent;
import org.caleydo.core.data.filter.event.MoveRecordFilterListener;
import org.caleydo.core.data.filter.event.NewRecordFilterEvent;
import org.caleydo.core.data.filter.event.NewRecordFilterListener;
import org.caleydo.core.data.filter.event.ReEvaluateRecordFilterListEvent;
import org.caleydo.core.data.filter.event.ReEvaluateRecordFilterListListener;
import org.caleydo.core.data.filter.event.RemoveRecordFilterEvent;
import org.caleydo.core.data.filter.event.RemoveRecordFilterListener;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.event.data.ReplaceRecordVAInUseCaseEvent;
import org.caleydo.core.manager.event.view.dimensionbased.RecordVAUpdateEvent;
import org.caleydo.core.view.opengl.canvas.listener.RecordVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IRecordVAUpdateHandler;

/**
 * Concrete implementation of {@link FilterManager} for {@link RecordVirtualArray}s.
 * 
 * @author Alexander Lex
 */
public class RecordFilterManager
	extends FilterManager<RecordVADelta, RecordFilter, RecordVirtualArray>
	implements IRecordVAUpdateHandler {

	private RecordVAUpdateListener recordVAUpdateListener;
	private RemoveRecordFilterListener removeContentFilterListener;
	private MoveRecordFilterListener moveContentFilterListener;
	private CombineRecordFilterListener combineContentFilterListener;
	private NewRecordFilterListener newContentFilterListener;
	private ReEvaluateRecordFilterListListener reEvaluateContentFilterListListener;

	public RecordFilterManager(ATableBasedDataDomain dataDomain) {
		super(dataDomain, dataDomain.getDataTable().getBaseRecordVA(), new RecordFilterFactory());
	}

	@Override
	public void replaceRecordVA(int dataTableID, String dataDomainType, String vaType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		recordVAUpdateListener = new RecordVAUpdateListener();
		recordVAUpdateListener.setHandler(this);
		recordVAUpdateListener.setDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(RecordVAUpdateEvent.class, recordVAUpdateListener);

		removeContentFilterListener = new RemoveRecordFilterListener();
		removeContentFilterListener.setHandler(this);
		removeContentFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(RemoveRecordFilterEvent.class, removeContentFilterListener);
		
		moveContentFilterListener = new MoveRecordFilterListener();
		moveContentFilterListener.setHandler(this);
		moveContentFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(MoveRecordtFilterEvent.class, moveContentFilterListener);
		
		combineContentFilterListener = new CombineRecordFilterListener();
		combineContentFilterListener.setHandler(this);
		combineContentFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(CombineRecordFilterEvent.class, combineContentFilterListener);

		newContentFilterListener = new NewRecordFilterListener();
		newContentFilterListener.setHandler(this);
		newContentFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(NewRecordFilterEvent.class, newContentFilterListener);

		reEvaluateContentFilterListListener = new ReEvaluateRecordFilterListListener();
		reEvaluateContentFilterListListener.setHandler(this);
		reEvaluateContentFilterListListener.setDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(ReEvaluateRecordFilterListEvent.class,
			reEvaluateContentFilterListListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (recordVAUpdateListener != null) {
			eventPublisher.removeListener(recordVAUpdateListener);
			recordVAUpdateListener = null;
		}

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
	protected void triggerVAUpdateEvent(RecordVADelta delta) {
		RecordVAUpdateEvent event = new RecordVAUpdateEvent();
		event.setSender(this);
		event.setDataDomainID(dataDomain.getDataDomainID());
		event.setVirtualArrayDelta(delta);
		eventPublisher.triggerEvent(event);
	}

	@Override
	protected void triggerReplaceVAEvent() {
		ReplaceRecordVAInUseCaseEvent event = new ReplaceRecordVAInUseCaseEvent();
		event.setVAType(DataTable.RECORD);
		event.setVirtualArray(currentVA);
		event.setSender(this);
		event.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.triggerEvent(event);
	}

}
