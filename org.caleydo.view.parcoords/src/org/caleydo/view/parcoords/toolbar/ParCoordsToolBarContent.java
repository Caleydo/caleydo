package org.caleydo.view.parcoords.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.gui.toolbar.ActionToolBarContainer;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.core.gui.toolbar.action.PropagateSelectionsAction;
import org.caleydo.core.gui.toolbar.action.ResetViewAction;
import org.caleydo.core.gui.toolbar.content.AToolBarContent;
import org.caleydo.view.parcoords.GLParallelCoordinates;

/**
 * ToolBarContent implementation for parcoords specific toolbar items.
 * 
 * @author Werner Puff
 */
public class ParCoordsToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/dimensionbased/parcoords/parcoords.png";

	public static final String VIEW_TITLE = "Parallel Coordinates";

	@Override
	public Class<?> getViewClass() {
		return GLParallelCoordinates.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		// all pc views
		IToolBarItem angularBrushingAction = new AngularBrushingAction();
		actionList.add(angularBrushingAction);
		// IAction occlusionPreventionAction = new
		// OcclusionPreventionAction(viewID);
		// alToolbar.add(occlusionPreventionAction);

		IToolBarItem resetAxisSpacing = new ResetAxisSpacingAction();
		actionList.add(resetAxisSpacing);

		if (renderType == STANDARD_RENDERING) {
			IToolBarItem saveSelectionsAction = new SaveSelectionsAction();
			actionList.add(saveSelectionsAction);
			IToolBarItem resetViewAction = new ResetViewAction();
			actionList.add(resetViewAction);
			IToolBarItem propagateSelectionAction = new PropagateSelectionsAction(
		);
			actionList.add(propagateSelectionAction);
		}

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
