package org.caleydo.view.matchmaker.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.gui.toolbar.ActionToolBarContainer;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.core.gui.toolbar.content.AToolBarContent;
import org.caleydo.view.matchmaker.GLMatchmaker;

/**
 * ToolBarContent implementation for compare.
 * 
 * @author Alexander Lex
 */
public class MatchmakerToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/compare/compare.png";
	public static final String VIEW_TITLE = "Compare";

	@Override
	public Class<?> getViewClass() {
		return GLMatchmaker.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		int targetViewID = getTargetViewData().getViewID();

		// UseSortingAction useSortingAction = new
		// UseSortingAction(targetViewID);
		// useSortingAction.setSortingEnabled(true);
		// actionList.add(useSortingAction);

		// UseFishEyeAction useFishEyeAction = new
		// UseFishEyeAction(targetViewID);
		// useFishEyeAction.setUseFishEye(true);
		// actionList.add(useFishEyeAction);

		UseZoomAction useZoomAction = new UseZoomAction();
		useZoomAction.setUseZoom(false);
		actionList.add(useZoomAction);

		UseBandBundlingAction useBandBundlingAction = new UseBandBundlingAction();
		useBandBundlingAction.setBandBundling(false);
		actionList.add(useBandBundlingAction);

		CreateSelectionTypesAction createSelectionTypesAction = new CreateSelectionTypesAction();
		createSelectionTypesAction.setCreateSelectionTypes(false);
		actionList.add(createSelectionTypesAction);

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
