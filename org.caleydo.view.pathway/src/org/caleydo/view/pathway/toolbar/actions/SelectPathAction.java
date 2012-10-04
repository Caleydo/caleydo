/**
 * 
 */
package org.caleydo.view.pathway.toolbar.actions;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.pathway.event.SelectPathModeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Button for toggling path selection.
 * 
 * @author Christian Partl
 * 
 */
public class SelectPathAction extends Action implements IToolBarItem {
	public static final String TEXT = "Toggle path selection (Ctrl + O)";
	public static final String ICON = "resources/icons/view/pathway/path_selection.png";

//	private UpdatePathSelectionModeButtonEventListener updatePathSelectionModeButtonEventListener;

	/**
	 * Constructor.
	 */
	public SelectPathAction(boolean isChecked) {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
		setChecked(isChecked);
		
//		registerEventListeners();
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher()
				.triggerEvent(new SelectPathModeEvent(isChecked()));
	}

//	@Override
//	public void queueEvent(final AEventListener<? extends IListenerOwner> listener,
//			final AEvent event) {
//		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				listener.handleEvent(event);
//			}
//		});
//
//	}
//
//	@Override
//	public void registerEventListeners() {
////		updatePathSelectionModeButtonEventListener = new UpdatePathSelectionModeButtonEventListener();
////		updatePathSelectionModeButtonEventListener.setHandler(this);
////		GeneralManager
////				.get()
////				.getEventPublisher()
////				.addListener(UpdatePathSelectionModeButtonEvent.class,
////						updatePathSelectionModeButtonEventListener);
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.caleydo.core.event.IListenerOwner#unregisterEventListeners()
//	 */
//	@Override
//	public void unregisterEventListeners() {
//		// TODO Auto-generated method stub
//
//	}
	
	
}
