/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */

package cerberus.data;

import java.util.Collection;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec2f;

import cerberus.data.DimensionGroupIterface;

/*
 * Iterface for all data items.
 */
public interface DataItemInterface {

	// ----  get  ----
	
	/// return layer id of data item
	int getLayerId();
	
	/// return group id of data item
	int getGroupId();
	
	/// return color id of data item
	int getColorId();
	
	
	/// return collection of one "group" bound to iDimensionGroupId.
	DimensionGroupIterface getDimensionGroup( int iDimensionGroupId );
	
	/// return collection of "data values".
	Collection getDataValueList( );
	
	
	/// get data point position in 3D
	Vec3f getPoint();
	
	/// get data point position in 2D
	Vec2f getPoint2d();
	
	
	// ----  set ----
	
	/// set layer id of data item
	void setLayerId( int iSetLayerId );
	
	/// set group id of data item
	void setGroupId( int iSetGroupId );
	
	/// set color id of data item
	void setColorId( int iSetColorId );
	
	
	/// set collection of one "group" bound to iDimensionGroupId.
	boolean setDimensionGroup( int iSetDimensionGroupId,
			DimensionGroupIterface setDimensionGroup );
	
	/// set collection of "data values".
	boolean setDataValueList( Collection setDataValueCollection );
	
	/// set data point position in 3D
	void setPoint( Vec3f SetPoint );
	
	/// set data point position in 2D
	void setPoint2d( Vec2f SetPoint );
	
	// ----  general stuff  ----
	
	// debug info per item.
	String toString();
	
}
