package org.caleydo.rcp.core.bridge;

import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.rcp.command.handler.ExitHandler;
import org.caleydo.rcp.view.opengl.ARcpGLViewPart;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class RCPBridge
	implements IGUIBridge {
	private String sFileNameCurrentDataSet;

	@Override
	public void closeApplication() {
		try {
			new ExitHandler().execute(null);
		}
		catch (ExecutionException e) {
			throw new IllegalStateException("Cannot execute exit command.");
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
					ARcpGLViewPart viewPart = (ARcpGLViewPart) page.showView(serializedView.getViewGUIID());
					AGLEventListener view = viewPart.getGLEventListener();
					view.initFromSerializableRepresentation(serializedView);
					// TODO re-init view with its serializedView

				}
				catch (PartInitException ex) {
					throw new RuntimeException("could not create view with gui-id="
						+ serializedView.getViewGUIID(), ex);
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
}
