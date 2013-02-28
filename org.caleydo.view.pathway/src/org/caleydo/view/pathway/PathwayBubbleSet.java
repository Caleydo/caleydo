/**
 * 
 */
package org.caleydo.view.pathway;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;

import setvis.BubbleSetGLRenderer;



public class PathwayBubbleSet 
{
	////////////////////////////////////////////////
	// private section
	////////////////////////////////////////////////	
	private BubbleSetGLRenderer renderer= new BubbleSetGLRenderer();
	private PathwayGraph pathway=null;
	////////////////////////////////////////////////
	// public section 
	////////////////////////////////////////////////
	public PathwayBubbleSet(){

	}
	public BubbleSetGLRenderer getBubbleSetGLRenderer(){
		return renderer;
	}
	public void setBubbleSetGLRenderer(BubbleSetGLRenderer newRenderer){
		renderer=newRenderer;
	}
	
	public void setPathwayGraph(PathwayGraph aPathway){
		pathway=aPathway;
	}
	
	public void clear(){
		renderer.clearBubbleSet();
	}
	
	public void addAllPaths(List<GraphPath<PathwayVertexRep, DefaultEdge>> allPaths){			
		for (GraphPath<PathwayVertexRep, DefaultEdge> path : allPaths) {
			if (path == null)
				break;
			addPath(path);
		}
	}

	public void addPath(GraphPath<PathwayVertexRep, DefaultEdge> path)
	{
		// single node path = no edge exist 
		if (path.getEndVertex() == path.getStartVertex()) { 
			PathwayVertexRep sourceVertexRep = path.getEndVertex();
			double bbItemW = sourceVertexRep.getWidth();
			double bbItemH = sourceVertexRep.getHeight();
			double posX = sourceVertexRep.getLowerLeftCornerX();//getLowerLeftCornerX();
			double posY = sourceVertexRep.getLowerLeftCornerY();//.getLowerLeftCornerY();
			ArrayList<Rectangle2D> items= new ArrayList<>();
			items.add(new Rectangle2D.Double(posX, posY, bbItemW, bbItemH));
			renderer.addGroup(items, null , null);
		}
		// add path by adding each of its nodes 
		else {
			if(pathway==null)return;
			ArrayList<Rectangle2D> items= new ArrayList<>();
			ArrayList<Line2D> edges= new ArrayList<>();
			for (DefaultEdge edge : path.getEdgeList()) {
				PathwayVertexRep sourceVertexRep = pathway.getEdgeSource(edge);
				PathwayVertexRep targetVertexRep = pathway.getEdgeTarget(edge);

//				double bbItemW = sourceVertexRep.getWidth();
//				double bbItemH = sourceVertexRep.getHeight();
//				double posX = sourceVertexRep.getCenterX();
//				double posY = sourceVertexRep.getCenterY();
//				double tX = targetVertexRep.getCenterX();
//				double tY = targetVertexRep.getCenterY();				
				double bbItemW = sourceVertexRep.getWidth();
				double bbItemH = sourceVertexRep.getHeight();
				double posX = sourceVertexRep.getLowerLeftCornerX();
				double posY = sourceVertexRep.getLowerLeftCornerY();
				double tX = targetVertexRep.getLowerLeftCornerX();
				double tY = targetVertexRep.getLowerLeftCornerY();

				items.add(new Rectangle2D.Double(posX, posY, bbItemW, bbItemH));
				edges.add(new Line2D.Double(posX, posY, tX, tY));
			}
			DefaultEdge lastEdge = path.getEdgeList().get(path.getEdgeList().size() - 1);
			if (lastEdge != null) {
				PathwayVertexRep targetVertexRep = pathway.getEdgeTarget(lastEdge);
				items.add(new Rectangle2D.Double(
						targetVertexRep.getLowerLeftCornerX(), targetVertexRep.getLowerLeftCornerY(),
						targetVertexRep.getWidth(), targetVertexRep.getHeight()));
			}			
			renderer.addGroup(items, edges , null);
		}
	}
	
	public void addPathSegements(List<PathwayPath> pathSegments){
		if (pathSegments.size() <= 0) return; 	
		float[] selColor=SelectionType.SELECTION.getColor();
		Color pathSegColor=new Color(selColor[0],selColor[1],selColor[2]); 
		int outlineThickness = 3;
		for (PathwayPath pathSegment : pathSegments) 
		{
			if (pathSegment.getPathway() == pathway) 
			{
				ArrayList<Rectangle2D> items= new ArrayList<>();
				ArrayList<Line2D> edges= new ArrayList<>();
				for (DefaultEdge edge : pathSegment.getPath().getEdgeList()) {
					PathwayVertexRep sourceVertexRep = pathway.getEdgeSource(edge);
					PathwayVertexRep targetVertexRep = pathway.getEdgeTarget(edge);
					double bbItemW = sourceVertexRep.getWidth();
					double bbItemH = sourceVertexRep.getHeight();
					double posX = sourceVertexRep.getLowerLeftCornerX();
					double posY = sourceVertexRep.getLowerLeftCornerY();
					double tX = targetVertexRep.getLowerLeftCornerX();
					double tY = targetVertexRep.getLowerLeftCornerY();

					items.add(new Rectangle2D.Double(posX, posY, bbItemW, bbItemH));
					edges.add(new Line2D.Double(posX, posY, tX, tY));
				}					
				// add last item
				if (pathSegment.getPath().getEdgeList().size() > 0) {
					DefaultEdge lastEdge = pathSegment.getPath().getEdgeList()
							.get(pathSegment.getPath().getEdgeList().size() - 1);
					if (lastEdge != null) {
						PathwayVertexRep targetVertexRep = pathway.getEdgeTarget(lastEdge);
						items.add(new Rectangle2D.Double(
								targetVertexRep.getLowerLeftCornerX(), targetVertexRep.getLowerLeftCornerY(),
								targetVertexRep.getWidth(), targetVertexRep.getHeight()));
					}
				}
				renderer.addGroup(items, edges, pathSegColor);
			}//if (pathSegment.getPathway() == pathway) 		
		}//for (PathwayPath pathSegment : pathSegments) 
	}
	
	
	public void addPortals(Set<PathwayVertexRep> portalVertexReps){
		if(portalVertexReps==null)return;

		Color portalColor=new Color(1f,0f,0f); 

		for (PathwayVertexRep portal : portalVertexReps) {
			double posX = portal.getLowerLeftCornerX();
			double posY = portal.getLowerLeftCornerY();
			double bbItemW = portal.getWidth();
			double bbItemH = portal.getHeight();		
			ArrayList<Rectangle2D> items= new ArrayList<>();
			items.add(new Rectangle2D.Double(posX, posY, bbItemW, bbItemH));
			renderer.addGroup(items, null , portalColor);
		}
	}
	
}

