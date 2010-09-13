package org.caleydo.core.data.filter;

import org.caleydo.core.data.filter.event.NewContentFilterEvent;
import org.caleydo.core.data.filter.event.NewContentFilterListener;
import org.caleydo.core.data.filter.event.RemoveContentFilterEvent;
import org.caleydo.core.data.filter.event.RemoveContentFilterListener;
import org.caleydo.core.data.virtualarray.ContentVAType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
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
	extends FilterManager<ContentVAType, ContentVADelta, ContentFilter, ContentVirtualArray>
	implements IContentVAUpdateHandler {

	private ContentVAUpdateListener contentVAUpdateListener;
	private RemoveContentFilterListener removeContentFilterListener;
	private NewContentFilterListener newContentFilterListener;

	public ContentFilterManager(ASetBasedDataDomain dataDomain) {
		super(dataDomain, dataDomain.getContentVA(ContentVAType.CONTENT), new ContentFilterFactory());
	}

	@Override
	public void replaceVA(int setID, String dataDomainType, ContentVAType vaType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		contentVAUpdateListener = new ContentVAUpdateListener();
		contentVAUpdateListener.setHandler(this);
		contentVAUpdateListener.setDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(ContentVAUpdateEvent.class, contentVAUpdateListener);

		removeContentFilterListener = new RemoveContentFilterListener();
		removeContentFilterListener.setHandler(this);
		removeContentFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(RemoveContentFilterEvent.class, removeContentFilterListener);

		newContentFilterListener = new NewContentFilterListener();
		newContentFilterListener.setHandler(this);
		newContentFilterListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(NewContentFilterEvent.class, newContentFilterListener);

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

		if (newContentFilterListener != null) {
			eventPublisher.removeListener(newContentFilterListener);
			newContentFilterListener = null;
		}
	}

	@Override
	protected void triggerVAUpdateEvent(ContentVADelta delta) {
		ContentVAUpdateEvent event = new ContentVAUpdateEvent();
		event.setSender(this);
		event.setDataDomainType(dataDomain.getDataDomainType());
		event.setVirtualArrayDelta(delta);
		eventPublisher.triggerEvent(event);
	}

	@Override
	protected void triggerReplaceVAEvent() {
		ReplaceContentVAInUseCaseEvent event = new ReplaceContentVAInUseCaseEvent();
		event.setVAType(ContentVAType.CONTENT);
		event.setVirtualArray(currentVA);
		event.setSender(this);
		event.setDataDomainType(dataDomain.getDataDomainType());

		eventPublisher.triggerEvent(event);
	}

}
