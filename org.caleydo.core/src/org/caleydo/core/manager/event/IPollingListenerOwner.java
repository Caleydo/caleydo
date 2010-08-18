package org.caleydo.core.manager.event;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Interface for {@link IListenerOwner}s that need to perform polling. All subclasses of {@link AGLView} do
 * so.
 * 
 * @author Alexander Lex
 */
public interface IPollingListenerOwner
	extends IListenerOwner {

	/**
	 * Submit an event which is executed by the specified listener once the IListenerOwner thinks it's safe to
	 * do so. This method needs to be implemented using the synchronized keyword.
	 * 
	 * @return The return value is a pair containing the listener used by the IListenerOwner to listen to the
	 *         event, and the event which is to be executed.
	 */
	public Pair<AEventListener<? extends IListenerOwner>, AEvent> getEvent();
}
