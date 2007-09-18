/*
 * @(#)SugiyamaLayoutAlgorithm.java 1.0 18-MAY-2004
 * 
 * Copyright (c) 2004, Sven Luzar
 * Copyright (c) 2004, Nicholas Sushkin
 * Copyright (c) 2004-2005, Gaudenz Alder
 * All rights reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the name of JGraph nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jgraph.plugins.layouts;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.VertexView;

/**
 * Arranges the nodes with the Sugiyama Layout Algorithm.<br>
 *
 * <a href="http://plg.uwaterloo.ca/~itbowman/CS746G/Notes/Sugiyama1981_MVU/">
 *  Link to the algorithm</a>
 *
 *<br>
 *<br>
 * @author Sven Luzar<br>
 * modified by Gaudenz Alder and Nicholas Sushkin
 */
public class SugiyamaLayoutAlgorithm extends JGraphLayoutAlgorithm {

	/** Const to add Attributes at the Nodes
	 *
	 */
	public static final String SUGIYAMA_VISITED = "SugiyamaVisited" /*#Frozen*/;

	/** Const to add the Cell Wrapper to the Nodes
	 */
	public static final String SUGIYAMA_CELL_WRAPPER =
		"SugiyamaCellWrapper" /*#Frozen*/;

	/** 
	 * Const to add Attributes at the Nodes indicating that the cell was explicitly specified to the layout.
     *
     * @see #run(org.jgraph.JGraph,Object[],Object[])
	 */
	public static final String SUGIYAMA_SELECTED = "SugiyamaSelected" /*#Frozen*/;

	/** represents the size of the grid in horizontal grid elements
	 *
	 */
	protected int gridAreaSize = Integer.MIN_VALUE;

	/** A list with Integer Objects. The list contains the
	 *  history of movements per loop
	 *  It was needed for the progress dialog
	 */
	List movements = null;
	
	/** Represents the movements in the current loop.
	 *  It was needed for the progress dialog
	 */
	int movementsCurrentLoop = -1;
	
	/** Represents the maximum of movements in the current loop.
	 *  It was needed for the progress dialog
	 */
	int movementsMax = Integer.MIN_VALUE;
	
	/** Represents the current loop number
	 *  It was needed for the progress dialog
	 */
	int iteration = 0;
	
    /**
     * The default layout direction is vertical (top-down)
     */
	protected boolean vertical = true;
	
    /**
     * The default grid spacing is (250, 150).
     */
	protected Point spacing = new Point(250, 150);
	
    /**
     * Controls whether the graph should be placed as close to the origin as possible.
     */
    protected boolean flushToOrigin = false;
    
	/**
	 * Returns an new instance of SugiyamaLayoutSettings
	 */
	public JGraphLayoutSettings createSettings() {
		return new SugiyamaLayoutSettings(this);
	}

	/**
	 * Returns the name of this algorithm in human
	 * readable form.
	 */
	public String toString() {
		return "Sugiyama";
	}

	/**
	 * Get a human readable hint for using this layout.
	 */
	public String getHint() {
		return "Ignores selection";
	}
	
	/**
	 * Implementation.
	 *
	 * First of all the Algorithm searches the roots from the
	 * Graph. Starting from this roots the Algorithm creates
	 * levels and stores them in the member <code>levels</code>.
	 * The Member levels contains Vector Objects and the Vector per level
	 * contains Cell Wrapper Objects. After that the Algorithm
	 * tries to solve the edge crosses from level to level and
	 * goes top down and bottom up. After minimization of the
	 * edge crosses the algorithm moves each node to its
	 * bary center. Last but not Least the method draws the Graph.
	 *
	 * @see LayoutAlgorithm
	 *
     * @param graph JGraph instance
     * @param dynamic_cells List of all nodes the layout should move
     * @param static_cells List of node the layout should not move but allow for
	 */

    public void run(JGraph graph, Object[] dynamic_cells, Object[] static_cells) {
		CellView[] selectedCellViews =
			graph.getGraphLayoutCache().getMapping(dynamic_cells);
 
		// gridAreaSize should really belong to the run state.
        gridAreaSize = Integer.MIN_VALUE;
       /*  The Algorithm distributes the nodes on a grid.
		 *  For this grid you can configure the horizontal spacing.
		 *  This field specifies the configured value
		 *
		 */
		
		Rectangle2D maxBounds = new Rectangle2D.Double();
		for (int i = 0; i < selectedCellViews.length; i++) {
			// Add vertex to list
			if (selectedCellViews[i] instanceof VertexView) {
				// Fetch Bounds
				Rectangle2D bounds = selectedCellViews[i].getBounds();
				// Update Maximum
				if (bounds != null)
					maxBounds.setFrame(0, 0,
							Math.max(bounds.getWidth(), maxBounds.getWidth()),
							Math.max(bounds.getHeight(), maxBounds.getHeight()));
			}
		}

		if (spacing.x == 0)
			spacing.x = (int) (2*maxBounds.getWidth());

		if (spacing.y == 0)
			spacing.y = (int) (2*maxBounds.getHeight()); // (jgraph.getGridSize()*6);

        // mark selected cell views in the graph
        markSelected(selectedCellViews, true);

		// search all roots
		List roots = searchRoots(graph, selectedCellViews);

		// return if no root found
		if (roots.size() == 0)
			return;

		// create levels
		List levels = fillLevels(graph, selectedCellViews, roots);

		// solves the edge crosses
		solveEdgeCrosses(graph, levels);

		// move all nodes into the barycenter
		moveToBarycenter(graph, selectedCellViews, levels);

		Point min = flushToOrigin ? new Point(0, 0) : findMinimumAndSpacing(selectedCellViews, spacing);

		// draw the graph in the window
		drawGraph(graph, levels, min, spacing);

        // remove marks from the selected cell views in the graph
        markSelected(selectedCellViews, false);
        
	}

    /**
     * Adds an attribute {@link #SUGIYAMA_SELECTED SUGIYAMA_SELECTED} to the specified selected cell views.
     *
     * @param selectedCellViews the specified cell views
     * @param addMark true to add the mark, false to remove the mark
     */
    protected void markSelected(CellView[] selectedCellViews, boolean addMark)
    {
    	if (addMark)
    	{
    		for (int i = 0; i < selectedCellViews.length; i++) {
    			if (selectedCellViews[i] != null) {
    				selectedCellViews[i].getAttributes().put(SUGIYAMA_SELECTED, Boolean.TRUE);
    			}
    		}
    	}
        else
        {
            for (int i = 0; i < selectedCellViews.length; i++) {
       			if (selectedCellViews[i] != null) {
       				selectedCellViews[i].getAttributes().remove(SUGIYAMA_SELECTED);
       			}
            }
        }
    }

    
    /**
     * Detects whether the specified cell has been marked selected.
     *
     * @see #markSelected(CellView[], boolean)
     *
     * @param cell the cell to inspect
     * @return true if the view has been marked selected and false otherwise.
     */
    protected boolean isSelected(final GraphLayoutCache cache, final Object cell)
    {
        final CellView view = cache.getMapping(cell, false);
        return view != null && view.getAttributes().get(SUGIYAMA_SELECTED) != null;
    }
    

	/** Searches all Roots for the current Graph
	 *  First the method marks any Node as not visited.
	 *  Than calls searchRoots(MyGraphCell) for each
	 *  not visited Cell.
	 *  The Roots are stored in the Vector named roots
	 *
	 * 	@return returns a Vector with the roots
	 *  @see #searchRoots(JGraph, CellView[])
	 */
	protected List searchRoots(JGraph jgraph, CellView[] selectedCellViews) {

		// get all cells and relations
		List vertexViews = new ArrayList(selectedCellViews.length);
		List roots = new ArrayList();

		// first: mark all as not visited
		// O(allCells&Edges)
		for (int i = 0; i < selectedCellViews.length; i++) {
			if (selectedCellViews[i] instanceof VertexView) {
				VertexView vertexView = (VertexView) selectedCellViews[i];
				vertexView.getAttributes().remove(SUGIYAMA_VISITED);
				vertexViews.add(selectedCellViews[i]);
			}
		}

		// O(graphCells)
		for (int i = 0; i < vertexViews.size(); i++) {
			VertexView vertexView = (VertexView) vertexViews.get(i);
			if (vertexView.getAttributes().get(SUGIYAMA_VISITED) == null) {
				searchRoots(jgraph, vertexView, roots);
			}
		}

		// Error Msg if the graph has no roots
		if (roots.size() == 0) {
			throw new IllegalArgumentException("The Graph is not a DAG. Can't use Sugiyama Algorithm!");
		}
		return roots;
	}

	/** Searches Roots for the current Cell.
	 *
	 *  Therefore he looks at all Ports from the Cell.
	 *  At the Ports he looks for Edges.
	 *  At the Edges he looks for the Target.
	 *  If the Ports of the current Cell contains the target ReViewNodePort
	 *  he follows the edge to the source and looks at the
	 *  Cell for this source.
	 *
	 *  @param graphCell The current cell
	 */
	protected void searchRoots(
		JGraph jgraph,
		VertexView vertexViewToInspect,
		List roots) {
		// the node already visited
		if (vertexViewToInspect.getAttributes().get(SUGIYAMA_VISITED)
			!= null) {
			return;
		}

		// mark as visited for cycle tests
		vertexViewToInspect.getAttributes().put(SUGIYAMA_VISITED, Boolean.TRUE);

		GraphModel model = jgraph.getModel();
	    GraphLayoutCache cache = jgraph.getGraphLayoutCache();
	    
		// get all Ports and search the relations at the ports
		//List vertexPortViewList = new ArrayList() ;

		Object vertex = vertexViewToInspect.getCell();

		int portCount = model.getChildCount(vertex);
		for (int j = 0; j < portCount; j++) {
			Object port = model.getChild(vertex, j);

			// Test all relations for where
			// the current node is a target node
			// for roots

			boolean isRoot = true;
			Iterator itrEdges = model.edges(port);
			while (itrEdges.hasNext()) {
				Object edge = itrEdges.next();

                // if not selected do not follow
                if (!isSelected(cache, edge)) { continue; }

				// if the current node is a target node
				// get the source node and test
				// the source node for roots

				if (model.getTarget(edge) == port) {
					Object sourcePort = model.getSource(edge);

					Object sourceVertex = model.getParent(sourcePort);

					CellView sourceVertexView =
						jgraph.getGraphLayoutCache().getMapping(
							sourceVertex,
							false);
					if (sourceVertexView instanceof VertexView) {
						searchRoots(
							jgraph,
							(VertexView) sourceVertexView,
							roots);
						isRoot = false;
					}
				}
			}
			// The current node is never a Target Node
			// -> The current node is a root node
			if (isRoot) {
				roots.add(vertexViewToInspect);
			}
		}
	}

	/** Method fills the levels and stores them in the member levels.
	
	 *  Each level was represended by a Vector with Cell Wrapper objects.
	 *  These Vectors are the elements in the <code>levels</code> Vector.
	 *
	 */
	protected List fillLevels(
		JGraph jgraph,
		CellView[] selectedCellViews,
		List rootVertexViews) {
		List levels = new Vector();

		// mark as not visited
		// O(allCells)
		for (int i = 0; i < selectedCellViews.length; i++) {
			CellView cellView = selectedCellViews[i];

			// more stabile
			if (cellView == null)
				continue;

			cellView.getAttributes().remove(SUGIYAMA_VISITED);
		}

		Iterator rootIter = rootVertexViews.iterator();
		while (rootIter.hasNext()) {
			VertexView vertexView = (VertexView) rootIter.next();
			fillLevels(jgraph, levels, 0, vertexView);
		}

		return levels;

	}

	/** Fills the Vector for the specified level with a wrapper
	 *  for the MyGraphCell. After that the method called for
	 *  each neighbor graph cell.
	 *
	 *  @param level        The level for the graphCell
	 *  @param graphCell    The Graph Cell
	 */
	protected void fillLevels(
		JGraph jgraph,
		List levels,
		int level,
		VertexView vertexView) {
		// precondition control
		if (vertexView == null)
			return;

		// be sure that the list container exists for the current level
		if (levels.size() == level)
			levels.add(level, new ArrayList());

		// if the cell already visited return
		if (vertexView.getAttributes().get(SUGIYAMA_VISITED) != null) {
			// The graph is cyclic return.
			return;
		}

		// is the cell already assigned to a level?
		CellWrapper w = (CellWrapper) vertexView.getAttributes().get(SUGIYAMA_CELL_WRAPPER);
		if (w != null) 
		{				
			// is the current level OK?
			if (w.getLevel() < level)
			{
				// The level is too high
				//System.out.println("Problem with level:" + vertexView.getCell().toString() + " Current Level: " + w.getLevel() + " Parent Level:" + (level-1));
				
				// Remove the cell from the high level				
				List listForTheHigherLevel = (ArrayList) levels.get(w.getLevel());
				listForTheHigherLevel.remove(w);
				vertexView.getAttributes().remove(SUGIYAMA_CELL_WRAPPER);
			}
			else
			{
				// the current assignment is OK, return
				return;
			}
		}

		// mark as visited for cycle tests
		vertexView.getAttributes().put(SUGIYAMA_VISITED, Boolean.TRUE);

		// put the current node into the current level
		// get the Level list
		List listForTheCurrentLevel = (ArrayList) levels.get(level);

		// Create a wrapper for the node
		int numberForTheEntry = listForTheCurrentLevel.size();

		CellWrapper wrapper =
			new CellWrapper(level, numberForTheEntry, vertexView);

		// put the Wrapper in the LevelVector
		listForTheCurrentLevel.add(wrapper);
		
//		System.out.println(vertexView.getCell().toString() + " Level: " + level + " Nr: " + numberForTheEntry);

		// concat the wrapper to the cell for an easy access
		vertexView.getAttributes().put(SUGIYAMA_CELL_WRAPPER, wrapper);

		// if the Cell has no Ports we can return, there are no relations
		Object vertex = vertexView.getCell();
		GraphModel model = jgraph.getModel();
		GraphLayoutCache cache = jgraph.getGraphLayoutCache();
		int portCount = model.getChildCount(vertex);

		// iterate any NodePort
		for (int i = 0; i < portCount; i++) {

			Object port = model.getChild(vertex, i);

			// iterate any Edge in the port
			Iterator itrEdges = model.edges(port);

			while (itrEdges.hasNext()) {
				Object edge = itrEdges.next();

                // if not selected, do not follow
                if (!isSelected(cache, edge)) { continue; }

                // if the Edge is a forward edge we should follow this edge
				if (port == model.getSource(edge)) {
					Object targetPort = model.getTarget(edge);
					Object targetVertex = model.getParent(targetPort);

					// if not selected, do not follow the vertex
                    if (!isSelected(cache, targetVertex)) { continue; }

                    VertexView targetVertexView =
						(VertexView) jgraph.getGraphLayoutCache().getMapping(
							targetVertex,
							false);
					fillLevels(jgraph, levels, (level + 1), targetVertexView);
				}
			}
		}

		if (listForTheCurrentLevel.size() > gridAreaSize) {
			gridAreaSize = listForTheCurrentLevel.size();
		}

		// unmark as visited for cycle tests
		vertexView.getAttributes().remove(SUGIYAMA_VISITED);
	}

	/** calculates the minimum for the paint area.
	 *
	 */
	protected Point findMinimumAndSpacing(
		CellView[] graphCellViews,
		Point spacing) {
		try {

			// variables
			/* represents the minimum x value for the paint area
			 */
			int min_x = 1000000;

			/* represents the minimum y value for the paint area
			 */
			int min_y = 1000000;

			// find the maximum & minimum coordinates

			for (int i = 0; i < graphCellViews.length; i++) {

				// the cellView and their bounds
				CellView cellView = graphCellViews[i];

				if (cellView == null)
					continue;
					
				Rectangle2D rect = cellView.getBounds();
				Rectangle cellViewBounds = new Rectangle((int) rect.getX(), 
						(int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());

				// checking min area
				try {
					if (cellViewBounds.x < min_x)
						min_x = cellViewBounds.x;
					if (cellViewBounds.y < min_y)
						min_y = cellViewBounds.y;
					/*
					if (cellViewBounds.width > spacing.x)
						spacing.x = cellViewBounds.width;
					if (cellViewBounds.height > spacing.y)
						spacing.y = cellViewBounds.height;
					*/

				} catch (Exception e) {
					System.err.println("---------> ERROR in calculateValues."
					/*#Frozen*/
					);
					e.printStackTrace();
				}
			}
			// if the cell sice is bigger than the userspacing
			// dublicate the spacingfactor
			return new Point(min_x, min_y);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** Updates the progress based on the movements count
	 *
	 */
	protected void updateProgress4Movements() {
		// adds the current loop count
		movements.add(new Integer(movementsCurrentLoop));
		iteration++;

		// if the current loop count is higher than the max movements count
		// memorize the new max
		if (movementsCurrentLoop > movementsMax) {
			movementsMax = movementsCurrentLoop;
		}
	}

	protected void solveEdgeCrosses(JGraph jgraph, List levels) {
		movements = new ArrayList(100);
		movementsCurrentLoop = -1;
		movementsMax = Integer.MIN_VALUE;
		iteration = 0;

		while (movementsCurrentLoop != 0) {

			// reset the movements per loop count
			movementsCurrentLoop = 0;

			// top down
			for (int i = 0; i < levels.size() - 1; i++) {
				movementsCurrentLoop
					+= solveEdgeCrosses(jgraph, true, levels, i);
			}

			// bottom up
			for (int i = levels.size() - 1; i >= 1; i--) {
				movementsCurrentLoop
					+= solveEdgeCrosses(jgraph, false, levels, i);
			}

			updateProgress4Movements();
		}
	}

	/**
	 *  @return movements
	 */
	protected int solveEdgeCrosses(
		JGraph jgraph,
		boolean down,
		List levels,
		int levelIndex) {
		// Get the current level
		List currentLevel = (List) levels.get(levelIndex);
		int movements = 0;

		// restore the old sort
		Object[] levelSortBefore = currentLevel.toArray();

		// new sort
		Collections.sort(currentLevel);

		// test for movements
		for (int j = 0; j < levelSortBefore.length; j++) {
			if (((CellWrapper) levelSortBefore[j]).getEdgeCrossesIndicator()
				!= ((CellWrapper) currentLevel.get(j))
					.getEdgeCrossesIndicator()) {
				movements++;

			}
		}

		GraphModel model = jgraph.getModel();
		GraphLayoutCache cache = jgraph.getGraphLayoutCache();

		// Colections Sort sorts the highest value to the first value
		for (int j = currentLevel.size() - 1; j >= 0; j--) {
			CellWrapper sourceWrapper = (CellWrapper) currentLevel.get(j);

			VertexView sourceView = sourceWrapper.getVertexView();

			Object sourceVertex = sourceView.getCell();
			int sourcePortCount = model.getChildCount(sourceVertex);

			for (int k = 0; k < sourcePortCount; k++) {
				Object sourcePort = model.getChild(sourceVertex, k);

				Iterator sourceEdges = model.edges(sourcePort);
				while (sourceEdges.hasNext()) {
					Object edge = sourceEdges.next();

                    // if not selected, do not follow
                    if (!isSelected(cache, edge)) { continue; }

					// if it is a forward edge follow it
					Object targetPort = null;
					if (down && sourcePort == model.getSource(edge)) {
						targetPort = model.getTarget(edge);
					}
					if (!down && sourcePort == model.getTarget(edge)) {
						targetPort = model.getSource(edge);
					}
					if (targetPort == null)
						continue;

					Object targetCell = model.getParent(targetPort);

                    // if the target cell not selected, do not follow
                    if (!isSelected(cache, targetCell))
                        continue;
                    
					VertexView targetVertexView =
						(VertexView) jgraph.getGraphLayoutCache().getMapping(
							targetCell,
							false);

					if (targetVertexView == null)
						continue;

					CellWrapper targetWrapper =
						(CellWrapper) targetVertexView.getAttributes().get(
							SUGIYAMA_CELL_WRAPPER);

					// do it only if the edge is a forward edge to a deeper level
					if (down
						&& targetWrapper != null
						&& targetWrapper.getLevel() > levelIndex) {
						targetWrapper.addToEdgeCrossesIndicator(
							sourceWrapper.getEdgeCrossesIndicator());
					}
					if (!down
						&& targetWrapper != null
						&& targetWrapper.getLevel() < levelIndex) {
						targetWrapper.addToEdgeCrossesIndicator(
							sourceWrapper.getEdgeCrossesIndicator());
					}
				}
			}
		}

		return movements;
	}

	protected void moveToBarycenter(
		JGraph jgraph,
		CellView[] allSelectedViews,
		List levels) {

		//================================================================
		// iterate any ReViewNodePort
		GraphModel model = jgraph.getModel();
		GraphLayoutCache cache = jgraph.getGraphLayoutCache();
		
		for (int i = 0; i < allSelectedViews.length; i++) {
			if (!(allSelectedViews[i] instanceof VertexView))
				continue;

			VertexView vertexView = (VertexView) allSelectedViews[i];

			CellWrapper currentwrapper =
				(CellWrapper) vertexView.getAttributes().get(
					SUGIYAMA_CELL_WRAPPER);

			Object vertex = vertexView.getCell();
			int portCount = model.getChildCount(vertex);

			for (int k = 0; k < portCount; k++) {
				Object port = model.getChild(vertex, k);

				// iterate any Edge in the port

				Iterator edges = model.edges(port);
				while (edges.hasNext()) {
					Object edge = edges.next();

                    // if edge not selected, do not follow
                    if (!isSelected(cache, edge))
                        continue;

					Object neighborPort = null;
					// if the Edge is a forward edge we should follow this edge
					if (port == model.getSource(edge)) {
						neighborPort = model.getTarget(edge);
					} else {
						if (port == model.getTarget(edge)) {
							neighborPort = model.getSource(edge);
						} else {
							continue;
						}
					}

					Object neighborVertex = model.getParent(neighborPort);

                    // if vertex not selected, do not follow
                    if (!isSelected(cache, neighborVertex))
                        continue;
                     
					VertexView neighborVertexView =
						(VertexView) jgraph.getGraphLayoutCache().getMapping(
							neighborVertex,
							false);

					if (neighborVertexView == null
						|| neighborVertexView == vertexView)
						continue;

					CellWrapper neighborWrapper =
						(CellWrapper) neighborVertexView.getAttributes().get(
							SUGIYAMA_CELL_WRAPPER);

					if (currentwrapper == null
						|| neighborWrapper == null
						|| currentwrapper.level == neighborWrapper.level)
						continue;

					currentwrapper.priority++;

				}
			}
		}

		//================================================================
		for (Iterator levelsIter = levels.iterator(); levelsIter.hasNext(); ) {
			List level = (List) levelsIter.next();
            int i = 0;
			for (Iterator levelIter = level.iterator(); levelIter.hasNext(); i++) {
				// calculate the initial Grid Positions 1, 2, 3, .... per Level
				CellWrapper wrapper = (CellWrapper) levelIter.next();
				wrapper.setGridPosition(i);
			}
		}

		movements.clear();
		movementsCurrentLoop = -1;
		movementsMax = Integer.MIN_VALUE;
		iteration = 0;

		//int movements = 1;

		while (movementsCurrentLoop != 0) {

			// reset movements
			movementsCurrentLoop = 0;

			// top down
			for (int i = 1; i < levels.size(); i++) {
				movementsCurrentLoop += moveToBarycenter(jgraph, levels, i);
			}

			// bottom up
			for (int i = levels.size() - 1; i >= 0; i--) {
				movementsCurrentLoop += moveToBarycenter(jgraph, levels, i);
			}

			this.updateProgress4Movements();
		}

	}

	protected int moveToBarycenter(
		JGraph jgraph,
		List levels,
		int levelIndex) {

		// Counter for the movements
		int movements = 0;

		// Get the current level
		List currentLevel = (List) levels.get(levelIndex);
		GraphModel model = jgraph.getModel();
		GraphLayoutCache cache = jgraph.getGraphLayoutCache();

		for (int currentIndexInTheLevel = 0;
			currentIndexInTheLevel < currentLevel.size();
			currentIndexInTheLevel++) {

			CellWrapper sourceWrapper =
				(CellWrapper) currentLevel.get(currentIndexInTheLevel);

			float gridPositionsSum = 0;
			float countNodes = 0;

			VertexView vertexView = sourceWrapper.getVertexView();
			Object vertex = vertexView.getCell();
			int portCount = model.getChildCount(vertex);

			for (int i = 0; i < portCount; i++) {
				Object port = model.getChild(vertex, i);

				Iterator edges = model.edges(port);
				while (edges.hasNext()) {
					Object edge = edges.next();

                    // if edge not selected, do not follow
                    if (!isSelected(cache, edge))
                        continue;

					// if it is a forward edge follow it
					Object neighborPort = null;
					if (port == model.getSource(edge)) {
						neighborPort = model.getTarget(edge);
					} else {
						if (port == model.getTarget(edge)) {
							neighborPort = model.getSource(edge);
						} else {
							continue;
						}
					}

					Object neighborVertex = model.getParent(neighborPort);

                    // if vertex not selected, do not follow
                    if (!isSelected(cache, neighborVertex))
                        continue;

					VertexView neighborVertexView =
						(VertexView) jgraph.getGraphLayoutCache().getMapping(
							neighborVertex,
							false);
					
					if (neighborVertexView == null)
						continue;
							
					CellWrapper targetWrapper =
						(CellWrapper) neighborVertexView.getAttributes().get(
							SUGIYAMA_CELL_WRAPPER);

					if (targetWrapper == sourceWrapper)
						continue;
					if (targetWrapper == null
						|| targetWrapper.getLevel() == levelIndex)
						continue;

					gridPositionsSum += targetWrapper.getGridPosition();
					countNodes++;
				}
			}

			//----------------------------------------------------------
			// move node to new x coord
			//----------------------------------------------------------

			if (countNodes > 0) {
				float tmp = (gridPositionsSum / countNodes);
				int newGridPosition = Math.round(tmp);
				boolean toRight =
					(newGridPosition > sourceWrapper.getGridPosition());

				boolean moved = true;

				while (newGridPosition != sourceWrapper.getGridPosition()
					&& moved) {
					int tmpGridPos = sourceWrapper.getGridPosition();

					moved =
						move(
							toRight,
							currentLevel,
							currentIndexInTheLevel,
							sourceWrapper.getPriority());

					if (moved)
						movements++;

				}
			}
		}
		return movements;
	}

	/**@param  toRight <tt>true</tt> = try to move the currentWrapper to right; <tt>false</tt> = try to move the currentWrapper to left;
	 * @param  currentLevel List which contains the CellWrappers for the current level
	 * @param  currentIndexInTheLevel
	 * @param  currentPriority
	 * @param  currentWrapper The Wrapper
	 *
	 * @return The free GridPosition or -1 is position is not free.
	 */
	protected boolean move(
		boolean toRight,
		List currentLevel,
		int currentIndexInTheLevel,
		int currentPriority) {

		CellWrapper currentWrapper =
			(CellWrapper) currentLevel.get(currentIndexInTheLevel);

		boolean moved = false;
		int neighborIndexInTheLevel =
			currentIndexInTheLevel + (toRight ? 1 : -1);
		int newGridPosition =
			currentWrapper.getGridPosition() + (toRight ? 1 : -1);

		// is the grid position possible?

		if (0 > newGridPosition || newGridPosition >= gridAreaSize) {
			return false;
		}

		// if the node is the first or the last we can move
		if (toRight
			&& currentIndexInTheLevel == currentLevel.size() - 1
			|| !toRight
			&& currentIndexInTheLevel == 0) {

			moved = true;

		} else {
			// else get the neighbor and ask his gridposition
			// if he has the requested new grid position
			// check the priority

			CellWrapper neighborWrapper =
				(CellWrapper) currentLevel.get(neighborIndexInTheLevel);

			int neighborPriority = neighborWrapper.getPriority();

			if (neighborWrapper.getGridPosition() == newGridPosition) {
				if (neighborPriority >= currentPriority) {
					return false;
				} else {
					moved =
						move(
							toRight,
							currentLevel,
							neighborIndexInTheLevel,
							currentPriority);
				}
			} else {
				moved = true;
			}
		}

		if (moved) {
			currentWrapper.setGridPosition(newGridPosition);
		}
		return moved;
	}

	/** This Method draws the graph. For the horizontal position
	 *  we are using the grid position from each graphcell.
	 *  For the vertical position we are using the level position.
	 *
	 */
	protected void drawGraph(
		JGraph jgraph,
		List levels,
		Point min,
		Point spacing) {
		// paint the graph

		Map viewMap = new Hashtable();

		for (int rowCellCount = 0;
			rowCellCount < levels.size();
			rowCellCount++) {
			List level = (List) levels.get(rowCellCount);

			for (int colCellCount = 0;
				colCellCount < level.size();
				colCellCount++) {
				CellWrapper wrapper = (CellWrapper) level.get(colCellCount);
				VertexView view = wrapper.vertexView;

				// remove the temp objects
				/* While the Algorithm is running we are putting some
				 *  attributeNames to the MyGraphCells. This method
				 *  cleans this objects from the MyGraphCells.
				 *
				 */
				view.getAttributes().remove(SUGIYAMA_CELL_WRAPPER);
				view.getAttributes().remove(SUGIYAMA_VISITED);
				wrapper.vertexView = null;

				// get the bounds from the cellView
				if (view == null)
					continue;
				Rectangle2D rect = (Rectangle2D) view.getBounds().clone();
				Rectangle bounds =  new Rectangle((int) rect.getX(), 
						(int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
				//(Rectangle) view.getBounds().clone();

				// adjust
				bounds.x = min.x + spacing.x * ((vertical) ? wrapper.getGridPosition() : rowCellCount);
				bounds.y = min.y + spacing.y * ((vertical) ? rowCellCount :  wrapper.getGridPosition());

				Object cell = view.getCell();
				Map map = new Hashtable();
				GraphConstants.setBounds(map, (Rectangle2D) bounds.clone());

				viewMap.put(cell, map);

			}

		}
		jgraph.getGraphLayoutCache().edit(viewMap, null, null, null);
	}

	/*
	 * The Sugiyama algorithm can be applied to Directed Acyclic Graphs (DAG's) ONLY.
	 * This function checks whether the given graph is acyclic or not
	 */
	//public boolean isDAG(JGraph graph, CellView[] cell){
	//}
	/** cell wrapper contains all values
	 *  for one node
	 */
	class CellWrapper implements Comparable {

		/** sum value for edge Crosses
		 */
		private double edgeCrossesIndicator = 0;
		/** counter for additions to the edgeCrossesIndicator
		 */
		private int additions = 0;
		/** the vertical level where the cell wrapper is inserted
		 */
		int level = 0;
		/** current position in the grid
		 */
		int gridPosition = 0;
		/** priority for movements to the barycenter
		 */
		int priority = 0;
		/** reference to the wrapped cell
		 */
		VertexView vertexView = null;

		/** creates an instance and memorizes the parameters
		 *
		 */
		CellWrapper(
			int level,
			double edgeCrossesIndicator,
			VertexView vertexView) {
			this.level = level;
			this.edgeCrossesIndicator = edgeCrossesIndicator;
			this.vertexView = vertexView;
			additions++;
		}

		/** returns the wrapped cell
		 */
		VertexView getVertexView() {
			return vertexView;
		}

		/** resets the indicator for edge crosses to 0
		 */
		void resetEdgeCrossesIndicator() {
			edgeCrossesIndicator = 0;
			additions = 0;
		}

		/** retruns the average value for the edge crosses indicator
		 *
		 *  for the wrapped cell
		 *
		 */

		double getEdgeCrossesIndicator() {
			if (additions == 0)
				return 0;
			return edgeCrossesIndicator / additions;
		}

		/** Addes a value to the edge crosses indicator
		 *  for the wrapped cell
		 *
		 */
		void addToEdgeCrossesIndicator(double addValue) {
			edgeCrossesIndicator += addValue;
			additions++;
		}
		/** gets the level of the wrapped cell
		 */
		int getLevel() {
			return level;
		}

		/** gets the grid position for the wrapped cell
		 */
		int getGridPosition() {
			return gridPosition;
		}

		/** Sets the grid position for the wrapped cell
		 */
		void setGridPosition(int pos) {
			this.gridPosition = pos;
		}

		/** increments the the priority of this cell wrapper.
		 *
		 *  The priority was used by moving the cell to its
		 *  barycenter.
		 *
		 */

		void incrementPriority() {
			priority++;
		}

		/** returns the priority of this cell wrapper.
		 *
		 *  The priority was used by moving the cell to its
		 *  barycenter.
		 */
		int getPriority() {
			return priority;
		}

		/**
		 * @see java.lang.Comparable#compareTo(Object)
		 */
		public int compareTo(Object compare) {
			if (((CellWrapper) compare).getEdgeCrossesIndicator()
				== this.getEdgeCrossesIndicator())
				return 0;

			double compareValue =
				(((CellWrapper) compare).getEdgeCrossesIndicator()
					- this.getEdgeCrossesIndicator());

			return (int) (compareValue * 1000);

		}
	}
	/**
	 * Returns the spacing.
	 * 
	 * @see #setSpacing
	 */
	public Point getSpacing() {
		return spacing;
	}
	
	/**
     * Sets grid spacing.
     *
     * The Algorithm distributes the nodes on a grid.  For this
     * grid you can configure the vertical and horizontal spacing.
     *
	 */
	public void setSpacing(Point spacing) {
		this.spacing = spacing;
	}
	
	/**
	 * Returns the current layout direction
	 * @return boolean whether or not direction is vertical
     * @see #setVertical(boolean)
	 */
	public boolean isVertical() {
		return vertical;
	}
    /**
     * Sets the layout direction.
     *
     * @param vertical true for vertical and false for horizontal direction
     */
	public void setVertical(boolean vertical) {
		this.vertical = vertical;
	}

    /**
     * Get the <code>FlushToOrigin</code> value.
     *
     * @see #setFlushToOrigin(boolean)
     *
     * @return a <code>boolean</code> value
     */
    public final boolean getFlushToOrigin()
    {
        return flushToOrigin;
    }

    /**
     * After layout, moves the graph as close to origin as possible.
     *
     * <p>After the layout has complete, this algorithm calculates the
     * minimum X and Y coordinates over all selected cells. If
     * flushToOrigin parameter is set to false, the algorithm will place
     * cells starting at coordinates corresponding to those minimum
     * values.
     *
     * <p>If set to true, the layout will place cells starting at the
     * origin, possibly shrinking the overall graph canvas size
     *
     * @param newFlushToOrigin The new FlushToOrigin value.
     */
    public final void setFlushToOrigin(final boolean newFlushToOrigin)
    {
        this.flushToOrigin = newFlushToOrigin;
    }

}

