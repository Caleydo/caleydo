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
package org.caleydo.view.radial;

import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.serialize.ASerializedSingleDataContainerBasedView;
import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class RcpGLRadialHierarchyView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLRadialHierarchyView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedRadialHierarchyView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		// minSizeComposite.setView(view);

		createGLCanvas();

		view = new GLRadialHierarchy(glCanvas, parentComposite,
				serializedView.getViewFrustum());
		view.initFromSerializableRepresentation(serializedView);
		if (view instanceof IDataDomainBasedView<?>) {
			IDataDomain dataDomain = DataDomainManager.get().getDataDomainByID(
					((ASerializedSingleDataContainerBasedView) serializedView).getDataDomainID());
			@SuppressWarnings("unchecked")
			IDataDomainBasedView<IDataDomain> dataDomainBasedView = (IDataDomainBasedView<IDataDomain>) view;
			dataDomainBasedView.setDataDomain(dataDomain);
		}

		view.initialize();
		createPartControlGL();
	}

	public static void createToolBarItems(int viewID) {
		alToolbar = new ArrayList<IAction>();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedRadialHierarchyView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLRadialHierarchy.VIEW_TYPE;
	}

}