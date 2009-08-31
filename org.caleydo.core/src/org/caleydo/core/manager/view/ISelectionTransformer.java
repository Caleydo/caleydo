package org.caleydo.core.manager.view;

import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.manager.event.IListenerOwner;

/**
 * Creates {@link SelectedElementRep} for management of Selections and related tasks
 * like drawing connection lines.
 *   
 * @author Werner Puff
 */
public interface ISelectionTransformer extends IListenerOwner {

	public void project(GL gl, String deskoXID, HashMap<EIDType, ConnectionMap> source, HashMap<EIDType, CanvasConnectionMap> target);

	public boolean transform(HashMap<EIDType, ConnectionMap> source, HashMap<EIDType, ConnectionMap> target);
	
	public void handleNewConnections();
	
	public void destroy();
	
}
