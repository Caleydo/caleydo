package org.caleydo.view.datagraph.node;

import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.view.datagraph.ForceDirectedGraphLayout;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.visbricks.GLVisBricks;
import org.eclipse.core.runtime.Status;

public class NodeCreator {

	private Map<Class<? extends IDataDomain>, Class<? extends ADataNode>> dataNodeClasses = new HashMap<Class<? extends IDataDomain>, Class<? extends ADataNode>>();
	private Map<Class<? extends AGLView>, Class<? extends ViewNode>> viewNodeClasses = new HashMap<Class<? extends AGLView>, Class<? extends ViewNode>>();

	public NodeCreator() {
		dataNodeClasses.put(ATableBasedDataDomain.class,
				TableBasedDataNode.class);
		dataNodeClasses.put(PathwayDataDomain.class, PathwayDataNode.class);

		viewNodeClasses.put(AGLView.class, ViewNode.class);
		viewNodeClasses.put(GLVisBricks.class, VisBricksNode.class);
	}

	public ADataNode createDataNode(ForceDirectedGraphLayout graphLayout,
			GLDataGraph view, DragAndDropController dragAndDropController,
			int id, IDataDomain dataDomain) {

		Class<? extends ADataNode> nodeClass = dataNodeClasses.get(dataDomain
				.getClass());

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
				ADataNode node = nodeClass.getConstructor(
						graphLayout.getClass(), view.getClass(),
						dragAndDropController.getClass(), Integer.class,
						IDataDomain.class).newInstance(graphLayout, view,
						dragAndDropController, id, dataDomain);
				node.init();

				return node;
			} catch (Exception e) {
				Logger.log(new Status(Status.ERROR, this.toString(),
						"Failed to create Data Node", e));
			}
		}

		return null;
	}

	public ViewNode createViewNode(ForceDirectedGraphLayout graphLayout,
			GLDataGraph view, DragAndDropController dragAndDropController,
			int id, AGLView representedView) {

		Class<? extends ViewNode> nodeClass = viewNodeClasses
				.get(representedView.getClass());

		if (nodeClass == null) {
			for (Class<? extends AGLView> c : viewNodeClasses.keySet()) {
				if (c.isAssignableFrom(representedView.getClass())) {
					nodeClass = viewNodeClasses.get(c);
					break;
				}
			}
		}

		if (nodeClass != null) {
			try {
				ViewNode node = nodeClass.getConstructor(
						graphLayout.getClass(), view.getClass(),
						dragAndDropController.getClass(), Integer.class,
						AGLView.class).newInstance(graphLayout, view,
						dragAndDropController, id, representedView);
				node.init();

				return node;
			} catch (Exception e) {
				Logger.log(new Status(Status.ERROR, this.toString(),
						"Failed to create Data Node", e));
			}
		}

		return null;
	}
}
