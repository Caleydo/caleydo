/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager;

import java.util.Iterator;



import prometheus.net.dwt.base.DGuiComponentType;
import prometheus.net.dwt.DNetEventComponentInterface;

/**
 * Manges all Set's.
 * 
 * Note: the SetManager must register itself to the singelton prometheus.app.SingeltonManager
 * 
 * @author Michael Kalkusch
 *
 */
public interface DComponentManager
extends GeneralManager
{
	
	public DNetEventComponentInterface createSet( final DGuiComponentType useSetType );
	
	public boolean deleteSet( final int iNetEventId );
	
	public DNetEventComponentInterface getItemSet( final int iNetEventId );
	
	public Iterator<DNetEventComponentInterface> getIteratorComponents();
	
	//public BaseManagerType getManagerType();
	
}
