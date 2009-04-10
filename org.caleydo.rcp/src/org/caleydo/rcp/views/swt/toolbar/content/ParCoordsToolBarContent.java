package org.caleydo.rcp.views.swt.toolbar.content;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;
import org.caleydo.rcp.action.toolbar.view.storagebased.ChangeOrientationAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.ClearSelectionsAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.PropagateSelectionsAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.ResetViewAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.parcoords.AngularBrushingAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.parcoords.ResetAxisSpacingAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.parcoords.SaveSelectionsAction;
import org.eclipse.jface.action.IAction;

/**
 * ToolBarContent implementation for heatmap specific toolbar items.  
 * @author Werner Puff
 */
public class ParCoordsToolBarContent
	extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/storagebased/parcoords/parcoords.png";

	public static final String VIEW_TITLE = "Parallel Coordinates";

	@Override
	public Class<?> getViewClass() {
		return GLParallelCoordinates.class;
	}
	
	@Override
	public List<ToolBarContainer> getDefaultToolBar() {
		ToolBarContainer container = new ToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);

		// all pc views
		IAction angularBrushingAction = new AngularBrushingAction(targetViewID);
		container.add(angularBrushingAction);
		// IAction occlusionPreventionAction = new OcclusionPreventionAction(iViewID);
		// alToolbar.add(occlusionPreventionAction);
		IAction switchAxesToPolylinesAction = new ChangeOrientationAction(targetViewID);
		container.add(switchAxesToPolylinesAction);

		IAction resetAxisSpacing = new ResetAxisSpacingAction(targetViewID);
		container.add(resetAxisSpacing);

		if (contentType == STANDARD_CONTENT) {
			IAction clearSelectionsAction = new ClearSelectionsAction(targetViewID);
			container.add(clearSelectionsAction);
			IAction saveSelectionsAction = new SaveSelectionsAction(targetViewID);
			container.add(saveSelectionsAction);
			IAction resetViewAction = new ResetViewAction(targetViewID);
			container.add(resetViewAction);
			IAction propagateSelectionAction = new PropagateSelectionsAction(targetViewID);
			container.add(propagateSelectionAction);
		}

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
