package org.caleydo.core.parser.ascii.lookuptable;

import org.caleydo.core.data.map.MultiHashArrayIntegerMap;
import org.caleydo.core.data.map.MultiHashArrayStringMap;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdMap;

/**
 * Abstract lookup table loader.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ALookupTableLoader
	implements ILookupTableLoader
{
	protected String sFileName;

	protected EMappingType currentGenomeIdType;

	protected final IGenomeIdManager genomeIdManager;

	protected LookupTableLoaderProxy lookupTableLoaderProxy;

	/**
	 * Factor with that the line index must be multiplied to get a normalized
	 * (0-100) progress percentage value.
	 */
	protected float fProgressBarFactor = 0;

	protected ISWTGUIManager swtGuiManager;

	/**
	 * Constructor.
	 */
	public ALookupTableLoader(final String sFileName, final EMappingType genomeIdType,
			final LookupTableLoaderProxy lookupTableLoaderProxy)
	{
		this.lookupTableLoaderProxy = lookupTableLoaderProxy;
		this.sFileName = sFileName;
		this.currentGenomeIdType = genomeIdType;

		swtGuiManager = GeneralManager.get().getSWTGUIManager();
		genomeIdManager = GeneralManager.get().getGenomeIdManager();

		lookupTableLoaderProxy.setTokenSeperator(IGeneralManager.sDelimiter_Parser_DataType);
	}

	/**
	 * empty method, must be overrwitten by sub-class, if required by logic of
	 * sub-class.
	 * 
	 * @see org.caleydo.core.parser.ascii.lookuptable.LookupTableMultiMapStringLoader#setMultiMapInteger(MultiHashArrayIntegerMap,
	 *      EMappingType)
	 */
	public void setMultiMapInteger(MultiHashArrayIntegerMap setHashMap, EMappingType type)
	{

		assert false : "place holder! must be overwritten by sub-class!";
	}

	/**
	 * empty method, must be overrwitten by sub-class, if required by logic of
	 * sub-class.
	 * 
	 * @see org.caleydo.core.parser.ascii.lookuptable.LookupTableMultiMapIntLoader#setMultiMapInteger(MultiHashArrayIntegerMap,
	 *      EMappingType)
	 */
	public void setMultiMapString(MultiHashArrayStringMap setHashMap, EMappingType type)
	{

		assert false : "place holder! must be overwritten by sub-class!";
	}

	/**
	 * empty method, must be overrwitten by sub-class, if required by logic of
	 * sub-class.
	 * 
	 * @see org.caleydo.core.parser.ascii.lookuptable.LookupTableHashMapLoader#setHashMap(IGenomeIdMap,
	 *      EMappingType)
	 */
	public void setHashMap(final IGenomeIdMap setHashMap, final EMappingType type)
	{

		assert false : "place holder! must be overwritten by sub-class!";
	}

	/**
	 * Per default the LUT needs not to be initialized. If internal data
	 * strucutres need to be allocated, the sub-class must implement this
	 * method.
	 * 
	 * @see org.caleydo.core.parser.ascii.lookuptable.ILookupTableLoader#initLUT()
	 */
	public void initLUT()
	{

	}

	/**
	 * Per default the LUT needs not to be destoryed. If internal data
	 * strucutres were allocated, the sub-class must implement this method.
	 * 
	 * @see org.caleydo.core.parser.ascii.lookuptable.ILookupTableLoader#destroyLUT()
	 */
	public void destroyLUT()
	{

	}
}
