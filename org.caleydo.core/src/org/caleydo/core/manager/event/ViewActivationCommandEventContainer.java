package org.caleydo.core.manager.event;

import java.util.List;


/**
 * Command that signals that a view has been activated.
 * the command holds a list of view-id/view-type pairs payload.  
 * @author Werner Puff
 */
public class ViewActivationCommandEventContainer
	extends ViewCommandEventContainer {

	/** type of the view that has been activated */
	private List<Integer> viewIDs;

	/**
	 * Constructor.
	 */
	public ViewActivationCommandEventContainer(EViewCommand eViewCommand) {
		super(eViewCommand);
	}

	public List<Integer> getViewIDs() {
		return viewIDs;
	}

	public void setViewIDs(List<Integer> viewIDs) {
		this.viewIDs = viewIDs;
	}

}
