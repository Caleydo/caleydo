/**
 * 
 */
package org.geneview.core.parser.ascii.lookuptable;


import org.geneview.core.data.map.MultiHashArrayIntegerMap;
import org.geneview.core.data.map.MultiHashArrayStringMap;
import org.geneview.core.data.mapping.EGenomeMappingType;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.data.IGenomeIdManager;
import org.geneview.core.manager.data.genome.IGenomeIdMap;
import org.geneview.core.parser.ascii.lookuptable.ILookupTableLoader;


/**
 * @author Michael Kalkusch
 *
 */
public abstract class ALookupTableLoader 
//extends AbstractLoader 
implements ILookupTableLoader {

//	protected final IGenomeIdManager refGenomeIdManager;
	
	protected String sFileName;
	
	protected EGenomeMappingType currentGenomeIdType;
	
	protected final IGeneralManager refGeneralManager;
	
	protected final IGenomeIdManager refGenomeIdManager;
	
	protected LookupTableLoaderProxy refLookupTableLoaderProxy;

	
	protected int iInitialSizeMultiHashMap = 1000;
	
	/**
	 * @param setGeneralManager
	 * @param setFileName
	 */
	public ALookupTableLoader( final IGeneralManager setGeneralManager,
			final String setFileName,
			final EGenomeMappingType genomeIdType,
			final LookupTableLoaderProxy setLookupTableLoaderProxy ) {

		refGeneralManager = setGeneralManager;
		refLookupTableLoaderProxy = setLookupTableLoaderProxy;
		sFileName = setFileName;	
	
		this.currentGenomeIdType = genomeIdType;
		
		refGenomeIdManager = 
			refGeneralManager.getSingelton().getGenomeIdManager();
		
		refLookupTableLoaderProxy.setTokenSeperator( 
				IGeneralManager.sDelimiter_Parser_DataType);
	}
	


	/**
	 * empty method, must be overrwitten by sub-class, 
	 * if required by logic of sub-class.
	 * 
	 * @see org.geneview.core.parser.ascii.lookuptable.LookupTableMultiMapStringLoader#setMultiMapInteger(MultiHashArrayIntegerMap, EGenomeMappingType)
	 */
	public void setMultiMapInteger(MultiHashArrayIntegerMap setHashMap, 
			EGenomeMappingType type) {
		assert false : "place holder! must be overwritten by sub-class!";
	}
	
	/**
	 * empty method, must be overrwitten by sub-class,
	 * if required by logic of sub-class.
	 * 
	 * @see org.geneview.core.parser.ascii.lookuptable.LookupTableMultiMapIntLoader#setMultiMapInteger(MultiHashArrayIntegerMap, EGenomeMappingType)
	 */
	public void setMultiMapString(MultiHashArrayStringMap setHashMap, 
			EGenomeMappingType type) {
		assert false : "place holder! must be overwritten by sub-class!";
	}
	
	/**
	 * empty method, must be overrwitten by sub-class,
	 * if required by logic of sub-class.
	 * 
	 * @see org.geneview.core.parser.ascii.lookuptable.LookupTableHashMapLoader#setHashMap(IGenomeIdMap, EGenomeMappingType)
	 */
	public void setHashMap( final IGenomeIdMap setHashMap,
			final EGenomeMappingType type) {
		assert false : "place holder! must be overwritten by sub-class!";
	}
	
	/**
	 * Per default the LUT needs not to be initialized.
	 * If internal data strucutres need to be allocated, 
	 * the sub-class must implement this method.
	 *
	 * @see org.geneview.core.parser.ascii.lookuptable.ILookupTableLoader#initLUT()
	 */
	public void initLUT() {

	}

	/**
	 * Per default the LUT needs not to be destoryed.
	 * If internal data strucutres were allocated, 
	 * the sub-class must implement this method.
	 * 
	 * @see org.geneview.core.parser.ascii.lookuptable.ILookupTableLoader#destroyLUT()
	 */
	public void destroyLUT() {

	}
	

	/**
	 * Define initial size. 
	 * Must be called before initLUT() is called!
	 * 
	 * @param iInitialSizeHashMap
	 */
	public final void setInitialSizeHashMap( final int iSetInitialSizeHashMap ) {
		
		this.iInitialSizeMultiHashMap = iSetInitialSizeHashMap;
	}
	
	
	protected final int getiInitialSizeHashMap( ) {
		
		return this.iInitialSizeMultiHashMap;
	}
}
