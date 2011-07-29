package org.caleydo.core.view.swt;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.view.AView;
import org.eclipse.swt.widgets.Composite;

public abstract class ASWTView
	extends AView
	implements IListenerOwner {

	/**
	 * Constructor.
	 */
	public ASWTView(int viewID, Composite parentComposite) {
		super(viewID, parentComposite);
		GeneralManager.get().getViewManager().registerItem(this);
	}
	
	public abstract void draw();

	@Override
	public synchronized void queueEvent(final AEventListener<? extends IListenerOwner> listener,
		final AEvent event) {
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				listener.handleEvent(event);
			}
		});
	}
	@Override
	public void registerEventListeners() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterEventListeners() {
		// TODO Auto-generated method stub

	}
}
