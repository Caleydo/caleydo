package org.caleydo.rcp.view.toolbar.content;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.rcp.view.toolbar.ActionToolBarContainer;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.ToolBarContainer;
import org.caleydo.rcp.view.toolbar.action.storagebased.PropagateSelectionsAction;
import org.caleydo.rcp.view.toolbar.action.storagebased.RenderContextAction;
import org.caleydo.rcp.view.toolbar.action.storagebased.ResetViewAction;
import org.caleydo.rcp.view.toolbar.action.storagebased.UseRandomSamplingAction;
import org.caleydo.rcp.view.toolbar.action.storagebased.parcoords.AngularBrushingAction;
import org.caleydo.rcp.view.toolbar.action.storagebased.parcoords.SaveSelectionsAction;
import org.eclipse.jface.preference.PreferenceStore;

/**
 * ToolBarContent implementation for heatmap specific toolbar items.
 * 
 * @author Werner Puff
 */
public class ClinicalParCoordsToolBarContent
	extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/storagebased/parcoords/parcoords.png";

	public static final String VIEW_TITLE = "Clinical Parallel Coordinates";

	@Override
	public Class<?> getViewClass() {
		return ClinicalParCoordsToolBarContent.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		// all pc views
		int targetViewID = getTargetViewData().getViewID();
		IToolBarItem angularBrushingAction = new AngularBrushingAction(targetViewID);
		actionList.add(angularBrushingAction);
		IToolBarItem saveSelectionsAction = new SaveSelectionsAction(targetViewID);
		actionList.add(saveSelectionsAction);
		IToolBarItem resetViewAction = new ResetViewAction(targetViewID);
		actionList.add(resetViewAction);
		IToolBarItem propagateSelectionAction = new PropagateSelectionsAction(targetViewID);
		actionList.add(propagateSelectionAction);

		PreferenceStore ps = GeneralManager.get().getPreferenceStore();
		boolean limit = ps.getBoolean(PreferenceConstants.PC_LIMIT_REMOTE_TO_CONTEXT);

		// only if standalone or explicitly requested
		if (renderType == STANDARD_RENDERING && !limit) {
			IToolBarItem toggleRenderContextAction = new RenderContextAction(targetViewID);
			actionList.add(toggleRenderContextAction);
			IToolBarItem useRandomSamplingAction = new UseRandomSamplingAction(targetViewID);
			actionList.add(useRandomSamplingAction);
		}

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
