package org.caleydo.core.view.opengl.canvas.glyph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.caleydo.core.manager.IGeneralManager;


public class GlyphAttributeType {
	private IGeneralManager generalManager;
	
	private HashMap<String,  GlyphAttributeGroup> hmNominalLookup;
	private HashMap<Float,   GlyphAttributeGroup> hmOrdinalLookup;
	private HashMap<Integer, GlyphAttributeGroup> hmGroupLookup;
	private String sName;
	private int iMaxIndex = 0;

	public GlyphAttributeType(final IGeneralManager generalManager, String name)
	{
		this.generalManager = generalManager;
		sName = name;
		hmNominalLookup = new HashMap<String, GlyphAttributeGroup>();
		hmOrdinalLookup = new HashMap<Float, GlyphAttributeGroup>();
		hmGroupLookup = new HashMap<Integer, GlyphAttributeGroup>();
	}
	
	public void addAttribute(int group, String sValue, float fValue)
	{
		if(!hmGroupLookup.containsKey(group))
			hmGroupLookup.put(group, new GlyphAttributeGroup(generalManager, group, sValue));
		
		GlyphAttributeGroup gag = hmGroupLookup.get(group);
		gag.addAttribute(sValue, fValue);
		
		//add to lookup tables
		if(!hmNominalLookup.containsKey(sValue))
			hmNominalLookup.put(sValue, gag);
		else
			generalManager.getLogger().log(Level.WARNING, "double nominal value ("+sValue+") found in " + sName + " (" + group + ")");
		
		if(!hmOrdinalLookup.containsKey(fValue))
			hmOrdinalLookup.put(fValue, gag);
		else
			generalManager.getLogger().log(Level.WARNING, "double ordinal value ("+sValue+") found in " + sName + " (" + group + ")");
		
		if(iMaxIndex < group)
			iMaxIndex = group;
		
	}
	
	public String getName() {
		return sName;
	}
	
	public int getIndex(String value)
	{
		if(hmNominalLookup.containsKey(value))
			return hmNominalLookup.get(value).getGroup();
		return -1;
	}

	public int getIndex(float value)
	{
		if(hmOrdinalLookup.containsKey(value))
			return hmOrdinalLookup.get(value).getGroup();
		return -1;
	}
	
	public int getMaxIndex() {
		return iMaxIndex;
	}

	public String[] getAttributeNames()
	{
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<Integer> ks2 = new ArrayList<Integer>();
		ks2.addAll(hmGroupLookup.keySet());
		java.util.Collections.sort(ks2);
		
		for(Integer i : ks2)
			names.add(hmGroupLookup.get(i).getGroupName());
		
		return names.toArray(new String[names.size()]);
	}
	

}

