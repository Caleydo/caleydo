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
package org.caleydo.view.enroute.path;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGroupRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.enroute.event.PathRendererChangedEvent;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.ANode;
import org.caleydo.view.enroute.path.node.ComplexNode;
import org.caleydo.view.enroute.path.node.CompoundNode;
import org.caleydo.view.enroute.path.node.GeneNode;
import org.caleydo.view.enroute.path.node.mode.ComplexNodeLinearizedMode;
import org.caleydo.view.enroute.path.node.mode.CompoundNodeLinearizedMode;
import org.caleydo.view.enroute.path.node.mode.GeneNodeLinearizedMode;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.event.EnRoutePathEvent;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.GraphPathImpl;

/**
 * Renderer that is responsible for rendering a single pathway path.
 *
 * @author Christian Partl
 *
 */
public abstract class APathwayPathRenderer extends ALayoutRenderer implements IEventBasedSelectionManagerUser,
		IListenerOwner {

	/**
	 * The list of path segments that are a list of {@link PathwayVertexRep}s.
	 */
	protected List<List<PathwayVertexRep>> pathSegments;

	/**
	 * List of renderable nodes for the path.
	 */
	protected List<ALinearizableNode> pathNodes = new ArrayList<>();

	/**
	 * View that renders this renderer.
	 */
	protected AGLView view;

	/**
	 * ID of the last node that was added. Used to create unique node IDs for picking.
	 */
	protected int lastNodeID = 0;

	/**
	 * Table perspectives for node previews.
	 */
	protected List<TablePerspective> tablePerspectives;

	/**
	 * Event space that is used for receiving and sending path events.
	 */
	protected String pathwayPathEventSpace = GLPathway.DEFAULT_PATHWAY_PATH_EVENT_SPACE;

	/**
	 * Minimum width in pixels required by the renderer.
	 */
	protected int minWidthPixels;

	/**
	 * Minimum height in pixels required by the renderer.
	 */
	protected int minHeightPixels;

	/**
	 * The queue which holds the events
	 */
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue = new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();

	protected EventBasedSelectionManager geneSelectionManager;
	protected EventBasedSelectionManager metaboliteSelectionManager;
	protected EventBasedSelectionManager sampleSelectionManager;

	protected PixelGLConverter pixelGLConverter;
	protected CaleydoTextRenderer textRenderer;

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

	public APathwayPathRenderer(AGLView view, List<TablePerspective> tablePerspectives) {
		this.view = view;
		this.tablePerspectives = tablePerspectives;
		this.pixelGLConverter = view.getPixelGLConverter();
		this.textRenderer = view.getTextRenderer();

		geneSelectionManager = new EventBasedSelectionManager(this, IDType.getIDType("DAVID"));
		geneSelectionManager.registerEventListeners();

		metaboliteSelectionManager = new EventBasedSelectionManager(this, IDType.getIDType("METABOLITE"));
		metaboliteSelectionManager.registerEventListeners();

		List<GeneticDataDomain> dataDomains = DataDomainManager.get().getDataDomainsByType(GeneticDataDomain.class);
		if (dataDomains.size() != 0) {
			IDType sampleIDType = dataDomains.get(0).getSampleIDType().getIDCategory().getPrimaryMappingType();
			sampleSelectionManager = new EventBasedSelectionManager(this, sampleIDType);
			sampleSelectionManager.registerEventListeners();
		}
	}

	/**
	 * Method that initializes the {@link APathwayPathRenderer}. Shall be called once prior use.
	 */
	public void init() {
		registerEventListeners();
	}

	/**
	 * Sets a new path to be linearized.
	 *
	 * @param pathSegments
	 *            List of path segments that are a List of {@link PathwayVertexRep}s. The last node of segment n and the
	 *            first node of segment n+1 must be equivalent (i.e. they must refer to the same {@link PathwayVertex}
	 *            objects).
	 */
	public void setPath(List<List<PathwayVertexRep>> pathSegments) {
		this.pathSegments = pathSegments;

		createNodes(pathSegments);

		PathRendererChangedEvent event = new PathRendererChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);

		updateLayout();

	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	protected void onPathwayPathChanged(EnRoutePathEvent event) {
		PathwayPath path = event.getPath();
		if (path != null && path.getPath() != null) {
			List<List<PathwayVertexRep>> pathParts = new ArrayList<>();
			pathParts.add(path.getNodes());
			if (path.getNodes().size() > 0) {
				PathwayVertexRep vertexRep = path.getNodes().get(path.getNodes().size() - 1);
				Set<PathwayVertexRep> equivalentVertexReps = PathwayManager.get().getEquivalentVertexReps(vertexRep);
				PathwayVertexRep eqVertexRep = null;
				PathwayVertexRep nextVertexRep = null;

				for (PathwayVertexRep vr : equivalentVertexReps) {
					eqVertexRep = vr;
					PathwayGraph pw = eqVertexRep.getPathway();
					if (pw.getType() == EPathwayDatabaseType.KEGG) {
						Set<DefaultEdge> edges = pw.edgesOf(eqVertexRep);
						for (DefaultEdge edge : edges) {
							if (pw.getEdgeSource(edge) == eqVertexRep) {
								nextVertexRep = pw.getEdgeTarget(edge);
								break;
							}
						}
						if (nextVertexRep != null)
							break;
					}
				}
				if (eqVertexRep != null) {
					if (nextVertexRep != null) {
						List<PathwayVertexRep> nextSegment = new ArrayList<>(2);
						nextSegment.add(eqVertexRep);
						nextSegment.add(nextVertexRep);
						pathParts.add(nextSegment);
					}
				}
			}

			setPath(pathParts);
		} else {
			setPath(new ArrayList<List<PathwayVertexRep>>());
		}
	}

	/**
	 * Updates the layout of the path. This method should be called everytime something changes in the layout of the
	 * path.
	 */
	protected abstract void updateLayout();

	protected void createNodes(List<List<PathwayVertexRep>> pathSegments) {

		for (ANode node : pathNodes) {
			node.destroy();
		}
		pathNodes.clear();
		for (List<PathwayVertexRep> vertexReps : pathSegments) {
			List<ALinearizableNode> currentNodes = new ArrayList<>();
			createNodesForList(currentNodes, vertexReps);
			appendNodes(pathNodes, currentNodes);
		}

	}

	/**
	 * Merges the last node of pathNodes with the first node of nodesToAppend and adds the remaining nodesToAppend to
	 * pathNodes.
	 *
	 * @param pathNodes
	 * @param nodesToAppend
	 */
	protected void appendNodes(List<ALinearizableNode> pathNodes, List<ALinearizableNode> nodesToAppend) {
		if (pathNodes.size() <= 0) {
			pathNodes.addAll(nodesToAppend);
		} else {
			if (nodesToAppend.size() > 0) {
				ALinearizableNode lastNodeOfPath = pathNodes.get(pathNodes.size() - 1);
				ALinearizableNode firstNodeOfNodesToAppend = nodesToAppend.get(0);
				mergeNodes(lastNodeOfPath, firstNodeOfNodesToAppend);
				nodesToAppend.remove(0);
				firstNodeOfNodesToAppend.destroy();
				pathNodes.addAll(nodesToAppend);
			}
		}
	}

	/**
	 * Merges node1 with node2, i.e., the {@link PathwayVertexRep}s from node2 are added to node1.
	 *
	 * @param node1
	 * @param node2
	 */
	protected void mergeNodes(ALinearizableNode node1, ALinearizableNode node2) {
		for (PathwayVertexRep vertexRep : node2.getVertexReps()) {
			node1.addPathwayVertexRep(vertexRep);
		}
		if (node1 instanceof ComplexNode) {
			List<ALinearizableNode> nodesOfNode1 = ((ComplexNode) node1).getNodes();
			for (ALinearizableNode node1Child : nodesOfNode1) {
				List<ALinearizableNode> nodesOfNode2 = ((ComplexNode) node2).getNodes();
				for (ALinearizableNode node2Child : nodesOfNode2) {
					if (node1Child.getMappedDavidIDs().size() == node2Child.getMappedDavidIDs().size()
							&& node1Child.getMappedDavidIDs().containsAll(node2Child.getMappedDavidIDs())) {
						mergeNodes(node1Child, node2Child);
					}
				}
			}
		}
	}

	protected void createNodesForList(List<ALinearizableNode> nodes, List<PathwayVertexRep> vertexReps) {

		CaleydoTextRenderer textRenderer = view.getTextRenderer();

		for (int i = 0; i < vertexReps.size(); i++) {
			PathwayVertexRep currentVertexRep = vertexReps.get(i);
			ALinearizableNode node = null;
			if (currentVertexRep.getType() == EPathwayVertexType.group) {
				PathwayVertexGroupRep groupRep = (PathwayVertexGroupRep) currentVertexRep;
				List<PathwayVertexRep> groupedReps = groupRep.getGroupedVertexReps();
				List<ALinearizableNode> groupedNodes = new ArrayList<ALinearizableNode>();
				createNodesForList(groupedNodes, groupedReps);
				ComplexNode complexNode = new ComplexNode(this, textRenderer, view, lastNodeID++,
						new ComplexNodeLinearizedMode(view, this));
				complexNode.setNodes(groupedNodes);
				for (ALinearizableNode groupedNode : groupedNodes) {
					groupedNode.setParentNode(complexNode);
				}
				complexNode.addPathwayVertexRep(currentVertexRep);
				complexNode.init();
				node = complexNode;
			} else if (currentVertexRep.getType() == EPathwayVertexType.compound) {
				CompoundNode compoundNode = new CompoundNode(this, view, lastNodeID++, new CompoundNodeLinearizedMode(
						view, this));

				compoundNode.addPathwayVertexRep(currentVertexRep);
				compoundNode.init();
				node = compoundNode;

			} else {

				// TODO: Verify that this is also the right approach for
				// enzymes and ortholog
				GeneNode geneNode = new GeneNode(this, textRenderer, view, lastNodeID++, new GeneNodeLinearizedMode(
						view, this));
				int commaIndex = currentVertexRep.getName().indexOf(',');
				if (commaIndex > 0) {
					geneNode.setLabel(currentVertexRep.getName().substring(0, commaIndex));
				} else {
					geneNode.setLabel(currentVertexRep.getName());
				}
				geneNode.addPathwayVertexRep(currentVertexRep);
				geneNode.init();
				node = geneNode;
			}

			nodes.add(node);
		}

	}

	@Override
	public void destroy(GL2 gl) {
		geneSelectionManager.unregisterEventListeners();
		metaboliteSelectionManager.unregisterEventListeners();
		sampleSelectionManager.unregisterEventListeners();
		unregisterEventListeners();
		super.destroy(gl);
	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
		setDisplayListDirty();
	}

	/**
	 * @return the geneSelectionManager, see {@link #geneSelectionManager}
	 */
	public EventBasedSelectionManager getGeneSelectionManager() {
		return geneSelectionManager;
	}

	/**
	 * @return the metaboliteSelectionManager, see {@link #metaboliteSelectionManager}
	 */
	public EventBasedSelectionManager getMetaboliteSelectionManager() {
		return metaboliteSelectionManager;
	}

	/**
	 * @return the sampleSelectionManager, see {@link #sampleSelectionManager}
	 */
	public EventBasedSelectionManager getSampleSelectionManager() {
		return sampleSelectionManager;
	}

	/**
	 * @param tablePerspectives
	 *            setter, see {@link tablePerspectives}
	 */
	public void setTablePerspectives(List<TablePerspective> tablePerspectives) {
		this.tablePerspectives = tablePerspectives;
	}

	/**
	 * @return the tablePerspectives, see {@link #tablePerspectives}
	 */
	public List<TablePerspective> getTablePerspectives() {
		return tablePerspectives;
	}

	/**
	 * @return the pathNodes, see {@link #pathNodes}
	 */
	public List<ALinearizableNode> getPathNodes() {
		return pathNodes;
	}

	/**
	 * @return the view, see {@link #view}
	 */
	public AGLView getView() {
		return view;
	}

	/**
	 * @param pathwayPathEventSpace
	 *            setter, see {@link pathwayPathEventSpace}
	 */
	public void setPathwayPathEventSpace(String pathwayPathEventSpace) {
		this.pathwayPathEventSpace = pathwayPathEventSpace;
	}

	@Override
	public final synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		queue.add(new Pair<AEventListener<? extends IListenerOwner>, AEvent>(listener, event));
	}

	/**
	 * This method should be called every display cycle when it is save to change the state of the object. It processes
	 * all the previously submitted events.
	 */
	protected final void processEvents() {
		Pair<AEventListener<? extends IListenerOwner>, AEvent> pair;
		while (queue.peek() != null) {
			pair = queue.poll();
			pair.getFirst().handleEvent(pair.getSecond());
		}
	}

	@Override
	protected void prepare() {
		processEvents();
		if (isDisplayListDirty()) {
			updateLayout();
		}
	}

	@Override
	public void registerEventListeners() {
		listeners.register(this, pathwayPathEventSpace);

	}

	@Override
	public void unregisterEventListeners() {
		listeners.unregisterAll();

	}

	/**
	 * Removes the specified node from the path if it is at the start or the end of the path.
	 *
	 * @param node
	 */
	public void removeNodeFromPath(ALinearizableNode node) {

		int linearizedNodeIndex = pathNodes.indexOf(node);

		if (linearizedNodeIndex == 0) {
			pathSegments.remove(0);
		} else if (linearizedNodeIndex == pathSegments.size() - 1) {
			pathSegments.remove(pathSegments.size() - 1);

		} else {
			return;
		}

		setPath(pathSegments);

		broadcastPath();
	}

	protected void broadcastPath() {

		PathwayPath pathwayPath = null;
		// FIXME: TODO: Use pathSegments correctly.
		if (pathSegments.size() > 0) {
			List<PathwayVertexRep> pathSegment = pathSegments.get(0);
			if (pathSegment.size() > 0) {
				PathwayVertexRep startVertexRep = pathSegment.get(0);
				PathwayVertexRep endVertexRep = pathSegment.get(pathSegment.size() - 1);
				List<DefaultEdge> edges = new ArrayList<DefaultEdge>();
				PathwayGraph pathway = startVertexRep.getPathway();

				for (int i = 0; i < pathSegment.size() - 1; i++) {
					PathwayVertexRep currentVertexRep = pathSegment.get(i);
					PathwayVertexRep nextVertexRep = pathSegment.get(i + 1);

					DefaultEdge edge = pathway.getEdge(currentVertexRep, nextVertexRep);
					if (edge == null)
						edge = pathway.getEdge(nextVertexRep, currentVertexRep);
					edges.add(edge);
				}
				GraphPath<PathwayVertexRep, DefaultEdge> graphPath = new GraphPathImpl<PathwayVertexRep, DefaultEdge>(
						pathway, startVertexRep, endVertexRep, edges, edges.size());

				pathwayPath = new PathwayPath(graphPath);
			}
		}
		EnRoutePathEvent event = new EnRoutePathEvent();
		event.setEventSpace(pathwayPathEventSpace);
		event.setPath(pathwayPath);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);

	}

	@Override
	public int getMinWidthPixels() {
		return super.getMinWidthPixels();
	}

	@Override
	public int getMinHeightPixels() {
		return minHeightPixels;
	}

	/**
	 * @param pixelGLConverter
	 *            setter, see {@link pixelGLConverter}
	 */
	public void setPixelGLConverter(PixelGLConverter pixelGLConverter) {
		this.pixelGLConverter = pixelGLConverter;
	}

	/**
	 * @param textRenderer
	 *            setter, see {@link textRenderer}
	 */
	public void setTextRenderer(CaleydoTextRenderer textRenderer) {
		this.textRenderer = textRenderer;
	}

}
