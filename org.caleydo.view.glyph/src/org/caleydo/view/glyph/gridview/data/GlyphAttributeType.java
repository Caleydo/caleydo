package org.caleydo.core.view.opengl.canvas.glyph.gridview.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * This class defines a data type. It uses GlyphAttributeGroup to combines more than one data type into one.
 * 
 * @author Stefan Sauer
 */
public class GlyphAttributeType {

	private GeneralManager generalManager;

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

	/**
	 * Constructor
	 * 
	 * @param name
	 *            Name of this Attribute
	 * @param externalColumnIndex
	 *            Index of the column in the set / csv file
	 */
	public GlyphAttributeType(String name, int externalColumnIndex) {
		this.generalManager = GeneralManager.get();
		sName = name;
		hmNominalLookup = new HashMap<String, GlyphAttributeGroup>();
		hmOrdinalLookup = new HashMap<Float, GlyphAttributeGroup>();
		hmGroupLookup = new HashMap<Integer, GlyphAttributeGroup>();

		hmDistribution = new HashMap<Integer, Integer>();
		hmSelectedDistribution = new HashMap<Integer, Integer>();

		iExternalColumnIndex = externalColumnIndex;

		hmDistribution.put(-1, 0); // NAV
		hmSelectedDistribution.put(-1, 0); // NAV
	}

	public void setDoesAutomaticAttribute(boolean truefalse) {

		bAutomaticAttribute = truefalse;
	}

	public boolean doesAutomaticAttribute() {

		return bAutomaticAttribute;
	}

	public void setInternalColumnNumber(int colnum) {

		iInternalColumnIndex = colnum;
	}

	public int getInternalColumnNumber() {

		return iInternalColumnIndex;
	}

	public int getExternalColumnNumber() {

		return iExternalColumnIndex;
	}

	/**
	 * Adds a possible value to the attribute type. Used to define the attribute type (in the xml parser)
	 * 
	 * @param group
	 *            used group
	 * @param sValue
	 *            string representation of the value
	 * @param fValue
	 *            float representation of the value
	 */
	public void addAttribute(int group, String sValue, float fValue) {

		if (!hmGroupLookup.containsKey(group))
			hmGroupLookup.put(group, new GlyphAttributeGroup(group, sValue));

		GlyphAttributeGroup gag = hmGroupLookup.get(group);
		gag.addAttribute(sValue, fValue);

		// add to lookup tables
		if (!hmNominalLookup.containsKey(sValue)) {
			hmNominalLookup.put(sValue, gag);
		}
		else {
			generalManager.getLogger().log(
				new Status(IStatus.WARNING, GeneralManager.PLUGIN_ID, "double nominal value (" + sValue
					+ ") found in " + sName + " (" + group + ")"));
		}

		if (!hmOrdinalLookup.containsKey(fValue)) {
			hmOrdinalLookup.put(fValue, gag);
		}
		else {
			generalManager.getLogger().log(
				new Status(IStatus.WARNING, GeneralManager.PLUGIN_ID, "double ordinal value (" + sValue
					+ ") found in " + sName + " (" + group + ")"));
		}

		if (!hmDistribution.containsKey(group))
			hmDistribution.put(group, 0);

		if (!hmSelectedDistribution.containsKey(group))
			hmSelectedDistribution.put(group, 0);

		if (iMaxIndex < group)
			iMaxIndex = group;

	}

	public String getName() {
		return sName;
	}

	/**
	 * Returns the index of the given string value.
	 * 
	 * @param value
	 *            string representation of a value.
	 * @return the index of this representation, or -1 if not defined.
	 */
	public int getIndex(String value) {

		if (hmNominalLookup.containsKey(value))
			return hmNominalLookup.get(value).getGroup();
		return -1;
	}

	public int getMaxIndex() {

		return iMaxIndex;
	}

	/**
	 * Returns the Attribute Name
	 * 
	 * @return
	 */
	public ArrayList<String> getAttributeNames() {

		ArrayList<String> names = new ArrayList<String>();
		ArrayList<Integer> ks2 = new ArrayList<Integer>();
		ks2.addAll(hmGroupLookup.keySet());
		java.util.Collections.sort(ks2);

		names.add("NAV");
		for (Integer i : ks2)
			names.add(hmGroupLookup.get(i).getGroupName());

		return names;
	}

	/**
	 * Returns the string representation of the given index.
	 * 
	 * @param index
	 * @return
	 */
	public String getParameterString(int index) {
		if (hmGroupLookup.containsKey(index))
			return hmGroupLookup.get(index).getGroupName();
		return "";
	}

	/**
	 * Increases the internal distribution calculation for the given index.
	 * 
	 * @param index
	 */
	public void incDistribution(int index) {

		if (!hmDistribution.containsKey(index)) {
			hmDistribution.put(index, 0);
		}
		hmDistribution.put(index, hmDistribution.get(index) + 1);
	}

	/**
	 * Dencreases the internal distribution calculation for the given index.
	 * 
	 * @param index
	 */
	public void decDistribution(int index) {

		if (!hmDistribution.containsKey(index)) {
			hmDistribution.put(index, 0);
			return;
		}
		hmDistribution.put(index, hmDistribution.get(index) - 1);
		if (hmDistribution.get(index) < 0) {
			hmDistribution.put(index, 0);
		}
	}

	/**
	 * Increases the internal selected distribution calculation for the given index.
	 * 
	 * @param index
	 */
	public void incSelectedDistribution(int index) {

		if (!hmSelectedDistribution.containsKey(index)) {
			hmSelectedDistribution.put(index, 0);
		}
		hmSelectedDistribution.put(index, hmSelectedDistribution.get(index) + 1);
	}

	/**
	 * Decreases the internal selected distribution calculation for the given index.
	 * 
	 * @param index
	 */
	public void decSelectedDistribution(int index) {

		if (!hmSelectedDistribution.containsKey(index)) {
			hmSelectedDistribution.put(index, 0);
			return;
		}
		hmSelectedDistribution.put(index, hmSelectedDistribution.get(index) - 1);
		if (hmSelectedDistribution.get(index) < 0) {
			hmSelectedDistribution.put(index, 0);
		}
	}

	/**
	 * Delivers the distribution of this type 1st dimension: 0->overall 1->selected
	 */
	public ArrayList<ArrayList<Float>> getDistributionNormalized() {

		TreeSet<Integer> sks = new TreeSet<Integer>();
		sks.addAll(hmDistribution.keySet());
		Integer[] sk = sks.toArray(new Integer[sks.size()]);

		ArrayList<ArrayList<Float>> distList = new ArrayList<ArrayList<Float>>();
		distList.add(new ArrayList<Float>(sk.length));
		distList.add(new ArrayList<Float>(sk.length));

		int max = 0;

		for (int i = 0; i < sks.size(); ++i) {
			int d = hmDistribution.get(sk[i]);
			if (d > max) {
				max = d;
			}
			float dist = 0;
			if (hmSelectedDistribution.containsKey(sk[i])) {
				dist = hmSelectedDistribution.get(sk[i]);
			}

			distList.get(0).add((float) d);
			distList.get(1).add(dist);
		}

		for (int i = 0; i < 2; ++i) {
			for (int j = 0; j < distList.get(i).size(); ++j) {
				distList.get(i).set(j, distList.get(i).get(j) / max);
			}
		}

		return distList;
	}

	/**
	 * Prints the current distribution to the console
	 */
	public void printDistribution() {

		ArrayList<ArrayList<Float>> dist = getDistributionNormalized();

		for (int i = 0; i < dist.get(0).size(); ++i)
			System.out.println(" -> " + i + " > " + dist.get(0).get(i) + " " + dist.get(1).get(i));
	}

}
