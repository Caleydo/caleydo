package org.caleydo.rcp.util.selections;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IContentVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.rcp.util.info.listener.InfoAreaUpdateListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

/**
 * Info area that is located in the side-bar. It shows the current view and the current selection (in a tree).
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class SelectionBrowser
	implements ISelectionUpdateHandler, IContentVAUpdateHandler, ISelectionCommandHandler,
	IViewCommandHandler {

	IGeneralManager generalManager = null;
	IEventPublisher eventPublisher = null;

	protected SelectionUpdateListener selectionUpdateListener;
	protected ContentVAUpdateListener virtualArrayUpdateListener;
	protected SelectionCommandListener selectionCommandListener;

	protected RedrawViewListener redrawViewListener;
	protected ClearSelectionsListener clearSelectionsListener;
	protected InfoAreaUpdateListener infoAreaUpdateListener;

	/**
	 * Constructor.
	 */
	public SelectionBrowser() {
		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();

		registerEventListeners();
	}

	public Control createControl(final Composite parent) {

		return parent;
	}

	@Override
	public void handleSelectionUpdate(final ISelectionDelta selectionDelta, final boolean scrollToSelection,
		final String info) {

	}

	@Override
	public void handleSelectionCommand(EIDCategory category, final SelectionCommand selectionCommand) {

	}

	@Override
	public void handleRedrawView() {
		// nothing to do here
	}

	@Override
	public void handleUpdateView() {
		// nothing to do here
	}

	@Override
	public void handleClearSelections() {

	}

	/**
	 * handling method for updates about the info text displayed in the this info-area
	 * 
	 * @param info
	 *            short-info of the sender to display
	 */
	public void handleInfoAreaUpdate(final String info) {

	}

	/**
	 * Registers the listeners for this view to the event system. To release the allocated resources
	 * unregisterEventListeners() has to be called.
	 */
	public void registerEventListeners() {
		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		virtualArrayUpdateListener = new ContentVAUpdateListener();
		virtualArrayUpdateListener.setHandler(this);
		eventPublisher.addListener(VirtualArrayUpdateEvent.class, virtualArrayUpdateListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);
	}

	/**
	 * Unregisters the listeners for this view from the event system. To release the allocated resources
	 * unregisterEventListenrs() has to be called.
	 */
	public void unregisterEventListeners() {
		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
		if (virtualArrayUpdateListener != null) {
			eventPublisher.removeListener(virtualArrayUpdateListener);
			virtualArrayUpdateListener = null;
		}
		if (selectionCommandListener != null) {
			eventPublisher.removeListener(selectionCommandListener);
			selectionCommandListener = null;
		}
		if (redrawViewListener != null) {
			eventPublisher.removeListener(redrawViewListener);
			redrawViewListener = null;
		}
		if (clearSelectionsListener != null) {
			eventPublisher.removeListener(clearSelectionsListener);
			clearSelectionsListener = null;
		}
	}

	public void dispose() {
		unregisterEventListeners();
	}

	@Override
	public synchronized void queueEvent(final AEventListener<? extends IListenerOwner> listener,
		final AEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				listener.handleEvent(event);
			}
		});
	}

	@Override
	public void handleContentVAUpdate(ContentVADelta vaDelta, final String info) {
	
	}

	@Override
	public void replaceContentVA(EIDCategory idCategory, ContentVAType vaType) {
		// TODO Auto-generated method stub
	}

}
