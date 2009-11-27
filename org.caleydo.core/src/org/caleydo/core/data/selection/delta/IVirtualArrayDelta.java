package org.caleydo.core.data.selection.delta;

import org.caleydo.core.data.selection.EVAType;

/**
 * Interface for a virtual array delta
 * 
 * @author Alexander Lex
 */
public interface IVirtualArrayDelta
	extends IDelta<VADeltaItem> {

	/**
	 * Returns the type of the virtual array as specified in {@link EVAType}
	 * 
	 * @return
	 */
	public EVAType getVAType();

	/**
	 * Set the type of the VA
	 * 
	 * @param vaType
	 */
	public void setVAType(EVAType vaType);

}
