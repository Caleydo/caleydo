package org.caleydo.core.manager.specialized.glyph;

import java.util.Collection;
import java.util.HashMap;
import org.caleydo.core.view.opengl.canvas.glyph.GLGlyph;
import org.caleydo.core.view.opengl.canvas.glyph.GLGlyphGenerator;
import org.caleydo.core.view.opengl.canvas.glyph.GlyphAttributeType;
import org.caleydo.core.view.opengl.canvas.glyph.GlyphEntry;

/**
 * @author Sauer Stefan
 */
public interface IGlyphManager
{

	public void registerGlyphView(GLGlyph view);

	public void unregisterGlyphView(GLGlyph view);

	public void loadGlyphDefinitaion(String xmlPath);

	public String getSetting(EGlyphSettingIDs type);

	public void setSetting(EGlyphSettingIDs type, String value);

	public int getSortOrder(int depth);

	public void addSortColumn(String value);

	public void addColumnAttributeType(GlyphAttributeType type);

	public Collection<GlyphAttributeType> getGlyphAttributes();

	public GlyphAttributeType getGlyphAttributeTypeWithExternalColumnNumber(int colnum);

	public GlyphAttributeType getGlyphAttributeTypeWithInternalColumnNumber(int colnum);

	public GLGlyphGenerator getGlyphGenerator();

	public void initGlyphGenerator();

	public void addGlyphs(HashMap<Integer, GlyphEntry> glyphlist);

	public HashMap<Integer, GlyphEntry> getGlyphs();

	public void addGlyph(int id, GlyphEntry glyph);

}
