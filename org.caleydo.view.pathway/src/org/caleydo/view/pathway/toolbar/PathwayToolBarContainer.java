package org.caleydo.view.pathway.toolbar;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.core.gui.toolbar.action.TakeSnapshotAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Widget based toolbar container to display pathway related toolbar content.
 * 
 * @author Marc Streit
 */
public class PathwayToolBarContainer extends ToolBarContainer {

	/** Mediator to handle actions triggered by the contributed elements */
	PathwayToolBarMediator pathwayToolBarMediator;

	/** serialized remote rendering view to read the configuration from */
	ASerializedView targetViewData;

	/**
	 * Creates a the pathway selection box and add the pathway toolbar items.
	 */
	@Override
	public List<IToolBarItem> getToolBarItems() {

		List<IToolBarItem> elements = new ArrayList<IToolBarItem>();

		PathwaySearchBox pathwaySearchBox = new PathwaySearchBox("");
		pathwaySearchBox.setPathwayToolBarMediator(pathwayToolBarMediator);
		elements.add(pathwaySearchBox);

		AGLView view = GeneralManager.get().getViewManager()
				.getGLView(targetViewData.getViewID());
		elements.add(new TakeSnapshotAction(view.getParentComposite()));

		return elements;
	}

	public PathwayToolBarMediator getPathwayToolBarMediator() {
		return pathwayToolBarMediator;
	}

	public void setPathwayToolBarMediator(PathwayToolBarMediator pathwayToolBarMediator) {
		this.pathwayToolBarMediator = pathwayToolBarMediator;
	}

	public ASerializedView getTargetViewData() {
		return targetViewData;
	}

	public void setTargetViewData(ASerializedView targetViewData) {
		this.targetViewData = targetViewData;
	}
}
