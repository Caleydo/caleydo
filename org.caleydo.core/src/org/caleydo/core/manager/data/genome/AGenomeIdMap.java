/**
 * 
 */
package org.geneview.core.manager.data.genome;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.geneview.core.data.mapping.EGenomeMappingDataType;
import org.geneview.core.data.mapping.EGenomeMappingType;
import org.geneview.core.manager.data.IGenomeIdManager;


/**
 * @author Michael Kalkusch
 *
 */
public abstract class AGenomeIdMap <K,V> 
implements IGenomeIdMap {

	protected HashMap <K,V> hashGeneric;
	
	protected final EGenomeMappingDataType dataType;
	
	
	/**
	 * 
	 */
	public AGenomeIdMap(final EGenomeMappingDataType dataType) {
		hashGeneric = new HashMap <K,V> ();
		this.dataType = dataType;
	}
	
	/**
	 * 
	 * @param iSizeHashMap define size of hashmap
	 */
	protected AGenomeIdMap(final EGenomeMappingDataType dataType, final int iSizeHashMap) {
		hashGeneric = new HashMap <K,V> (iSizeHashMap);
		this.dataType = dataType;
	}

	public final Set <K> getKeys() {
		return hashGeneric.keySet();
	}
	
	public final Collection <V> getValues() {
		return hashGeneric.values();
	}
	
	
	/**
	 * @see org.geneview.core.manager.data.genome.IGenomeIdMap#size()
	 * @see java.util.Map#size()
	 */
	public final int size() {
		return hashGeneric.size();
	}
	
	/**
	 * @see org.geneview.core.manager.data.genome.IGenomeIdMap#getReversedMap()
	 */
	public final IGenomeIdMap getReversedMap() {
		IGenomeIdMap reversedMap = null;
		
			switch ( dataType ) 
			{
			case INT2INT:
				reversedMap = new GenomeIdMapInt2Int(dataType,
						this.size());
				break;
				
			case STRING2STRING:
				reversedMap = new GenomeIdMapString2String(dataType,
						this.size());
				break;
				
				/* invert type for reverse map! */
			case INT2STRING:
				/* ==> use STRING2INT */
				reversedMap = new GenomeIdMapString2Int(
						EGenomeMappingDataType.STRING2INT,
						this.size());
				break;
				
			case STRING2INT:
				/* ==> use INT2STRING */
				reversedMap = new GenomeIdMapInt2String(
						EGenomeMappingDataType.INT2STRING,
						this.size());
				break;
				
				default:
					assert false : "unsupported data type=" + dataType.toString();
			}	
	
		/** 
		 * Read HashMap and write it to new HashMap
		 */
		Set <Entry<K,V>> entrySet = hashGeneric.entrySet();			
		Iterator <Entry<K,V>> iterOrigin = entrySet.iterator();
		
		while ( iterOrigin.hasNext() ) 
		{
			Entry<K,V> entryBuffer = iterOrigin.next();
			
			reversedMap.put( 
					entryBuffer.getValue().toString() , 
					entryBuffer.getKey().toString() );
		}
			
		return reversedMap;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.genome.IGenomeIdMap#getCodeResolvedMap(org.geneview.core.manager.data.IGenomeIdManager, org.geneview.core.data.mapping.EGenomeMappingType, org.geneview.core.data.mapping.EGenomeMappingType)
	 */
	public final IGenomeIdMap getCodeResolvedMap(
			IGenomeIdManager refGenomeIdManager,
			EGenomeMappingType genomeMappingLUT_1,
			EGenomeMappingType genomeMappingLUT_2,
			EGenomeMappingDataType targetMappingDataType,
			EGenomeMappingDataType sourceMappingDataType) {
		
		IGenomeIdMap codeResolvedMap = null;
		
		switch ( targetMappingDataType ) 
		{
		case INT2INT:
		{
			codeResolvedMap = new GenomeIdMapInt2Int(targetMappingDataType,
					this.size());
			
			/** 
			 * Read HashMap and write it to new HashMap
			 */
			Set <Entry<K,V>> entrySet = hashGeneric.entrySet();			
			Iterator <Entry<K,V>> iterOrigin = entrySet.iterator();
			int iResolvedID_1 = 0;
			int iResolvedID_2 = 0;
			
			Entry<K,V> entryBuffer = null;
			
			while ( iterOrigin.hasNext() ) 
			{
				entryBuffer = iterOrigin.next();
												
				iResolvedID_1 = refGenomeIdManager.getIdIntFromStringByMapping(
						entryBuffer.getKey().toString(), 
						genomeMappingLUT_1);
				
				if (sourceMappingDataType == EGenomeMappingDataType.STRING2INT)
				{						
					codeResolvedMap.put(Integer.toString(iResolvedID_1), 
							entryBuffer.getValue().toString());
				}
				else if (sourceMappingDataType == EGenomeMappingDataType.STRING2STRING)
				{
					iResolvedID_2 = refGenomeIdManager.getIdIntFromStringByMapping(
							entryBuffer.getValue().toString(),
							genomeMappingLUT_2);

					codeResolvedMap.put(new Integer(iResolvedID_1).toString(), 
							new Integer(iResolvedID_2).toString());
				}
			}
					
			break;
		}
		case INT2STRING:
		{
			codeResolvedMap = new GenomeIdMapInt2String(targetMappingDataType,
					this.size());	
			
			/** 
			 * Read HashMap and write it to new HashMap
			 */
			Set <Entry<K,V>> entrySet = hashGeneric.entrySet();			
			Iterator <Entry<K,V>> iterOrigin = entrySet.iterator();
			int iResolvedID_1 = 0;
			
			Entry<K,V> entryBuffer = null;
			
			while ( iterOrigin.hasNext() ) 
			{
				entryBuffer = iterOrigin.next();
							
				iResolvedID_1 = refGenomeIdManager.getIdIntFromStringByMapping(
						entryBuffer.getKey().toString(), 
						genomeMappingLUT_1);
				
				codeResolvedMap.put(new Integer(iResolvedID_1).toString(), 
						entryBuffer.getValue().toString());
			}
				
			break;
		}
			
		default:
			System.err.println("unsupported data type= " +dataType.toString());
			//assert false : "unsupported data type=" + dataType.toString();
		}	
			
		return codeResolvedMap;
	}
	
	/* ----------------------------------------------- */
	/* -----  Methods to overload in subclasses  ----- */
	
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.event.IEventPublisherMap#getIntByInt(int)
	 */
	public int getIntByInt(int key) {

		assert false : "getIntByInt() is not overloaded and thus can not be used!";
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.manager.event.IEventPublisherMap#getIntByString(Stringt)
	 */
	public int getIntByString(String key) {

		assert false : "getIntByInt() is not overloaded and thus can not be used!";
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.manager.event.IEventPublisherMap#getStringByInt(int)
	 */
	public String getStringByInt(int key) {

		assert false : "getIntByInt() is not overloaded and thus can not be used!";
		return "";
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.manager.event.IEventPublisherMap#getStringByString(Stringt)
	 */
	public String getStringByString(String key) {

		assert false : "getIntByInt() is not overloaded and thus can not be used!";
		return "";
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.manager.event.IEventPublisherMap#getIntByIntChecked(int)
	 */
	public int getIntByIntChecked(int key) {

		assert false : "getIntByIntChecked() is not overloaded and thus can not be used!";
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.manager.event.IEventPublisherMap#getIntByStringChecked(Stringt)
	 */
	public int getIntByStringChecked(String key) {

		assert false : "getIntByIntChecked() is not overloaded and thus can not be used!";
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.manager.event.IEventPublisherMap#getStringByIntChecked(int)
	 */
	public String getStringByIntChecked(int key) {

		assert false : "getIntByIntChecked() is not overloaded and thus can not be used!";
		return "";
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.manager.event.IEventPublisherMap#getStringByStringChecked(Stringt)
	 */
	public String getStringByStringChecked(String key) {

		assert false : "getIntByIntChecked() is not overloaded and thus can not be used!";
		return "";
	}
		
	/* -----  Methods to overload in subclasses  ----- */
	/* ----------------------------------------------- */
	
}
