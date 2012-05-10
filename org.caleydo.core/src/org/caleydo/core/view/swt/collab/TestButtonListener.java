/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.swt.collab;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class TestButtonListener
	implements Listener {

	Object requester;

	@Override
	public void handleEvent(Event e) {
		// ViewManager vm = GeneralManager.get().getViewGLCanvasManager();
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
