package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.clinical.glyph.EGlyphSettingIDs;
import org.caleydo.core.view.opengl.util.wavefrontobjectloader.ObjectGroup;
import org.caleydo.core.view.opengl.util.wavefrontobjectloader.ObjectLoader;
import org.caleydo.core.view.opengl.util.wavefrontobjectloader.ObjectModel;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Stores the xml definition of a glyph object, matching to the wavefront object.
 * 
 * @author Stefan Sauer
 */
public class GlyphObjectDefinition {
	public enum DIRECTION {
		X,
		Y,
		Z
	}

	public enum ANCHOR {
		BOTTOM,
		TOP,
		LEFT,
		RIGHT,
		BACK,
		FRONT
	}

	private int iDetailLevel = -1;
	private String sDescription = "";
	private String sourceFile = null;
	private HashMap<String, GlyphObjectDefinitionPart> parts;
	private ArrayList<String> partOrder;
	private ObjectModel model = null;

	private ILog logger;

	/**
	 * Constructor
	 */
	public GlyphObjectDefinition() {
		parts = new HashMap<String, GlyphObjectDefinitionPart>();
		partOrder = new ArrayList<String>();
		logger = GeneralManager.get().getLogger();
	}

	/**
	 * Sets the detail level of this Object (This Object IS the detail level)
	 * 
	 * @param detaillevel
	 */
	public void setDetailLevel(int detaillevel) {
		iDetailLevel = detaillevel;
	}

	/**
	 * Returns the detail level of this Object
	 * 
	 * @return
	 */
	public int getDetailLevel() {
		return iDetailLevel;
	}

	/**
	 * Loads the Wavefront Object file.
	 * 
	 * @param file
	 */
	public void setSourceFile(String file) {
		sourceFile = file;
		if (sourceFile != null) {
			loadObjectFile();
		}
	}

	/**
	 * Loads the Wavefront Object file.
	 */
	public void loadObjectFile() {
		ObjectLoader loader = new ObjectLoader();
		model = loader.loadFile(sourceFile);
	}

	/**
	 * Sets a description of this detail level.
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		sDescription = description;
	}

	/**
	 * Returns the description of this object.
	 * 
	 * @return description text
	 */
	public String getDescription() {
		return sDescription;
	}

	/**
	 * Adds a object part to this Glyph Object.
	 * 
	 * @param name
	 */
	public void addGlyphPart(String name) {
		if (!parts.containsKey(name)) {
			partOrder.add(name);
			GlyphObjectDefinitionPart part = new GlyphObjectDefinitionPart();
			parts.put(name, part);
		}
	}

	public void addGlyphAnchor(String name, ANCHOR anc, String to) {
		if (parts.containsKey(name)) {
			parts.get(name).addAnchor(anc, to);
		}
	}

	public String getGlyphAnchorPlace(String name, ANCHOR anchor) {
		if (!parts.containsKey(name)) {
			logger.log(new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
				"GlyphObjectDefinition::getAnchorPlace() - " + "part does not exist '" + name + "'"));
			return null;
		}
		return parts.get(name).getAnchorPlace(anchor);
	}

	/**
	 * This adds a possible parameter (SCALE/COLOR) to the glyph object.
	 * 
	 * @param part
	 *            name
	 * @param transformation
	 *            type
	 * @param value
	 * @param description
	 */
	public void addGlyphPartParameter(String name, String type, String value, String description) {
		if (!parts.containsKey(name)) {
			logger.log(new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
				"GlyphObjectDefinition::addGlyphPartParameter() - " + "part does not exist '" + name + "'"));
			return;
		}

		parts.get(name).addParameter(type, value, description);
	}

	public ArrayList<String> getObjectPartNames() {
		return new ArrayList<String>(partOrder);
	}

	public ObjectGroup getObjectPart(String name) {
		if (model == null)
			return null;

		return model.getObjectGroup(name);
	}

	public GlyphObjectDefinitionPart getObjectPartDefinition(String name) {
		if (!parts.containsKey(name)) {
			logger
				.log(new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
					"GlyphObjectDefinition::getObjectPartDefinition() - " + "part does not exist '" + name
						+ "'"));
			return null;
		}
		return parts.get(name);
	}

	public boolean canPartScale(String name, DIRECTION dir) {
		if (!parts.containsKey(name)) {
			logger.log(new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
				"GlyphObjectDefinition::canPartScale() - " + "part does not exist '" + name + "'"));
			return false;
		}
		return parts.get(name).canScale(dir);
	}

	public boolean canPartColorChange(String name) {

		if (!parts.containsKey(name)) {
			logger.log(new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
				"GlyphObjectDefinition::canPartColorChange() - " + "part does not exist '" + name + "'"));
			return false;
		}
		GlyphObjectDefinitionPart def = parts.get(name);

		if (def.getColor(0) != null)
			return true;

		return false;
	}

	/**
	 * Gets the Description of the possible transformation.
	 * 
	 * @param group
	 *            name
	 * @param type
	 * @param direction
	 * @return
	 */
	public String getPartParameterDescription(String name, EGlyphSettingIDs type, DIRECTION dir) {
		if (!parts.containsKey(name)) {
			logger.log(new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
				"GlyphObjectDefinition::getPartParameterDescription() - " + "part does not exist '" + name
					+ "'"));
			return "";
		}
		GlyphObjectDefinitionPart def = parts.get(name);

		String description = def.getDescription(type, dir);

		if (description != null)
			return description;

		return "";
	}

	public void setPartParameterIndex(String name, EGlyphSettingIDs type, DIRECTION dir, int colNum) {
		if (!parts.containsKey(name)) {
			logger.log(new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
				"GlyphObjectDefinition::setPartParameterIndex() - " + "part does not exist '" + name + "'"));
			return;
		}
		GlyphObjectDefinitionPart def = parts.get(name);

		def.setParameterIndex(type, dir, colNum);
	}

	public int getPartParameterIndexExternal(String name, EGlyphSettingIDs type, DIRECTION dir) {
		if (!parts.containsKey(name)) {
			logger.log(new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
				"GlyphObjectDefinition::setPartParameterIndex() - " + "part does not exist '" + name + "'"));
			return -1;
		}
		return parts.get(name).getParameterIndexExternal(type, dir);
	}

	public int getPartParameterIndexInternal(String name, EGlyphSettingIDs type, DIRECTION dir) {
		if (!parts.containsKey(name)) {
			logger.log(new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
				"GlyphObjectDefinition::setPartParameterIndex() - " + "part does not exist '" + name + "'"));
			return -1;
		}
		return parts.get(name).getParameterIndexInternal(type, dir);
	}

}
