package org.caleydo.core.manager.specialized.clinical.glyph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphEntry;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.data.GlyphAttributeType;
import org.eclipse.core.runtime.Status;

/**
 * Glyph manager
 * 
 * @author Sauer Stefan
 */
public class GlyphManager {
	private IGeneralManager generalManager;

	private HashMap<EGlyphSettingIDs, String> settings;

	private Vector<Integer> sortOrderExt;

	private HashMap<Integer, GlyphAttributeType> dataTypesExt = null;

	private HashMap<Integer, GlyphEntry> hmGlyphList = null;

	private HashMap<String, String> hmLoadedStoraged = null;

	private boolean bIsActive = false;

	private int iSelectionBrushSize;

	/**
	 * Constructor.
	 */
	public GlyphManager() {
		generalManager = GeneralManager.get();

		settings = new HashMap<EGlyphSettingIDs, String>();
		hmGlyphList = new HashMap<Integer, GlyphEntry>();
		hmLoadedStoraged = new HashMap<String, String>();
		sortOrderExt = new Vector<Integer>();

		dataTypesExt = new HashMap<Integer, GlyphAttributeType>();
	}

	public void loadGlyphDefinitaion(String xmlPath) {
		generalManager.getLogger().log(new Status(Status.INFO, GeneralManager.PLUGIN_ID,
			"loadGlyphDefinitaion"));
		generalManager.getXmlParserManager().parseXmlFileByName(xmlPath);

		bIsActive = true;
	}

	public boolean isActive() {
		return bIsActive;
	}

	public void setSelectionBrushSize(int size) {
		iSelectionBrushSize = size;
	}

	public int getSelectionBrushSize() {
		return iSelectionBrushSize;
	}

	// settings accessors
	public String getSetting(EGlyphSettingIDs type) {

		if (settings.containsKey(type))
			return settings.get(type);
		return null;
	}

	public void setSetting(EGlyphSettingIDs type, String value) {

		if (settings.containsKey(type)) {
			settings.remove(type);
		}
		settings.put(type, value);

		for (AGLEventListener agleventlistener : generalManager.getViewGLCanvasManager()
			.getAllGLEventListeners())
			if (agleventlistener instanceof GLGlyph) {
				((GLGlyph) agleventlistener).forceRebuild();
			}
	}

	public int getSortOrder(int depth) {

		if (sortOrderExt.size() > depth) {
			Integer extindex = sortOrderExt.get(depth);
			if (dataTypesExt.containsKey(extindex))
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
		if (dataTypesExt.containsKey(index)) {
			dataTypesExt.remove(index);
			generalManager.getLogger().log(new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
				"GlyphManager::addColumnAttributeType() - double column definition, dropping first one"));
		}
		dataTypesExt.put(index, type);
	}

	public Collection<GlyphAttributeType> getGlyphAttributes() {

		return dataTypesExt.values();
	}

	public HashMap<String, Integer> getGlyphAttributeComboboxEntryList() {
		HashMap<String, Integer> list = new HashMap<String, Integer>();

		// get all combo box entrys
		Iterator<GlyphAttributeType> it = this.getGlyphAttributes().iterator();
		while (it.hasNext()) {
			GlyphAttributeType at = it.next();

			if (at.doesAutomaticAttribute()) {
				continue;
			}

			list.put(at.getName(), at.getInternalColumnNumber());
		}

		return list;
	}

	public GlyphAttributeType getGlyphAttributeTypeWithExternalColumnNumber(int colnum) {

		if (dataTypesExt.containsKey(colnum))
			return dataTypesExt.get(colnum);
		return null;
	}

	public GlyphAttributeType getGlyphAttributeTypeWithInternalColumnNumber(int colnum) {

		for (GlyphAttributeType t : dataTypesExt.values())
			if (t.getInternalColumnNumber() == colnum)
				return t;
		return null;
	}

	public String getGlyphAttributeInfoStringWithInternalColumnNumber(int parameterIndex, int parameterValue) {
		GlyphAttributeType type = getGlyphAttributeTypeWithInternalColumnNumber(parameterIndex);

		if (type != null)
			return type.getName() + ": " + type.getParameterString(parameterValue);

		return "";
	}

	public void addGlyph(int id, GlyphEntry glyph) {
		hmGlyphList.put(id, glyph);
	}

	public void addGlyphs(HashMap<Integer, GlyphEntry> glyphlist) {
		hmGlyphList.putAll(glyphlist);
	}

	public void addGlyphs(HashMap<Integer, GlyphEntry> glyphlist, String storagename) {
		if (hmLoadedStoraged.containsKey(storagename))
			return;

		for (GlyphEntry e : glyphlist.values()) {
			e.select();
		}

		hmGlyphList.putAll(glyphlist);
		hmLoadedStoraged.put(storagename, null);
	}

	public HashMap<Integer, GlyphEntry> getGlyphs() {
		return hmGlyphList;
	}

	public HashMap<Integer, GlyphEntry> getSelectedGlyphs() {
		HashMap<Integer, GlyphEntry> temp = new HashMap<Integer, GlyphEntry>();
		for (int i : hmGlyphList.keySet())
			if (hmGlyphList.get(i).isSelected()) {
				temp.put(i, hmGlyphList.get(i));
			}

		return temp;
	}

	public boolean storageLoaded(String storageName) {
		if (hmLoadedStoraged.containsKey(storageName))
			return true;
		return false;
	}

}
