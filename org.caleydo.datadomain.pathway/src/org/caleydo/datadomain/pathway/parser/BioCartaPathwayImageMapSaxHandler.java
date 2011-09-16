package org.caleydo.datadomain.pathway.parser;

import java.util.ArrayList;
import java.util.Set;

import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.mapping.IDMappingManagerRegistry;
import org.caleydo.core.parser.xml.AXmlParserHandler;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.util.graph.IGraphItem;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class is able to parse BioCarta pathway files. The creation of the pathway
 * objects is triggered.
 * 
 * @author Marc Streit
 */
public class BioCartaPathwayImageMapSaxHandler extends AXmlParserHandler {

	private PathwayItemManager pathwayItemManager;
	private PathwayManager pathwayManager;

	private final static String BIOCARTA_EXTERNAL_URL_PATHWAY = "http://cgap.nci.nih.gov/Pathways/BioCarta/";
	private final static String BIOCARTA_EXTERNAL_URL_VERTEX = "http://cgap.nci.nih.gov";

	private Attributes attributes;

	private String sAttributeName = "";

	private boolean bReadTitle = false;

	private PathwayGraph currentPathway;

	private String sTitle = "";

	/**
	 * Constructor.
	 */
	public BioCartaPathwayImageMapSaxHandler() {
		super();

		pathwayItemManager = PathwayItemManager.get();
		pathwayManager = PathwayManager.get();

		setXmlActivationTag("span");
	}

	@Override
	public void startElement(String namespaceURI, String sSimpleName,
			String sQualifiedName, Attributes attributes) throws SAXException {

		String sElementName = sSimpleName;
		this.attributes = attributes;

		if ("".equals(sElementName)) {
			sElementName = sQualifiedName; // namespaceAware = false
		}

		if (attributes != null) {
			if (sElementName.equals("b")) {
				handleTitleTag();
			} else if (sElementName.equals("img")) {
				handleImageLinkTag();
			} else if (sElementName.equals("area")) {
				handleAreaTag();
			}
		}
	}

	@Override
	public void endElement(String namespaceURI, String sSimpleName, String sQualifiedName)
			throws SAXException {

		String sName = "".equals(sSimpleName) ? sQualifiedName : sSimpleName;

		if (sName.equals("map")) {
			// Early abort parsing current file
			xmlParserManager.sectionFinishedByHandler(this);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		if (!bReadTitle)
			return;

		for (int iCharIndex = start; iCharIndex < length; iCharIndex++) {
			sTitle += ch[iCharIndex];
		}

		bReadTitle = false;
	}

	/**
	 * Reacts on the elements of the <b> tag.
	 */
	protected void handleTitleTag() {

		if (sTitle.length() == 0) {
			bReadTitle = true;
		}
	}

	/**
	 * Reacts on the elements of the <b> tag.
	 */
	protected void handleImageLinkTag() {

		String sName = "";
		String sImageLink = "";

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
			sAttributeName = attributes.getLocalName(iAttributeIndex);

			if ("".equals(sAttributeName)) {
				sAttributeName = attributes.getQName(iAttributeIndex);
			}

			if (sAttributeName.equals("src")) {
				sImageLink = attributes.getValue(iAttributeIndex);
			} else if (sAttributeName.equals("name")) {
				sName = attributes.getValue(iAttributeIndex);
			}
		}

		if (sImageLink.length() == 0 || sName.length() == 0)
			return;

		sImageLink = sImageLink.substring(sImageLink.lastIndexOf('/') + 1,
				sImageLink.length());

		currentPathway = pathwayManager.createPathway(PathwayDatabaseType.BIOCARTA,
				"<name>", sTitle, sImageLink, BIOCARTA_EXTERNAL_URL_PATHWAY + sName);

		sTitle = "";
	}

	private void handleAreaTag() {

		String sName = "<unknown>";
		String sCoords = "";
		String sShape = "";
		String sExternalLink = "";

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
			sAttributeName = attributes.getLocalName(iAttributeIndex);

			if ("".equals(sAttributeName)) {
				sAttributeName = attributes.getQName(iAttributeIndex);
			}

			if (sAttributeName.equals("shape")) {
				sShape = attributes.getValue(iAttributeIndex);
			} else if (sAttributeName.equals("coords")) {
				sCoords = attributes.getValue(iAttributeIndex);
			} else if (sAttributeName.equals("href")) {
				sExternalLink = attributes.getValue(iAttributeIndex);

				if (sExternalLink.contains("BCID=")) {
					// Create name from link
					sName = sExternalLink.substring(
							sExternalLink.lastIndexOf("BCID=") + 5,
							sExternalLink.length());
				}
			}
		}

		// Convert BioCarta ID to DAVID ID
		IDMappingManager genomeIdManager = IDMappingManagerRegistry.get()
				.getIDMappingManager(IDCategory.getIDCategory("GENE"));

		Set<Integer> DataTableDavidID = genomeIdManager.getID(
				IDType.getIDType("BIOCARTA_GENE_ID"), IDType.getIDType("DAVID"), sName);

		if (DataTableDavidID == null)
			return;

		ArrayList<IGraphItem> alVertex = pathwayItemManager.createVertexGene(sName,
				"gene", BIOCARTA_EXTERNAL_URL_VERTEX + sExternalLink, "",
				DataTableDavidID);

		pathwayItemManager.createVertexRep(currentPathway, alVertex, sName, sShape,
				sCoords);
	}

	@Override
	public void destroyHandler() {
		super.destroyHandler();
	}
}
