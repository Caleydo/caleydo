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
package org.caleydo.view.treemap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.view.treemap.actions.LevelHighlightingAction;
import org.caleydo.view.treemap.actions.ToggleColoringModeAction;
import org.caleydo.view.treemap.actions.ToggleLabelAction;
import org.caleydo.view.treemap.actions.ZoomInAction;
import org.caleydo.view.treemap.actions.ZoomOutAction;
import org.eclipse.swt.widgets.Composite;

public class RcpGLTreeMapView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLTreeMapView() {
		super();

		try {
			viewContext = JAXBContext
					.newInstance(SerializedHierarchicalTreeMapView.class);
			viewContext = JAXBContext.newInstance(SerializedTreeMapView.class);

		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new GLHierarchicalTreeMap(glCanvas, parentComposite,
				serializedView.getViewFrustum());
		initializeView();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedHierarchicalTreeMapView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLHierarchicalTreeMap.VIEW_TYPE;
	}

	@Override
	public void addToolBarContent() {
		toolBarManager.add(new LevelHighlightingAction());
		toolBarManager.add(new ToggleColoringModeAction());
		toolBarManager.add(new ToggleLabelAction());
		toolBarManager.add(new ZoomInAction());
		toolBarManager.add(new ZoomOutAction());
	}
}