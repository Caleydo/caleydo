package cerberus.view;

import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.view.IView;

/**
 * Interface for all View Representations.
 * 
 * @author Micahel Kalkusch
 *
 * @see cerberus.view.AViewRep
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
