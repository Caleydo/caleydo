/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.data;

import java.util.Vector;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.data.collection.IVirtualArray;

/**
 * Manage all IVirtualArray's.
 * 
 * Note: the ISelectionManager must register itself to the singelton prometheus.app.SingeltonManager
 * 
 * @author Michael Kalkusch
 *
 */
public interface IVirtualArrayManager
extends IGeneralManager
{
	
	public IVirtualArray createSelection( final ManagerObjectType useSelectionType );
	
	public boolean deleteSelection( IVirtualArray deleteSelection );
	
	public boolean deleteSelection( final int iItemId  );
	
	public IVirtualArray getItemSelection( final int iItemId );
	
	public IVirtualArray[] getAllSelectionItems();
	
	public Vector<IVirtualArray> getAllSelectionItemsVector();
	
	//public ManagerObjectType getManagerType();
	
}
