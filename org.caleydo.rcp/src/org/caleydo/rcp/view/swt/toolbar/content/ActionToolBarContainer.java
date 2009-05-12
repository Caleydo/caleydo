package org.caleydo.rcp.view.swt.toolbar.content;

import java.util.List;

/**
 * ToolBarContainer for toolbar groups that only contains actions.
 * @author Werner Puff
 */
public class ActionToolBarContainer
	extends ToolBarContainer {

	/** list of actions within this tool bar container */
	private List<IToolBarItem> actions;

	/**
	 * Gets the list of actions currently defined within this tool bar container
	 * @return list of actions
	 */
	@Override
	public List<IToolBarItem> getToolBarItems() {
		return actions;
	}

	/**
	 * sets the list of actions for this tool bar container
	 * @param actions list of actions
	 */
	@Override
	public void setToolBarItems(List<IToolBarItem> actions) {
		this.actions = actions;
	}
	
}
