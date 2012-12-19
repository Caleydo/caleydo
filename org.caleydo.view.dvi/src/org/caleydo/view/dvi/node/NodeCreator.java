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
package org.caleydo.view.dvi.node;

import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
import org.caleydo.core.view.ITablePerspectiveBasedView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.layout.AGraphLayout;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class NodeCreator {

	private Map<Class<? extends IDataDomain>, Class<? extends ADataNode>> dataNodeClasses = new HashMap<Class<? extends IDataDomain>, Class<? extends ADataNode>>();
	private Map<Class<? extends ITablePerspectiveBasedView>, Class<? extends ViewNode>> viewNodeClasses = new HashMap<Class<? extends ITablePerspectiveBasedView>, Class<? extends ViewNode>>();

	public NodeCreator() {
		dataNodeClasses.put(ATableBasedDataDomain.class, TableBasedDataNode.class);
		dataNodeClasses.put(PathwayDataDomain.class, PathwayDataNode.class);

		viewNodeClasses.put(ISingleTablePerspectiveBasedView.class, ViewNode.class);
		viewNodeClasses.put(IMultiTablePerspectiveBasedView.class,
				MultiTablePerspectiveViewNode.class);
		// viewNodeClasses.put(GLLin.class, MultiTablePerspectiveViewNode.class);
	}

	public ADataNode createDataNode(AGraphLayout graphLayout, GLDataViewIntegrator view,
			DragAndDropController dragAndDropController, int id, IDataDomain dataDomain) {

		Class<? extends ADataNode> nodeClass = dataNodeClasses.get(dataDomain.getClass());

		if (nodeClass == null) {
			for (Class<? extends IDataDomain> c : dataNodeClasses.keySet()) {
				if (c.isAssignableFrom(dataDomain.getClass())) {
					nodeClass = dataNodeClasses.get(c);
					break;
				}
			}
		}

		if (nodeClass != null) {
			try {
				ADataNode node = nodeClass.getConstructor(AGraphLayout.class,
						view.getClass(), dragAndDropController.getClass(), Integer.class,
						IDataDomain.class).newInstance(graphLayout, view,
						dragAndDropController, id, dataDomain);
				node.init();

				return node;
			} catch (Exception e) {
				Logger.log(new Status(IStatus.ERROR, this.toString(),
						"Failed to create Data Node", e));
			}
		}

		return null;
	}

	public ViewNode createViewNode(AGraphLayout graphLayout, GLDataViewIntegrator view,
			DragAndDropController dragAndDropController, int id, AGLView representedView) {

		Class<? extends ViewNode> nodeClass = viewNodeClasses.get(representedView
				.getClass());

		if (nodeClass == null) {
			for (Class<? extends ITablePerspectiveBasedView> c : viewNodeClasses.keySet()) {
				if (c.isAssignableFrom(representedView.getClass())) {
					nodeClass = viewNodeClasses.get(c);
					break;
				}
			}
		}

		if (nodeClass != null) {
			try {
				ViewNode node = nodeClass.getConstructor(AGraphLayout.class,
						view.getClass(), dragAndDropController.getClass(), Integer.class,
						AGLView.class).newInstance(graphLayout, view,
						dragAndDropController, id, representedView);
				node.init();

				return node;
			} catch (Exception e) {
				Logger.log(new Status(IStatus.ERROR, this.toString(),
						"Failed to create Data Node", e));
			}
		}

		return null;
	}
}
