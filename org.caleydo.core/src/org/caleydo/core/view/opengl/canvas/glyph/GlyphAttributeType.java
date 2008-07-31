package org.caleydo.core.view.opengl.canvas.glyph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Level;
import org.caleydo.core.manager.IGeneralManager;

/**
 * This class defines a data type It uses GlyphAttributeGroup to combines more
 * than one data type into one
 * 
 * @author Stefan Sauer
 */
public class GlyphAttributeType
{

	private IGeneralManager generalManager;

	private HashMap<String, GlyphAttributeGroup> hmNominalLookup;

	private HashMap<Float, GlyphAttributeGroup> hmOrdinalLookup;

	private HashMap<Integer, GlyphAttributeGroup> hmGroupLookup;

	private String sName;

	private int iMaxIndex = 0;

	private boolean bAutomaticAttribute = true;

	private int iInternalColumnIndex = -1;

	private int iExternalColumnIndex = 0;

	private HashMap<Integer, Integer> hmDistribution;

	private HashMap<Integer, Integer> hmSelectedDistribution;

	public GlyphAttributeType(final IGeneralManager generalManager, String name,
			int externalColumnIndex)
	{

		this.generalManager = generalManager;
		sName = name;
		hmNominalLookup = new HashMap<String, GlyphAttributeGroup>();
		hmOrdinalLookup = new HashMap<Float, GlyphAttributeGroup>();
		hmGroupLookup = new HashMap<Integer, GlyphAttributeGroup>();

		hmDistribution = new HashMap<Integer, Integer>();
		hmSelectedDistribution = new HashMap<Integer, Integer>();

		iExternalColumnIndex = externalColumnIndex;
	}

	public void setDoesAutomaticAttribute(boolean truefalse)
	{

		bAutomaticAttribute = truefalse;
	}

	public boolean doesAutomaticAttribute()
	{

		return bAutomaticAttribute;
	}

	public void setInternalColumnNumber(int colnum)
	{

		iInternalColumnIndex = colnum;
	}

	public int getInternalColumnNumber()
	{

		return iInternalColumnIndex;
	}

	public int getExternalColumnNumber()
	{

		return iExternalColumnIndex;
	}

	public void addAttribute(int group, String sValue, float fValue)
	{

		if (!hmGroupLookup.containsKey(group))
			hmGroupLookup.put(group, new GlyphAttributeGroup(generalManager, group, sValue));

		GlyphAttributeGroup gag = hmGroupLookup.get(group);
		gag.addAttribute(sValue, fValue);

		// add to lookup tables
		if (!hmNominalLookup.containsKey(sValue))
			hmNominalLookup.put(sValue, gag);
		else
			generalManager.getLogger().log(
					Level.WARNING,
					"double nominal value (" + sValue + ") found in " + sName + " (" + group
							+ ")");

		if (!hmOrdinalLookup.containsKey(fValue))
			hmOrdinalLookup.put(fValue, gag);
		else
			generalManager.getLogger().log(
					Level.WARNING,
					"double ordinal value (" + sValue + ") found in " + sName + " (" + group
							+ ")");

		if (iMaxIndex < group)
			iMaxIndex = group;

	}

	public String getName()
	{

		return sName;
	}

	public int getIndex(String value)
	{

		if (hmNominalLookup.containsKey(value))
			return hmNominalLookup.get(value).getGroup();
		return -1;
	}

	/*
	 * public int getIndex(float value) { if(hmOrdinalLookup.containsKey(value))
	 * return hmOrdinalLookup.get(value).getGroup(); return -1; }
	 */

	public int getMaxIndex()
	{

		return iMaxIndex;
	}

	public String[] getAttributeNames()
	{

		ArrayList<String> names = new ArrayList<String>();
		ArrayList<Integer> ks2 = new ArrayList<Integer>();
		ks2.addAll(hmGroupLookup.keySet());
		java.util.Collections.sort(ks2);

		for (Integer i : ks2)
			names.add(hmGroupLookup.get(i).getGroupName());

		return names.toArray(new String[names.size()]);
	}

	public void incDistribution(int index)
	{

		if (!hmDistribution.containsKey(index))
			hmDistribution.put(index, 0);
		hmDistribution.put(index, hmDistribution.get(index) + 1);
	}

	public void decDistribution(int index)
	{

		if (!hmDistribution.containsKey(index))
		{
			hmDistribution.put(index, 0);
			return;
		}
		hmDistribution.put(index, hmDistribution.get(index) - 1);
		if (hmDistribution.get(index) < 0)
			hmDistribution.put(index, 0);
	}

	public void incSelectedDistribution(int index)
	{

		if (!hmSelectedDistribution.containsKey(index))
			hmSelectedDistribution.put(index, 0);
		hmSelectedDistribution.put(index, hmSelectedDistribution.get(index) + 1);
	}

	public void decSelectedDistribution(int index)
	{

		if (!hmSelectedDistribution.containsKey(index))
		{
			hmSelectedDistribution.put(index, 0);
			return;
		}
		hmSelectedDistribution.put(index, hmSelectedDistribution.get(index) - 1);
		if (hmSelectedDistribution.get(index) < 0)
			hmSelectedDistribution.put(index, 0);
	}

	public float[][] getDistributionNormalized()
	{

		TreeSet<Integer> sks = new TreeSet<Integer>();
		sks.addAll(hmDistribution.keySet());
		Integer[] sk = sks.toArray(new Integer[sks.size()]);

		float[][] dist = new float[2][sk.length];

		int max = 0;

		for (int i = 0; i < sks.size(); ++i)
		{
			int d = hmDistribution.get(sk[i]);
			if (d > max)
				max = d;
			dist[0][i] = d;
			dist[1][i] = 0;
			if (hmSelectedDistribution.containsKey(sk[i]))
				dist[1][i] = hmSelectedDistribution.get(sk[i]);
		}

		for (int i = 0; i < 2; ++i)
			for (int j = 0; j < dist[i].length; ++j)
				dist[i][j] /= max;

		return dist;
	}

	public void printDistribution()
	{

		float[][] dist = getDistributionNormalized();

		for (int i = 0; i < dist[0].length; ++i)
		{
			System.out.println(" -> " + i + " > " + dist[0][i] + " " + dist[1][i]);
		}
		/*
		 * Set<Integer> ks = hmDistribution.keySet(); for(Integer k : ks) { int
		 * v = hmDistribution.get(k); int s = 0;
		 * if(hmSelectedDistribution.containsKey(k)) s =
		 * hmSelectedDistribution.get(k); System.out.println( " group: " + k +
		 * " " + v + " " + s); }
		 */
	}

}
