/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.bucket.toolbar;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.gui.toolbar.AToolBarContent;
import org.caleydo.core.gui.toolbar.ActionToolBarContainer;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
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

		IToolBarItem closeOrResetContainedViews = new CloseOrResetContainedViews(mediator);
		actionList.add(closeOrResetContainedViews);
		// IAction toggleLayoutAction = new ToggleLayoutAction(viewID);
		// alToolbar.add(toggleLayoutAction);
		toggleConnectionLinesAction = new ToggleConnectionLinesAction(mediator);
		toggleConnectionLinesAction.setConnectionLinesEnabled(serializedView
				.isConnectionLinesEnabled());
		actionList.add(toggleConnectionLinesAction);

		NavigationModeAction navigationModeAction = new NavigationModeAction(mediator);
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

		container.setPathwayToolBarMediator(new PathwayToolBarMediator(
				((ASerializedSingleTablePerspectiveBasedView) targetViewData).getDataDomainID()));
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
