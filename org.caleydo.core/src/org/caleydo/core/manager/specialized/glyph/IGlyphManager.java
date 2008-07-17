package org.caleydo.core.manager.specialized.glyph;

import org.caleydo.core.manager.IManager;



/**
 * 
 * @author Sauer Stefan
 *
 */
public interface IGlyphManager
extends IManager {
	
	public void loadGlyphDefinitaion(String xmlPath);
	public String getSetting(EGlyphSettingIDs type);
	public void setSetting(EGlyphSettingIDs type, String value);
	public void addSortColumn(String value);
	public void addColumnMapping(String name, int colnum);

}
