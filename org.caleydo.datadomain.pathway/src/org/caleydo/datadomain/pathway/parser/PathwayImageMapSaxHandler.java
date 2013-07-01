/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.parser;

import java.awt.Rectangle;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX handler for loading KEGG imagemaps.
 *
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class PathwayImageMapSaxHandler extends DefaultHandler {

	protected Attributes attributes;

	protected String sAttributeName = "";

	public PathwayImageMapSaxHandler() {
		super();
	}

	@Override
	public void startElement(String namespaceURI, String sSimpleName, String sQualifiedName, Attributes attributes)
			throws SAXException {

		String sElementName = sSimpleName;
		this.attributes = attributes;

		if ("".equals(sElementName)) {
			sElementName = sQualifiedName; // namespaceAware = false
		}

		if (attributes != null) {
			if (sElementName.equals("imagemap")) {
				handleImageMapTag();
			} else if (sElementName.equals("area")) {
				handleAreaTag();
			}
		}
	}

	/**
	 * Reacts on the elements of the imagemap tag. An example imagemap tag looks like this:
	 */
	protected void handleImageMapTag() {

		// String sImageLink = "";

		sAttributeName = attributes.getLocalName(0);

		if (sAttributeName.equals("")) {
			sAttributeName = attributes.getQName(0);
		}

		if (sAttributeName.equals("image")) {
			// sImageLink = attributes.getValue(0);
		}

		// generalManager.getPathwayManager().createPathwayImageMap(sImageLink);
	}

	/**
	 * Reacts on the elements of the area tag. An example area tag looks like this: <area shape="rect"
	 * coords="439,63,558,98" link="data/XML/pathways/map01196.html" />
	 */
	protected void handleAreaTag() {

		String sCoords = "";
		// String sImageLink = "";
		String sShape = "";
		Rectangle rectArea = new Rectangle();

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
			sAttributeName = attributes.getLocalName(iAttributeIndex);

			if ("".equals(sAttributeName)) {
				sAttributeName = attributes.getQName(iAttributeIndex);
			}

			if (sAttributeName.equals("coords")) {
				sCoords = attributes.getValue(iAttributeIndex);
			} else if (sAttributeName.equals("link")) {
				// sImageLink = attributes.getValue(iAttributeIndex);
			} else if (sAttributeName.equals("shape")) {
				sShape = attributes.getValue(iAttributeIndex);
			}

		}

		// TODO: handle circular shapes!
		if (!sShape.equals("rect"))
			return;

		// Extract coordinates and set rectangle
		StringTokenizer token = new StringTokenizer(sCoords, ",");

		rectArea.x = Integer.parseInt(token.nextToken());
		rectArea.y = Integer.parseInt(token.nextToken());
		rectArea.add(Integer.parseInt(token.nextToken()), Integer.parseInt(token.nextToken()));

		// ((PathwayManager)
		// generalManager.getPathwayManager()).getCurrentPathwayImageMap().addArea(rectArea,
		// sImageLink);
	}
}
