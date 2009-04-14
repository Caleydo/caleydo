package org.caleydo.rcp.views.swt.toolbar.content;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;
import org.caleydo.rcp.action.toolbar.view.storagebased.ChangeOrientationAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.ClearSelectionsAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.PropagateSelectionsAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.RenderContextAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.ResetViewAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.UseRandomSamplingAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.parcoords.AngularBrushingAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.parcoords.OcclusionPreventionAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.parcoords.SaveSelectionsAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceStore;

/**
 * ToolBarContent implementation for heatmap specific toolbar items.  
 * @author Werner Puff
 */
public class ClinicalParCoordsToolBarContent
	extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/storagebased/heatmap/heatmap.png";

	public static final String VIEW_TITLE = "Heat Map";

	@Override
	public Class<?> getViewClass() {
		return GLHeatMap.class;
	}

	public static void createToolBarItems(int iViewID) {
		GLParallelCoordinates pcs =
			(GLParallelCoordinates) GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iViewID);


	}

	@Override
	public List<ToolBarContainer> getDefaultToolBar() {
		ToolBarContainer container = new ToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);

		// all pc views
		IAction angularBrushingAction = new AngularBrushingAction(targetViewID);
		container.add(angularBrushingAction);
		IAction occlusionPreventionAction = new OcclusionPreventionAction(targetViewID);
		container.add(occlusionPreventionAction);
		IAction switchAxesToPolylinesAction = new ChangeOrientationAction(targetViewID);
		container.add(switchAxesToPolylinesAction);
		IAction clearSelectionsAction = new ClearSelectionsAction(targetViewID);
		container.add(clearSelectionsAction);
		IAction saveSelectionsAction = new SaveSelectionsAction(targetViewID);
		container.add(saveSelectionsAction);
		IAction resetViewAction = new ResetViewAction(targetViewID);
		container.add(resetViewAction);
		IAction propagateSelectionAction = new PropagateSelectionsAction(targetViewID);
		container.add(propagateSelectionAction);

		PreferenceStore ps = GeneralManager.get().getPreferenceStore();
		boolean limit = ps.getBoolean(PreferenceConstants.PC_LIMIT_REMOTE_TO_CONTEXT);

		// only if standalone or explicitly requested
		if (contentType == STANDARD_CONTENT && !limit) {
			IAction toggleRenderContextAction = new RenderContextAction(targetViewID);
			container.add(toggleRenderContextAction);
			IAction useRandomSamplingAction = new UseRandomSamplingAction(targetViewID);
			container.add(useRandomSamplingAction);
		}
		
		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
