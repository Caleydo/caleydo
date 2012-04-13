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
package org.caleydo.view.dataflipper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLDataFlipperView extends ARcpGLViewPart {

	// private ArrayList<Integer> iAlContainedViewIDs;

	/**
	 * Constructor.
	 */
	public RcpGLDataFlipperView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedDataFlipperView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}

		// iAlContainedViewIDs = new ArrayList<Integer>();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		view = new GLDataFlipper(glCanvas, parentComposite,
				serializedView.getViewFrustum());
		view.initFromSerializableRepresentation(serializedView);
		view.initialize();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {

		serializedView = new SerializedDataFlipperView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public void dispose() {
		// GLDataFlipper glDataFlipperView =
		// (GLDataFlipper)
		// GeneralManager.get().getViewGLCanvasManager().getGLEventListener(viewID);

		// glRemoteView.clearAll();

		// TODO
		// for (Integer iContainedViewID : iAlContainedViewIDs) {
		// glDataFlipperView.removeView(GeneralManager.get().getViewGLCanvasManager().getGLEventListener(
		// iContainedViewID));
		// }

		super.dispose();
	}

	@Override
	public String getViewGUIID() {
		return GLDataFlipper.VIEW_TYPE;
	}
}
