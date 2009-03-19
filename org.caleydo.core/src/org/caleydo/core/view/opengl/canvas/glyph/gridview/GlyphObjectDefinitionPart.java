package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import gleem.linalg.Vec4f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.glyph.EGlyphSettingIDs;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphObjectDefinition.ANCHOR;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphObjectDefinition.DIRECTION;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.data.GlyphAttributeType;

/**
 * Stores the xml definition of a glyph object, matching to the wavefront object group.
 * 
 * @author Stefan Sauer
 */
public class GlyphObjectDefinitionPart {
	private GlyphManager gman = null;

	private HashMap<ANCHOR, String> anchors;

	private HashMap<EGlyphSettingIDs, String> parameter;
	private HashMap<EGlyphSettingIDs, String> parameterDescription;
	private HashMap<EGlyphSettingIDs, Integer> parameterIndex;

	private boolean bScaleX = false;
	private boolean bScaleY = false;
	private boolean bScaleZ = false;

	private int iScaleIndexX = -1;
	private int iScaleIndexY = -1;
	private int iScaleIndexZ = -1;

	private String sScaleDescriptionX;
	private String sScaleDescriptionY;
	private String sScaleDescriptionZ;

	private Vector<Vec4f> colors;

	public GlyphObjectDefinitionPart() {
		anchors = new HashMap<ANCHOR, String>();
		colors = new Vector<Vec4f>();

		parameter = new HashMap<EGlyphSettingIDs, String>();
		parameterDescription = new HashMap<EGlyphSettingIDs, String>();
		parameterIndex = new HashMap<EGlyphSettingIDs, Integer>();

		gman = GeneralManager.get().getGlyphManager();
	}

	public void addColor(Vec4f color) {
		colors.add(color);
	}

	public Vec4f getColor(int index) {
		if (index < colors.size() && index >= 0)
			return new Vec4f(colors.get(index));
		return null;
	}

	public void addParameter(String type, String value, String description) {
		if (type == null)
			return;

		EGlyphSettingIDs Etype = EGlyphSettingIDs.valueOf(type.toUpperCase());

		if (Etype == null)
			return;

		type = type.toUpperCase();
		if (value != null) {
			value = value.toUpperCase();
		}

		if (Etype == EGlyphSettingIDs.SCALE) {
			DIRECTION dir = DIRECTION.valueOf(value);
			if (dir == DIRECTION.X) {
				bScaleX = true;
				sScaleDescriptionX = description;
			}
			if (dir == DIRECTION.Y) {
				bScaleY = true;
				sScaleDescriptionY = description;
			}
			if (dir == DIRECTION.Z) {
				bScaleZ = true;
				sScaleDescriptionZ = description;
			}
		}
		else {

			if (parameter.containsKey(type)) {
				parameter.remove(Etype);
				parameterDescription.remove(Etype);
				parameterIndex.remove(Etype);
			}
			parameter.put(Etype, value);
			parameterDescription.put(Etype, description);
			parameterIndex.put(Etype, -1);
		}

	}

	public boolean canScale(DIRECTION dir) {
		if (dir == DIRECTION.X)
			return bScaleX;
		if (dir == DIRECTION.Y)
			return bScaleY;
		if (dir == DIRECTION.Z)
			return bScaleZ;

		return false;
	}

	public String getDescription(EGlyphSettingIDs type, DIRECTION dir) {
		if (type == EGlyphSettingIDs.SCALE) {
			if (dir == DIRECTION.X)
				return sScaleDescriptionX;
			if (dir == DIRECTION.Y)
				return sScaleDescriptionY;
			if (dir == DIRECTION.Z)
				return sScaleDescriptionZ;
		}

		return parameterDescription.get(type);
	}

	public void addAnchor(ANCHOR anc, String to) {
		if (!anchors.containsKey(anc)) {
			anchors.put(anc, to);
		}
	}

	public Set<ANCHOR> getAnchors() {
		return new HashSet<ANCHOR>(anchors.keySet());
	}

	public boolean hasAnchor(ANCHOR anc) {
		if (anchors.containsKey(anc))
			return true;
		return false;
	}

	public String getAnchorPlace(ANCHOR anc) {
		if (anchors.containsKey(anc))
			return new String(anchors.get(anc));
		return null;
	}

	public void setParameterIndex(EGlyphSettingIDs type, DIRECTION dir, int index) {

		if (type == EGlyphSettingIDs.SCALE) {
			if (dir == DIRECTION.X) {
				iScaleIndexX = index;
			}
			if (dir == DIRECTION.Y) {
				iScaleIndexY = index;
			}
			if (dir == DIRECTION.Z) {
				iScaleIndexZ = index;
			}
		}
		else {
			if (!parameter.containsKey(type))
				return;

			parameterIndex.remove(type);
			parameterIndex.put(type, index);
		}

	}

	public int getParameterIndexExternal(EGlyphSettingIDs type, DIRECTION dir) {
		if (type == EGlyphSettingIDs.SCALE) {
			if (dir == DIRECTION.X)
				return iScaleIndexX;
			if (dir == DIRECTION.Y)
				return iScaleIndexY;
			if (dir == DIRECTION.Z)
				return iScaleIndexZ;
		}
		else {
			if (!parameter.containsKey(type))
				return -1;

			return parameterIndex.get(type);
		}
		return -1;
	}

	public int getParameterIndexInternal(EGlyphSettingIDs type, DIRECTION dir) {
		return remapIndex(getParameterIndexExternal(type, dir));
	}

	private int remapIndex(int externalIndex) {

		GlyphAttributeType typ = gman.getGlyphAttributeTypeWithExternalColumnNumber(externalIndex);
		if (typ != null)
			return typ.getInternalColumnNumber();

		return -1;
	}

}