package org.caleydo.core.command.base;

import org.caleydo.core.command.ECommandType;

/**
 * This abstract command provides functionality to return
 * the created object.
 * 
 * @author Marc Streit
 * @param <T> Template type holding the created object.
 */
public abstract class ACmdCreational<T>
	extends ACmdExternalAttributes
{
	protected T createdObject;
	
	/**
	 * Constructor
	 * 
	 */
	protected ACmdCreational(final ECommandType cmdType)
	{
		super(cmdType);
	}
	
	public T getCreatedObject() 
	{
		return createdObject;
	}
}
