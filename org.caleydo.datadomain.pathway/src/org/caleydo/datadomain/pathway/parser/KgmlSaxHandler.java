package org.caleydo.datadomain.pathway.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.parser.xml.AXmlParserHandler;
import org.caleydo.core.parser.xml.IXmlParserHandler;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayReactionEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayRelationEdge;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * XML Parser that is able to load KEGG pathway files. The KEGG XML files follow
 * the KGML. The class triggers the calls in the PathwayManager that actually
 * creates the pathway graph and the items + item reps.
 * 
 * @author Marc Streit
 */
public class KgmlSaxHandler
	extends AXmlParserHandler
	implements IXmlParserHandler {

	private PathwayItemManager pathwayItemManager;
	private PathwayManager pathwayManager;

	private Attributes attributes;

	private String attributeName = "";

	private PathwayGraph currentPathway;

	private ArrayList<PathwayVertex> currentVertices;

	private HashMap<Integer, PathwayVertexRep> hashKgmlEntryIdToVertexRepId = new HashMap<Integer, PathwayVertexRep>();

	private HashMap<String, PathwayVertexRep> hashKgmlNameToVertexRepId = new HashMap<String, PathwayVertexRep>();

	private HashMap<String, PathwayVertexRep> hashKgmlReactionIdToVertexRepId = new HashMap<String, PathwayVertexRep>();

	private String currentReactionName;
	// private String currentReactionType;

	private int currentEntryId;

	/**
	 * Constructor.
	 */
	public KgmlSaxHandler() {
		super();

		pathwayItemManager = PathwayItemManager.get();
		pathwayManager = PathwayManager.get();

		currentVertices = new ArrayList<PathwayVertex>();

		setXmlActivationTag("pathway");
	}

	@Override
	public void startElement(String namespaceURI, String sSimpleName, String sQualifiedName,
			Attributes attributes) throws SAXException {

		String sElementName = sSimpleName;
		this.attributes = attributes;

		if ("".equals(sElementName)) {
			sElementName = sQualifiedName; // namespaceAware = false
		}

		if (attributes != null) {
			if (sElementName.equals("pathway")) {
				handlePathwayTag();
			}
			else if (sElementName.equals("entry")) {
				handleEntryTag();
			}
			else if (sElementName.equals("graphics")) {
				handleGraphicsTag();
			}
			else if (sElementName.equals("relation")) {
				handleRelationTag();
			}
			else if (sElementName.equals("reaction")) {
				handleReactionTag();
			}
			else if (sElementName.equals("product")) {
				handleReactionProductTag();
			}
			else if (sElementName.equals("substrate")) {
				handleReactionSubstrateTag();
			}
		}
	}

	@Override
	public void endElement(String namespaceURI, String sSimpleName, String sQualifiedName)
			throws SAXException {

		String eName = "".equals(sSimpleName) ? sQualifiedName : sSimpleName;

		if (null != eName) {
			if (eName.equals(sOpeningTag)) {
				/**
				 * section (xml block) finished, call callback function from
				 * XmlParserManager
				 */
				xmlParserManager.sectionFinishedByHandler(this);
			}
		}
	}

	/**
	 * Reacts on the elements of the pathway tag. An example pathway tag looks
	 * like this: <pathway name="path:map00271" org="map" number="00271"
	 * title="Methionine metabolism"
	 * image="http://www.genome.jp/kegg/pathway/map/map00271.gif"
	 * link="http://www.genome.jp/dbget-bin/show_pathway?map00271">
	 */
	protected void handlePathwayTag() {

		String name = "";
		String title = "";
		String sImageLink = "";
		String externalLink = "";
		// int iKeggId = 0;

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
			attributeName = attributes.getLocalName(iAttributeIndex);

			if ("".equals(attributeName)) {
				attributeName = attributes.getQName(iAttributeIndex);
			}

			if (attributeName.equals("name")) {
				name = attributes.getValue(iAttributeIndex);
			}
			else if (attributeName.equals("title")) {
				title = attributes.getValue(iAttributeIndex);
			}
			// else if (sAttributeName.equals("number"))
			// {
			// iKeggId = new Integer(attributes.getValue(iAttributeIndex));
			// }
			else if (attributeName.equals("image")) {
				sImageLink = attributes.getValue(iAttributeIndex);
			}
			else if (attributeName.equals("link")) {
				externalLink = attributes.getValue(iAttributeIndex);
			}
		}

		if (title.length() == 0) {
			title = "unknown title";
		}

		String pathwayTexturePath = sImageLink.substring(sImageLink.lastIndexOf('/') + 1,
				sImageLink.length());

		// FIX inconsistency between XML data which state the pathway images as
		// GIFs - but we have them as
		// PNGs
		pathwayTexturePath = pathwayTexturePath.replace(".gif", ".png");

		currentPathway = pathwayManager.createPathway(PathwayDatabaseType.KEGG, name, title,
				pathwayTexturePath, externalLink);
	}

	/**
	 * Reacts on the elements of the entry tag. An example entry tag looks like
	 * this: <entry id="1" name="ec:1.8.4.1" type="enzyme" reaction="rn:R01292"
	 * link="http://www.genome.jp/dbget-bin/www_bget?enzyme+1.8.4.1">
	 */
	protected void handleEntryTag() {
		int entryId = 0;
		String name = "";
		String type = "";
		String externalLink = "";
		String reactionName = "";

		for (int attributeIndex = 0; attributeIndex < attributes.getLength(); attributeIndex++) {
			attributeName = attributes.getLocalName(attributeIndex);

			if ("".equals(attributeName)) {
				attributeName = attributes.getQName(attributeIndex);
			}

			if (attributeName.equals("id")) {
				entryId = Integer.valueOf(attributes.getValue(attributeIndex)).intValue();
			}
			else if (attributeName.equals("name")) {
				name = attributes.getValue(attributeIndex);
			}
			else if (attributeName.equals("type")) {
				type = attributes.getValue(attributeIndex);
			}
			else if (attributeName.equals("link")) {
				externalLink = attributes.getValue(attributeIndex);
			}
			else if (attributeName.equals("reaction")) {
				currentReactionName = attributes.getValue(attributeIndex);
			}
		}

		currentEntryId = entryId;
		currentVertices.clear();

		if (type.equals("gene")) {
			StringTokenizer tokenText = new StringTokenizer(name, " ");
			Integer davidId = -1;
			String tmpVertexName = "";
			Set<Integer> DataTableDavidID = new HashSet<Integer>();

			while (tokenText.hasMoreTokens()) {
				tmpVertexName = tokenText.nextToken();

				if (tmpVertexName.substring(4).equals("")) {
					continue;
				}

				try {
					IDMappingManager genomeIdManager = ((PathwayDataDomain) DataDomainManager
							.get().getDataDomainByType(PathwayDataDomain.DATA_DOMAIN_TYPE))
							.getGeneIDMappingManager();
					davidId = genomeIdManager.getID(IDType.getIDType("ENTREZ_GENE_ID"),
							IDType.getIDType("DAVID"),
							Integer.valueOf(tmpVertexName.substring(4)));
				}
				catch (Exception e) {
					// TODO: investigate!!
					System.out.println("TODO: check why the parsing error occurs");
				}

				if (davidId == null) {
					// TODO: what should we do in this case?
					// generalManager.getLogger().log(
					// Level.WARNING,
					// "NCBI Gene ID " + sTmpVertexName
					// + " cannot be mapped to David ID.");

					continue;
				}

				DataTableDavidID.add(davidId);
			}

			currentVertices.addAll(pathwayItemManager.createVertexGene(tmpVertexName, type,
					externalLink, DataTableDavidID));
		}
		else {
			PathwayVertex currentVertex = pathwayItemManager.createVertex(name, type,
					externalLink);

			currentVertices.add(currentVertex);
		}
	}

	/**
	 * Reacts on the elements of the graphics tag. An example graphics tag looks
	 * like this: <graphics name="1.8.4.1" fgcolor="#000000" bgcolor="#FFFFFF"
	 * type="rectangle" x="142" y="304" width="45" height="17"/>
	 */
	protected void handleGraphicsTag() {

		String name = "";
		String shapeType = "";
		short height = 0;
		short width = 0;
		short x = 0;
		short y = 0;

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
			attributeName = attributes.getLocalName(iAttributeIndex);

			try {
				if ("".equals(attributeName)) {
					attributeName = attributes.getQName(iAttributeIndex);
				}

				if (attributeName.equals("name")) {
					name = attributes.getValue(iAttributeIndex);
				}
				else if (attributeName.equals("height")) {
					height = new Short(attributes.getValue(iAttributeIndex));
				}
				else if (attributeName.equals("width")) {
					width = new Short(attributes.getValue(iAttributeIndex));
				}
				else if (attributeName.equals("x")) {
					x = new Short(attributes.getValue(iAttributeIndex));
				}
				else if (attributeName.equals("y")) {
					y = new Short(attributes.getValue(iAttributeIndex));
				}
				else if (attributeName.equals("type")) {
					shapeType = attributes.getValue(iAttributeIndex);
				}
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		if (currentVertices.isEmpty()) {
			// TODO: investigate!
			System.out.println("TODO: check why the parsing error occurs");
			return;
		}

		PathwayVertexRep vertexRep = pathwayItemManager.createVertexRep(currentPathway,
				currentVertices, name, shapeType, x, y, width, height);

		hashKgmlEntryIdToVertexRepId.put(currentEntryId, vertexRep);

		hashKgmlNameToVertexRepId.put(currentVertices.get(0).getName(), vertexRep);

		if (currentReactionName != null && !currentReactionName.isEmpty())
			hashKgmlReactionIdToVertexRepId.put(currentReactionName, vertexRep);
	}

	/**
	 * Reacts on the elements of the relation tag. An example relation tag looks
	 * like this: <relation entry1="28" entry2="32" type="ECrel">
	 */
	protected void handleRelationTag() {

		int sourceVertexId = 0;
		int targetVertexId = 0;
		String type = "";

		for (int attributeIndex = 0; attributeIndex < attributes.getLength(); attributeIndex++) {
			attributeName = attributes.getLocalName(attributeIndex);

			if ("".equals(attributeName)) {
				attributeName = attributes.getQName(attributeIndex);
			}

			if (attributeName.equals("type")) {
				type = attributes.getValue(attributeIndex);
			}
			else if (attributeName.equals("entry1")) {
				sourceVertexId = Integer.valueOf(attributes.getValue(attributeIndex))
						.intValue();
			}
			else if (attributeName.equals("entry2")) {
				targetVertexId = Integer.valueOf(attributes.getValue(attributeIndex))
						.intValue();
			}

			// System.out.println("Attribute name: " + attributeName);
			// System.out.println("Attribute value: " +
			// attributes.getValue(attributeIndex));
		}

		PathwayVertexRep sourceVertexRep = hashKgmlEntryIdToVertexRepId.get(sourceVertexId);
		PathwayVertexRep targetVertexRep = hashKgmlEntryIdToVertexRepId.get(targetVertexId);

		// Create edge (data)
		PathwayRelationEdge relationEdge = pathwayItemManager.createRelationEdge(
				sourceVertexRep.getPathwayVertices(), targetVertexRep.getPathwayVertices(),
				type);

		// Create edge representation
		pathwayItemManager.createRelationEdgeRep(currentPathway, relationEdge,
				sourceVertexRep, targetVertexRep);
	}

	protected void handleSubtypeTag() {

		// String sName = "";
		// int iCompoundId = 0;
		//
		// for (int iAttributeIndex = 0; iAttributeIndex <
		// attributes.getLength();
		// iAttributeIndex++)
		// {
		// sAttributeName = attributes.getLocalName(iAttributeIndex);
		//
		// if ("".equals(sAttributeName))
		// {
		// sAttributeName = attributes.getQName(iAttributeIndex);
		// }
		//
		// if (sAttributeName.equals("name"))
		// sName = attributes.getValue(iAttributeIndex);
		// else if (sAttributeName.equals("value"))
		// {
		// // TODO: handle special case of value "-->" in signalling pathways
		// if (attributes.getValue(iAttributeIndex).contains("-") ||
		// attributes.getValue(iAttributeIndex).contains("=") ||
		// attributes.getValue(iAttributeIndex).contains("+") ||
		// attributes.getValue(iAttributeIndex).contains(":") ||
		// attributes.getValue(iAttributeIndex).contains("."))
		// iCompoundId = 0;
		// else
		// iCompoundId = new Integer(attributes.getValue(iAttributeIndex));
		// }
		//
		//
		// //System.out.println("Attribute name: " +sAttributeName);
		// //System.out.println("Attribute value: "
		// +attributes.getValue(iAttributeIndex));
		// }
		//
		// if (sName.equals("compound"))
		// {
		// //retrieve the internal element ID and add the compound value to the
		// edge
		// generalManager.getSingelton().getPathwayElementManager().
		// addRelationCompound(kgmlIdToElementIdLUT.get(iCompoundId));
		// }
	}

	/**
	 * Reacts on the elements of the reaction tag. An example reaction tag looks
	 * like this: <reaction name="rn:R01001" type="irreversible">
	 */
	protected void handleReactionTag() {

		String reactionName = "";
		// String reactionType = "";

		for (int attributeIndex = 0; attributeIndex < attributes.getLength(); attributeIndex++) {
			attributeName = attributes.getLocalName(attributeIndex);

			if ("".equals(attributeName)) {
				attributeName = attributes.getQName(attributeIndex);
			}

			if (attributeName.equals("type")) {
				// reactionType = attributes.getValue(attributeIndex);
			}
			else if (attributeName.equals("name")) {
				reactionName = attributes.getValue(attributeIndex);
			}
		}

		currentReactionName = reactionName;
		// currentReactionType = reactionType;
	}

	/**
	 * Reacts on the elements of the reaction substrate tag. An example reaction
	 * substrate tag looks like this: <substrate name="cpd:C01118"/>
	 */
	protected void handleReactionSubstrateTag() {

		String reactionSubstrateName = "";

		for (int attributeIndex = 0; attributeIndex < attributes.getLength(); attributeIndex++) {
			attributeName = attributes.getLocalName(attributeIndex);

			if ("".equals(attributeName)) {
				attributeName = attributes.getQName(attributeIndex);
			}

			if (attributeName.equals("name")) {
				reactionSubstrateName = attributes.getValue(attributeIndex);
			}
		}

		PathwayVertexRep sourceVertexRep = hashKgmlNameToVertexRepId
				.get(reactionSubstrateName);

		PathwayVertexRep targetVertexRep = hashKgmlReactionIdToVertexRepId
				.get(currentReactionName);

		// Edge from the substrate to the gene
		pathwayItemManager.createReactionEdgeRep(currentPathway, sourceVertexRep,
				targetVertexRep);
	}

	/**
	 * Reacts on the elements of the reaction product tag. An example reaction
	 * product tag looks like this: <product name="cpd:C02291"/>
	 */
	protected void handleReactionProductTag() {

		String reactionProductName = "";

		for (int attributeIndex = 0; attributeIndex < attributes.getLength(); attributeIndex++) {
			attributeName = attributes.getLocalName(attributeIndex);

			if ("".equals(attributeName)) {
				attributeName = attributes.getQName(attributeIndex);
			}

			if (attributeName.equals("name")) {
				reactionProductName = attributes.getValue(attributeIndex);
			}
		}

		PathwayVertexRep sourceVertexRep = hashKgmlReactionIdToVertexRepId
				.get(currentReactionName);

		PathwayVertexRep targetVertexRep = hashKgmlNameToVertexRepId.get(reactionProductName);

		// Edge from the product to the gene
		pathwayItemManager.createReactionEdgeRep(currentPathway, sourceVertexRep,
				targetVertexRep);
	}

	@Override
	public void destroyHandler() {

		super.destroyHandler();

		hashKgmlEntryIdToVertexRepId.clear();
		hashKgmlNameToVertexRepId.clear();
		hashKgmlReactionIdToVertexRepId.clear();
	}
}
