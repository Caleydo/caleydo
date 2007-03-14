/**
 * 
 */
package cerberus.xml.parser.handler.importer.ascii.lookuptable;


import cerberus.base.map.MultiHashArrayIntegerMap;
import cerberus.base.map.MultiHashArrayStringMap;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.manager.data.genome.IGenomeIdMap;
//import cerberus.xml.parser.ISaxParserHandler;
//import cerberus.xml.parser.handler.importer.ascii.AbstractLoader;
import cerberus.xml.parser.handler.importer.ascii.LookupTableLoaderProxy;


/**
 * @author Michael Kalkusch
 *
 */
public abstract class ALookupTableLoader 
//extends AbstractLoader 
implements ILookupTableLoader {

//	protected final IGenomeIdManager refGenomeIdManager;
	
	protected String sFileName;
	
	protected GenomeMappingType currentGenomeIdType;
	
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
			final GenomeMappingType genomeIdType,
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
	 * empty methode, must be overrwitten by sub-class, 
	 * if required by logic of sub-class.
	 * 
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.LookupTableMultiMapStringLoader#setMultiMapInteger(MultiHashArrayIntegerMap, GenomeMappingType)
	 */
	public void setMultiMapInteger(MultiHashArrayIntegerMap setHashMap, 
			GenomeMappingType type) {
		assert false : "place holder! must be overwritten by sub-class!";
	}
	
	/**
	 * empty methode, must be overrwitten by sub-class,
	 * if required by logic of sub-class.
	 * 
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.LookupTableMultiMapIntLoader#setMultiMapInteger(MultiHashArrayIntegerMap, GenomeMappingType)
	 */
	public void setMultiMapString(MultiHashArrayStringMap setHashMap, 
			GenomeMappingType type) {
		assert false : "place holder! must be overwritten by sub-class!";
	}
	
	/**
	 * empty methode, must be overrwitten by sub-class,
	 * if required by logic of sub-class.
	 * 
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.LookupTableHashMapLoader#setHashMap(IGenomeIdMap, GenomeMappingType)
	 */
	public void setHashMap( final IGenomeIdMap setHashMap,
			final GenomeMappingType type) {
		assert false : "place holder! must be overwritten by sub-class!";
	}
	
	/**
	 * Per default the LUT needs not to be initialized.
	 * If internal data strucutres need to be allocated, 
	 * the sub-class must implement this methode.
	 *
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader#initLUT()
	 */
	public void initLUT() {

	}

	/**
	 * Per default the LUT needs not to be destoryed.
	 * If internal data strucutres were allocated, 
	 * the sub-class must implement this methode.
	 * 
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader#destroyLUT()
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
