package org.caleydo.rcp.views.swt.toolbar.content;

import java.util.List;

import org.eclipse.jface.action.IAction;

/**
 * ToolBarContainer for toolbar groups that only contains actions.
 * @author Werner Puff
 */
public class ActionToolBarContainer
	extends ToolBarContainer {

	/** list of actions within this tool bar container */
	private List<IAction> actions;

	/**
	 * Gets the list of actions currently defined within this tool bar container
	 * @return list of actions
	 */
	public List<IAction> getActions() {
		return actions;
	}

	/**
	 * sets the list of actions for this tool bar container
	 * @param actions list of actions
	 */
	public void setActions(List<IAction> actions) {
		this.actions = actions;
	}
	
}
