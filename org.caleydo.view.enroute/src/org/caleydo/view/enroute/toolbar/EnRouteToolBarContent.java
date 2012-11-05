/**
 * 
 */
package org.caleydo.view.enroute.toolbar;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.gui.toolbar.AToolBarContent;
import org.caleydo.core.gui.toolbar.ActionToolBarContainer;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.OpenOnlineHelpAction;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.SerializedEnRoutePathwayView;
import org.caleydo.view.enroute.toolbar.actions.FitToViewWidthAction;

/**
 * Toolbar content for the enRoute view.
 * 
 * @author Christian Partl
 * 
 */
public class EnRouteToolBarContent extends AToolBarContent {

	public static final String VIEW_TITLE = "enRoute";

	@Override
	public Class<?> getViewClass() {
		return GLEnRoutePathway.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		// container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		SerializedEnRoutePathwayView serializedView = (SerializedEnRoutePathwayView) getTargetViewData();
		actionList.add(new FitToViewWidthAction(serializedView.isFitToViewWidth()));
		container.setToolBarItems(actionList);

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		container = new ActionToolBarContainer();
		actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);
		actionList.add(new OpenOnlineHelpAction(
				"http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/enroute",
				false));

		list.add(container);

		return list;
	}

}
