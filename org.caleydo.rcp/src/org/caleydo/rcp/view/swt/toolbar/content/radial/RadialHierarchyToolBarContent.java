package org.caleydo.rcp.view.swt.toolbar.content.radial;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.radial.GLRadialHierarchy;
import org.caleydo.core.view.opengl.canvas.radial.SerializedRadialHierarchyView;
import org.caleydo.rcp.action.toolbar.view.radial.ChangeColorModeAction;
import org.caleydo.rcp.action.toolbar.view.radial.GoBackInHistoryAction;
import org.caleydo.rcp.action.toolbar.view.radial.GoForthInHistoryAction;
import org.caleydo.rcp.action.toolbar.view.radial.ToggleMagnifyingGlassAction;
import org.caleydo.rcp.view.swt.toolbar.content.AToolBarContent;
import org.caleydo.rcp.view.swt.toolbar.content.ActionToolBarContainer;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.caleydo.rcp.view.swt.toolbar.content.ToolBarContainer;

/**
 * ToolBarContent implementation for radial layout specific toolbar items.
 * 
 * @author Christian Partl
 */
public class RadialHierarchyToolBarContent
	extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/radial/radial_color_mapping.png";

	public static final String VIEW_TITLE = "Radial Hierarchy";
	private IToolBarItem depthSlider;

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		SerializedRadialHierarchyView serializedView = (SerializedRadialHierarchyView) getTargetViewData();
		int targetViewID = serializedView.getViewID();

		IToolBarItem goBackInHistory = new GoBackInHistoryAction(targetViewID);
		IToolBarItem goForthInHistory = new GoForthInHistoryAction(targetViewID);
		IToolBarItem changeColorMode = new ChangeColorModeAction(targetViewID);
		IToolBarItem magnifyingGlass = new ToggleMagnifyingGlassAction();
		if (depthSlider == null) {
			depthSlider = new DepthSlider("", serializedView.getMaxDisplayedHierarchyDepth());
		}
		actionList.add(goBackInHistory);
		actionList.add(goForthInHistory);
		actionList.add(changeColorMode);
		actionList.add(magnifyingGlass);
		actionList.add(depthSlider);

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

	@Override
	public Class<?> getViewClass() {
		return GLRadialHierarchy.class;
	}

}
