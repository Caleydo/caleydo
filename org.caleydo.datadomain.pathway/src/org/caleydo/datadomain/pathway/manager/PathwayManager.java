/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.manager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ZipUtils;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.system.FileOperations;
import org.caleydo.core.util.system.RemoteFile;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.genetic.GeneticMetaData;
import org.caleydo.datadomain.genetic.Organism;
import org.caleydo.datadomain.pathway.graph.PathSegment;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import edu.asu.emit.qyan.alg.KShortestPathsAdapter;

/**
 * The pathway manager is in charge of creating and handling the pathways. The class is implemented as a singleton.
 *
 * @author Marc Streit
 */
public class PathwayManager {
	private static final Logger log = Logger.create(PathwayManager.class);

	private volatile static PathwayManager pathwayManager = new PathwayManager();

	private final Map<Integer, PathwayGraph> pathways = new HashMap<>();
	private final Map<PathwayGraph, Boolean> hashPathwayToVisibilityState = new HashMap<PathwayGraph, Boolean>();

	private final Map<EPathwayDatabaseType, Map<String, PathwayGraph>> mapPathwayDBToPathways = new HashMap<>();

	/**
	 * Root pathway contains all nodes that are loaded into the system. Therefore it represents the overall topological
	 * network. (The root pathway is independent from the representation of the nodes.)
	 */
	private DirectedGraph<PathwayVertex, DefaultEdge> rootPathwayGraph = new DefaultDirectedGraph<PathwayVertex, DefaultEdge>(
			DefaultEdge.class);

	private boolean pathwayLoadingFinished;

	private KShortestPathsAdapter<PathwayVertex, DefaultEdge> kShortestPathsAdapter;

	private PathwayManager() {

	}

	/**
	 * Returns the pathway manager as a singleton object. When first called the manager is created (lazy).
	 *
	 * @return singleton PathwayManager instance
	 */
	public static PathwayManager get() {
		return pathwayManager;
	}

	public boolean hasPathways(EPathwayDatabaseType type) {
		return this.mapPathwayDBToPathways.containsKey(type) && !this.mapPathwayDBToPathways.get(type).isEmpty();
	}

	public PathwayGraph createPathway(final EPathwayDatabaseType type, final String sName, final String sTitle,
			final File image, final String sExternalLink) {
		PathwayGraph pathway = new PathwayGraph(type, sName, sTitle, image, sExternalLink);

		this.pathways.put(pathway.getID(), pathway);
		Map<String, PathwayGraph> mapTitleToPathway = mapPathwayDBToPathways.get(type);
		if (mapTitleToPathway == null) {
			mapTitleToPathway = new HashMap<>();
			mapPathwayDBToPathways.put(type, mapTitleToPathway);
		}
		mapTitleToPathway.put(sTitle, pathway);
		hashPathwayToVisibilityState.put(pathway, false);

		return pathway;
	}

	/**
	 * Gets all pathways that belong to the specified {@link EPathwayDatabaseType}.
	 *
	 * @param pathwayDatabaseType
	 * @return
	 */
	public Set<PathwayGraph> getPathwaysOfDatabase(EPathwayDatabaseType pathwayDatabaseType) {
		Set<PathwayGraph> pathways = new HashSet<>();

		Map<String, PathwayGraph> pws = mapPathwayDBToPathways.get(pathwayDatabaseType);
		if (pws != null) {
			pathways.addAll(pws.values());
		}

		return pathways;
	}

	public PathwayGraph getPathwayByTitle(final String pathwayTitle, EPathwayDatabaseType pathwayDatabaseType) {

		waitUntilPathwayLoadingIsFinished();

		Map<String, PathwayGraph> mapTitleToPathway = mapPathwayDBToPathways.get(pathwayDatabaseType);
		if (mapTitleToPathway == null)
			return null;

		Iterator<String> iterPathwayName = mapTitleToPathway.keySet().iterator();
		Pattern pattern = Pattern.compile(pathwayTitle, Pattern.CASE_INSENSITIVE);
		Matcher regexMatcher;
		String tempPathwayTitle;

		while (iterPathwayName.hasNext()) {
			tempPathwayTitle = iterPathwayName.next();
			regexMatcher = pattern.matcher(tempPathwayTitle);

			if (regexMatcher.find() && tempPathwayTitle.length() == pathwayTitle.length()) {
				PathwayGraph pathway = mapTitleToPathway.get(tempPathwayTitle);

				// Ignore the found pathway if it has the same name but is
				// contained
				// in a different database
				// if (getItem(pathway.getID()).getType() != pathwayDatabaseType) {
				// continue;
				// }
				return pathway;
			}
		}
		return null;
	}

	public DirectedGraph<PathwayVertex, DefaultEdge> getRootPathway() {
		return rootPathwayGraph;
	}

	/**
	 * Adds edges between all from-to pairs of the associated {@link PathwayVertex} objects of the specified
	 * {@link PathwayVertexRep}s to the root pathway. Edges are only added for vertices that already exist in the root
	 * pathway.
	 *
	 * @param from
	 * @param to
	 */
	public void addEdgesToRootPathway(PathwayVertexRep from, PathwayVertexRep to) {

		for (PathwayVertex fromVertex : from.getPathwayVertices()) {
			for (PathwayVertex toVertex : to.getPathwayVertices()) {
				if (getRootPathway().containsVertex(fromVertex) && getRootPathway().containsVertex(toVertex)) {
					getRootPathway().addEdge(fromVertex, toVertex);
				}
			}
		}
	}

	public Collection<PathwayGraph> getAllItems() {
		waitUntilPathwayLoadingIsFinished();

		return this.pathways.values();
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

	/**
	 * Returns all pathways where a specific gene is contained at least once.
	 *
	 * @param idType
	 * @param id
	 * @return a Set of PathwayGraphs
	 */
	public Set<PathwayGraph> getPathwayGraphsByGeneID(IDType idType, Object id) {

		// set to avoid duplicate pathways
		Set<PathwayGraph> pathways = new HashSet<PathwayGraph>();

		IDMappingManager geneIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType);
		Set<Integer> vertexRepIDs = geneIDMappingManager.getIDAsSet(idType, PathwayVertexRep.getIdType(), id);
		if (vertexRepIDs == null)
			return pathways;
		for (Integer vertexRepID : vertexRepIDs) {

			pathways.add(PathwayItemManager.get().getPathwayVertexRep(vertexRepID).getPathway());
		}

		return pathways;
	}

	/**
	 * Gets the subset of specified genes that is contained in the specified pathway
	 *
	 * @param pathway
	 * @param geneIDs
	 *            Gene IDs that should be looked for in the pathway
	 * @param idType
	 *            IDType of the specified gene IDs
	 * @return The subset of specified Gene IDs that is contained by the pathway
	 */
	public static Set<Object> getContainedGenes(PathwayGraph pathway, Set<Object> geneIDs, IDType geneIDType) {
		IDMappingManager geneIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(geneIDType);
		IIDTypeMapper<Object, Integer> mapper = geneIDMappingManager.getIDTypeMapper(geneIDType,
				PathwayVertexRep.getIdType());
		Set<Object> result = new HashSet<>();
		for (Object id : geneIDs) {
			Set<Integer> vertexRepIDs = mapper.apply(id);
			if (vertexRepIDs != null) {
				for (Integer vertexRepID : vertexRepIDs) {
					if (PathwayItemManager.get().getPathwayVertexRep(vertexRepID).getPathway() == pathway) {
						result.add(id);
						break;
					}
				}
			}
		}
		return result;
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
	 * Gets the number of equivalent vertexReps of two pathways. Each equivalent vertexRep is only counted once, i.e.,
	 * if a vertexRep of pathway1 has 2 equivalent vertexReps in pathway2, it only counts for 1 equivalence.
	 *
	 * @param pathway1
	 * @param pathway2
	 * @return
	 */
	public int getNumEquivalentVertexReps(PathwayGraph pathway1, PathwayGraph pathway2) {
		// Set<PathwayVertexRep> uniquePathway1VertexReps = new HashSet<>();
		Set<PathwayVertexRep> uniquePathway1VertexReps = filterEquivalentVertexReps(pathway1);

		int sum = 0;
		for (PathwayVertexRep v1 : uniquePathway1VertexReps) {
			Set<PathwayVertexRep> equivalentVertexReps = getEquivalentVertexRepsInPathway(v1, pathway2);
			if (equivalentVertexReps.size() > 0) {
				sum++;
			}
		}

		return sum;
	}

	/**
	 * Filters all vertexReps that are equivalent within a pathway such that the returned set does not contain any
	 * equivalent vertexReps. It is not determined which of n equivalent vertexReps will be in the returned set.
	 *
	 * @param pathway
	 * @return
	 */
	public Set<PathwayVertexRep> filterEquivalentVertexReps(PathwayGraph pathway) {
		Set<PathwayVertexRep> uniquePathwayVertexReps = new HashSet<>(pathway.vertexSet());
		Set<PathwayVertexRep> equivalentVertexReps = new HashSet<>();
		boolean vertexRepsToRemove = false;
		do {
			vertexRepsToRemove = false;
			equivalentVertexReps.clear();
			for (PathwayVertexRep v : uniquePathwayVertexReps) {
				equivalentVertexReps = getEquivalentVertexRepsInPathway(v, v.getPathway());
				for (PathwayVertexRep eV : equivalentVertexReps) {
					if (uniquePathwayVertexReps.contains(eV)) {
						vertexRepsToRemove = true;
						break;
					}

				}
				if (vertexRepsToRemove)
					break;
			}
			if (vertexRepsToRemove) {
				for (PathwayVertexRep v : equivalentVertexReps) {
					uniquePathwayVertexReps.remove(v);
				}
			}
		} while (vertexRepsToRemove);
		return uniquePathwayVertexReps;
	}

	/**
	 * Convenience method for {@link #getEquivalentVertexRepsInPathway(PathwayVertexRep, null)}.
	 *
	 *
	 * @param vertexRep
	 * @return Set of equivalent vertexReps.
	 */
	public Set<PathwayVertexRep> getEquivalentVertexReps(PathwayVertexRep vertexRep) {
		return getEquivalentVertexRepsInPathway(vertexRep, null);
	}

	/**
	 * Gets all {@link PathwayVertexRep}s that are equivalent to the specified one. Equivalence is defined as that they
	 * at least share one {@link PathwayVertex} object. The returned vertexReps are from the specified
	 * {@link PathwayGraph} or can be from multiple different <code>PathwayGraph</code>s if null is specified. The
	 * specified vertexRep is not part of the returned set.
	 *
	 * @param vertexRep
	 * @param pathway
	 * @return
	 */
	public Set<PathwayVertexRep> getEquivalentVertexRepsInPathway(PathwayVertexRep vertexRep, PathwayGraph pathway) {
		Set<PathwayVertexRep> equivalentVertexReps = new HashSet<>();

		List<PathwayVertex> vertices = vertexRep.getPathwayVertices();
		for (PathwayVertex vertex : vertices) {
			List<PathwayVertexRep> vertexReps = vertex.getPathwayVertexReps();
			for (PathwayVertexRep vr : vertexReps) {
				if (vr != vertexRep && (pathway == null || vr.getPathway() == pathway)) {
					equivalentVertexReps.add(vr);
					// List<PathwayVertex> currentVertices = vr.getPathwayVertices();
					// if (currentVertices.size() == vertices.size() && currentVertices.containsAll(vertices)) {
					// equivalentVertexReps.add(vr);
					// }
				}
			}
		}

		return equivalentVertexReps;
	}

	/**
	 *
	 * @param vertexRep1
	 * @param vertexRep2
	 * @return True if the vertexReps are the same or equivalent, false otherwise or if one vertexRep is null.
	 */
	public boolean areVerticesEquivalent(PathwayVertexRep vertexRep1, PathwayVertexRep vertexRep2) {
		if (vertexRep1 == null || vertexRep2 == null)
			return false;
		List<PathwayVertex> vertices = vertexRep1.getPathwayVertices();
		for (PathwayVertex v : vertices) {
			for (PathwayVertexRep vr : v.getPathwayVertexReps()) {
				if (vr == vertexRep2) {
					return true;
				}
			}
		}

		return false;
		// return (vertexRep1 == vertexRep2)
		// || (vertexRep1.getPathwayVertices().size() == vertexRep2.getPathwayVertices().size() && vertexRep1
		// .getPathwayVertices().containsAll(vertexRep2.getPathwayVertices()));
	}

	/**
	 * Calculates a path consisting of {@link PathwayVertexRep} objects for a specified vertexRep. This path ends if
	 * there is no unambiguous way to continue, the direction of edges changes, the pathway ends, or the
	 * {@link #maxBranchSwitchingPathLength} is reached. The specified <code>PathwayVertexRep</code> that represents the
	 * start of the path is added at the beginning of the path.
	 *
	 * @param vertexRep
	 *            The <code>PathwayVertexRep</code> that represents the start of the branch path.
	 *
	 * @param isLeavingPath
	 *            Determines whether the path leaves or comes into the specified vertexRep.
	 * @param maxPathLength
	 *            Maximum length of the returned path. Specify -1 if there should be no limit.
	 * @return
	 */
	public List<PathwayVertexRep> determineDirectionalPath(PathwayVertexRep vertexRep, boolean isLeavingPath,
			int maxPathLength) {

		List<PathwayVertexRep> vertexReps = new ArrayList<PathwayVertexRep>();
		vertexReps.add(vertexRep);
		// DefaultEdge existingEdge = pathway.getEdge(branchVertexRep, linearizedVertexRep);
		// if (existingEdge == null)
		// existingEdge = pathway.getEdge(linearizedVertexRep, branchVertexRep);

		PathwayVertexRep currentVertexRep = vertexRep;
		PathwayGraph pathway = vertexRep.getPathway();

		for (int i = 0; i < maxPathLength; i++) {
			List<PathwayVertexRep> nextVertices = null;
			if (!isLeavingPath) {
				nextVertices = Graphs.predecessorListOf(pathway, currentVertexRep);
			} else {
				nextVertices = Graphs.successorListOf(pathway, currentVertexRep);
			}

			if (nextVertices.size() == 0 || nextVertices.size() > 1) {
				return vertexReps;
			} else {
				currentVertexRep = nextVertices.get(0);
				vertexReps.add(currentVertexRep);
			}

		}

		return vertexReps;
	}

	/**
	 * Calculates a path consisting of {@link PathwayVertexRep} objects for a specified vertexRep. This path ends if the
	 * direction of edges changes, the pathway ends, or the {@link #maxBranchSwitchingPathLength} is reached. If there
	 * are multiple branches, the branch is determined using the specified {@link Comparator}. The specified
	 * <code>PathwayVertexRep</code> that represents the start of the path is added at the beginning of the path.
	 *
	 * @param vertexRep
	 *            The <code>PathwayVertexRep</code> that represents the start of the branch path.
	 *
	 * @param isLeavingPath
	 *            Determines whether the path leaves or comes into the specified vertexRep.
	 * @param maxPathLength
	 *            Maximum length of the returned path. Specify -1 if there should be no limit.
	 * @param comparator
	 *            Comparator that should be used when comparing different branch vertexReps. The vertexRep with the
	 *            highest associated value will be considered for path continuation.
	 *
	 * @return
	 */
	public List<PathwayVertexRep> determineDirectionalPath(PathwayVertexRep vertexRep, boolean isLeavingPath,
			int maxPathLength, Comparator<PathwayVertexRep> comparator) {

		List<PathwayVertexRep> vertexReps = new ArrayList<PathwayVertexRep>();
		vertexReps.add(vertexRep);
		// DefaultEdge existingEdge = pathway.getEdge(branchVertexRep, linearizedVertexRep);
		// if (existingEdge == null)
		// existingEdge = pathway.getEdge(linearizedVertexRep, branchVertexRep);

		PathwayVertexRep currentVertexRep = vertexRep;
		PathwayGraph pathway = vertexRep.getPathway();

		for (int i = 0; i < maxPathLength; i++) {
			List<PathwayVertexRep> nextVertices = null;
			if (!isLeavingPath) {
				nextVertices = Graphs.predecessorListOf(pathway, currentVertexRep);
			} else {
				nextVertices = Graphs.successorListOf(pathway, currentVertexRep);
			}

			if (nextVertices.size() == 0) {
				return vertexReps;
			} else if (nextVertices.size() > 1) {
				List<PathwayVertexRep> nextVerticesCopy = new ArrayList<>(nextVertices);
				Collections.sort(nextVerticesCopy, comparator);
				Collections.reverse(nextVerticesCopy);
				currentVertexRep = nextVerticesCopy.get(0);
			} else {
				currentVertexRep = nextVertices.get(0);
			}
			vertexReps.add(currentVertexRep);

		}

		return vertexReps;
	}

	public List<PathwayPath> getShortestPaths(final PathwayVertex from, final PathwayVertex to) {

		if (kShortestPathsAdapter == null)
			kShortestPathsAdapter = new KShortestPathsAdapter<>(getRootPathway());

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				List<GraphPath<PathwayVertex, DefaultEdge>> paths = kShortestPathsAdapter.getKShortestPaths(from, to,
						10);

				int i = 1;
				for (GraphPath<PathwayVertex, DefaultEdge> path : paths) {
					System.out.println(i + " NEW ALGO, length: " + (path.getEdgeList().size() + 1));
					printPath(path);
					i++;
				}

				// DirectedGraph<PathwayVertex, DefaultEdge> rootPathway = getRootPathway();
				// KShortestPaths<PathwayVertex, DefaultEdge> pathAlgo = new KShortestPaths<PathwayVertex, DefaultEdge>(
				// rootPathway, from, 3, 5);
				// List<GraphPath<PathwayVertex, DefaultEdge>> paths = pathAlgo.getPaths(to);
				// DijkstraShortestPath<PathwayVertex, DefaultEdge> pathAlgo = new DijkstraShortestPath<>(rootPathway,
				// from, to);

				// Graph rootGraph = convertRootGraph();
				// DirectedGraph<PathwayVertex, DefaultEdge> rootPathway2 = new DefaultDirectedGraph<PathwayVertex,
				// DefaultEdge>(
				// DefaultEdge.class);
				// for (BaseVertex vertex : rootGraph.get_vertex_list()) {
				// rootPathway2.addVertex(vertex.toPathwayVertex());
				// }
				// for (edu.asu.emit.qyan.alg.model.Pair<Integer, Integer> edge : rootGraph.getEdges()) {
				// PathwayVertex s = rootGraph.get_vertex(edge.first()).toPathwayVertex();
				// PathwayVertex d = rootGraph.get_vertex(edge.second()).toPathwayVertex();
				// rootPathway2.addEdge(s, d);
				// }
				//
				// DijkstraShortestPath<PathwayVertex, DefaultEdge> pathAlgo2 = new DijkstraShortestPath<>(rootPathway2,
				// from, to);

				// System.out.println("OLD ALGO");
				// GraphPath<PathwayVertex, DefaultEdge> vertexPath = pathAlgo.getPath();
				// printPath(vertexPath);

				// DijkstraShortestPathAlg alg = new DijkstraShortestPathAlg(rootGraph);
				// YenTopKShortestPathsAlg alg = new YenTopKShortestPathsAlg(rootGraph);
				// Path p = alg.get_shortest_path(rootGraph.getVertex(from), rootGraph.getVertex(to));
				// List<Path> paths = alg.get_shortest_paths(rootGraph.getVertex(from), rootGraph.getVertex(to), 10);
				// int k = 1;
				// for (Path p : paths) {
				// List<DefaultEdge> pathEdges = new ArrayList<>(p.get_vertices().size() - 1);
				// List<BaseVertex> pathVertices = p.get_vertices();
				// for (int i = 0; i < pathVertices.size() - 1; i++) {
				//
				// BaseVertex from = pathVertices.get(i);
				// BaseVertex to = pathVertices.get(i + 1);
				//
				// pathEdges.add(rootPathway.getEdge(from.toPathwayVertex(), to.toPathwayVertex()));
				//
				// }
				// System.out.println(k + " NEW ALGO, length: " + pathVertices.size());
				// GraphPath<PathwayVertex, DefaultEdge> path = new GraphPathImpl<PathwayVertex, DefaultEdge>(
				// rootPathway, pathVertices.get(0).toPathwayVertex(), pathVertices.get(
				// pathVertices.size() - 1).toPathwayVertex(), pathEdges, 0);
				//
				// printPath(path);
				// k++;
				// }

			}
		});

		t.start();

		return null;
	}

	protected void printPath(GraphPath<PathwayVertex, DefaultEdge> vertexPath) {
		DirectedGraph<PathwayVertex, DefaultEdge> rootPathway = getRootPathway();

		if (vertexPath != null) {
			PathwayPath path = new PathwayPath();

			// GraphPath<PathwayVertex, DefaultEdge> vertexPath = paths.get(0);

			StringBuilder b = new StringBuilder();
			PathSegment currentSegment = null;

			for (DefaultEdge edge : vertexPath.getEdgeList()) {
				PathwayVertex source = rootPathway.getEdgeSource(edge);
				PathwayVertex target = rootPathway.getEdgeTarget(edge);

				if (currentSegment == null) {
					Pair<PathwayVertexRep, PathwayVertexRep> vertexReps = getConnectedVertexRepsFromVertices(source,
							target, null);
					if (vertexReps == null) {
						Logger.log(new Status(IStatus.ERROR, "Path determination failed!",
								"Could not find connected vertex reps."));
						return;
					}
					currentSegment = new PathSegment();
					path.add(currentSegment);

					currentSegment.add(vertexReps.getFirst());
					currentSegment.add(vertexReps.getSecond());

				} else {
					// Test if the next vertex rep is directly connected to the previous vertex rep
					PathwayVertexRep lastVertexRep = currentSegment.get(currentSegment.size() - 1);
					PathwayVertexRep nextVertexRep = getConnectedVertexRep(lastVertexRep, target);
					if (nextVertexRep != null) {
						currentSegment.add(nextVertexRep);
					} else {
						// Try to find an equivalent node that connects to the next one within the same pathway
						// -> new path segment
						currentSegment = new PathSegment();
						path.add(currentSegment);
						Pair<PathwayVertexRep, PathwayVertexRep> vertexReps = getConnectedVertexRepsFromVertices(
								source, target, lastVertexRep.getPathway());
						if (vertexReps == null) {
							// Try to find an equivalent node that connects to the next one in any pathway
							vertexReps = getConnectedVertexRepsFromVertices(source, target, null);
							if (vertexReps == null) {
								Logger.log(new Status(IStatus.ERROR, "Path determination failed!",
										"Could not find connected vertex reps."));
								return;
							}
						}

						currentSegment.add(vertexReps.getFirst());
						currentSegment.add(vertexReps.getSecond());
					}
				}

				b.append(source.getHumanReadableName()).append(" - ").append(target.getHumanReadableName())
						.append(" , ");
			}
			System.out.println(b);
			System.out.println(path.toString());
		}
	}

	protected Pair<PathwayVertexRep, PathwayVertexRep> getConnectedVertexRepsFromVertices(PathwayVertex from,
			PathwayVertex to, PathwayGraph pathway) {
		for (PathwayVertexRep fromVertexRep : from.getPathwayVertexReps()) {
			PathwayGraph fromPathway = fromVertexRep.getPathway();
			if (pathway == null || fromPathway == pathway) {
				PathwayVertexRep toVertexRep = getConnectedVertexRep(fromVertexRep, to);
				if (toVertexRep != null) {
					return Pair.make(fromVertexRep, toVertexRep);
				}
			}
		}
		return null;
	}

	protected PathwayVertexRep getConnectedVertexRep(PathwayVertexRep from, PathwayVertex to) {
		for (PathwayVertexRep toVertexRep : to.getPathwayVertexReps()) {
			if (from.getPathway() == toVertexRep.getPathway() && from.getPathway().containsEdge(from, toVertexRep)) {
				return toVertexRep;
			}
		}
		return null;
	}

	/**
	 * @param monitor
	 * @param wikipathways
	 */
	public File preparePathwayData(String name, IProgressMonitor monitor) {
		final String URL_PATTERN = GeneralManager.DATA_URL_PREFIX + "pathways/%s_%s.zip";
		final Organism organism = GeneticMetaData.getOrganism();

		URL url = null;
		try {
			url = new URL(String.format(URL_PATTERN, name.toLowerCase(), organism.name().toLowerCase()));
			RemoteFile zip = RemoteFile.of(url);

			if (!zip.inCache(true)) {
				File tmp = zip.getFile();
				File unpacked = new File(tmp.getParentFile(), tmp.getName().replaceAll("\\.zip", ""));
				FileOperations.deleteDirectory(unpacked);
			}

			File localZip = zip.getOrLoad(false, monitor, "Caching Pathways (this may take a while): Downloading "
					+ name + " pathways (%2$d MB)");
			if (localZip == null || !localZip.exists()) {
				log.error("can't download: " + url);
				return null;
			}
			File unpacked = new File(localZip.getParentFile(), localZip.getName().replaceAll("\\.zip", ""));
			if (unpacked.exists())
				return unpacked;
			ZipUtils.unzipToDirectory(localZip.getAbsolutePath(), unpacked.getAbsolutePath());
			return unpacked;
		} catch (MalformedURLException e) {
			log.error("can't download: " + url);
			return null;
		}
	}

	/**
	 * Gets all mapped gene ids for a specified pathway
	 *
	 * @param pathway
	 * @param idType
	 *            The gene id type of the result set.
	 * @return
	 */
	public Set<Object> getPathwayGeneIDs(PathwayGraph pathway, IDType idType) {
		Set<Object> ids = new HashSet<>();
		IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType);
		IIDTypeMapper<Object, Object> mapper = idMappingManager.getIDTypeMapper(
				IDType.getIDType(EGeneIDTypes.DAVID.name()), idType);
		if (mapper == null)
			return ids;

		for (PathwayVertexRep v : pathway.vertexSet()) {
			ArrayList<Integer> vertexIDs = v.getDavidIDs();
			if (vertexIDs != null) {
				for (int davidID : vertexIDs) {
					Set<Object> mappedIDs = mapper.apply(davidID);
					if (mappedIDs != null) {
						ids.addAll(mappedIDs);
					}
				}
			}
		}

		return ids;
	}

	/**
	 *
	 */
	public static void createPathwayDatabases() {
		for (EPathwayDatabaseType type : EPathwayDatabaseType.values()) {
			type.load();
		}
	}

	/**
	 * @param pathwayID
	 * @return
	 */
	public PathwayGraph getPathway(int pathwayID) {
		return this.pathways.get(pathwayID);
	}

	/**
	 * @param id
	 * @return
	 */
	public boolean hasPathway(int pathwayID) {
		return this.pathways.containsKey(pathwayID);
	}
}
