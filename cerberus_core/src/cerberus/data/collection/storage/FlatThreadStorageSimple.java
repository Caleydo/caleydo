/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.storage;

import java.lang.NullPointerException;
import java.util.Hashtable;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.parser.xml.sax.ISaxParserHandler;

//import org.xml.sax.InputSource;

import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
//import cerberus.data.manager.DComponentManager;
import cerberus.data.xml.IMementoNetEventXML;
import cerberus.data.collection.parser.CollectionFlatStorageSaxParserHandler;
import cerberus.data.collection.thread.impl.ACollectionThreadItem;
import cerberus.data.collection.thread.lock.ICollectionLock;

/**
 * @author Michael Kalkusch
 *
 */
public class FlatThreadStorageSimple 
extends ACollectionThreadItem 
implements IStorage, IMementoNetEventXML, ICollectionLock
{

	private static final int iNumerOfUsedArrays = 6;
	
	/**
	 * One dimensional INT array.
	 */
	protected int [] dataInt = null;
	
	/**
	 * One dimensional FLOAT array.
	 */
	protected float [] dataFloat = null;
	
	/**
	 * One dimensional STRING array.
	 */
	protected String [] dataString = null;
	
	/**
	 * One dimensional DOUBLE array.
	 */
	protected double [] dataDouble = null;
	
	/**
	 * One dimensional BOOLEAN array.
	 */
	protected boolean [] dataBoolean = null;
	
	protected Object [] dataObject = null;
	
	private int [] setSizeForAllocation = null;
	
//	/**
//	 * Defines type per index.
//	 */
//	private StorageType[] typePerContainer = 
//		  { StorageType.INT, 
//			StorageType.FLOAT,
//			StorageType.STRING,
//			StorageType.BOOLEAN,
//			StorageType.DOUBLE,
//			StorageType.OBJECT };
	
//	private final static int INT = 0;  
//	private final static int FLOAT = 1;
//	private final static int STRING = 2;  
//	private final static int BOOLEAN = 3; 
//	private final static int DOUBLE = 4; 
//	private final static int OBJECT = 5;
	
	
	/**
	 * @param iSetCollectionId
	 * @param setGeneralManager
	 */
	public FlatThreadStorageSimple( final int iSetCollectionId, final IGeneralManager setGeneralManager,
			final ICollectionLock setCollectionLock ) {
		super(iSetCollectionId, setGeneralManager, setCollectionLock);
		
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#addStorageTypePerContainer(cerberus.data.collection.StorageType)
	 */
	public int addStorageTypePerContainer(StorageType setStorageType) {
		
		assert false: "FlatStorageSimple.addStorageTypePerContainer()  not supported.";
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#setStorageTypePerContainer(cerberus.data.collection.StorageType, int)
	 */
	public void setStorageTypePerContainer(StorageType setStorageType,
			int iAtContainerPosition) {

		assert false: "FlatStorageSimple.setStorageTypePerContainer() not supported.";
	}

//	/* (non-Javadoc)
//	 * @see cerberus.data.collection.IStorage#getStorageTypePerContainer(int)
//	 */
//	public StorageType getStorageTypePerContainer( StorageType type) {
//		try {
//			return typePerContainer[ iAtContainerPosition ];
//		} 
//		catch (ArrayIndexOutOfBoundsException ae) {
//			return StorageType.NONE;
//		}
//	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#removeStorage(int)
	 */
	public void removeStorage(StorageType useStorageType) {

		switch ( useStorageType ) {
		
		case INT:
			dataInt = null;
			return;
			
		case FLOAT:
			dataFloat = null;
			return;
			
		case STRING:
			dataString = null;
			return;
			
		case BOOLEAN:
			dataBoolean = null;
			return;
			
		case DOUBLE:
			dataDouble = null;
			return;
		
		case OBJECT:
			dataObject = null;
			return;
			
		default:
			throw new RuntimeException("removeStorage() with unknown type.");
		} // end switch

	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#allocate()
	 */
	public boolean allocate() {

		if ( setSizeForAllocation == null ) {
			// no new tasks...
			return true;
		}
		
		for ( int i=0; i < setSizeForAllocation.length; i++ ){
			allocatePerIndex( StorageType.getTypeByIndex(i) , setSizeForAllocation[i] );
		}
		
		return true;
	}

//	/* (non-Javadoc)
//	 * @see cerberus.data.collection.IStorage#setAllSize(int[])
//	 */
//	public void setAllSize(int[] size) {
//	
//		final int iSizeIn = size.length;
//		
//		if ( this.setSizeForAllocation == null ) {
//			setSizeForAllocation = new int [ typePerContainer.length ];
//		}
//		
//		for( int i=0; (i< setSizeForAllocation.length) && (i < iSizeIn) ; i++ ) {
//			setSizeForAllocation[i] = size[i];
//		}
//	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#setSize(int, int)
	 */
	public void setSize( final StorageType byStorageType, final int iSetSize) {
		
		if ( iSetSize < 0 ) {
			assert false: "setSize()  iSetSize < 0 !";
		
			return;
		}
		
		switch ( byStorageType ) {
		case INT:
			if  (dataInt == null) 
			{
				dataInt = new int [iSetSize];
				return;
			}
			if ( dataInt.length != iSetSize )
			{
				dataInt = new int [iSetSize];
			}
			return;
			
		case FLOAT:
			if ( dataFloat == null )
			{
				dataFloat = new float [iSetSize];
				return;
			}
			if ( dataFloat.length != iSetSize )
			{
				dataFloat = new float [iSetSize];
			}
			return;
			
		case STRING:
			if ( dataString == null )
			{
				dataString = new String [iSetSize];
				return;
			}
			if ( dataString.length != iSetSize )
			{
				dataString = new String [iSetSize];
			}
			return;
			
		case BOOLEAN:
			if ( dataBoolean == null )
			{
				dataBoolean = new boolean [iSetSize];
				return;
			}
			if ( dataBoolean.length != iSetSize )
			{
				dataBoolean = new boolean [iSetSize];
			}
			return;
			
		case DOUBLE:
			if ( dataDouble == null )
			{
				dataDouble = new double [iSetSize];
				return;
			}
			if ( dataDouble.length != iSetSize )
			{
				dataDouble = new double [iSetSize];
			}
			return;
		
		case OBJECT:
			if ( dataObject == null )
			{
				dataObject = new Object [iSetSize];
				return;
			}
			if ( dataObject.length != iSetSize )
			{
				dataObject = new Object [iSetSize];
			}
			return;
		
		default:
			throw new RuntimeException("getSize() with unknown index.");
		} // end switch
	
	}

	
	public int getSize( final StorageType type ) {
		
		switch ( type ) {
		case INT:
			if ( dataInt == null ) return 0;
			return dataInt.length;
			
		case FLOAT:
			if ( dataFloat == null ) return 0;
			return dataFloat.length;
			
		case STRING:
			if ( dataString == null ) return 0;
			return dataString.length;
			
		case BOOLEAN:
			if ( dataBoolean == null ) return 0;
			return dataBoolean.length;
			
		case DOUBLE:
			if ( dataDouble == null ) return 0;
			return dataDouble.length;
		
		case OBJECT:
			if ( dataObject == null ) return 0;
			return dataObject.length;
		
		default:
			throw new RuntimeException("getSize() with unknown index.");
		} // end switch
		
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#getSize(int)
	 */
	private boolean allocatePerIndex( final StorageType type, final int iAllocationSize) {

		if ( iAllocationSize <= 0 ) {
			assert false: "allocatePerIndex() with negative size!";
		
			return false;
		}
		
		switch ( type ) {
		
		case INT:		
			if ( dataInt == null ) {
				dataInt = new int[iAllocationSize];
				return true;
			}
			
			if ( dataInt.length != iAllocationSize ) {
				dataInt = null;
				dataInt = new int[iAllocationSize];
			}
			return true;
			
		case FLOAT:
			if ( dataFloat == null ) {
				dataFloat = new float[iAllocationSize];
				return true;
			}
			
			if ( dataFloat.length != iAllocationSize ) {
				dataFloat = null;
				dataFloat = new float[iAllocationSize];
			}
			return true;
			
		case STRING:
			if ( dataString == null ) {
				dataString = new String[iAllocationSize];
				return true;
			}
			
			if ( dataString.length != iAllocationSize ) {
				dataString = null;
				dataString = new String[iAllocationSize];
			}
			return true;
			
		case BOOLEAN:
			if ( dataBoolean == null ) {
				dataBoolean = new boolean[iAllocationSize];
				return true;
			}
			
			if ( dataBoolean.length != iAllocationSize ) {
				dataBoolean = null;
				dataBoolean = new boolean[iAllocationSize];
			}
			return true;
			
		case DOUBLE:
			if ( dataDouble == null ) {
				dataDouble = new double[iAllocationSize];
				return true;
			}
			
			if ( dataDouble.length != iAllocationSize ) {
				dataDouble = null;
				dataDouble = new double[iAllocationSize];
			}
			return true;
			
		case OBJECT:
			if ( dataObject == null ) {
				dataObject = new Object[iAllocationSize];
				return true;
			}
			
			if ( dataObject.length != iAllocationSize ) {
				dataObject = null;
				dataObject = new Object[iAllocationSize];
			}
			return true;
		
		default:
			throw new RuntimeException("allocatePerIndex() with unsupported type [" + 
					type.toString() +"]");
		
		} // end switch
	}
	
	/**
	 * ISet size of all containers.
	 * Note: allocate() must be called to make the change permanent.
	 * 
	 * @param size
	 */
	public Hashtable <StorageType,Integer> getAllSize() {
		Hashtable <StorageType,Integer> resultHasttable = 
			new Hashtable <StorageType,Integer> (iNumerOfUsedArrays);
		
		for ( int i=0; i < iNumerOfUsedArrays; i++ )
		{			
			StorageType help = 
				StorageType.getTypeByIndex( i );
			
			resultHasttable.put( help, this.getSize(help) );
		}
		
		return resultHasttable;
	}

	public int getMaximumLengthOfAllArrays() {
		
		int iMaximumSizeOfAllArrays = 0;
		
		if ( getReadToken() )
		{
			for ( int i=0; i < iNumerOfUsedArrays; i++ )
			{			
				int iCurrentArraySize = 
					this.getSize( StorageType.getTypeByIndex( i ) );
				
				if ( iMaximumSizeOfAllArrays < iCurrentArraySize ) 
				{
					iMaximumSizeOfAllArrays = iCurrentArraySize;
				}
			} // for ( int i=0; i < iNumerOfUsedArrays; i++ )
		} // if ( getReadToken() )
		
		returnReadToken();
		
		return iMaximumSizeOfAllArrays;
	}
	
	
//	/* (non-Javadoc)
//	 * @see cerberus.data.collection.IStorage#getAllSize()
//	 */
//	public int[] getAllSize() {
//		final int iSize =  typePerContainer.length;
//		
//		int [] resultArray = new int[ iSize ];
//
//		for ( int i=0; i < iSize; i++ ) {
//			resultArray[i] = getSize( StorageType.getTypeByIndex(i) );
//		}
//		
//		return resultArray;
//	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#getNumberArrays()
	 */
	public final int getNumberArrays() {
		return FlatThreadStorageSimple.iNumerOfUsedArrays;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#getArrayInt()
	 */
	public int[] getArrayInt() {
		return dataInt;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#getArray2DInt()
	 */
	public int[][] getArray2DInt() {
		assert false: "not supported by FlatStorageSimple";
	
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#getArrayFloat()
	 */
	public float[] getArrayFloat() {
		return dataFloat;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#getArray2DFloat()
	 */
	public float[][] getArray2DFloat() {
		assert false: "not supported by FlatStorageSimple";
	
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#getArrayDouble()
	 */
	public double[] getArrayDouble() {
		return dataDouble;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#getArray2DDouble()
	 */
	public double[][] getArray2DDouble() {
		assert false: "not supported by FlatStorageSimple";
	
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#getArrayString()
	 */
	public String[] getArrayString() {
		return dataString;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#getArray2DString()
	 */
	public String[][] getArray2DString() {
		assert false: "not supported by FlatStorageSimple";
	
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#getArrayObject()
	 */
	public Object[] getArrayObject() {
		assert false: "not supported by FlatStorageSimple";
	
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#getArray2DObject()
	 */
	public Object[][] getArray2DObject() {
		assert false: "not supported by FlatStorageSimple";
	
		return null;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#getArrayBoolean()
	 */
	public boolean[] getArrayBoolean() {
		return dataBoolean;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.IStorage#getArray2DObject()
	 */
	public boolean[][] getArray2DBoolean() {
		assert false: "not supported by FlatStorageSimple";
	
		return null;
	}

	/**
	 * Creates a memento containing all infomation for this component.
	 * 
	 * @return String containing a XML definition for this component
	 */
	public String createMementoXMLperObject() {
		String result = "<DataComponentSet data_Id=\"";
		
		result += getId() + "\" type=\"" +
		ManagerObjectType.STORAGE_FLAT.name() + "\">\n";
		
		/**
		 * Integer
		 */
		result += "<DNetEventViewItemDetails type=\"Integer\" >\n";
					
		result += "</DNetEventViewItemDetails>\n";
		
		/**
		 * Float
		 */
		result += "<DNetEventViewItemDetails type=\"Float\" >\n";
		
		result += "</DNetEventViewItemDetails>\n";
			
		result += "</DataComponentSet>\n";
		
		return result;
	}
	
	
	/**
	 * Defines a callback, that can be triggert by the parser once a cetain tag is reached.
	 * 
	 * Note: this is used to notify the end of a tag in most cases, 
	 * to be able to create an object with the information provided.
	 * 
	 * This callback is triggert via the reference passed to the parser in the constructor.
	 * 
	 * @param tag_causes_callback Tag that cased the callback
	 */
	public void callbackForParser( final ManagerObjectType type,
			final String tag_causes_callback,
			final String details,
			final ISaxParserHandler refSaxHandler) {
		
		if ( tag_causes_callback.equalsIgnoreCase( ManagerObjectType.STORAGE_FLAT.name() )) {
			
			setMementoXML_usingHandler( refSaxHandler );
		}
		
		System.out.println("FlatThreadStorageSimple.callbackForParser() STORAGE CALLBACK!");
	}
	
	/**
	 * Restore state of object by update data from SaxHandler
	 * 
	 * @param refSaxHandler reference to SaxHandler
	 * @return TRUE if the provided handler provided same Id as object.
	 */
	public boolean setMementoXML_usingHandler( final ISaxParserHandler refSaxHandler ) {
		
		try {
			CollectionFlatStorageSaxParserHandler handler =
				(CollectionFlatStorageSaxParserHandler) refSaxHandler;
				
			setId( handler.getXML_DataComponent_Id() );
			
			dataInt = handler.getDataInteger();				
			dataBoolean = handler.getDataBoolean();
			dataDouble = handler.getDataDouble();
			dataFloat = handler.getDataFloat();
			dataString = handler.getDataString();
			//handler.getDataLong();
			//handler.getDataShort();
			
			getManager().registerItem( this, 
					handler.getXML_DataComponent_Id(),
					ManagerObjectType.STORAGE_FLAT );
			
			return true;			
		}
		catch ( NullPointerException npe ) {
			return false;
		}
	}
	
	
	/**
	 * Creates a memento containing all infomation for this component.
	 * 
	 * @return String containing a XML definition for this component
	 */
	public String createMementoXML() {
		return createMementoXMLperObject();
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.UniqueManagedInterface#getBaseType()
	 */
	public ManagerObjectType getBaseType() {
		return ManagerObjectType.STORAGE_FLAT;
	}
	
	public void setArrayInt( int[] set ) {
		this.dataInt = set;
	}
	
	public void setArrayFloat( float[] set ) {
		this.dataFloat = set;
	}
	
	public void setArrayString( String[] set ) {
		this.dataString = set;
	}

	
	/**
	 * @see cerberus.data.collection.ICollection#getCacheId()
	 */
	public int getCacheId() {
		return this.iCacheId;
	}

	
	public final String toString() {
		String infoString = "(" + Integer.toString(getId()) + " " +
			getLabel() + ": B=";
		
		if ( dataBoolean == null) {
			infoString += "-";
		}
		else {
			infoString += "=" + Integer.toString(dataBoolean.length);
		}
		
		infoString += " I";
		if ( dataInt == null) {
			infoString += "-";
		}
		else {
			infoString += "=" + Integer.toString(dataInt.length);
		}
		
		infoString += " F=";
		if ( dataFloat == null) {
			infoString += "-";
		}
		else {
			infoString += "=" + Integer.toString(dataFloat.length);
		}
		
		infoString += " D";
		if ( dataDouble == null) {
			infoString += "-";
		}
		else {
			infoString += "=" + Integer.toString(dataDouble.length);
		}
		
		infoString += " S";
		if ( dataFloat == null) {
			infoString += "-";
		}
		else {
			infoString += "=" + Integer.toString(dataString.length);
		}
		
		infoString += " O";
		if ( dataObject == null) {
			infoString += "-";
		}
		else {
			infoString += "=" + Integer.toString(dataObject.length);
		}
		infoString += ")";
		
		return infoString;
	}
}
