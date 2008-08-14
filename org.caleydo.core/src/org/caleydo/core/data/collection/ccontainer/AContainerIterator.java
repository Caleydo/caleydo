package org.caleydo.core.data.collection.ccontainer;

import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.VAIterator;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Abstract container iterator for all ICContainers. Supports virtual arrays.
 * 
 * @author Alexander Lex
 * 
 */
public class AContainerIterator
	implements ICContainerIterator
{
	protected IVirtualArray virtualArray = null;
	protected VAIterator vaIterator = null;
	protected int iIndex = 0;
	protected int iSize = 0;

	@Override
	public boolean hasNext()
	{
		if (virtualArray == null)
		{
			if (iIndex < iSize - 1)
				return true;
			else
				return false;
		}
		else
			return vaIterator.hasNext();
	}

	@Override
	public void remove()
	{
		if (virtualArray == null)
			throw new CaleydoRuntimeException(
					"Remove is only defined if a virtual array is enabled, which is currently not the case",
					CaleydoRuntimeExceptionType.DATAHANDLING);
		else
			vaIterator.remove();
	}
}
