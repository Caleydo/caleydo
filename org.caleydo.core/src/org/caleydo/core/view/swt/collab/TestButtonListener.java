package org.caleydo.core.view.swt.collab;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class TestButtonListener
	implements Listener {

	Object requester;

	@Override
	public void handleEvent(Event e) {
		// IViewManager vm = GeneralManager.get().getViewGLCanvasManager();
		// for (IView v : vm.getAllGLEventListeners()) {
		// if (v.getClass().equals(GLRemoteRendering.class)) {
		// GLRemoteRendering rr = (GLRemoteRendering) v;
		// printRemoteLevel("FocusLevel: ", rr.getFocusLevel());
		// printRemoteLevel("StackLevel: ", rr.getStackLevel());
		// }
		// }
	}

	// private void printRemoteLevel(String name, RemoteLevel rl) {
	// System.out.println(name);
	// ArrayList<RemoteLevelElement> rles = rl.getAllElements();
	// for (RemoteLevelElement rle : rles) {
	// IView rv = rle.getGLView();
	// if (rv != null) {
	// System.out.println(" - " + rv.getClass());
	// }
	// else {
	// System.out.println(" - [empty]");
	// }
	// }
	//
	// }

	public void setRequester(Object requester) {
		this.requester = requester;
	}

}
