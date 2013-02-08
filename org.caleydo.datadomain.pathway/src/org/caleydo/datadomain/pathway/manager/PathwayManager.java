/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.datadomain.pathway.manager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.specialized.Organism;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.parser.KgmlSaxHandler;
import org.caleydo.datadomain.pathway.parser.PathwayImageMap;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * The pathway manager is in charge of creating and handling the pathways. The class is implemented as a singleton.
 *
 * @author Marc Streit
 */
public class PathwayManager extends AManager<PathwayGraph> {

	private volatile static PathwayManager pathwayManager;

	private PathwayParserManager xmlParserManager;

	public IPathwayResourceLoader keggPathwayResourceLoader;
	public IPathwayResourceLoader biocartaPathwayResourceLoader;
	public IPathwayResourceLoader wikipathwaysResourceLoader;

	private HashMap<PathwayGraph, Boolean> hashPathwayToVisibilityState;

	private HashMap<String, PathwayGraph> hashPathwayTitleToPathway;

	private HashMap<EPathwayDatabaseType, PathwayDatabase> hashPathwayDatabase;

	/**
	 * Root pathway contains all nodes that are loaded into the system. Therefore it represents the overall topological
	 * network. (The root pathway is independent from the representation of the nodes.)
	 */
	private DirectedGraph<PathwayVertex, DefaultEdge> rootPathwayGraph = new DefaultDirectedGraph<PathwayVertex, DefaultEdge>(
			DefaultEdge.class);

	/**
	 * Used for pathways where only images can be loaded. The image map defines the clickable regions on that pathway
	 * image.
	 */
	private PathwayImageMap currentPathwayImageMap;

	private PathwayGraph currentPathwayGraph;

	private boolean pathwayLoadingFinished;

	private PathwayManager() {

	}

	/**
	 * Returns the pathway manager as a singleton object. When first called the manager is created (lazy).
	 *
	 * @return singleton PathwayManager instance
	 */
	public static PathwayManager get() {
		synchronized (PathwayManager.class) {
			if (pathwayManager == null) {
				pathwayManager = new PathwayManager();
				pathwayManager.init();
			}
		}

		return pathwayManager;
	}

	private void init() {
		hashPathwayTitleToPathway = new HashMap<String, PathwayGraph>();
		hashPathwayDatabase = new HashMap<EPathwayDatabaseType, PathwayDatabase>();
		hashPathwayToVisibilityState = new HashMap<PathwayGraph, Boolean>();

		xmlParserManager = new PathwayParserManager();

		KgmlSaxHandler kgmlParser = new KgmlSaxHandler();
		xmlParserManager.registerAndInitSaxHandler(kgmlParser);
		// BioCartaPathwayImageMapSaxHandler biocartaPathwayParser = new
		// BioCartaPathwayImageMapSaxHandler();
		// xmlParserManager.registerAndInitSaxHandler(biocartaPathwayParser);
	}

	public PathwayDatabase createPathwayDatabase(final EPathwayDatabaseType type, final String XMLPath,
			final String imagePath, final String imageMapPath) {

		// Check if requested pathway database is already loaded (e.g. using
		// caching)
		if (hashPathwayDatabase.containsKey(type))
			return hashPathwayDatabase.get(type);

		PathwayDatabase pathwayDatabase = new PathwayDatabase(type, XMLPath, imagePath, imagePath);

		hashPathwayDatabase.put(type, pathwayDatabase);
		createPathwayResourceLoader(pathwayDatabase.getType());

		Logger.log(new Status(IStatus.INFO, this.toString(), "Setting pathway loading path: database-type:[" + type
				+ "] " + "xml-path:[" + pathwayDatabase.getXMLPath() + "] image-path:["
				+ pathwayDatabase.getImagePath() + "] image-map-path:[" + pathwayDatabase.getImageMapPath() + "]"));

		return pathwayDatabase;
	}

	public PathwayGraph createPathway(final EPathwayDatabaseType type, final String sName, final String sTitle,
			final String sImageLink, final String sExternalLink) {
		PathwayGraph pathway = new PathwayGraph(type, sName, sTitle, sImageLink, sExternalLink);

		registerItem(pathway);
		hashPathwayTitleToPathway.put(sTitle, pathway);
		hashPathwayToVisibilityState.put(pathway, false);

		currentPathwayGraph = pathway;

		return pathway;
	}

	public PathwayGraph getPathwayByTitle(final String pathwayTitle, EPathwayDatabaseType pathwayDatabaseType) {

		waitUntilPathwayLoadingIsFinished();
		Iterator<String> iterPathwayName = hashPathwayTitleToPathway.keySet().iterator();
		Pattern pattern = Pattern.compile(pathwayTitle, Pattern.CASE_INSENSITIVE);
		Matcher regexMatcher;
		String tempPathwayTitle;

		while (iterPathwayName.hasNext()) {
			tempPathwayTitle = iterPathwayName.next();
			regexMatcher = pattern.matcher(tempPathwayTitle);

			if (regexMatcher.find()) {
				PathwayGraph pathway = hashPathwayTitleToPathway.get(tempPathwayTitle);

				// Ignore the found pathway if it has the same name but is
				// contained
				// in a different database
				if (getItem(pathway.getID()).getType() != pathwayDatabaseType) {
					continue;
				}
				return pathway;
			}
		}
		return null;
	}

	public DirectedGraph<PathwayVertex, DefaultEdge> getRootPathway() {
		return rootPathwayGraph;
	}

	@Override
	public Collection<PathwayGraph> getAllItems() {
		waitUntilPathwayLoadingIsFinished();

		return super.getAllItems();
	}

	public void setPathwayVisibilityState(final PathwayGraph pathway, final boolean bVisibilityState) {
		waitUntilPathwayLoadingIsFinished();

		hashPathwayToVisibilityState.put(pathway, bVisibilityState);
	}

	public void resetPathwayVisiblityState() {
		waitUntilPathwayLoadingIsFinished();

		for (PathwayGraph pathway : hashPathwayToVisibilityState.keySet()) {
			hashPathwayToVisibilityState.put(pathway, false);
		}
	}

	public boolean isPathwayVisible(final PathwayGraph pathway) {
		waitUntilPathwayLoadingIsFinished();

		return hashPathwayToVisibilityState.get(pathway);
	}

	public void createPathwayImageMap(final String sImageLink) {
		currentPathwayImageMap = new PathwayImageMap(sImageLink);
	}

	public PathwayImageMap getCurrentPathwayImageMap() {
		return currentPathwayImageMap;
	}

	public PathwayDatabase getPathwayDatabaseByType(EPathwayDatabaseType type) {
		return hashPathwayDatabase.get(type);
	}

	public void notifyPathwayLoadingFinished(boolean pathwayLoadingFinished) {
		this.pathwayLoadingFinished = pathwayLoadingFinished;
	}

	public void waitUntilPathwayLoadingIsFinished() {
		while (!pathwayLoadingFinished) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new IllegalThreadStateException("Pathway loader thread has been interrupted!");
			}
		}
	}

	public boolean isPathwayLoadingFinished() {
		return pathwayLoadingFinished;
	}

	public void createPathwayResourceLoader(EPathwayDatabaseType type) {

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		try {
			if (type == EPathwayDatabaseType.KEGG) {
				IExtensionPoint ep = reg.getExtensionPoint("org.caleydo.data.pathway.PathwayResourceLoader");
				IExtension ext = ep.getExtension("org.caleydo.data.pathway.kegg.KEGGPathwayResourceLoader");
				IConfigurationElement[] ce = ext.getConfigurationElements();

				keggPathwayResourceLoader = (IPathwayResourceLoader) ce[0].createExecutableExtension("class");

			} else if (type == EPathwayDatabaseType.BIOCARTA) {

				IExtensionPoint ep = reg.getExtensionPoint("org.caleydo.data.pathway.PathwayResourceLoader");
				IExtension ext = ep.getExtension("org.caleydo.data.pathway.biocarta.BioCartaPathwayResourceLoader");
				IConfigurationElement[] ce = ext.getConfigurationElements();

				biocartaPathwayResourceLoader = (IPathwayResourceLoader) ce[0].createExecutableExtension("class");
			} else if (type == EPathwayDatabaseType.WIKIPATHWAYS) {

				IExtensionPoint ep = reg.getExtensionPoint("org.caleydo.data.pathway.PathwayResourceLoader");
				IExtension ext = ep.getExtension("org.caleydo.data.pathway.wikipathways.WikiPathwaysResourceLoader");
				IConfigurationElement[] ce = ext.getConfigurationElements();

				wikipathwaysResourceLoader = (IPathwayResourceLoader) ce[0].createExecutableExtension("class");
			} else {
				throw new IllegalStateException("Unknown pathway database " + type);
			}
		} catch (Exception ex) {
			Logger.log(new Status(IStatus.INFO, "PathwayLoaderThread", "Could not load " + type.getName()
					+ " pathways."));
		}
	}

	public IPathwayResourceLoader getPathwayResourceLoader(EPathwayDatabaseType type) {

		if (type == EPathwayDatabaseType.KEGG) {
			return keggPathwayResourceLoader;
		} else if (type == EPathwayDatabaseType.BIOCARTA) {
			return biocartaPathwayResourceLoader;
		} else if (type == EPathwayDatabaseType.WIKIPATHWAYS) {
			return wikipathwaysResourceLoader;
		}

		throw new IllegalStateException("Unknown pathway database " + type);
	}

	public PathwayParserManager getXmlParserManager() {
		return xmlParserManager;
	}

	public void loadPathwaysByType(PathwayDatabase pathwayDatabase) {

		// // Try reading list of files directly from local hard dist
		// File folder = new File(sXMLPath);
		// File[] arFiles = folder.listFiles();

		GeneralManager generalManager = GeneralManager.get();

		Logger.log(new Status(IStatus.INFO, "PathwayLoaderThread", "Start parsing " + pathwayDatabase.getName()
				+ " pathways."));

		BufferedReader file = null;
		String line = null;
		String fileName = "";
		String pathwayPath = pathwayDatabase.getXMLPath();
		IPathwayResourceLoader pathwayResourceLoader = null;
		Organism organism = GeneralManager.get().getBasicInfo().getOrganism();

		if (pathwayDatabase.getType() == EPathwayDatabaseType.KEGG) {

			if (organism == Organism.HOMO_SAPIENS) {
				fileName = "data/pathway_list_KEGG_homo_sapiens.txt";
			} else if (organism == Organism.MUS_MUSCULUS) {
				fileName = "data/pathway_list_KEGG_mus_musculus.txt";
			} else {
				throw new IllegalStateException("Cannot load pathways from organism " + organism);
			}

			generalManager.getSWTGUIManager().setProgressBarTextFromExternalThread("Loading KEGG Pathways...");
		} else if (pathwayDatabase.getType() == EPathwayDatabaseType.BIOCARTA) {

			if (organism == Organism.HOMO_SAPIENS) {
				fileName = "data/pathway_list_BIOCARTA_homo_sapiens.txt";
			} else if (organism == Organism.MUS_MUSCULUS) {
				fileName = "data/pathway_list_BIOCARTA_mus_musculus.txt";
			} else {
				throw new IllegalStateException("Cannot load pathways from organism " + organism);
			}

			generalManager.getSWTGUIManager().setProgressBarTextFromExternalThread("Loading BioCarta Pathways...");
		}

		pathwayResourceLoader = PathwayManager.get().getPathwayResourceLoader(pathwayDatabase.getType());

		if (pathwayResourceLoader == null)
			return;

		try {

			if (pathwayDatabase.getType() == EPathwayDatabaseType.KEGG
					|| pathwayDatabase.getType() == EPathwayDatabaseType.BIOCARTA)
				file = pathwayResourceLoader.getResource(fileName);
			else
				file = GeneralManager.get().getResourceLoader().getResource(fileName);

			StringTokenizer tokenizer;
			String pathwayName;

			while ((line = file.readLine()) != null) {
				tokenizer = new StringTokenizer(line, " ");

				pathwayName = tokenizer.nextToken();

				// Skip non pathway files
				if (!pathwayName.endsWith(".xml") && !line.contains("h_") && !line.contains("m_")) {
					continue;
				}

				PathwayManager.get().getXmlParserManager().parseXmlFileByName(pathwayPath + pathwayName);

				currentPathwayGraph.setWidth(Integer.valueOf(tokenizer.nextToken()).intValue());
				currentPathwayGraph.setHeight(Integer.valueOf(tokenizer.nextToken()).intValue());

				int iImageWidth = currentPathwayGraph.getWidth();
				int iImageHeight = currentPathwayGraph.getHeight();

				if (iImageWidth == -1 || iImageHeight == -1) {
					Logger.log(new Status(IStatus.INFO, "PathwayLoaderThread", "Pathway texture width=" + iImageWidth
							+ " / height=" + iImageHeight));
				}
			}

		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Pathway list file " + fileName + " not found.");
		} catch (IOException e) {
			throw new IllegalStateException("Error reading data from pathway list file: " + fileName);
		} finally {
			try {
				if (file != null)
					file.close();
			} catch (IOException e) {
			}
		}

		Logger.log(new Status(IStatus.INFO, "PathwayLoaderThread", "Finished parsing " + pathwayDatabase.getName()
				+ " pathways."));
	}

	/**
	 * Returns all pathways where a specific gene is contained at least once.
	 *
	 * @param idType
	 * @param id
	 * @return a Set of PathwayGraphs or null if no such mapping exists
	 */
	public Set<PathwayGraph> getPathwayGraphsByGeneID(IDType idType, int id) {

		// set to avoid duplicate pathways
		Set<PathwayGraph> pathways = new HashSet<PathwayGraph>();

		IDMappingManager geneIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType);
		Set<Integer> vertexRepIDs = geneIDMappingManager.getIDAsSet(idType, PathwayVertexRep.getIdType(), id);
		if (vertexRepIDs == null)
			return null;
		for (Integer vertexRepID : vertexRepIDs) {

			pathways.add(PathwayItemManager.get().getPathwayVertexRep(vertexRepID).getPathway());
		}

		return pathways;
	}

	/**
	 * Returns all pathways where a specific gene is contained at least once.
	 *
	 * @param idType
	 * @param geneIDs
	 * @return a Set of PathwayGraphs or null if no such mapping exists
	 */
	public HashMap<PathwayGraph, Integer> getPathwayGraphsWithOccurencesByGeneIDs(GeneticDataDomain dataDomain,
			IDType idType, List<Integer> geneIDs) {

		IDType davidIDType = IDType.getIDType("DAVID");
		HashMap<PathwayGraph, Integer> hashPathwaysToOccurences = new HashMap<PathwayGraph, Integer>();
		IIDTypeMapper<Integer, Integer> mapper = dataDomain.getGeneIDMappingManager().getIDTypeMapper(idType,
				davidIDType);

		for (Integer gene : geneIDs) {

			Set<Integer> davids = mapper.apply(gene);
			if (davids == null || davids.size() == 0)
				continue;
			for (Integer david : davids) {
				Set<PathwayGraph> pathwayGraphs = PathwayManager.get().getPathwayGraphsByGeneID(davidIDType, david);

				if (pathwayGraphs != null) {

					for (PathwayGraph pathwayGraph : pathwayGraphs) {

						if (!hashPathwaysToOccurences.containsKey(pathwayGraph))
							hashPathwaysToOccurences.put(pathwayGraph, 1);
						else {
							int occurences = hashPathwaysToOccurences.get(pathwayGraph);
							occurences++;
							hashPathwaysToOccurences.put(pathwayGraph, occurences);
						}
					}
				}
			}
		}

		return hashPathwaysToOccurences;
	}

	/**
	 * Gets all {@link PathwayVertexRep}s that are equivalent to the specified one. Equivalence is defined as that the
	 * vertexReps refer to the same set of {@link PathwayVertex} objects. The returned vertexReps can be from different
	 * {@link PathwayGraph}s or also the same one as the specified vertexRep. The specified vertexRep is not part of the
	 * returned set.
	 *
	 * @param vertexRep
	 * @return Set of equivalent vertexReps.
	 */
	public Set<PathwayVertexRep> getEquivalentVertexReps(PathwayVertexRep vertexRep) {
		Set<PathwayVertexRep> equivalentVertexReps = new HashSet<>();

		List<PathwayVertex> vertices = vertexRep.getPathwayVertices();
		for (PathwayVertex vertex : vertices) {
			List<PathwayVertexRep> vertexReps = vertex.getPathwayVertexReps();
			for (PathwayVertexRep vr : vertexReps) {
				if (vr != vertexRep) {
					List<PathwayVertex> currentVertices = vr.getPathwayVertices();
					if (currentVertices.size() == vertices.size() && currentVertices.containsAll(vertices)) {
						equivalentVertexReps.add(vr);
					}
				}
			}
		}

		return equivalentVertexReps;
	}
}
