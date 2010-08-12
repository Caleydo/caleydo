package org.caleydo.view.glyph.parser;

import gleem.linalg.Vec4f;

import java.util.Vector;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.parser.xml.sax.handler.AXmlParserHandler;
import org.caleydo.view.glyph.gridview.GLGlyphGenerator;
import org.caleydo.view.glyph.gridview.GlyphObjectDefinition;
import org.caleydo.view.glyph.gridview.GlyphObjectDefinition.ANCHOR;
import org.caleydo.view.glyph.gridview.GlyphObjectDefinition.DIRECTION;
import org.caleydo.view.glyph.gridview.GlyphObjectDefinitionPart;
import org.caleydo.view.glyph.gridview.data.GlyphAttributeType;
import org.caleydo.view.glyph.manager.EGlyphSettingIDs;
import org.eclipse.core.runtime.Status;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler
 * @author Sauer Stefan
 */
public class GlyphDefinitionSaxHandler
	extends AXmlParserHandler {
	protected Attributes attributes;

	protected String sAttributeName = "";

	protected Vector<String> tagHierarchie = new Vector<String>();

	private GlyphAttributeType gatActualColumn = null;

	private GlyphObjectDefinition glyphDefinition = null;
	private GlyphObjectDefinitionPart glyphDefinitionPart = null;
	private String glyphPartName = null;

	public GlyphDefinitionSaxHandler() {
		super();

		setXmlActivationTag("glyphview");
	}

	@Override
	public void startElement(String namespaceURI, String sSimpleName, String sQualifiedName,
		Attributes attributes) throws SAXException {

		String sElementName = sSimpleName;
		this.attributes = attributes;

		if ("".equals(sElementName)) {
			sElementName = sQualifiedName;
		}

		if (attributes != null) {

			if (sElementName.equals("glyphview")) {
				tagHierarchie.clear();
			}
			else if (sElementName.equals("profile")) {
				handleProfileTag();
			}
			else if (sElementName.equals("item")) {
				handleItemTag();
			}
			else if (sElementName.equals("column")) {
				handleColumnBeginTag();
			}
			else if (sElementName.equals("nominal")) {
				handleNominalTag();
			}
			else if (sElementName.equals("int")) {
				handleIntTag();
			}
			else if (sElementName.equals("color")) {
				handleColorTag();
			}
			else if (sElementName.equals("glyph")) {
				handleGlyphTag();
			}
			else if (sElementName.equals("part")) {
				handleGlyphPartTag();
			}
			else if (sElementName.equals("parameter")) {
				handleGlyphParameterTag();
			}
			else if (sElementName.equals("anchor")) {
				handleGlyphAnchorTag();
			}

			tagHierarchie.add(sElementName);
		}
	}

	@Override
	public void endElement(String namespaceURI, String sSimpleName, String sQualifiedName)
		throws SAXException {

		// emit("</"+sName+">");

		String eName = "".equals(sSimpleName) ? sQualifiedName : sSimpleName;
		tagHierarchie.remove(tagHierarchie.size() - 1);

		if (null != eName) {
			if (eName.equals("column")) {
				handleColumnEndTag();
			}
			if (eName.equals("item")) {
				handleItemEndTag();
			}
			if (eName.equals("glyph")) {
				handleGlyphEndTag();

			}
			if (eName.equals(sOpeningTag)) {
				/**
				 * section (xml block) finished, call callback function from IXmlParserManager
				 */
				xmlParserManager.sectionFinishedByHandler(this);
			}
		}
	}

	private void handleItemTag() {

		String pTag = tagHierarchie.lastElement(); // parent tag

		if (pTag.equals("settings")) {
			handleSettings();
		}

	}

	private void handleItemEndTag() {
		if (glyphDefinitionPart != null) {
			glyphDefinitionPart = null;
		}
	}

	private void handleSettings() {

		String type = "";
		String colnum = "";
		String level = "";
		String on = "";
		String direction = "";

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
			String sAttributeName = attributes.getLocalName(iAttributeIndex);

			if (sAttributeName.equals("")) {
				sAttributeName = attributes.getQName(iAttributeIndex);
			}
			if (sAttributeName.equals("level")) {
				level = attributes.getValue(iAttributeIndex);
			}
			if (sAttributeName.equals("type")) {
				type = attributes.getValue(iAttributeIndex);
			}
			if (sAttributeName.equals("colnum")) {
				colnum = attributes.getValue(iAttributeIndex);
			}
			if (sAttributeName.equals("on")) {
				on = attributes.getValue(iAttributeIndex);
			}
			if (sAttributeName.equals("direction")) {
				direction = attributes.getValue(iAttributeIndex);
			}
		}

		if (type.equals("sort")) {
			generalManager.getGlyphManager().addSortColumn(colnum);
		}
		if (type.equals("scatterPlotAxisX")) {
			generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.SCATTERPLOTX, colnum);
		}
		if (type.equals("scatterPlotAxisY")) {
			generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.SCATTERPLOTY, colnum);
		}

		if (type.equals("scale")) {
			if (!level.equals("") && !direction.equals("")) {
				int iLevel = Integer.parseInt(level);
				int iColnum = Integer.parseInt(colnum);
				DIRECTION dir = DIRECTION.valueOf(direction.toUpperCase());

				GLGlyphGenerator.getDetailLevelModel(iLevel).setPartParameterIndex(on,
					EGlyphSettingIDs.SCALE, dir, iColnum);
			}
		}

		if (type.equals("color") && !level.equals("")) {
			int iLevel = Integer.parseInt(level);
			int iColnum = Integer.parseInt(colnum);

			GLGlyphGenerator.getDetailLevelModel(iLevel).setPartParameterIndex(on, EGlyphSettingIDs.COLOR,
				null, iColnum);

			glyphDefinitionPart = GLGlyphGenerator.getDetailLevelModel(iLevel).getObjectPartDefinition(on);
		}

	}

	private void handleProfileTag() {

		// we might want to support some profiles in the future
	}

	private void handleColumnBeginTag() {

		// String type = "";
		String col = "";
		String label = "";
		int colnum = 0;

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
			String sAttributeName = attributes.getLocalName(iAttributeIndex);

			if (sAttributeName.equals("")) {
				sAttributeName = attributes.getQName(iAttributeIndex);
			}
			if (sAttributeName.equals("colnum")) {
				col = attributes.getValue(iAttributeIndex);
			}
			if (sAttributeName.equals("label")) {
				label = attributes.getValue(iAttributeIndex);
			}
		}

		try {
			colnum = Integer.parseInt(col);
		}
		catch (NumberFormatException ex) {
			gatActualColumn = null;
			generalManager.getLogger().log(
				new Status(Status.ERROR, GeneralManager.PLUGIN_ID,
					"GlyphSaxDefinitionHandler:: colnumber is not an integer! (" + label + ")", ex));
			return;
		}

		gatActualColumn = new GlyphAttributeType(label, colnum);

	}

	private void handleColumnEndTag() {

		if (gatActualColumn != null) {
			generalManager.getGlyphManager().addColumnAttributeType(gatActualColumn);
		}
	}

	private void handleNominalTag() {

		// <nominal string="X" group="0" numeric="0.0" />
		String pTag = tagHierarchie.lastElement(); // parent tag

		if (!pTag.equals("column")) {
			generalManager.getLogger().log(
				new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
					"GlyphSaxDefinitionHandler::handleNominalTag() - nominal tag not in column tag embeded"));
			return;
		}

		gatActualColumn.setDoesAutomaticAttribute(false);

		String st = "";
		String gr = "";
		int igr = -1;
		String nu = "";
		float fnu = 0.0f;

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
			String sAttributeName = attributes.getLocalName(iAttributeIndex);

			if (sAttributeName.equals("")) {
				sAttributeName = attributes.getQName(iAttributeIndex);
			}
			if (sAttributeName.equals("string")) {
				st = attributes.getValue(iAttributeIndex);
			}
			if (sAttributeName.equals("group")) {
				gr = attributes.getValue(iAttributeIndex);
			}
			if (sAttributeName.equals("numeric")) {
				nu = attributes.getValue(iAttributeIndex);
			}
		}

		// if numeric is not set its the same as the group number
		if (nu.equals("")) {
			nu = gr;
		}

		// convert numbers
		try {
			fnu = Float.parseFloat(nu);
		}
		catch (NumberFormatException ex) {
			generalManager.getLogger().log(
				new Status(Status.ERROR, GeneralManager.PLUGIN_ID,
					"GlyphSaxDefinitionHandler::handleNominalTag() nominal numeric is not an float!", ex));
			return;
		}
		try {
			igr = Integer.parseInt(gr);
		}
		catch (NumberFormatException ex) {
			generalManager.getLogger().log(
				new Status(Status.ERROR, GeneralManager.PLUGIN_ID,
					"GlyphSaxDefinitionHandler::handleNominalTag() group is not an integer!", ex));
			return;
		}

		// add this parameter
		gatActualColumn.addAttribute(igr, st, fnu);

	}

	private void handleIntTag() {

		// <int min="1900" max="2008" interval="1" />
		String pTag = tagHierarchie.lastElement(); // parent tag

		if (!pTag.equals("column")) {
			generalManager.getLogger().log(
				new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
					"GlyphSaxDefinitionHandler::handleNominalTag() - int tag not in column tag embeded"));
			return;
		}

		gatActualColumn.setDoesAutomaticAttribute(false);

		String smin = "";
		String smax = "";
		String sint = "";
		int imin = 0;
		int imax = 0;
		int iint = 0;

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
			String sAttributeName = attributes.getLocalName(iAttributeIndex);

			if (sAttributeName.equals("")) {
				sAttributeName = attributes.getQName(iAttributeIndex);
			}
			if (sAttributeName.equals("min")) {
				smin = attributes.getValue(iAttributeIndex);
			}
			if (sAttributeName.equals("max")) {
				smax = attributes.getValue(iAttributeIndex);
			}
			if (sAttributeName.equals("interval")) {
				sint = attributes.getValue(iAttributeIndex);
			}
		}

		try {
			imin = Integer.parseInt(smin);
			imax = Integer.parseInt(smax);
			iint = Integer.parseInt(sint);
		}
		catch (NumberFormatException ex) {
			generalManager.getLogger().log(
				new Status(Status.ERROR, GeneralManager.PLUGIN_ID,
					"GlyphSaxDefinitionHandler::handleIntTag() given data is not an integer!", ex));
			return;
		}

		for (int i = imin; i < imax; i += iint) {
			gatActualColumn.addAttribute(i - imin, Integer.toString(i), i);
		}

	}

	private void handleColorTag() {

		String pTag = tagHierarchie.lastElement(); // parent tag

		if (!pTag.equals("item")) {
			generalManager.getLogger().log(
				new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
					"GlyphSaxDefinitionHandler::handleColorTag() - color tag not in item tag embeded"));
			return;
		}

		Vec4f color = new Vec4f();

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
			String sAttributeName = attributes.getLocalName(iAttributeIndex);

			String svalue = attributes.getValue(iAttributeIndex);

			if (sAttributeName.equals("rgb")) { // html style
				if (svalue.charAt(0) == '#') {
					svalue = svalue.substring(1, svalue.length());
				}

				if (svalue.length() != 6) {
					generalManager
						.getLogger()
						.log(
							new Status(Status.ERROR, GeneralManager.PLUGIN_ID,
								"GlyphSaxDefinitionHandler::handleColorTag() color definition error (string too short (6))"));
					continue;
				}

				String red = svalue.substring(0, 2);
				String green = svalue.substring(2, 4);
				String blue = svalue.substring(4, 6);

				try {
					color.set(0, Integer.parseInt(red, 16) / 255.0f);
					color.set(1, Integer.parseInt(green, 16) / 255.0f);
					color.set(2, Integer.parseInt(blue, 16) / 255.0f);
				}
				catch (NumberFormatException ex) {
					generalManager.getLogger().log(
						new Status(Status.ERROR, GeneralManager.PLUGIN_ID,
							"GlyphSaxDefinitionHandler::handleColorTag() given data is not an float!", ex));
				}

			}
			else { // color component style

				float fvalue = 0.5f;
				try {
					fvalue = Float.parseFloat(svalue);
				}
				catch (NumberFormatException ex) {
					generalManager.getLogger().log(
						new Status(Status.ERROR, GeneralManager.PLUGIN_ID,
							"GlyphSaxDefinitionHandler::handleColorTag() given data is not an float!", ex));
				}

				if (sAttributeName.equals("rn")) {
					color.set(0, fvalue);
				}
				if (sAttributeName.equals("gn")) {
					color.set(1, fvalue);
				}
				if (sAttributeName.equals("bn")) {
					color.set(2, fvalue);
				}

				if (sAttributeName.equals("r")) {
					color.set(0, fvalue / 255.0f);
				}
				if (sAttributeName.equals("g")) {
					color.set(1, fvalue / 255.0f);
				}
				if (sAttributeName.equals("b")) {
					color.set(2, fvalue / 255.0f);
				}

				if (sAttributeName.equals("a")) {
					color.set(3, fvalue);
				}
			}
		}
		glyphDefinitionPart.addColor(color);
	}

	private void handleGlyphTag() {
		String pTag = tagHierarchie.lastElement(); // parent tag

		if (!pTag.equals("glyphobjects")) {
			generalManager
				.getLogger()
				.log(
					new Status(Status.ERROR, GeneralManager.PLUGIN_ID,
						"GlyphSaxDefinitionHandler::handleGlyphTag() - glyph tag not in glyphobjects tag embeded"));
			return;
		}

		String file = "";
		String description = "";
		int detaillevel = -1;

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
			String sAttributeName = attributes.getLocalName(iAttributeIndex);

			if (sAttributeName.equals("")) {
				sAttributeName = attributes.getQName(iAttributeIndex);
			}

			if (sAttributeName.equals("source")) {
				file = attributes.getValue(iAttributeIndex);
			}

			if (sAttributeName.equals("description")) {
				description = attributes.getValue(iAttributeIndex);
			}

			if (sAttributeName.equals("detaillevel")) {
				try {
					detaillevel = Integer.parseInt(attributes.getValue(iAttributeIndex));
				}
				catch (Exception e) {
					generalManager.getLogger().log(
						new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
							"GlyphSaxDefinitionHandler::handleGlyphTag() - detaillevel wasn't a integer", e));
				}
			}
		}
		glyphDefinition = new GlyphObjectDefinition();
		glyphDefinition.setDetailLevel(detaillevel);
		glyphDefinition.setDescription(description);
		glyphDefinition.setSourceFile(file);
	}

	private void handleGlyphEndTag() {
		if (glyphDefinition == null)
			return;

		GLGlyphGenerator.setDetailLevelModel(glyphDefinition);
		glyphDefinition = null;
		glyphPartName = null;
	}

	private void handleGlyphPartTag() {
		String pTag = tagHierarchie.lastElement(); // parent tag

		if (!pTag.equals("glyph")) {
			generalManager
				.getLogger()
				.log(
					new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
						"GlyphSaxDefinitionHandler::handleGlyphPartTag() - glyphpart tag not in glyph tag embeded"));
			return;
		}

		if (glyphDefinition == null) {
			generalManager.getLogger().log(
				new Status(Status.ERROR, GeneralManager.PLUGIN_ID,
					"GlyphSaxDefinitionHandler::handleGlyphPartTag() - wtf?"));
			return;
		}

		glyphPartName = null;

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
			String sAttributeName = attributes.getLocalName(iAttributeIndex);

			if (sAttributeName.equals("")) {
				sAttributeName = attributes.getQName(iAttributeIndex);
			}

			if (sAttributeName.equals("name")) {
				glyphPartName = attributes.getValue(iAttributeIndex);
			}
		}

		glyphDefinition.addGlyphPart(glyphPartName);
	}

	private void handleGlyphParameterTag() {
		String pTag = tagHierarchie.lastElement(); // parent tag

		if (!pTag.equals("part")) {
			generalManager
				.getLogger()
				.log(
					new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
						"GlyphSaxDefinitionHandler::handleGlyphParameterTag() - glyph parameter tag not in glyph part tag embeded"));
			return;
		}

		String type = null;
		String value = null;
		String description = null;

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
			String sAttributeName = attributes.getLocalName(iAttributeIndex);

			if (sAttributeName.equals("")) {
				sAttributeName = attributes.getQName(iAttributeIndex);
			}

			if (sAttributeName.equals("type")) {
				type = attributes.getValue(iAttributeIndex);
			}
			if (sAttributeName.equals("value")) {
				value = attributes.getValue(iAttributeIndex);
			}
			if (sAttributeName.equals("description")) {
				description = attributes.getValue(iAttributeIndex);
			}
		}

		if (type != null) {
			glyphDefinition.addGlyphPartParameter(glyphPartName, type, value, description);
		}
	}

	private void handleGlyphAnchorTag() {
		String pTag = tagHierarchie.lastElement(); // parent tag

		if (!pTag.equals("part")) {
			generalManager
				.getLogger()
				.log(
					new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
						"GlyphSaxDefinitionHandler::handleGlyphAnchorTag() - glyph parameter tag not in glyph part tag embeded"));
			return;
		}

		ANCHOR type = null;
		String to = null;

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
			String sAttributeName = attributes.getLocalName(iAttributeIndex);

			if (sAttributeName.equals("")) {
				sAttributeName = attributes.getQName(iAttributeIndex);
			}

			if (sAttributeName.equals("type")) {
				String temp = attributes.getValue(iAttributeIndex).toLowerCase().toUpperCase();
				type = ANCHOR.valueOf(temp);
			}
			if (sAttributeName.equals("to")) {
				to = attributes.getValue(iAttributeIndex);
			}

		}

		if (type != null) {
			glyphDefinition.addGlyphAnchor(glyphPartName, type, to);
		}
	}

	/**
	 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler#destroyHandler()
	 * @see org.caleydo.core.parser.xml.sax.handler.AXmlParserHandler#destroyHandler()
	 */
	@Override
	public void destroyHandler() {

		super.destroyHandler();
	}
}
