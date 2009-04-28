package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.clinical.glyph.GlyphManager;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.data.GlyphAttributeType;

/**
 * This class represents a single glyph. It stores all its parameter.
 * 
 * @author Steve
 */
public class GlyphEntry {

	private IGeneralManager generalManager;

	private GlyphManager gman;

	private int id_;

	private boolean selected_ = false;

	private Vector<Integer> parameter_ = null;

	private HashMap<String, String> vsParameterString = new HashMap<String, String>();

	private int glList_ = 0;

	private int glListSelected_ = 0;

	public GlyphEntry(int id) {
		this.generalManager = GeneralManager.get();
		gman = (GlyphManager) generalManager.getGlyphManager();
		id_ = id;
		parameter_ = new Vector<Integer>();

	}

	/**
	 * Returns the internal mapping id of the glyhp. Use the IDMappingManager to get the id inside the given
	 * csv file
	 * 
	 * @return the id of the glyph.
	 */
	public int getID() {
		return id_;
	}

	public void select() {
		if (selected_)
			return;

		selected_ = true;
		for (int i = 0; i < parameter_.size(); ++i) {
			generalManager.getGlyphManager().getGlyphAttributeTypeWithInternalColumnNumber(i)
				.incSelectedDistribution(parameter_.get(i));
		}
	}

	public void deSelect() {
		if (!selected_)
			return;

		selected_ = false;
		for (int i = 0; i < parameter_.size(); ++i) {
			generalManager.getGlyphManager().getGlyphAttributeTypeWithInternalColumnNumber(i)
				.decSelectedDistribution(parameter_.get(i));
		}
	}

	public boolean isSelected() {
		return selected_;
	}

	public int getGlList(GL gl) {
		if (selected_)
			return glListSelected_;

		return glList_;
	}

	public void addParameter(int value) {

		parameter_.add(value);
	}

	public void addStringParameter(String column, String value) {

		if (vsParameterString.containsKey(column)) {
			vsParameterString.remove(column);
		}
		vsParameterString.put(column, value);
	}

	public int getParameter(int index) {
		if (index < 0)
			return -1;
		if (parameter_.size() <= index)
			return -1;
		return parameter_.get(index);
	}

	public int getNumberOfParameters() {
		return parameter_.size();
	}

	public String getStringParameter(String colname) {

		if (!vsParameterString.containsKey(colname))
			return "";
		return vsParameterString.get(colname);
	}

	public ArrayList<String> getStringParameterColumnNames() {
		ArrayList<String> temp = new ArrayList<String>();
		temp.addAll(vsParameterString.keySet());
		return temp;
	}

	public int getNumberOfStringParameters() {
		return vsParameterString.size();
	}

	public void generateGLLists(GL gl, GLGlyphGenerator generator) {

		glListSelected_ = generator.generateGlyph(gl, this, true);
		glList_ = generator.generateGlyph(gl, this, false);
	}

	/**
	 * This method returns a String representation of the holding data
	 * 
	 * @param seperator
	 *            between the data fields
	 * @return data text
	 */
	public String getGlyphDescription(String seperator) {
		StringBuffer sInfoText = new StringBuffer();
		String name;
		String value;

		sInfoText.append("ID "
			+ GeneralManager.get().getIDMappingManager().getID(EMappingType.EXPERIMENT_INDEX_2_EXPERIMENT,
				id_) + seperator);

		for (int iAttributeIndex = 1; iAttributeIndex < gman.getGlyphAttributes().size(); ++iAttributeIndex) {

			name = gman.getGlyphAttributeTypeWithInternalColumnNumber(iAttributeIndex).getName();
			GlyphAttributeType type = gman.getGlyphAttributeTypeWithInternalColumnNumber(iAttributeIndex);

			value = type.getParameterString(getParameter(iAttributeIndex));

			sInfoText.append(name + ": " + value + seperator);
		}

		return sInfoText.toString();

	}

}
