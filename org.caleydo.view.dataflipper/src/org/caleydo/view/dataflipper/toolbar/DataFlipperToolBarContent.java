package org.caleydo.view.dataflipper.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.core.gui.toolbar.content.AToolBarContent;
import org.caleydo.view.dataflipper.GLDataFlipper;

/**
 * ToolBarContent implementation for data flipper specific toolbar items.
 * 
 * @author Werner Puff
 * @author Marc Streit
 */
public class DataFlipperToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/remote/remote.png";
	public static final String VIEW_TITLE = "Bucket";

	DataFlipperToolBarMediator mediator;

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
	// SerializedDataFlipperView serializedView = (SerializedDataFlipperView)
	// getTargetViewData();
	// ActionToolBarContainer container = new ActionToolBarContainer();
	//
	// container.setImagePath(IMAGE_PATH);
	// container.setTitle(VIEW_TITLE);
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
