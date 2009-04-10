package org.caleydo.rcp.views.swt.toolbar.content;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.rcp.action.toolbar.view.pathway.GeneMappingAction;
import org.caleydo.rcp.action.toolbar.view.pathway.NeighborhoodAction;
import org.caleydo.rcp.action.toolbar.view.pathway.TextureAction;
import org.caleydo.rcp.action.toolbar.view.remote.CloseOrResetContainedViews;
import org.caleydo.rcp.action.toolbar.view.remote.ToggleConnectionLinesAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.ClearSelectionsAction;
import org.eclipse.jface.action.IAction;

/**
 * ToolBarContent implementation for heatmap specific toolbar items.  
 * @author Werner Puff
 */
public class RemoteRenderingToolBarContent
	extends AToolBarContent {

	public static final String BUCKET_IMAGE_PATH = "resources/icons/view/remote/remote.png";
	public static final String BUCKET_VIEW_TITLE = "Bucket";

	public static final String PATHWAY_IMAGE_PATH = "resources/icons/view/pathway/pathway.png";
	public static final String PATHWAY_VIEW_TITLE = "Pathways";

	@Override
	public Class<?> getViewClass() {
		return GLRemoteRendering.class;
	}
	
	@Override
	public List<ToolBarContainer> getDefaultToolBar() {
		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(createBucketContainer());
		list.add(createPathwayContainer());

		return list;
	}

	/**
	 * Creates and returns icons for bucket related toolbar box
	 * @return bucket related toolbar box
	 */
	private ToolBarContainer createBucketContainer() {
		ToolBarContainer container = new ToolBarContainer();
		container.setImagePath(BUCKET_IMAGE_PATH);
		container.setTitle(BUCKET_VIEW_TITLE);

		// IAction takeSnapshotAction = new TakeSnapshotAction(-1);
		// alToolbar.add(takeSnapshotAction);
		IAction closeOrResetContainedViews = new CloseOrResetContainedViews(targetViewID);
		container.add(closeOrResetContainedViews);
		// IAction toggleLayoutAction = new ToggleLayoutAction(viewID);
		// alToolbar.add(toggleLayoutAction);
		IAction toggleConnectionLinesAction = new ToggleConnectionLinesAction(targetViewID);
		container.add(toggleConnectionLinesAction);
		IAction clearSelectionsAction = new ClearSelectionsAction(targetViewID);
		container.add(clearSelectionsAction);

		return container;
	}

	/**
	 * Creates and returns icons for pathway related toolbar box
	 *
	 * FIXME: pathway buttons do not work this way at the moment, because the related 
	 * commands need a pathway-view-id, not a bucket id. instead of commands an event should be dispatched
	 * where all pathways are listening, too.
	 * 
	 * @return pathway related toolbar box
	 */
	private ToolBarContainer createPathwayContainer() {
		ToolBarContainer container = new ToolBarContainer();
		container.setImagePath(PATHWAY_IMAGE_PATH);
		container.setTitle(PATHWAY_VIEW_TITLE);

		IAction textureAction = new TextureAction(targetViewID);
		container.add(textureAction);
		IAction neighborhoodAction = new NeighborhoodAction(targetViewID);
		container.add(neighborhoodAction);
		IAction geneMappingAction = new GeneMappingAction(targetViewID);
		container.add(geneMappingAction);

		return container;
	}
}
