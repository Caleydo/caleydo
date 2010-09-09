package org.caleydo.core.data.filter;

import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
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
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (contentVAUpdateListener != null) {
			eventPublisher.removeListener(contentVAUpdateListener);
			contentVAUpdateListener = null;
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

}
