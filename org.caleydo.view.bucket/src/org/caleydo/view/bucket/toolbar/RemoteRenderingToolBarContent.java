package org.caleydo.view.bucket.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.rcp.view.toolbar.ActionToolBarContainer;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.ToolBarContainer;
import org.caleydo.rcp.view.toolbar.content.AToolBarContent;
import org.caleydo.view.bucket.GLBucket;
import org.caleydo.view.bucket.SerializedBucketView;
import org.caleydo.view.bucket.toolbar.actions.CloseOrResetContainedViews;
import org.caleydo.view.bucket.toolbar.actions.NavigationModeAction;
import org.caleydo.view.bucket.toolbar.actions.ToggleConnectionLinesAction;
import org.caleydo.view.bucket.toolbar.actions.ToggleZoomAction;
import org.caleydo.view.pathway.toolbar.PathwayToolBarMediator;

/**
 * ToolBarContent implementation for bucket specific toolbar items.
 * 
 * @author Werner Puff
 */
public class RemoteRenderingToolBarContent extends AToolBarContent {

	public static final String BUCKET_IMAGE_PATH = "resources/icons/view/remote/remote.png";
	public static final String BUCKET_VIEW_TITLE = "Bucket";

	public static final String PATHWAY_IMAGE_PATH = "resources/icons/view/pathway/pathway.png";
	public static final String PATHWAY_VIEW_TITLE = "Pathways";

	RemoteRenderingToolBarMediator mediator;

	ToggleConnectionLinesAction toggleConnectionLinesAction;

	@Override
	public Class<?> getViewClass() {
		return GLBucket.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(createBucketContainer());
		list.add(createPathwayContainer());

		return list;
	}

	/**
	 * Creates and returns icons for bucket related toolbar box
	 * 
	 * @return bucket related toolbar box
	 */
	private ToolBarContainer createBucketContainer() {
		mediator = new RemoteRenderingToolBarMediator();
		mediator.setToolBarContent(this);
		SerializedBucketView serializedView = (SerializedBucketView) getTargetViewData();
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(BUCKET_IMAGE_PATH);
		container.setTitle(BUCKET_VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		IToolBarItem closeOrResetContainedViews = new CloseOrResetContainedViews(
				mediator);
		actionList.add(closeOrResetContainedViews);
		// IAction toggleLayoutAction = new ToggleLayoutAction(viewID);
		// alToolbar.add(toggleLayoutAction);
		toggleConnectionLinesAction = new ToggleConnectionLinesAction(mediator);
		toggleConnectionLinesAction.setConnectionLinesEnabled(serializedView
				.isConnectionLinesEnabled());
		actionList.add(toggleConnectionLinesAction);

		NavigationModeAction navigationModeAction = new NavigationModeAction(
				mediator);
		actionList.add(navigationModeAction);

		ToggleZoomAction toggleZoomAction = new ToggleZoomAction(mediator);
		actionList.add(toggleZoomAction);

		return container;
	}

	/**
	 * Creates and returns icons for pathway related toolbar box FIXME: pathway
	 * buttons do not work this way at the moment, because the related commands
	 * need a pathway-view-id, not a bucket id. instead of commands an event
	 * should be dispatched where all pathways are listening, too.
	 * 
	 * @return pathway related toolbar box
	 */
	private ToolBarContainer createPathwayContainer() {

		PathwayToolBarContainer container = new PathwayToolBarContainer();

		container.setImagePath(PATHWAY_IMAGE_PATH);
		container.setTitle(PATHWAY_VIEW_TITLE);

		container.setPathwayToolBarMediator(new PathwayToolBarMediator());
		container.setTargetViewData((SerializedBucketView) getTargetViewData());

		return container;
	}

	@Override
	public void dispose() {
		if (mediator != null) {
			mediator.dispose();
			mediator = null;
		}
	}
}
