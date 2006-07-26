/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection;

import cerberus.data.UniqueManagedInterface;
import cerberus.data.collection.StorageType;
import cerberus.data.collection.CollectionInterface;
import cerberus.data.collection.thread.CollectionThreadObject;
import cerberus.data.xml.MementoNetEventXML;

/**
 * @author Michael Kalkusch
 *
 */
public interface Storage 
	extends CollectionInterface,
	MementoNetEventXML,
	CollectionThreadObject
{

	/**
	 * Adds a new container with the storage type defined in setStorageType.
	 * Returns the index of the new container.
	 * 
	 * Note: allocate() must be called to make the change permanent.
	 * 
	 * @param setStorageType define the new storage type
	 * @return new index of the storage
	 */
	public int addStorageTypePerContainer( StorageType setStorageType );
	
	/**
	 * Sets the storage type for a container.
	 * Note: allocate() must be called to make the change permanent.
	 * 
	 * Some implementations use only one contaier, while others use several
	 * container.
	 * 
	 * @param setStorageType new storage type for the container.
	 */	
	public void setStorageTypePerContainer( StorageType setStorageType,
			final int iAtContainerPosition );

	
	/**
	 * Get the storage type of one container 
	 * 
	 * @param iAtContainerPosition address the container
	 * @return storage type of the container.
	 */
	public StorageType getStorageTypePerContainer( 
			final int iAtContainerPosition );
	
	/**
	 * Removes a container immediately.
	 * 
	 * @param iAtContainerPosition 
	 */
	public void removeStorage( final int iAtContainerPosition  );
	
	/**
	 * Allocates all arrays.
	 *  If the size of the array was change the content of the array is lost.
	 * 
	 * @return
	 */
	public boolean allocate();
	
	/**
	 * Set size of all containers.
	 * Note: allocate() must be called to make the change permanent.
	 * 
	 * @param size
	 */
	public void setAllSize( final int [] size );
	
	/**
	 * Sets size for one container.
	 * Note: allocate() must be called to make the change permanent.
	 * 
	 * @param iAtContainerPosition address a container. range [0.. getNumberArrays()-1 ]
	 * @param iSetSize new size of the container (array)
	 * @return TRUE if all was fine
	 */
	public boolean setSize( final int iAtContainerPosition, final int iSetSize);
	
	/**
	 * Get size for one container.
	 * 
	 * @param iAtContainerPosition address a container. range [0.. getNumberArrays()-1 ]
	 * @return number of allocated elements in container at position iAtContainerPosition
	 */
	public int getSize( final int iAtContainerPosition );
	
	/**
	 * Get each size of each container (array)
	 * @return
	 */
	public int[] getAllSize();
	
	/**
	 * Return the number of hosted containers (arrays).
	 *  
	 * @return number of container (arrays) hosted
	 */
	public int getNumberArrays();
	
	//-----------------------------------
	
	public int[] getArrayInt();
	
	public void setArrayInt( int[] set );
	
	public void setArrayFloat( float[] set );
	
	public void setArrayString( String[] set );
	
	public int[][] getArray2DInt();
	
	public float[] getArrayFloat();
	
	public float[][] getArray2DFloat();
	
	public double[] getArrayDouble();
	
	public double[][] getArray2DDouble();
	
	public String[] getArrayString();
	
	public String[][] getArray2DString();
	
	public Object[] getArrayObject();
	
	public Object[][] getArray2DObject();
	
	public boolean[] getArrayBoolean();

	public boolean[][] getArray2DBoolean();
	
}
