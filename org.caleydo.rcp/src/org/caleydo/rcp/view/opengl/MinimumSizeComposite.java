package org.caleydo.rcp.view.opengl;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.SetMinViewSizeEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class MinimumSizeComposite
	extends ScrolledComposite
	implements IListenerOwner {

	SetMinViewSizeEventListener setMinSizeEventListener;

	public MinimumSizeComposite(Composite parent, int style) {
		super(parent, style);
		setMinSizeEventListener = new SetMinViewSizeEventListener();
		setMinSizeEventListener.setHandler(this);
		GeneralManager.get().getEventPublisher().addListener(SetMinViewSizeEvent.class,
			setMinSizeEventListener);
	}

	public void setView(AGLEventListener view) {
		setMinSizeEventListener.setView(view);
	}

	@Override
	public void queueEvent(final AEventListener<? extends IListenerOwner> listener, final AEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				listener.handleEvent(event);
			}
		});
	}
}
