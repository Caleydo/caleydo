package org.caleydo.core.view.swt.collab;

import java.util.ArrayList;

import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class TestButtonListener 
	implements Listener  {
		
	Object requester;

	@Override
	public void handleEvent(Event e) {
		IViewManager vm = GeneralManager.get().getViewGLCanvasManager();
		for (IView v : vm.getAllGLEventListeners()) {
			if (v.getClass().equals(GLRemoteRendering.class)) {
				GLRemoteRendering rr = (GLRemoteRendering) v;
				printRemoteLevel("FocusLevel: ", rr.getFocusLevel());
				printRemoteLevel("StackLevel: ", rr.getStackLevel());
			}
		}
	}

	private void printRemoteLevel(String name, RemoteLevel rl) {
		IViewManager vm = GeneralManager.get().getViewGLCanvasManager();
		System.out.println(name);
		ArrayList<RemoteLevelElement> rles = rl.getAllElements();
		for (RemoteLevelElement rle : rles) {
			IView rv = vm.getGLEventListener(rle.getContainedElementID());
			if (rv != null) {
				System.out.println(" - " + rv.getClass());
			} else {
				System.out.println(" - [empty]");
			}
		}

	}
	
	public void setRequester(Object requester) {
		this.requester = requester;
	}

}
