package org.caleydo.core.view.opengl.canvas.glyph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;

/**
 * @author Stefan Sauer
 */
public class GlyphAttributeGroup
{

	private IGeneralManager generalManager;

	private String sKey;

	private int iGroup;

	private HashMap<String, Float> hmMappingNominalOrdinal;

	private HashMap<Float, String> hmMappingOrdinalNominal;

	private ArrayList<String> alAttributes;

	public GlyphAttributeGroup(int group, String key)
	{
		this.generalManager = GeneralManager.get();
		sKey = key;
		iGroup = group;
		hmMappingNominalOrdinal = new HashMap<String, Float>();
		hmMappingOrdinalNominal = new HashMap<Float, String>();
		alAttributes = new ArrayList<String>();
	}

	public int getGroup()
	{

		return iGroup;
	}

	public String getGroupName()
	{

		return sKey;
	}

	public void addAttribute(String sValue, float fValue)
	{

		if (!hmMappingNominalOrdinal.containsKey(sValue))
			hmMappingNominalOrdinal.put(sValue, fValue);
		else
			generalManager.getLogger().log(Level.WARNING,
					"double nominal value (" + sValue + ") found in " + sKey);
		if (!hmMappingOrdinalNominal.containsKey(fValue))
			hmMappingOrdinalNominal.put(fValue, sValue);
		else
			generalManager.getLogger().log(Level.WARNING,
					"double ordinal value (" + sValue + ") found in " + sKey);

		alAttributes.add(sValue);
	}

	public String getNominalValue(float value)
	{

		if (hmMappingOrdinalNominal.containsKey(value))
			return hmMappingOrdinalNominal.get(value);
		return "";
	}

	public Float getOrdinalValue(String value)
	{

		if (hmMappingNominalOrdinal.containsKey(value))
			return hmMappingNominalOrdinal.get(value);
		return -1.0f;
	}

}
