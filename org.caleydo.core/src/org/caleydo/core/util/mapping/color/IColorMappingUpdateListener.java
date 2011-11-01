/**
 * 
 */
package org.caleydo.core.util.mapping.color;

import org.caleydo.core.event.IListenerOwner;

/**
 * Interface
 * 
 * @author Alexander Lex
 */
public interface IColorMappingUpdateListener
	extends IListenerOwner {

	/** see {@link UpdateColorMappingEvent} S*/
	public void updateColorMapping();

}
