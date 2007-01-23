/**
 * 
 */
package cerberus.xml.parser.handler.importer.ascii.lookuptable;


import cerberus.base.map.MultiHashArrayMap;
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
	
	protected GenomeMappingType genomeType;
	
	protected final IGeneralManager refGeneralManager;
	
	protected LookupTableLoaderProxy refLookupTableLoaderProxy;
	
	protected IGenomeIdMap refGenomeIdMap;
	
	/**
	 * @param setGeneralManager
	 * @param setFileName
	 */
	public ALookupTableLoader( final IGeneralManager setGeneralManager,
			final String setFileName,
			final GenomeMappingType genometype,
			final LookupTableLoaderProxy setLookupTableLoaderProxy ) {

		refGeneralManager = setGeneralManager;
		refLookupTableLoaderProxy = setLookupTableLoaderProxy;
		sFileName = setFileName;	
	
		this.genomeType = genometype;
		
		IGenomeIdManager refGenomeIdManager = 
			refGeneralManager.getSingelton().getGenomeIdManager();

		IGenomeIdMap bufferMap = refGenomeIdManager.getMapByType( genomeType );
		
		setHashMap( bufferMap, genomeType );
		
	}

	public final void setHashMap( final IGenomeIdMap setHashMap,
			final GenomeMappingType type) {
		
		assert type == genomeType : "must use same type as in constructor!";
		//genomeType = type;
		
		if ( type.isMultiMap() )
		{
			setMultiHashMap( (MultiHashArrayMap) setHashMap );
			return;
		}
		
		refGenomeIdMap = setHashMap;		
	}
	
	public void setMultiHashMap( MultiHashArrayMap setMultiHashMap ) {
		assert false : "This methode must be overloaded by sub-class";
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader#initLUT()
	 */
	public void initLUT() {

	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader#destroyLUT()
	 */
	public void destroyLUT() {

	}

}
