package org.caleydo.core.data.collection.storage;

import java.util.Hashtable;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.data.filter.CmdDataFilterMinMax;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.StorageType;
import org.caleydo.core.data.collection.parser.CollectionFlatStorageSaxParserHandler;
import org.caleydo.core.data.collection.thread.impl.ACollectionThreadItem;
import org.caleydo.core.data.collection.thread.lock.ICollectionLock;
import org.caleydo.core.data.xml.IMementoNetEventXML;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.xml.sax.ISaxParserHandler;

/**
 * @author Michael Kalkusch
 * @author Alexander Lex
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
	
	private int minInt = java.lang.Integer.MAX_VALUE;
	private int maxInt = java.lang.Integer.MIN_VALUE;
	
	/**
	 * One dimensional FLOAT array.
	 */
	protected float [] dataFloat = null;
	
	private float minFloat = java.lang.Float.POSITIVE_INFINITY;
	private float maxFloat = java.lang.Float.NEGATIVE_INFINITY;
	
	/**
	 * One dimensional DOUBLE array.
	 */
	protected double [] dataDouble = null;
	
	private double minDouble = java.lang.Double.POSITIVE_INFINITY;
	private double maxDouble = java.lang.Double.NEGATIVE_INFINITY;
		
	/**
	 * One dimensional STRING array.
	 */
	protected String [] dataString = null;
		
	/**
	 * One dimensional BOOLEAN array.
	 */
	protected boolean [] dataBoolean = null;
	
	/**
	 * One dimensional Object array.
	 */
	
	protected Object [] dataObject = null;
	
	private int [] setSizeForAllocation = null;
	
	/**
	 * TODO: Doku
	 * 
	 * @param iSetCollectionId
	 * @param setGeneralManager
	 * @param setCollectionLock
	 */
	public FlatThreadStorageSimple( final int iSetCollectionId, final IGeneralManager setGeneralManager,
			final ICollectionLock setCollectionLock ) {
		super(iSetCollectionId, setGeneralManager, setCollectionLock);
		
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#addStorageTypePerContainer(org.caleydo.core.data.collection.StorageType)
	 */
	public int addStorageTypePerContainer(StorageType setStorageType) {
		
		assert false: "FlatStorageSimple.addStorageTypePerContainer()  not supported.";
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#setStorageTypePerContainer(org.caleydo.core.data.collection.StorageType, int)
	 */
	public void setStorageTypePerContainer(StorageType setStorageType,
			int iAtContainerPosition) {

		assert false: "FlatStorageSimple.setStorageTypePerContainer() not supported.";
	}

//	/* (non-Javadoc)
//	 * @see org.caleydo.core.data.collection.IStorage#getStorageTypePerContainer(int)
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
	 * @see org.caleydo.core.data.collection.IStorage#removeStorage(int)
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
	 * @see org.caleydo.core.data.collection.IStorage#allocate()
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
//	 * @see org.caleydo.core.data.collection.IStorage#setAllSize(int[])
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
	 * @see org.caleydo.core.data.collection.IStorage#setSize(int, int)
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
	 * @see org.caleydo.core.data.collection.IStorage#getSize(int)
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
//	 * @see org.caleydo.core.data.collection.IStorage#getAllSize()
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
	 * @see org.caleydo.core.data.collection.IStorage#getNumberArrays()
	 */
	public final int getNumberArrays() {
		return FlatThreadStorageSimple.iNumerOfUsedArrays;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getArrayInt()
	 */
	public int[] getArrayInt() {
		return dataInt;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getMinInt()
	 */
	public int getMinInt() {

		
		if (minInt == java.lang.Integer.MAX_VALUE)
		{
			CmdDataFilterMinMax createdCmd = (CmdDataFilterMinMax) generalManager
				.getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MIN_MAX);
			
			createdCmd.setAttributes(this, StorageType.INT);
			createdCmd.doCommand();
			minInt = createdCmd.getIMinValue();
		}
		return minInt;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getMaxInt()
	 */
	public int getMaxInt() {

		// TODO Auto-generated method stub
		if (maxInt == java.lang.Integer.MIN_VALUE)
		{
			CmdDataFilterMinMax createdCmd = (CmdDataFilterMinMax) generalManager
		    	.getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MIN_MAX);
			
			createdCmd.setAttributes(this, StorageType.INT);
			createdCmd.doCommand();
			maxInt = createdCmd.getIMaxValue();
		}
		return maxInt;
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getArray2DInt()
	 */
	public int[][] getArray2DInt() {
		assert false: "not supported by FlatStorageSimple";
	
		return null;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getArrayFloat()
	 */
	public float[] getArrayFloat() {
		return dataFloat;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getMinFloat()
	 */
	public float getMinFloat() 
	{

		if (minFloat == java.lang.Float.POSITIVE_INFINITY)
		{
			CmdDataFilterMinMax createdCmd = (CmdDataFilterMinMax) generalManager
		    	.getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MIN_MAX);
			
			createdCmd.setAttributes(this, StorageType.FLOAT);
			createdCmd.doCommand();
			minFloat = createdCmd.getFMinValue();
		}
		return minFloat;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getMaxFloat()
	 */
	public float getMaxFloat() 
	{	
		if (maxFloat == java.lang.Float.NEGATIVE_INFINITY)
		{
			CmdDataFilterMinMax createdCmd = (CmdDataFilterMinMax) generalManager
		    	.getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MIN_MAX);
			
			createdCmd.setAttributes(this, StorageType.FLOAT);
			createdCmd.doCommand();
			maxFloat = createdCmd.getFMaxValue();
		}
		return maxFloat;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getArray2DFloat()
	 */
	public float[][] getArray2DFloat() {
		assert false: "not supported by FlatStorageSimple";
	
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#setArrayDouble(double[])
	 */
	public void setArrayDouble(double[] set) {
		dataDouble = set;	
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getArrayDouble()
	 */
	public double[] getArrayDouble() 
	{
		return dataDouble;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getMinDouble()
	 */
	public double getMinDouble() 
	{

		if (minDouble == java.lang.Double.POSITIVE_INFINITY)
		{
			CmdDataFilterMinMax createdCmd = (CmdDataFilterMinMax) generalManager
		    	.getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MIN_MAX);
			
			createdCmd.setAttributes(this, StorageType.DOUBLE);
			createdCmd.doCommand();
			minDouble = createdCmd.getDMinValue();
		}
		return minDouble;
	
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getMaxDouble()
	 */
	public double getMaxDouble() 
	{
		if (maxDouble == java.lang.Double.NEGATIVE_INFINITY)
		{
			CmdDataFilterMinMax createdCmd = (CmdDataFilterMinMax) generalManager
		    	.getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MIN_MAX);
			
			createdCmd.setAttributes(this, StorageType.DOUBLE);
			createdCmd.doCommand();
			maxDouble = createdCmd.getDMaxValue();
		}
		return maxDouble;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getArray2DDouble()
	 */
	public double[][] getArray2DDouble() {
		assert false: "not supported by FlatStorageSimple";
	
		return null;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getArrayString()
	 */
	public String[] getArrayString() {
		return dataString;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getArray2DString()
	 */
	public String[][] getArray2DString() {
		assert false: "not supported by FlatStorageSimple";
	
		return null;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getArrayObject()
	 */
	public Object[] getArrayObject() {
		assert false: "not supported by FlatStorageSimple";
	
		return null;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getArray2DObject()
	 */
	public Object[][] getArray2DObject() {
		assert false: "not supported by FlatStorageSimple";
	
		return null;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getArrayBoolean()
	 */
	public boolean[] getArrayBoolean() {
		return dataBoolean;
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getArray2DObject()
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
			final ISaxParserHandler saxHandler) {
		
		if ( tag_causes_callback.equalsIgnoreCase( ManagerObjectType.STORAGE_FLAT.name() )) {
			
			setMementoXML_usingHandler( saxHandler );
		}
		
		System.out.println("FlatThreadStorageSimple.callbackForParser() STORAGE CALLBACK!");
	}
	
	/**
	 * Restore state of object by update data from SaxHandler
	 * 
	 * @param saxHandler reference to SaxHandler
	 * @return TRUE if the provided handler provided same Id as object.
	 */
	public boolean setMementoXML_usingHandler( final ISaxParserHandler saxHandler ) {
		
		try {
			CollectionFlatStorageSaxParserHandler handler =
				(CollectionFlatStorageSaxParserHandler) saxHandler;
				
			setId( handler.getXML_DataComponent_Id() );
			
			dataInt = handler.getDataInteger();				
			dataBoolean = handler.getDataBoolean();
			dataDouble = handler.getDataDouble();
			dataFloat = handler.getDataFloat();
			dataString = handler.getDataString();
			//handler.getDataLong();
			//handler.getDataShort();
			
			generalManager.getStorageManager().registerItem( this, 
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
	 * @see org.caleydo.core.data.collection.UniqueManagedInterface#getBaseType()
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
	 * @see org.caleydo.core.data.collection.ICollection#getCacheId()
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
