/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.pathway.wikipathways;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridgedb.Xref;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.datadomain.genetic.GeneticMetaData;
import org.caleydo.datadomain.genetic.Organism;
import org.caleydo.datadomain.pathway.IPathwayParser;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGroupRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayDatabase;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.GpmlFormat;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.PathwayImporter;

public class WikiPathwaysParser implements IPathwayParser, IRunnableWithProgress {

	/**
	 * Maps wikipathway db names to idtypes in caleydo.
	 */
	private Map<String, String> dbNameMap = new HashMap<>();

	StringBuilder idMappingErrors = new StringBuilder();

	public WikiPathwaysParser() {
		dbNameMap.put("Ensembl Mouse", "ENSEMBL_GENE_ID");
		dbNameMap.put("Entrez Gene", "ENTREZ_GENE_ID");
		dbNameMap.put("RefSeq", "REFSEQ_MRNA");
		org.pathvisio.core.debug.Logger.log.setLogLevel(false, false, false, false, false, false);
		// dbNameMap.put("EC Number", "EC_NUMBER");
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		PathwayManager pathwayManager = PathwayManager.get();
		pathwayManager.preparePathwayData(EPathwayDatabaseType.WIKIPATHWAYS, monitor);
	}

	@Override
	public void parse() {
		PathwayManager pathwayManager = PathwayManager.get();
		File baseDir = pathwayManager.preparePathwayData(EPathwayDatabaseType.WIKIPATHWAYS, new NullProgressMonitor());
		Organism organism = GeneticMetaData.getOrganism();
		if (baseDir == null)
			throw new IllegalStateException("Cannot load pathways from organism " + organism);

		PathwayDatabase pathwayDatabase = pathwayManager.getPathwayDatabaseByType(EPathwayDatabaseType.WIKIPATHWAYS);

		try (BufferedReader pathwayListFile = new BufferedReader(new FileReader(new File(baseDir, "metadata.txt")))) {
			String line = null;
			while ((line = pathwayListFile.readLine()) != null) {
				String[] tokens = line.split("\\s");

				File pathwayFile = new File(baseDir, tokens[0]);
				PathwayImporter importer = new GpmlFormat();
				Pathway pathway = importer.doImport(pathwayFile);
				createPathwayGraph(pathway, tokens[0].substring(0, tokens[0].length() - 5), Integer.valueOf(tokens[1])
						.intValue(), Integer.valueOf(tokens[2]).intValue(), baseDir);

			}
			if (idMappingErrors.length() > 0) {
				String message = "Failed to parse the following IDs while parsing Wikipathways:\n "
						+ idMappingErrors.toString();
				Logger.log(new Status(IStatus.INFO, this.toString(), message));
			}
		} catch (IOException | ConverterException e) {
			throw new IllegalStateException("Error reading pathway list file");
		}
	}

	/**
	 * Creates a {@link PathwayGraph} object for the specified pathway.
	 *
	 * @param pathway
	 *            Pathway a graph shall be created for.
	 * @param imageName
	 *            FileName of the image without a preceeding path or file extension, e.g., "filename". PNG format is
	 *            assumed.
	 * @param pixelWidth
	 *            Width of the image in pixels.
	 * @param pixelHeight
	 *            Height of the image in pixels.
	 * @param baseDir
	 */
	private void createPathwayGraph(Pathway pathway, String imageFileName, int pixelWidth, int pixelHeight, File baseDir) {
		PathwayManager pathwayManager = PathwayManager.get();
		PathwayItemManager pathwayItemManager = PathwayItemManager.get();

		PathwayGraph pathwayGraph = pathwayManager.createPathway(EPathwayDatabaseType.WIKIPATHWAYS, imageFileName,
				pathway.getMappInfo().getMapInfoName(), new File(baseDir, imageFileName + ".png"), "");
		pathwayGraph.setWidth(pixelWidth);
		pathwayGraph.setHeight(pixelHeight);

		// Height and width specified in gpml file
		// double boardDimensions[] = pathway.getMBoardSize();
		// int boardWidth = (int) boardDimensions[0];
		// int boardHeight = (int) boardDimensions[1];

		IDMappingManager genomeIdManager = ((PathwayDataDomain) DataDomainManager.get().getDataDomainByType(
				PathwayDataDomain.DATA_DOMAIN_TYPE)).getGeneIDMappingManager();
		Map<String, PathwayVertexRep> vertexReps = new HashMap<>();
		Map<String, PathwayVertexGroupRep> vertexGroupReps = new HashMap<>();

		Map<ObjectType, List<PathwayElement>> typeToElements = new HashMap<>();
		// Make sure that there is no null list for an object type
		for (ObjectType objectType : ObjectType.values()) {
			typeToElements.put(objectType, new ArrayList<PathwayElement>());
		}
		// Add all elements to their type specific list
		for (PathwayElement element : pathway.getDataObjects()) {
			typeToElements.get(element.getObjectType()).add(element);
		}

		Map<String, List<PathwayVertexGroupRep>> groupedGroups = new HashMap<>();
		for (PathwayElement element : typeToElements.get(ObjectType.GROUP)) {
			PathwayVertexGroupRep vertexGroupRep = pathwayItemManager.createVertexGroupRep(pathwayGraph);
			vertexReps.put(element.getGraphId(), vertexGroupRep);
			vertexGroupReps.put(element.getGroupId(), vertexGroupRep);

			String groupRef = element.getGroupRef();
			if (groupRef != null) {
				PathwayVertexGroupRep parent = vertexGroupReps.get(groupRef);
				if (parent != null) {
					parent.addVertexRep(vertexGroupRep);
				} else {
					// Parent group has not (yet) been created. Store for retrieval when parent is created.
					List<PathwayVertexGroupRep> groups = groupedGroups.get(groupRef);
					if (groups == null) {
						groups = new ArrayList<>();
						groupedGroups.put(groupRef, groups);
					}
					groups.add(vertexGroupRep);
				}
			}

			// Retrieve children that have been created earlier.
			List<PathwayVertexGroupRep> groups = groupedGroups.get(element.getGroupId());
			if (groups != null) {
				for (PathwayVertexGroupRep group : groups) {
					vertexGroupRep.addVertexRep(group);
				}
			}
		}

		for (PathwayElement element : typeToElements.get(ObjectType.DATANODE)) {
			Xref xref = element.getXref();
			String label = element.getTextLabel();
			if (xref != null && xref.getDataSource() != null && xref.getId() != null && !xref.getId().isEmpty()) {
				String idType = dbNameMap.get(xref.getDataSource().getFullName());
				if (idType != null) {
					IDType sourceIDType = IDType.getIDType(idType);
					if (sourceIDType != null) {

						Set<Integer> davidIDs = null;
						if (sourceIDType.getDataType() == EDataType.INTEGER) {
							try {
								davidIDs = genomeIdManager.getIDAsSet(sourceIDType, IDType.getIDType("DAVID"),
										Integer.valueOf(xref.getId()));
							} catch (NumberFormatException e) {
								createVertexWithoutDavidID(pathwayGraph, element, vertexReps, vertexGroupReps);
								continue;
							}
						} else {
							davidIDs = genomeIdManager
									.getIDAsSet(sourceIDType, IDType.getIDType("DAVID"), xref.getId());
						}

						if (davidIDs == null) {
							idMappingErrors.append(xref.getId() + "(" + idType + ");");
							createVertexWithoutDavidID(pathwayGraph, element, vertexReps, vertexGroupReps);
							continue;
						}

						List<PathwayVertex> vertices = pathwayItemManager.createGeneVertex(label, xref.getDataSource()
								.getType(), "", davidIDs);

						PathwayVertexRep vertexRep = pathwayItemManager.createVertexRep(pathwayGraph, vertices, label,
								"rectangle", (short) (element.getMCenterX()), (short) (element.getMCenterY()),
								(short) (element.getMWidth()), (short) (element.getMHeight()));
						vertexReps.put(element.getGraphId(), vertexRep);
						addVertexRepToGroup(element, vertexRep, vertexGroupReps);
					}
				} else {
					createVertexWithoutDavidID(pathwayGraph, element, vertexReps, vertexGroupReps);
				}
			} else {
				createVertexWithoutDavidID(pathwayGraph, element, vertexReps, vertexGroupReps);
			}
		}
		for (PathwayElement element : typeToElements.get(ObjectType.LINE)) {
			String startGraphRef = element.getStartGraphRef();
			String endGraphRef = element.getEndGraphRef();

			if (startGraphRef != null && endGraphRef != null && !startGraphRef.isEmpty() && !endGraphRef.isEmpty()) {
				PathwayVertexRep startVertexRep = vertexReps.get(startGraphRef);
				PathwayVertexRep endVertexRep = vertexReps.get(endGraphRef);
				if (startVertexRep != null && endVertexRep != null) {
					pathwayGraph.addEdge(startVertexRep, endVertexRep);
					pathwayManager.addEdgesToRootPathway(startVertexRep, endVertexRep);
				}
			}
		}
	}

	/**
	 * Creates a {@link PathwayVertex} and {@link PathwayVertexRep} for the specified element and adds it to a
	 * {@link PathwayVertexGroupRep} if specified.
	 *
	 * @param pathwayGraph
	 * @param element
	 * @param vertexReps
	 *            Map where the created VertexRep is stored.
	 * @param vertexGroupReps
	 */
	private void createVertexWithoutDavidID(PathwayGraph pathwayGraph, PathwayElement element,
			Map<String, PathwayVertexRep> vertexReps, Map<String, PathwayVertexGroupRep> vertexGroupReps) {
		PathwayItemManager pathwayItemManager = PathwayItemManager.get();
		PathwayVertex vertex = pathwayItemManager.createVertex(element.getTextLabel(), "gene", "");
		List<PathwayVertex> vertices = new ArrayList<>(1);
		vertices.add(vertex);
		PathwayVertexRep vertexRep = pathwayItemManager.createVertexRep(pathwayGraph, vertices, element.getTextLabel(),
				"rectangle", (short) (element.getMCenterX()), (short) (element.getMCenterY()),
				(short) (element.getMWidth()), (short) (element.getMHeight()));
		vertexReps.put(element.getGraphId(), vertexRep);

		addVertexRepToGroup(element, vertexRep, vertexGroupReps);
	}

	/**
	 * Adds an the vertex rep of an element to a group if specified.
	 *
	 * @param element
	 * @param vertexRep
	 * @param vertexGroupReps
	 */
	private void addVertexRepToGroup(PathwayElement element, PathwayVertexRep vertexRep,
			Map<String, PathwayVertexGroupRep> vertexGroupReps) {
		if (element.getGroupRef() != null) {
			PathwayVertexGroupRep groupRep = vertexGroupReps.get(element.getGroupRef());
			if (groupRep != null) {
				groupRep.addVertexRep(vertexRep);
			}
		}
	}

}
