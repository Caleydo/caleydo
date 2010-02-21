package org.caleydo.core.data.selection.delta;

import org.caleydo.core.data.selection.IVAType;

/**
 * Interface for a virtual array delta
 * 
 * @author Alexander Lex
 */
public interface IVirtualArrayDelta <T extends IVAType>
	extends  {

	/**
	 * Returns the type of the virtual array as specified in {@link VAType}
	 * 
	 * @return
	 */
	public T getVAType();

	/**
	 * Set the type of the VA
	 * 
	 * @param vaType
	 */
	public void setVAType(T vaType);
	
	public IVirtualArrayDelta<T> getInstance();

}
