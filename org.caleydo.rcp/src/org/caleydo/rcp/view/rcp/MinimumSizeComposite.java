package org.caleydo.rcp.view.rcp;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.SetMinViewSizeEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

public class MinimumSizeComposite extends ScrolledComposite
		implements
			IListenerOwner {

	SetMinViewSizeEventListener setMinSizeEventListener;

	public MinimumSizeComposite(Composite parent, int style) {
		super(parent, style);
		setMinSizeEventListener = new SetMinViewSizeEventListener();
		setMinSizeEventListener.setHandler(this);
		GeneralManager.get().getEventPublisher().addListener(
				SetMinViewSizeEvent.class, setMinSizeEventListener);
		addListener(SWT.MouseWheel, new Listener() {
			public void handleEvent(Event event) {
				
				Point origin = getOrigin();
				if (event.count < 0) {
					origin.y = Math.min(origin.y + 40, origin.y + getSize().y);
				} else {
					origin.y = Math.max(origin.y - 40, 0);
				}
				setOrigin(origin);
			}
		});

	}

	public void setView(AGLView view) {
		setMinSizeEventListener.setView(view);
	}

	@Override
	public void queueEvent(
			final AEventListener<? extends IListenerOwner> listener,
			final AEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				listener.handleEvent(event);
			}
		});
	}
}
