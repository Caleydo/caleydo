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

import cerberus.data.collection.Selection;
import cerberus.data.xml.MementoXML;

/**
 * Manage all Selection's.
 * 
 * Note: the SelectionManager must register itself to the singelton prometheus.app.SingeltonManager
 * 
 * @author Michael Kalkusch
 *
 */
public interface SelectionManager
extends GeneralManager
{
	
	public MementoXML createSelection( final ManagerObjectType useSelectionType );
	
	public boolean deleteSelection( Selection deleteSelection );
	
	public boolean deleteSelection( final int iItemId  );
	
	public Selection getItemSelection( final int iItemId );
	
	public Selection[] getAllSelectionItems();
	
	public Vector<Selection> getAllSelectionItemsVector();
	
	//public ManagerObjectType getManagerType();
	
}
