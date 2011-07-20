package org.caleydo.core.data.filter;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.filter.event.CombineContentFilterEvent;
import org.caleydo.core.data.filter.event.CombineContentFilterListener;
import org.caleydo.core.data.filter.event.MoveContentFilterEvent;
import org.caleydo.core.data.filter.event.MoveContentFilterListener;
import org.caleydo.core.data.filter.event.NewContentFilterEvent;
import org.caleydo.core.data.filter.event.NewContentFilterListener;
import org.caleydo.core.data.filter.event.ReEvaluateContentFilterListEvent;
import org.caleydo.core.data.filter.event.ReEvaluateContentFilterListListener;
import org.caleydo.core.data.filter.event.RemoveContentFilterEvent;
import org.caleydo.core.data.filter.event.RemoveContentFilterListener;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.event.data.ReplaceContentVAInUseCaseEvent;
import org.caleydo.core.manager.event.view.storagebased.ContentVAUpdateEvent;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IContentVAUpdateHandler;

/**
 * Concrete implementation of {@link FilterManager} for {@link ContentVirtualArray}s.
 * 
 * @author Alexander Lex
 */
public class ContentFilterManager
	extends FilterManager<ContentVADelta, ContentFilter, ContentVirtualArray>
	implements IContentVAUpdateHandler {

	private ContentVAUpdateListener contentVAUpdateListener;
	private RemoveContentFilterListener removeContentFilterListener;
	private MoveContentFilterListener moveContentFilterListener;
	private CombineContentFilterListener combineContentFilterListener;
	private NewContentFilterListener newContentFilterListener;
	private ReEvaluateContentFilterListListener reEvaluateContentFilterListListener;

	public ContentFilterManager(ATableBasedDataDomain dataDomain) {
		super(dataDomain, dataDomain.getDataTable().getBaseContentVA(), new ContentFilterFactory());
	}

	@Override
	public void replaceContentVA(int setID, String dataDomainType, String vaType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		contentVAUpdateListener = new ContentVAUpdateListener();
		contentVAUpdateListener.setHandler(this);
		contentVAUpdateListener.setDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(ContentVAUpdateEvent.class, contentVAUpdateListener);

		removeContentFilterListener = new RemoveContentFilterListener();
		removeContentFilterListener.setHandler(this);
		removeContentFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(RemoveContentFilterEvent.class, removeContentFilterListener);
		
		moveContentFilterListener = new MoveContentFilterListener();
		moveContentFilterListener.setHandler(this);
		moveContentFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(MoveContentFilterEvent.class, moveContentFilterListener);
		
		combineContentFilterListener = new CombineContentFilterListener();
		combineContentFilterListener.setHandler(this);
		combineContentFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(CombineContentFilterEvent.class, combineContentFilterListener);

		newContentFilterListener = new NewContentFilterListener();
		newContentFilterListener.setHandler(this);
		newContentFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(NewContentFilterEvent.class, newContentFilterListener);

		reEvaluateContentFilterListListener = new ReEvaluateContentFilterListListener();
		reEvaluateContentFilterListListener.setHandler(this);
		reEvaluateContentFilterListListener.setDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(ReEvaluateContentFilterListEvent.class,
			reEvaluateContentFilterListListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (contentVAUpdateListener != null) {
			eventPublisher.removeListener(contentVAUpdateListener);
			contentVAUpdateListener = null;
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
	protected void triggerVAUpdateEvent(ContentVADelta delta) {
		ContentVAUpdateEvent event = new ContentVAUpdateEvent();
		event.setSender(this);
		event.setDataDomainID(dataDomain.getDataDomainID());
		event.setVirtualArrayDelta(delta);
		eventPublisher.triggerEvent(event);
	}

	@Override
	protected void triggerReplaceVAEvent() {
		ReplaceContentVAInUseCaseEvent event = new ReplaceContentVAInUseCaseEvent();
		event.setVAType(DataTable.CONTENT);
		event.setVirtualArray(currentVA);
		event.setSender(this);
		event.setDataDomainID(dataDomain.getDataDomainID());

		eventPublisher.triggerEvent(event);
	}

}
