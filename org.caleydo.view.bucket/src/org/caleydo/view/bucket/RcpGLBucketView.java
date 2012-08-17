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
package org.caleydo.view.bucket;

import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.heatmap.heatmap.SerializedHeatMapView;
import org.caleydo.view.parcoords.SerializedParallelCoordinatesView;
import org.caleydo.view.pathway.SerializedPathwayView;
import org.eclipse.swt.widgets.Composite;

public class RcpGLBucketView extends ARcpGLViewPart {

	private ArrayList<Integer> iAlContainedViewIDs;

	/**
	 * Constructor.
	 */
	public RcpGLBucketView() {
		super();

		try {
			Collection<Class<? extends ASerializedView>> viewTypes = new ArrayList<Class<? extends ASerializedView>>();
			viewTypes.add(SerializedBucketView.class);
			viewTypes.add(SerializedHeatMapView.class);
			viewTypes.add(SerializedParallelCoordinatesView.class);
			viewTypes.add(SerializedPathwayView.class);

			Class<?>[] classes = new Class<?>[viewTypes.size()];
			classes = viewTypes.toArray(classes);
			viewContext = JAXBContext.newInstance(classes);

		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}

		iAlContainedViewIDs = new ArrayList<Integer>();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();

		view = new GLBucket(glCanvas, parentComposite, serializedView.getViewFrustum());
		initializeView();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {

		serializedView = new SerializedBucketView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public void dispose() {
		GLBucket glRemoteView = (GLBucket) GeneralManager.get().getViewManager()
				.getGLView(view.getID());

		for (Integer iContainedViewID : iAlContainedViewIDs) {
			glRemoteView.removeView(GeneralManager.get().getViewManager()
					.getGLView(iContainedViewID));
		}

		super.dispose();

		GeneralManager.get().getViewManager().getConnectedElementRepresentationManager()
				.clearByView(view.getID());

		PathwayManager.get().resetPathwayVisiblityState();
	}

	@Override
	public String getViewGUIID() {
		return GLBucket.VIEW_TYPE;
	}
}
