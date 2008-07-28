package org.caleydo.core.manager.specialized.glyph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;

import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;
import org.caleydo.core.view.opengl.canvas.glyph.GLCanvasGlyphGenerator;
import org.caleydo.core.view.opengl.canvas.glyph.GlyphAttributeType;
import org.caleydo.core.view.opengl.canvas.glyph.GlyphEntry;


/**
 * 
 * @author Sauer Stefan
 *
 */
public class GlyphManager
extends AManager
implements IGlyphManager{
	
	private static final long serialVersionUID = 1L;
	private HashMap<EGlyphSettingIDs, String> settings;
	private Vector<Integer> sortOrderExt;

	private GLCanvasGlyphGenerator generator = null;

	private HashMap<Integer, GlyphAttributeType> dataTypesExt = new HashMap<Integer, GlyphAttributeType>();
	
	private HashMap<Integer, GlyphEntry> hmGlyphList = new HashMap<Integer, GlyphEntry>();

	/**
	 * Constructor.
	 */
	public GlyphManager(final IGeneralManager generalManager) {
		super(generalManager, 
			IGeneralManager.iUniqueId_TypeOffset_Pathways_Pathway,
			ManagerType.DATA_PATHWAY_ELEMENT );
		
		settings = new HashMap<EGlyphSettingIDs, String>();
		sortOrderExt = new Vector<Integer>();
		
		generator = new GLCanvasGlyphGenerator();
	}
	
	
	public void loadGlyphDefinitaion(String xmlPath) {
		generalManager.getLogger().log(Level.INFO, "loadGlyphDefinitaion");
		generalManager.getXmlParserManager().parseXmlFileByName(xmlPath);
	}
	
	
	
	//settings accessors
	public String getSetting(EGlyphSettingIDs type) {
		if(settings.containsKey(type))
			return settings.get(type);
		return null;
	}
	
	public void setSetting(EGlyphSettingIDs type, String value) {
		if(settings.containsKey(type))
			settings.remove(type);
		settings.put(type, value);
	}
	
	
	public int getSortOrder(int depth) {
		if(sortOrderExt.size() > depth) {
			Integer extindex = sortOrderExt.get(depth);
			if(dataTypesExt.containsKey(extindex))
				return dataTypesExt.get(extindex).getInternalColumnNumber();
		}
		return -1;
	}


	public void addSortColumn(String value) {
		int x = Integer.parseInt(value);
		sortOrderExt.add(x);
	}
	

	public void addColumnAttributeType(GlyphAttributeType type) {
		int index = type.getExternalColumnNumber();
		if(dataTypesExt.containsKey(index)) {
			dataTypesExt.remove(index);
			generalManager.getLogger().log(Level.WARNING, "GlyphManager::addColumnAttributeType() - double column definition, dropping first one");
		}
		dataTypesExt.put(index, type);
	}

	public Collection<GlyphAttributeType> getGlyphAttributes() {
		return dataTypesExt.values();
	}
	public GlyphAttributeType getGlyphAttributeTypeWithExternalColumnNumber(int colnum) {
		if(dataTypesExt.containsKey(colnum))
			return dataTypesExt.get(colnum);
		return null;
	}
	public GlyphAttributeType getGlyphAttributeTypeWithInternalColumnNumber(int colnum) {
		for (GlyphAttributeType t : dataTypesExt.values())
			if(t.getInternalColumnNumber() == colnum)
				return t;
		return null;
	}


	
	public GLCanvasGlyphGenerator getGlyphGenerator() {
		return generator;
	}
	
	public void initGlyphGenerator() {
		try {
			int ebtc = Integer.parseInt(getSetting(EGlyphSettingIDs.TOPCOLOR));
			int ibtc = getGlyphAttributeTypeWithExternalColumnNumber(ebtc).getInternalColumnNumber();
			generator.setIndexTopColor(ibtc);
			ebtc = Integer.parseInt(getSetting(EGlyphSettingIDs.BOXCOLOR));
			ibtc = getGlyphAttributeTypeWithExternalColumnNumber(ebtc).getInternalColumnNumber();
			generator.setIndexBoxColor(ibtc);
			ebtc = Integer.parseInt(getSetting(EGlyphSettingIDs.BOXHEIGHT));
			ibtc = getGlyphAttributeTypeWithExternalColumnNumber(ebtc).getInternalColumnNumber();
			generator.setIndexHeight(ibtc);
		
			//set max height value
			ebtc = Integer.parseInt(getSetting(EGlyphSettingIDs.BOXHEIGHT));
			int maxHeight = getGlyphAttributeTypeWithExternalColumnNumber( ebtc ).getMaxIndex();
			generator.setMaxHeight(maxHeight);

		} catch(Exception ex) {
			this.generalManager.getLogger().log(Level.WARNING, "GlyphManager::initGlyphGenerator() - parsing integer failed!" );
		}
	}


	public void addGlyph(int id, GlyphEntry glyph) {
		hmGlyphList.put(id, glyph);
	}


	public void addGlyphs(HashMap<Integer, GlyphEntry> glyphlist) {
		hmGlyphList.putAll(glyphlist);
	}


	public GlyphEntry getGlyph(int id) {
		if(hmGlyphList.containsKey(id))
			return hmGlyphList.get(id);
		return null;
	}


	public HashMap<Integer, GlyphEntry> getGlyphs() {
		return hmGlyphList;
	}
	
	//std interface
	public Object getItem(int iItemId) {
		return getGlyph(iItemId);
	}

	public boolean hasItem(int iItemId) {
		if(hmGlyphList.containsKey(iItemId))
			return true;
		return false;
	}

	public boolean registerItem(Object registerItem, int iItemId) {

		// TODO Auto-generated method stub
		return false;
	}

	public int size() {
		return hmGlyphList.size();
	}

	public boolean unregisterItem(int iItemId) {

		// TODO Auto-generated method stub
		return false;
	}






}
