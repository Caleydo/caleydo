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
//import cerberus.data.collection.ISet;
//import prometheus.data.collection.BaseManagerItemInterface;
import cerberus.data.xml.IMementoItemXML;
//import cerberus.net.dwt.DNetEventComponentInterface;

/**
 * Defines a set containing of selections and storage
 * 
 * @author Michael Kalkusch
 *
 */
public interface IViewCanvas
	extends 
	IUniqueManagedObject, 
	IMementoItemXML
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
//	 * Adds a IVirtualArray to a specific dimension.
//	 * Note, that addSelection() can not overwrite existing references to other IVirtualArray
//	 * 
//	 * @param addSelection
//	 * @param iAtDimension range [0.. getDimensionSize()-1 ]
//	 * @return TRUE if adding was successful
//	 */
//	public boolean setDataSet( final ISet addSet, 
//			final int iAtDimension );
//	
//	/**
//	 * Removes a selection bound to a dimension.
//	 * 
//	 * @param removeSelection IVirtualArray to be removed
//	 * @param iFromDimension address which dimension removeSelection shall be removed from, range [0.. getDimensionSize()-1 ]
//	 * @return TRUE if removeSelection was removed from dimension iFromDimension
//	 */
//	public boolean removeDataSet( final ISet removeSelection, 
//			final int iFromDimension );
//	
//	/**
//	 * Tests, if testSelection is in a specific dimension addressed by iAtDimension.
//	 * 
//	 * @param testSelection IVirtualArray to search for
//	 * @param iAtDimension address a dimension
//	 * @return TRUE if the testSelection is used for dimension iAtDimension
//	 */
//	public boolean hasDataSet( final ISet testSelection, 
//			final int iAtDimension );
//	
//	/**
//	 * Tests if testSelection is in any Selecions of this set. 
//	 * 
//	 * @param testSelection in any dimension of this set
//	 * @return TRUE if testSelection was in any dimension
//	 */
//	public boolean hasDataSetAnyDimension( final ISet testSelection );
//	
//
//	
//	public ISet getDataSet( final int iAtDimension );
	
}
