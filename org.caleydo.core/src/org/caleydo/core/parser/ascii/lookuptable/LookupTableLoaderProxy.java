package org.caleydo.core.parser.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.caleydo.core.data.map.MultiHashArrayIntegerMap;
import org.caleydo.core.data.map.MultiHashArrayStringMap;
import org.caleydo.core.data.mapping.EMappingDataType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdMap;
import org.caleydo.core.manager.specialized.genome.id.GenomeIdManager;
import org.caleydo.core.parser.ascii.AbstractLoader;
import org.caleydo.core.parser.xml.sax.ISaxParserHandler;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public final class LookupTableLoaderProxy
	extends AbstractLoader
{

	private ILookupTableLoader proxyLookupTableLoader;

	/**
	 * Constructor.
	 */
	public LookupTableLoaderProxy(final IGeneralManager generalManager,
			final String sFileName, final EMappingType genomeIdType,
			final EMappingDataType type)
	{

		super(generalManager, sFileName);

		GenomeIdManager dgi_mng = (GenomeIdManager) generalManager.getGenomeIdManager();

		switch (type)
		{

			case INT2INT:
			case INT2STRING:
			case STRING2INT:
			case STRING2STRING:
				proxyLookupTableLoader = new LookupTableHashMapLoader(generalManager,
						sFileName, genomeIdType, this);

				// dgi_mng.createMapByType( genomeIdType,
				// genomeIdType.getDataMapppingType() );
				dgi_mng.createMapByType(genomeIdType, type);

				IGenomeIdMap setCurrentMap = dgi_mng.getMapByType(genomeIdType);

				proxyLookupTableLoader.setHashMap(setCurrentMap, genomeIdType);

				break;

			case MULTI_INT2INT:
				proxyLookupTableLoader = new LookupTableMultiMapIntLoader(generalManager,
						sFileName, genomeIdType, this);

				dgi_mng.createMapByType(genomeIdType, type);// genomeIdType.
				// getDataMapppingType()
				// );

				MultiHashArrayIntegerMap setCurrentMultiMap = dgi_mng
						.getMultiMapIntegerByType(genomeIdType);

				proxyLookupTableLoader.setMultiMapInteger(setCurrentMultiMap, genomeIdType);
				break;

			// case MULTI_STRING2STRING_USE_LUT:
			// proxyLookupTableLoader = new LookupTableMultiMapStringLoader(
			// setGeneralManager,
			// setFileName,
			// genomeIdType,
			// this );
			// proxyLookupTableLoader.setInitialSizeHashMap( 1000 );
			//			
			// dgi_mng.createMapByType( genomeIdType,
			// genomeIdType.getDataMapppingType() );
			//			
			// MultiHashArrayIntegerMap mha_IntegerMap =
			// dgi_mng.getMultiMapIntegerByType( genomeIdType_optionalTarget );
			//			
			// proxyLookupTableLoader.setMultiMapInteger( mha_IntegerMap,
			// genomeIdType_optionalTarget );
			//			
			// break;

			case MULTI_STRING2STRING:
				proxyLookupTableLoader = new LookupTableMultiMapStringLoader(generalManager,
						sFileName, genomeIdType, this);

				dgi_mng.createMapByType(genomeIdType, type);// genomeIdType.
				// getDataMapppingType()
				// );

				MultiHashArrayStringMap mha_StringMap = dgi_mng
						.getMultiMapStringByType(genomeIdType);

				proxyLookupTableLoader.setMultiMapString(mha_StringMap, genomeIdType);

				break;

			default:
				assert false : "unsupported type! " + type;
		}

		proxyLookupTableLoader.initLUT();
	}

	public void setHashMap(final IGenomeIdMap setHashMap, final EMappingType type)
	{

		proxyLookupTableLoader.setHashMap(setHashMap, type);
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.parser.handler.importer.ascii.AbstractLoader#
	 * loadDataParseFile(java.io.BufferedReader, int)
	 */
	protected void loadDataParseFile(BufferedReader brFile, final int iNumberOfLinesInFile)
			throws IOException
	{
		swtGuiManager.setLoadingProgressBarPercentage(0);
		swtGuiManager.setLoadingProgressBarText(
				"Load Lookuptable from file " + getFileName());

		proxyLookupTableLoader.loadDataParseFileLUT(brFile,
				iNumberOfLinesInFile);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.parser.ascii.AbstractLoader#setArraysToStorages()
	 */
	protected void setArraysToStorages()
	{
		proxyLookupTableLoader.wirteBackMapToGenomeIdManager();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.xml.IMementoXML#setMementoXML_usingHandler(org.
	 * caleydo.core.xml.parser.ISaxParserHandler)
	 */
	public boolean setMementoXML_usingHandler(ISaxParserHandler saxHandler)
	{

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.xml.parser.IParserObject#init()
	 */
	public void init()
	{

	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.xml.parser.IParserObject#destroy()
	 */
	public void destroy()
	{

		proxyLookupTableLoader.destroyLUT();
	}

	public final IGenomeIdMap createReverseMapFromMap(final IGeneralManager generalManager,
			final EMappingType originMultiMapType,
			final EMappingType targetMultiMapType)
	{

		/* consistency check */
		if ((originMultiMapType.getTypeOrigin() != targetMultiMapType.getTypeTarget())
				|| (originMultiMapType.getTypeTarget() != targetMultiMapType.getTypeOrigin()))
		{
			assert false : "Can not create reverse multimap, because originMapType and targetMapType do not match!";
			return null;
		}

		GenomeIdManager dgi_mng = (GenomeIdManager) generalManager.getGenomeIdManager();

		IGenomeIdMap origionMap = dgi_mng.getMapByType(originMultiMapType);

		IGenomeIdMap targetMap = origionMap.getReversedMap();

		dgi_mng.setMapByType(targetMultiMapType, targetMap);

		return targetMap;
	}

	/**
	 * Creates a new MultiHashArrayIntegerMap inside the DynamicGenomeIdManager.
	 * Fills the new MultiMap with data from originMultiMapType.
	 * 
	 * @param generalManager
	 * @param originMultiMapType
	 * @param targetMultiMapType
	 * @return new MultiHashArrayIntegerMap
	 */
	public final MultiHashArrayIntegerMap createReverseMultiMapFromMultiMapInt(
			final IGeneralManager generalManager, final EMappingType originMultiMapType,
			final EMappingType targetMultiMapType)
	{

		/* consistency check */
		if ((originMultiMapType.getTypeOrigin() != targetMultiMapType.getTypeTarget())
				|| (originMultiMapType.getTypeTarget() != targetMultiMapType.getTypeOrigin())
				|| (originMultiMapType.getDataMapppingType() != EMappingDataType.MULTI_INT2INT))
		{
			assert false : "Can not create reverse multimap, because originMultMapType and targetMultMapType do not match!";
			return null;
		}

		GenomeIdManager dgi_mng = (GenomeIdManager) generalManager.getGenomeIdManager();

		MultiHashArrayIntegerMap intMultiMapOrigin = dgi_mng
				.getMultiMapIntegerByType(originMultiMapType);

		dgi_mng.createMapByType(targetMultiMapType, targetMultiMapType.getDataMapppingType(),
				intMultiMapOrigin.size());

		MultiHashArrayIntegerMap intMultiMapTarget = dgi_mng
				.getMultiMapIntegerByType(targetMultiMapType);

		Set<Integer> setKeysOrigin = intMultiMapOrigin.keySet();

		Iterator<Integer> iterKeysOrigin = setKeysOrigin.iterator();
		while (iterKeysOrigin.hasNext())
		{
			int iKeyOrigin = iterKeysOrigin.next().intValue();

			ArrayList<Integer> buffer = intMultiMapOrigin.get(iKeyOrigin);
			Iterator<Integer> iterValues = buffer.iterator();
			// Iterator <Integer> iterValues =
			// intMultiMapOrigin.get(iKeyOrigin).iterator();
			while (iterValues.hasNext())
			{
				intMultiMapTarget.put(iterValues.next().intValue(), iKeyOrigin);

			} // while (iterValues.hasNext())

		} // while ( iterKeysOrigin.hasNext())

		return intMultiMapTarget;
	}

	/**
	 * Creates a new MultiHashArrayStringMap inside the DynamicGenomeIdManager.
	 * Fills the new MultiMap with data from originMultiMapType.
	 * 
	 * @param generalManager
	 * @param originMultiMapType
	 * @param targetMultiMapType
	 * @return new MultiHashArrayStringMap
	 */
	public final MultiHashArrayStringMap createReverseMultiMapFromMultiMapString(
			final IGeneralManager generalManager, final EMappingType originMultiMapType,
			final EMappingType targetMultiMapType)
	{

		/* consistency check */
		if ((originMultiMapType.getTypeOrigin() != targetMultiMapType.getTypeTarget())
				|| (originMultiMapType.getTypeTarget() != targetMultiMapType.getTypeOrigin())
				|| (originMultiMapType.getDataMapppingType() != EMappingDataType.MULTI_INT2INT))
		{
			assert false : "Can not create reverse multimap, because originMultMapType and targetMultMapType do not match!";
			return null;
		}

		GenomeIdManager dgi_mng = (GenomeIdManager) generalManager.getGenomeIdManager();

		MultiHashArrayStringMap stringMultiMapOrigin = dgi_mng
				.getMultiMapStringByType(originMultiMapType);

		dgi_mng.createMapByType(targetMultiMapType, targetMultiMapType.getDataMapppingType(),
				stringMultiMapOrigin.size());

		MultiHashArrayStringMap stringMultiMapTarget = dgi_mng
				.getMultiMapStringByType(targetMultiMapType);

		Set<String> setKeysOrigin = stringMultiMapOrigin.keySet();

		Iterator<String> iterKeysOrigin = setKeysOrigin.iterator();
		while (iterKeysOrigin.hasNext())
		{
			String sKeyOrigin = iterKeysOrigin.next();

			ArrayList<String> buffer = stringMultiMapOrigin.get(sKeyOrigin);
			Iterator<String> iterValues = buffer.iterator();
			// Iterator <String> iterValues =
			// intMultiMapOrigin.get(sKeyOrigin).iterator();
			while (iterValues.hasNext())
			{
				stringMultiMapTarget.put(iterValues.next(), sKeyOrigin);

			} // while (iterValues.hasNext())

		} // while ( iterKeysOrigin.hasNext())

		return stringMultiMapTarget;
	}

	public final IGenomeIdMap createCodeResolvedMapFromMap(
			final IGeneralManager generalManager, EMappingType originMapMappingType,
			EMappingType genomeMappingLUT_1, EMappingType genomeMappingLUT_2,
			EMappingDataType sourceMapMappingType)
	{

		GenomeIdManager dgi_mng = (GenomeIdManager) generalManager.getGenomeIdManager();

		IGenomeIdMap mapToConvert = dgi_mng.getMapByType(originMapMappingType);

		IGenomeIdMap targetMap = mapToConvert.getCodeResolvedMap(dgi_mng, genomeMappingLUT_1,
				genomeMappingLUT_2, originMapMappingType.getDataMapppingType(),
				sourceMapMappingType);

		// Removes old map that contains the codes instead of the IDs
		dgi_mng.removeMapByType(originMapMappingType);

		dgi_mng.setMapByType(originMapMappingType, targetMap);

		return targetMap;
	}

	public final MultiHashArrayIntegerMap createCodeResolvedMultiMapFromMultiMapString(
			final IGeneralManager generalManager, EMappingType originMapMappingType,
			EMappingType genomeMappingLUT_1, EMappingType genomeMappingLUT_2)
	{

		GenomeIdManager dgi_mng = (GenomeIdManager) generalManager.getGenomeIdManager();

		MultiHashArrayStringMap mapToConvert = dgi_mng
				.getMultiMapStringByType(originMapMappingType);

		MultiHashArrayIntegerMap targetMap = mapToConvert.getCodeResolvedMap(dgi_mng,
				genomeMappingLUT_1, genomeMappingLUT_2);

		// Removes old map that contains the codes instead of the IDs
		dgi_mng.removeMapByType(originMapMappingType);

		dgi_mng.setMapByType(originMapMappingType, targetMap);

		return targetMap;
	}
}
