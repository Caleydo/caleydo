package org.caleydo.core.data.filter;

import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IContentVAUpdateHandler;

public class ContentFilterManager
	extends FilterManager<ContentVAType, ContentVADelta, ContentFilter, ContentVirtualArray>
	implements IContentVAUpdateHandler {

	private ContentVAUpdateListener contentVAUpdateListener;

	public ContentFilterManager(Set set) {
		super(set);
	}

	@Override
	public void handleVAUpdate(ContentVADelta vaDelta, String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void replaceVA(int setID, String dataDomainType, ContentVAType vaType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerEventListeners() {

		contentVAUpdateListener = new ContentVAUpdateListener();
		contentVAUpdateListener.setHandler(this);
//		contentVAUpdateListener.setDataDomainType(dataDomainType);
		eventPublisher.addListener(VirtualArrayUpdateEvent.class, contentVAUpdateListener);
	}

}
