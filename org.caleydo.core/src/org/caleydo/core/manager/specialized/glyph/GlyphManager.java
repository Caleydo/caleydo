package org.caleydo.core.manager.specialized.glyph;

import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;

import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;


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
	private Vector<String> sortOrder;
	
	private HashMap<String, Integer> colMapSI = new HashMap<String, Integer>();
	private HashMap<Integer, String> colMapIS = new HashMap<Integer, String>();

	/**
	 * Constructor.
	 */
	public GlyphManager(final IGeneralManager generalManager) {
		super(generalManager, 
			IGeneralManager.iUniqueId_TypeOffset_Pathways_Pathway,
			ManagerType.DATA_PATHWAY_ELEMENT );
		
		settings = new HashMap<EGlyphSettingIDs, String>();
		sortOrder = new Vector<String>();
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
		//TODO: map column string to column index
		return -1;
	}


	public void addSortColumn(String value) {
		sortOrder.add(value);
	}
	
	
	
	public void addColumnMapping(String name, int colnum) {
		colMapSI.put(name, colnum);
		colMapIS.put(colnum, name);
	}
	
	
	
	
	
	
	
	
	
	
	
	

	//interface
	public Object getItem(int iItemId) {

		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasItem(int iItemId) {

		// TODO Auto-generated method stub
		return false;
	}

	public boolean registerItem(Object registerItem, int iItemId, ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}

	public int size() {

		// TODO Auto-generated method stub
		return 0;
	}

	public boolean unregisterItem(int iItemId, ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}


}
