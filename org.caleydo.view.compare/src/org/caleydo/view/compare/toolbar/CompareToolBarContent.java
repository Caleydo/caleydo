package org.caleydo.view.compare.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.rcp.view.toolbar.ActionToolBarContainer;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.ToolBarContainer;
import org.caleydo.rcp.view.toolbar.content.AToolBarContent;
import org.caleydo.view.compare.GLCompare;

/**
 * ToolBarContent implementation for compare.
 * 
 * @author Alexander Lex
 */
public class CompareToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/compare/compare.png";
	public static final String VIEW_TITLE = "Compare";

	@Override
	public Class<?> getViewClass() {
		return GLCompare.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		int targetViewID = getTargetViewData().getViewID();

		UseSortingAction useSortingAction = new UseSortingAction(targetViewID);
		useSortingAction.setSortingEnabled(true);
		actionList.add(useSortingAction);

		UseFishEyeAction useFishEyeAction = new UseFishEyeAction(targetViewID);
		useFishEyeAction.setUseFishEye(true);
		actionList.add(useFishEyeAction);

		UseZoomAction useZoomAction = new UseZoomAction(targetViewID);
		useZoomAction.setUseZoom(false);
		actionList.add(useZoomAction);

		UseBandBundlingAction useBandBundlingAction = new UseBandBundlingAction(
				targetViewID);
		useBandBundlingAction.setBandBundling(false);
		actionList.add(useBandBundlingAction);

		CreateSelectionTypesAction createSelectionTypesAction = new CreateSelectionTypesAction(
				targetViewID);
		createSelectionTypesAction.setCreateSelectionTypes(false);
		actionList.add(createSelectionTypesAction);

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
