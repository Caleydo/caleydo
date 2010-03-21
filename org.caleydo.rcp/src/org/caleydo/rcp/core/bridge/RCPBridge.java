package org.caleydo.rcp.core.bridge;

import java.util.ArrayList;

import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.ClusterSetEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.rcp.action.toolbar.view.StartClusteringAction;
import org.caleydo.rcp.command.handler.ExitHandler;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class RCPBridge
	implements IGUIBridge, IListenerOwner {
	
	private String sFileNameCurrentDataSet;
	
	private ClusterSetListener clusterSetListener;
	
	public RCPBridge() {
		clusterSetListener = new ClusterSetListener();
	}
	
	public void init() {
		registerEventListeners();
	}
	
	@Override
	public void closeApplication() {

		unregisterEventListeners();
		
		try {
			new ExitHandler().execute(null);
		}
		catch (ExecutionException e) {
			throw new IllegalStateException("Cannot execute exit command.");
		}
	}

	public void registerEventListeners() {

		clusterSetListener.setHandler(this);
		GeneralManager.get().getEventPublisher().addListener(ClusterSetEvent.class,
			clusterSetListener);

	}

	public void unregisterEventListeners() {
		if (clusterSetListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(clusterSetListener);
			clusterSetListener = null;
		}
	}
	
	@Override
	public void setShortInfo(String sMessage) {
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	@Override
	public void setFileNameCurrentDataSet(String sFileName) {
		this.sFileNameCurrentDataSet = sFileName;
	}

	@Override
	public String getFileNameCurrentDataSet() {
		return sFileNameCurrentDataSet;
	}

	@Override
	public Display getDisplay() {

		return PlatformUI.getWorkbench().getDisplay();
	}

	@Override
	public void createView(final ASerializedView serializedView) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					IWorkbenchPage page =
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					ARcpGLViewPart viewPart = (ARcpGLViewPart) page.showView(serializedView.getViewType());
					AGLView view = viewPart.getGLView();
					view.initFromSerializableRepresentation(serializedView);
					// TODO re-init view with its serializedView

				}
				catch (PartInitException ex) {
					throw new RuntimeException("could not create view with gui-id="
						+ serializedView.getViewType(), ex);
				}
			}
		});
	}

	@Override
	public void closeView(final String viewGUIID) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IViewPart viewToClose =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(viewGUIID);
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(viewToClose);

			}
		});
	}

	@Override
	public void queueEvent(final AEventListener<? extends IListenerOwner> listener, final AEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				listener.handleEvent(event);
			}
		});
	}

	public void clusterSet(ArrayList<ISet> sets) {
		StartClusteringAction action = new StartClusteringAction();
		action.setSets(sets);
		action.run();
	}
}