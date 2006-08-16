/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager;

import java.util.Vector;

import cerberus.manager.type.ManagerObjectType;

import cerberus.data.collection.ISelection;

/**
 * Manage all ISelection's.
 * 
 * Note: the ISelectionManager must register itself to the singelton prometheus.app.SingeltonManager
 * 
 * @author Michael Kalkusch
 *
 */
public interface ISelectionManager
extends IGeneralManager
{
	
	public ISelection createSelection( final ManagerObjectType useSelectionType );
	
	public boolean deleteSelection( ISelection deleteSelection );
	
	public boolean deleteSelection( final int iItemId  );
	
	public ISelection getItemSelection( final int iItemId );
	
	public ISelection[] getAllSelectionItems();
	
	public Vector<ISelection> getAllSelectionItemsVector();
	
	//public ManagerObjectType getManagerType();
	
}
