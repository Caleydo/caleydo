package org.caleydo.view.base.swt.toolbar.content.dataflipper;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.remote.dataflipper.GLDataFlipper;
import org.caleydo.view.base.action.toolbar.view.remote.ToggleConnectionLinesAction;
import org.caleydo.view.base.swt.toolbar.content.AToolBarContent;
import org.caleydo.view.base.swt.toolbar.content.ToolBarContainer;

/**
 * ToolBarContent implementation for data flipper specific toolbar items.
 * 
 * @author Werner Puff
 * @author Marc Streit
 */
public class DataFlipperToolBarContent
	extends AToolBarContent {

	public static final String BUCKET_IMAGE_PATH = "resources/icons/view/remote/remote.png";
	public static final String BUCKET_VIEW_TITLE = "Bucket";

	// public static final String PATHWAY_IMAGE_PATH = "resources/icons/view/pathway/pathway.png";
	// public static final String PATHWAY_VIEW_TITLE = "Pathways";

	DataFlipperToolBarMediator mediator;

	ToggleConnectionLinesAction toggleConnectionLinesAction;

	@Override
	public Class<?> getViewClass() {
		return GLDataFlipper.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		// list.add(createBucketContainer());

		return list;
	}

	// /**
	// * Creates and returns icons for data flipper related toolbar box
	// *
	// * @return bucket related toolbar box
	// */
	// private ToolBarContainer createBucketContainer() {
	// mediator = new DataFlipperToolBarMediator();
	// mediator.setToolBarContent(this);
	// SerializedRemoteRenderingView serializedView = (SerializedRemoteRenderingView) getTargetViewData();
	// ActionToolBarContainer container = new ActionToolBarContainer();
	//
	// container.setImagePath(BUCKET_IMAGE_PATH);
	// container.setTitle(BUCKET_VIEW_TITLE);
	// List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
	// container.setToolBarItems(actionList);
	//
	// return container;
	// }

	@Override
	public void dispose() {
		if (mediator != null) {
			mediator.dispose();
			mediator = null;
		}
	}
}
