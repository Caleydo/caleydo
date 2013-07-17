/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.edge.EPathwayReactionEdgeType;
import org.caleydo.datadomain.pathway.graph.item.edge.EPathwayRelationEdgeType;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayReactionEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayRelationEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGroupRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML Parser that is able to load KEGG pathway files. The KEGG XML files follow the KGML. The class triggers the calls
 * in the PathwayManager that actually creates the pathway graph and the items + item reps.
 *
 * @author Marc Streit
 */
public class KgmlSaxHandler extends DefaultHandler {

	private PathwayItemManager pathwayItemManager;
	private PathwayManager pathwayManager;

	private Attributes attributes;

	private String attributeName = "";

	private PathwayGraph currentPathway;

	private ArrayList<PathwayVertex> currentVertices;

	private HashMap<Integer, PathwayVertexRep> hashKgmlEntryIdToVertexRep = new HashMap<Integer, PathwayVertexRep>();

	private HashMap<String, PathwayVertexRep> hashKgmlReactionNameToVertexRep = new HashMap<String, PathwayVertexRep>();

	private String currentReactionName;
	private EPathwayReactionEdgeType currentReactionType;
	private int currentEntryId;
	private PathwayVertexGroupRep currentVertexGroupRep;

	private PathwayVertexRep relationSourceVertexRep;
	private PathwayVertexRep relationTargetVertexRep;
	private EPathwayRelationEdgeType relationType;

	private final File image;

	/**
	 * Constructor.
	 */
	public KgmlSaxHandler(File image) {
		super();

		pathwayItemManager = PathwayItemManager.get();
		pathwayManager = PathwayManager.get();

		currentVertices = new ArrayList<PathwayVertex>();

		this.image = image;
	}

	/**
	 * @return the currentPathway, see {@link #currentPathway}
	 */
	public PathwayGraph getCurrentPathway() {
		return currentPathway;
	}

	@Override
	public void startElement(String namespaceURI, String sSimpleName, String sQualifiedName, Attributes attributes)
			throws SAXException {

		String elementName = sSimpleName;
		this.attributes = attributes;

		if ("".equals(elementName)) {
			elementName = sQualifiedName; // namespaceAware = false
		}

		if (attributes != null) {
			if (elementName.equals("pathway")) {
				handlePathwayTag();
			}
			else if (elementName.equals("entry")) {
				handleEntryTag();
			}
			else if (elementName.equals("graphics")) {
				handleGraphicsTag();
			}
			else if (elementName.equals("relation")) {
				handleRelationTag();
			}
			else if (elementName.equals("reaction")) {
				handleReactionTag();
			}
			else if (elementName.equals("product")) {
				handleReactionProductTag();
			}
			else if (elementName.equals("substrate")) {
				handleReactionSubstrateTag();
			}
			else if (elementName.equals("subtype")) {
				handleSubtypeTag();
			}
			else if (elementName.equals("component")) {
				handleComponentTag();
			}
		}
	}

	@Override
	public void endElement(String namespaceURI, String sSimpleName, String sQualifiedName) throws SAXException {

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

		for (int attributeIndex = 0; attributeIndex < attributes.getLength(); attributeIndex++) {
			attributeName = attributes.getLocalName(attributeIndex);

			if ("".equals(attributeName)) {
				attributeName = attributes.getQName(attributeIndex);
			}

			if (attributeName.equals("name")) {
				name = attributes.getValue(attributeIndex);
			}
			else if (attributeName.equals("title")) {
				title = attributes.getValue(attributeIndex);
			}
			else if (attributeName.equals("image")) {
				sImageLink = attributes.getValue(attributeIndex);
			}
			else if (attributeName.equals("link")) {
				externalLink = attributes.getValue(attributeIndex);
			}
		}

		if (title.length() == 0) {
			title = "unknown title";
		}

		currentPathway = pathwayManager.createPathway(EPathwayDatabaseType.KEGG, name, title, image,
				externalLink);

		hashKgmlEntryIdToVertexRep.clear();
		hashKgmlReactionNameToVertexRep.clear();
		currentEntryId = -1;
		currentReactionName = null;
		currentReactionType = null;
		currentVertices.clear();
		currentVertexGroupRep = null;
	}

	/**
	 * Reacts on the elements of the entry tag. An example entry tag looks like this:
	 *
	 * <entry id="1" name="ec:1.8.4.1" type="enzyme" reaction="rn:R01292"
	 * link="http://www.genome.jp/dbget-bin/www_bget?enzyme+1.8.4.1">
	 *
	 */
	protected void handleEntryTag() {
		int entryId = 0;
		String name = "";
		String type = "";
		String externalLink = "";

		currentReactionName = null;

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
			Set<Integer> davidIDs = null;
			String vertexName = "";
			Set<Integer> mappingDavidIDs = new HashSet<Integer>();

			while (tokenText.hasMoreTokens()) {
				vertexName = tokenText.nextToken();

				String entrezID = vertexName.substring(4);
				if (entrezID.isEmpty()) {
					continue;
				}

				IDMappingManager genomeIdManager = ((PathwayDataDomain) DataDomainManager.get().getDataDomainByType(
						PathwayDataDomain.DATA_DOMAIN_TYPE)).getGeneIDMappingManager();
				try {
				davidIDs = genomeIdManager.getIDAsSet(IDType.getIDType("ENTREZ_GENE_ID"), IDType.getIDType("DAVID"),
						Integer.valueOf(entrezID));
				} catch (NumberFormatException e) {
					Logger.log(new Status(IStatus.INFO, this.toString(), "Invalid Entrez ID: " + entrezID));
					continue;
				}

				if (davidIDs == null) {
					Logger.log(new Status(IStatus.INFO, this.toString(), "No david mapping for Entrez ID: " + entrezID));
					continue;
				}

				mappingDavidIDs.addAll(davidIDs);
			}

			currentVertices.addAll(pathwayItemManager.createGeneVertex(vertexName, type, externalLink,
					mappingDavidIDs));
		}
		else {
			PathwayVertex currentVertex = pathwayItemManager.createVertex(name, type, externalLink);

			currentVertices.add(currentVertex);
		}
	}

	/**
	 * Handles compound tags which are subtags of group entry elements.
	 */
	protected void handleComponentTag() {

		String kgmlEntryID = attributes.getValue(0);

		PathwayVertexRep vertexRep = hashKgmlEntryIdToVertexRep.get(Integer.parseInt(kgmlEntryID));

		if (vertexRep == null)
			return;

		currentVertexGroupRep.addVertexRep(vertexRep);
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

		for (int attributeIndex = 0; attributeIndex < attributes.getLength(); attributeIndex++) {
			attributeName = attributes.getLocalName(attributeIndex);

			try {
				if ("".equals(attributeName)) {
					attributeName = attributes.getQName(attributeIndex);
				}

				if (attributeName.equals("name")) {
					name = attributes.getValue(attributeIndex);
				}
				else if (attributeName.equals("height")) {
					height = new Short(attributes.getValue(attributeIndex));
				}
				else if (attributeName.equals("width")) {
					width = new Short(attributes.getValue(attributeIndex));
				}
				else if (attributeName.equals("x")) {
					x = new Short(attributes.getValue(attributeIndex));
				}
				else if (attributeName.equals("y")) {
					y = new Short(attributes.getValue(attributeIndex));
				}
				else if (attributeName.equals("type")) {
					shapeType = attributes.getValue(attributeIndex);
				}
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		if (currentVertices.isEmpty()) {
			// Logger.log(new Status(IStatus.INFO, GeneralManager.PLUGIN_ID,
			// "Cannot handle graphics tag because no gene was mapped for this entry."));
			return;
		}

		// Check if we need to create a group vertex rep instead of a standard
		// vertex rep
		if (currentVertices.get(0).getType().equals(EPathwayVertexType.group)) {

			currentVertexGroupRep = pathwayItemManager.createVertexGroupRep(currentPathway);

			hashKgmlEntryIdToVertexRep.put(currentEntryId, currentVertexGroupRep);
		}
		else {
			PathwayVertexRep vertexRep = pathwayItemManager.createVertexRep(currentPathway, currentVertices, name,
					shapeType, x, y, width, height);

			hashKgmlEntryIdToVertexRep.put(currentEntryId, vertexRep);

			if (currentReactionName != null && !currentReactionName.isEmpty()) {

				// Check if a vertex rep node for that reaction has already been
				// added. If this is the case, then the node will be removed and
				// replaced by a vertex group rep node.
				if (hashKgmlReactionNameToVertexRep.get(currentReactionName) != null) {
					PathwayVertexRep alreadyPresentReactionNode = hashKgmlReactionNameToVertexRep
							.get(currentReactionName);

					if (alreadyPresentReactionNode instanceof PathwayVertexGroupRep) {
						((PathwayVertexGroupRep) alreadyPresentReactionNode).addVertexRep(vertexRep);
					}
					else {
						PathwayVertexGroupRep vertexGroupRep = pathwayItemManager.createVertexGroupRep(currentPathway);
						vertexGroupRep.addVertexRep(alreadyPresentReactionNode);
						vertexGroupRep.addVertexRep(vertexRep);
						hashKgmlReactionNameToVertexRep.remove(alreadyPresentReactionNode);
						hashKgmlReactionNameToVertexRep.put(currentReactionName, vertexGroupRep);
					}
				}
				else {
					hashKgmlReactionNameToVertexRep.put(currentReactionName, vertexRep);
				}
			}
		}
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
				sourceVertexId = Integer.valueOf(attributes.getValue(attributeIndex)).intValue();
			}
			else if (attributeName.equals("entry2")) {
				targetVertexId = Integer.valueOf(attributes.getValue(attributeIndex)).intValue();
			}
		}

		relationSourceVertexRep = hashKgmlEntryIdToVertexRep.get(sourceVertexId);
		relationTargetVertexRep = hashKgmlEntryIdToVertexRep.get(targetVertexId);
		relationType = EPathwayRelationEdgeType.valueOf(type);
	}

	protected void handleSubtypeTag() {

		String subType = "";

		for (int attributeIndex = 0; attributeIndex < attributes.getLength(); attributeIndex++) {
			attributeName = attributes.getLocalName(attributeIndex);

			if ("".equals(attributeName)) {
				attributeName = attributes.getQName(attributeIndex);
			}

			if (attributeName.equals("name"))
				subType = attributes.getValue(attributeIndex);
			else if (attributeName.equals("value")) {

				if (subType.equals("compound")) {

					// int compoundID =
					// Integer.parseInt(attributes.getValue(attributeIndex));
					//
					// PathwayVertexRep compoundVertexRep =
					// hashKgmlEntryIdToVertexRep
					// .get(compoundID);
					//
					// // Create edge representation
					// PathwayRelationEdgeRep pathwayRelationEdgeRep =
					// (PathwayRelationEdgeRep) currentPathway
					// .getEdge(relationSourceVertexRep, compoundVertexRep);
					//
					// // edge from compound to gene
					// if (pathwayRelationEdgeRep == null) {
					// pathwayRelationEdgeRep = new
					// PathwayRelationEdgeRep(relationType);
					// try {
					// currentPathway.addEdge(relationSourceVertexRep,
					// compoundVertexRep,
					// pathwayRelationEdgeRep);
					// }
					// catch (Exception e) {
					// // TODO: marc, investigate why this happens here
					// e.printStackTrace();
					// }
					//
					// pathwayRelationEdgeRep = new
					// PathwayRelationEdgeRep(relationType);
					// try {
					// currentPathway.addEdge(compoundVertexRep,
					// relationSourceVertexRep,
					// pathwayRelationEdgeRep);
					// }
					// catch (Exception e) {
					// // TODO: marc, investigate why this happens here
					// e.printStackTrace();
					// }
					// }
					// else {
					// pathwayRelationEdgeRep.addRelationSubType(subType);
					//
					// if (!subType.equals("compound"))
					// System.out.println("STOP");
					// }
					//
					// pathwayRelationEdgeRep = (PathwayRelationEdgeRep)
					// currentPathway.getEdge(
					// relationSourceVertexRep, compoundVertexRep);
					//
					// // edge from gene to compound
					// if (pathwayRelationEdgeRep == null) {
					// pathwayRelationEdgeRep = new
					// PathwayRelationEdgeRep(relationType);
					// currentPathway.addEdge(compoundVertexRep,
					// relationTargetVertexRep,
					// pathwayRelationEdgeRep);
					// }
					// else {
					// pathwayRelationEdgeRep.addRelationSubType(subType);
					// }
				}
				else {
					// all other cases except "compound"

					// TODO check why this can happen
					if (relationSourceVertexRep == null || relationTargetVertexRep == null)
						return;

					// create edge representation
					PathwayRelationEdgeRep pathwayRelationEdgeRep = (PathwayRelationEdgeRep) currentPathway.getEdge(
							relationSourceVertexRep, relationTargetVertexRep);

					// edge from vertex to vertex
					if (pathwayRelationEdgeRep == null) {
						pathwayRelationEdgeRep = new PathwayRelationEdgeRep(relationType);

						currentPathway
								.addEdge(relationSourceVertexRep, relationTargetVertexRep, pathwayRelationEdgeRep);
					}

					pathwayRelationEdgeRep.addRelationSubType(subType);

				}
			}
		}
	}

	/**
	 * Reacts on the elements of the reaction tag. An example reaction tag looks
	 * like this: <reaction name="rn:R01001" type="irreversible">
	 */
	protected void handleReactionTag() {

		String reactionName = "";
		String reactionType = "";

		for (int attributeIndex = 0; attributeIndex < attributes.getLength(); attributeIndex++) {
			attributeName = attributes.getLocalName(attributeIndex);

			if ("".equals(attributeName)) {
				attributeName = attributes.getQName(attributeIndex);
			}

			if (attributeName.equals("type")) {
				reactionType = attributes.getValue(attributeIndex);
			}
			else if (attributeName.equals("name")) {
				reactionName = attributes.getValue(attributeIndex);
			}
		}

		currentReactionName = reactionName;
		currentReactionType = EPathwayReactionEdgeType.valueOf(reactionType);
	}

	/**
	 * Reacts on the elements of the reaction substrate tag. An example reaction
	 * substrate tag looks like this: <substrate name="cpd:C01118"/>
	 */
	protected void handleReactionSubstrateTag() {

		Integer reactionSubstrateID = -1;

		for (int attributeIndex = 0; attributeIndex < attributes.getLength(); attributeIndex++) {
			attributeName = attributes.getLocalName(attributeIndex);

			if (attributeName.equals("id")) {
				reactionSubstrateID = Integer.valueOf(attributes.getValue(attributeIndex));
			}
		}

		PathwayVertexRep sourceVertexRep = hashKgmlEntryIdToVertexRep.get(reactionSubstrateID);

		PathwayVertexRep targetVertexRep = hashKgmlReactionNameToVertexRep.get(currentReactionName);

		// Prevent double insertion of edges that connect to group nodes
		if (currentPathway.getEdge(sourceVertexRep, targetVertexRep) != null
				&& targetVertexRep.getType().equals(EPathwayVertexType.group))
			return;

		// Edge from the substrate to the gene
		PathwayReactionEdgeRep pathwayReactionEdgeRep = new PathwayReactionEdgeRep(currentReactionType);

		try {
			currentPathway.addEdge(sourceVertexRep, targetVertexRep, pathwayReactionEdgeRep);

			if (currentReactionType == EPathwayReactionEdgeType.reversible) {
				pathwayReactionEdgeRep = new PathwayReactionEdgeRep(currentReactionType);
				currentPathway.addEdge(targetVertexRep, sourceVertexRep, pathwayReactionEdgeRep);
			}
		}
		catch (Exception e) {
			// Logger.log(new Status(
			// IStatus.INFO,
			// GeneralManager.PLUGIN_ID,
			// "Cannot add edge because one of the gene vertices was not mapped to David and therefore not inserted in the graph.\n"));
		}
	}

	/**
	 * Reacts on the elements of the reaction product tag. An example reaction
	 * product tag looks like this: <product name="cpd:C02291"/>
	 */
	protected void handleReactionProductTag() {

		Integer reactionProductID = -1;

		for (int attributeIndex = 0; attributeIndex < attributes.getLength(); attributeIndex++) {
			attributeName = attributes.getLocalName(attributeIndex);

			if (attributeName.equals("id")) {
				reactionProductID = Integer.valueOf(attributes.getValue(attributeIndex));
			}
		}

		PathwayVertexRep sourceVertexRep = hashKgmlReactionNameToVertexRep.get(currentReactionName);

		PathwayVertexRep targetVertexRep = hashKgmlEntryIdToVertexRep.get(reactionProductID);

		// Prevent double insertion of edges that connect to group nodes
		if (currentPathway.getEdge(sourceVertexRep, targetVertexRep) != null
				&& sourceVertexRep.getType().equals(EPathwayVertexType.group))
			return;

		// Edge from the product to the gene
		PathwayReactionEdgeRep pathwayReactionEdgeRep = new PathwayReactionEdgeRep(currentReactionType);

		try {
			currentPathway.addEdge(sourceVertexRep, targetVertexRep, pathwayReactionEdgeRep);

			if (currentReactionType == EPathwayReactionEdgeType.reversible) {
				pathwayReactionEdgeRep = new PathwayReactionEdgeRep(currentReactionType);
				currentPathway.addEdge(targetVertexRep, sourceVertexRep, pathwayReactionEdgeRep);
			}
		}
		catch (Exception e) {
			// Logger.log(new Status(
			// IStatus.INFO,
			// GeneralManager.PLUGIN_ID,
			// "Cannot add edge because one of the gene vertices was not mapped to David and therefore not inserted in the graph.\n"));
		}
	}
}
