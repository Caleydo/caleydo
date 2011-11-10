package org.caleydo.view.parcoords.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.gui.toolbar.AToolBarContent;
import org.caleydo.core.gui.toolbar.ActionToolBarContainer;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.core.gui.toolbar.action.PropagateSelectionsAction;
import org.caleydo.core.gui.toolbar.action.ResetViewAction;
import org.caleydo.core.gui.toolbar.action.TakeSnapshotAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.parcoords.GLParallelCoordinates;

/**
 * ToolBarContent implementation for parcoords specific toolbar items.
 * 
 * @author Werner Puff
 * @author Marc Streit
 */
public class ParCoordsToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/tablebased/parcoords/parcoords.png";

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
		actionList.add(new AngularBrushingAction());
		// IAction occlusionPreventionAction = new
		// OcclusionPreventionAction(viewID);
		// alToolbar.add(occlusionPreventionAction);

		actionList.add(new ResetAxisSpacingAction());

		if (renderType == STANDARD_RENDERING) {
			actionList.add(new SaveSelectionsAction());
			actionList.add(new ResetViewAction());
			actionList.add(new PropagateSelectionsAction());
		}

		AGLView view = GeneralManager.get().getViewManager()
				.getGLView(targetViewData.getViewID());
		actionList.add(new TakeSnapshotAction(view.getParentComposite()));

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}
}
