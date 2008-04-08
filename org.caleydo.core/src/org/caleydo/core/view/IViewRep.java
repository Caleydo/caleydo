package org.geneview.core.view;

import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.view.IView;

/**
 * Interface for all View Representations.
 * 
 * @author Micahel Kalkusch
 *
 * @see org.geneview.core.view.AViewRep
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
