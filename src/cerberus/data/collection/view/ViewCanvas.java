/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.view;

import java.awt.Graphics;

import cerberus.data.IUniqueManagedObject;
import cerberus.data.collection.Set;
//import prometheus.data.collection.BaseManagerItemInterface;
import cerberus.data.xml.MementoItemXML;
import cerberus.net.dwt.DNetEventComponentInterface;

/**
 * Defines a set containing of selections and storage
 * 
 * @author Michael Kalkusch
 *
 */
public interface ViewCanvas
	extends 
	IUniqueManagedObject, 
	MementoItemXML,
	DNetEventComponentInterface
{
	
	/**
	 * @see javax.swing.JComponent#paintDComponent(java.awt.Graphics)
	 * 
	 * @param g canvas to draw to
	 */
	public void paintDComponent(Graphics g);
	
	/**
	 * Update state.
	 *
	 */
	public void updateState();
	
//	/**
//	 * Adds a Selection to a specific dimension.
//	 * Note, that addSelection() can not overwrite existing references to other Selection
//	 * 
//	 * @param addSelection
//	 * @param iAtDimension range [0.. getDimensionSize()-1 ]
//	 * @return TRUE if adding was successful
//	 */
//	public boolean setDataSet( final Set addSet, 
//			final int iAtDimension );
//	
//	/**
//	 * Removes a selection bound to a dimension.
//	 * 
//	 * @param removeSelection Selection to be removed
//	 * @param iFromDimension address which dimension removeSelection shall be removed from, range [0.. getDimensionSize()-1 ]
//	 * @return TRUE if removeSelection was removed from dimension iFromDimension
//	 */
//	public boolean removeDataSet( final Set removeSelection, 
//			final int iFromDimension );
//	
//	/**
//	 * Tests, if testSelection is in a specific dimension addressed by iAtDimension.
//	 * 
//	 * @param testSelection Selection to search for
//	 * @param iAtDimension address a dimension
//	 * @return TRUE if the testSelection is used for dimension iAtDimension
//	 */
//	public boolean hasDataSet( final Set testSelection, 
//			final int iAtDimension );
//	
//	/**
//	 * Tests if testSelection is in any Selecions of this set. 
//	 * 
//	 * @param testSelection in any dimension of this set
//	 * @return TRUE if testSelection was in any dimension
//	 */
//	public boolean hasDataSetAnyDimension( final Set testSelection );
//	
//
//	
//	public Set getDataSet( final int iAtDimension );
	
}
