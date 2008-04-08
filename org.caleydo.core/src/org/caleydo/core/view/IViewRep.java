package org.caleydo.core.view;

import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.view.IView;

/**
 * Interface for all View Representations.
 * 
 * @author Micahel Kalkusch
 *
 * @see org.caleydo.core.view.AViewRep
 */
public interface IViewRep 
extends IView, IMediatorSender, IMediatorReceiver {


	/**
	 * Set the ViewType of this ViewRep.
	 * 
	 * @return 
	 */
	public void setViewType(ViewType viewType);
	
}
