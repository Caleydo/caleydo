package org.caleydo.core.manager.data;

import java.util.Vector;

import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.manager.IManager;
import org.caleydo.core.manager.type.ManagerObjectType;

/**
 * Manage all IVirtualArray's.
 * 
 * @author Michael Kalkusch
 *
 */
public interface IVirtualArrayManager
extends IManager
{
	public IVirtualArray createVirtualArray( final ManagerObjectType useSelectionType );
	
	public boolean deleteVirtualArray( IVirtualArray deleteSelection );
	
	public boolean deleteVirtualArray( final int iItemId  );
	
	public IVirtualArray getItemVirtualArray( final int iItemId );
	
	public IVirtualArray[] getAllVirtualArrayItems();
	
	public Vector<IVirtualArray> getAllVirtualArrayItemsVector();
}
