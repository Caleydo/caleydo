/**
 * 
 */
package setvis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import setvis.gui.CanvasComponent;
import setvis.shape.AbstractShapeGenerator;
import setvis.SetOutline;
import setvis.bubbleset.BubbleSet;
import setvis.shape.BSplineShapeGenerator;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.ColorManager;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.pathway.EPickingType;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;

import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.texture.Texture;


public class GLBubbleSetTexture {

	private Texture bubbleSetsTexture;
	private TextureRenderer texRenderer;
	private SetOutline setOutline;
	private AbstractShapeGenerator shaper;
	private CanvasComponent bubblesetCanvas;
	
	public GLBubbleSetTexture(){
		setOutline = new BubbleSet(100, 20, 3, 10.0, 7.0, 0.5, 2.5, 15.0, 8);
		((BubbleSet) setOutline).useVirtualEdges(false);
		shaper = new BSplineShapeGenerator(setOutline);
		bubblesetCanvas = new CanvasComponent(shaper);
		bubblesetCanvas.setDefaultView();
		// we will adapt the dimensions in each frame
		texRenderer = new TextureRenderer(1280, 768, true);
	}
	
	public void clearBubbleSet(){
		int groupID = bubblesetCanvas.getGroupCount() - 1;
		while (bubblesetCanvas.getGroupCount() > 0) {
			bubblesetCanvas.setCurrentGroup(groupID);
			bubblesetCanvas.removeCurrentGroup();
			groupID--;
		}
	}
	
	//List<GraphPath<PathwayVertexRep, DefaultEdge>> allPaths, 
	
	public void update(GL2 gl, int selectedPathID)
	{
		int bbGroupID = -1;
//		HashSet<PathwayVertexRep> visitedNodes = new HashSet<PathwayVertexRep>();
//		int numNodes = 0;
//		for (GraphPath<PathwayVertexRep, DefaultEdge> path : allPaths) {
//			numNodes += path.getEdgeList().size();
//			numNodes++;
//		}
//		int pos = 1;
//		double nodeOffsetScale = 2.0;
//		float[] colorValues = new float[3];
//		Integer outlineThickness;
//		// pathSegmentList
//		if (pathSegments.size() > 0) {
//			colorValues = SelectionType.SELECTION.getColor();
//			outlineThickness = 3;
//
//			for (PathwayPath pathSegment : pathSegments) {
//				if (pathSegment.getPathway() == pathway) {
//					bbGroupID++;
//					bubblesetCanvas.addGroup(new Color(colorValues[0], colorValues[1], colorValues[2]),
//							outlineThickness, true);
//					for (DefaultEdge edge : pathSegment.getPath().getEdgeList()) {
//						PathwayVertexRep sourceVertexRep = pathway.getEdgeSource(edge);
//						PathwayVertexRep targetVertexRep = pathway.getEdgeTarget(edge);
//						// src
//						double bbItemW = sourceVertexRep.getWidth();
//						double bbItemH = sourceVertexRep.getHeight();
//						double posX = sourceVertexRep.getLowerLeftCornerX();
//						double posY = sourceVertexRep.getLowerLeftCornerY();
//						bubblesetCanvas.addItem(bbGroupID, posX, posY, bbItemW, bbItemH);
//						//
//						double tX = targetVertexRep.getLowerLeftCornerX();
//						double tY = targetVertexRep.getLowerLeftCornerY();
//						bubblesetCanvas.addEdge(bbGroupID, posX, posY, tX, tY);
//					}
//					// add last item
//					if (pathSegment.getPath().getEdgeList().size() > 0) {
//						DefaultEdge lastEdge = pathSegment.getPath().getEdgeList()
//								.get(pathSegment.getPath().getEdgeList().size() - 1);
//						if (lastEdge != null) {
//							PathwayVertexRep targetVertexRep = pathway.getEdgeTarget(lastEdge);
//							double posX = targetVertexRep.getLowerLeftCornerX();
//							double posY = targetVertexRep.getLowerLeftCornerY();
//							double bbItemW = targetVertexRep.getWidth();
//							double bbItemH = targetVertexRep.getHeight();
//							bubblesetCanvas.addItem(bbGroupID, posX, posY, bbItemW, bbItemH);
//						}
//					}
//				}
//			}
//		}
//		for (GraphPath<PathwayVertexRep, DefaultEdge> path : allPaths) {
//
//			if (path == null)
//				return;
//
//			double bbItemW = 10;
//			double bbItemH = 10;
//
//			// group0
////			gl.glPushName(generalManager.getViewManager().getPickingManager()
////					.getPickingID(uniqueID, EPickingType.PATHWAY_PATH_SELECTION.name(), allPaths.indexOf(path)));
//
//			bbGroupID++;
//			if (path == selectedPath) {
//				colorValues = SelectionType.SELECTION.getColor();
//				outlineThickness = 3;
//				// bubble sets do not allow to delete
//				bubblesetCanvas.addGroup(new Color(colorValues[0], colorValues[1], colorValues[2]), outlineThickness,
//						true);
//			} else {
//				List<org.caleydo.core.util.color.Color> colorTable = (ColorManager.get())
//						.getColorList("qualitativeColors");
//				int colorID;
//				// avoid the last two colors because they are close to orange
//				// (the selection color)
//				if (bbGroupID < colorTable.size() - 2)
//					colorID = bbGroupID;
//				else
//					colorID = colorTable.size() - 1;
//				org.caleydo.core.util.color.Color c = colorTable.get(colorID);
//				outlineThickness = 1;
//				// bubble sets do not allow to delete
//				bubblesetCanvas.addGroup(new Color(c.r, c.g, c.b), outlineThickness, true);
//			}
//
//			if (path.getEndVertex() == path.getStartVertex()) {
//				PathwayVertexRep sourceVertexRep = path.getEndVertex();
//				bbItemW = sourceVertexRep.getWidth();
//				bbItemH = sourceVertexRep.getHeight();
//				double posX = sourceVertexRep.getLowerLeftCornerX();
//				double posY = sourceVertexRep.getLowerLeftCornerY();
//				bubblesetCanvas.addItem(bbGroupID, posX, posY, bbItemW, bbItemH);
//			} else {
//				for (DefaultEdge edge : path.getEdgeList()) {
//					PathwayVertexRep sourceVertexRep = pathway.getEdgeSource(edge);
//					PathwayVertexRep targetVertexRep = pathway.getEdgeTarget(edge);
//
//					bbItemW = sourceVertexRep.getWidth();
//					bbItemH = sourceVertexRep.getHeight();
//					double posX = sourceVertexRep.getLowerLeftCornerX();
//					double posY = sourceVertexRep.getLowerLeftCornerY();
//					if (!visitedNodes.contains(sourceVertexRep)) {
//						visitedNodes.add(sourceVertexRep);
//					} else {
//						double high = 1.0;
//						double low = 0.0;
//						double offX = nodeOffsetScale * (pos / numNodes) * (high - low) + low;
//						double offY = nodeOffsetScale * (pos / numNodes) * (high - low) + low;
//
//						posX += offX;
//						posY += offY;
//					}
//					pos++;
//					double tX = targetVertexRep.getLowerLeftCornerX();
//					double tY = targetVertexRep.getLowerLeftCornerY();
//
//					bubblesetCanvas.addItem(bbGroupID, posX, posY, bbItemW, bbItemH);
//
//					//
//					bubblesetCanvas.addEdge(bbGroupID, posX, posY, tX, tY);
//
//				}
//				DefaultEdge lastEdge = path.getEdgeList().get(path.getEdgeList().size() - 1);
//				if (lastEdge != null) {
//					PathwayVertexRep targetVertexRep = pathway.getEdgeTarget(lastEdge);
//					double posX = targetVertexRep.getLowerLeftCornerX();
//					double posY = targetVertexRep.getLowerLeftCornerY();
//					if (!visitedNodes.contains(targetVertexRep)) {
//						visitedNodes.add(targetVertexRep);
//					} else {
//						double high = 1.0;
//						double low = 0.0;
//						double offX = nodeOffsetScale * (pos / numNodes) * (high - low) + low;
//						double offY = nodeOffsetScale * (pos / numNodes) * (high - low) + low;
//
//						posX += offX;
//						posY += offY;
//					}
//
//					bbItemW = targetVertexRep.getWidth();
//					bbItemH = targetVertexRep.getHeight();
//					bubblesetCanvas.addItem(bbGroupID, posX, posY, bbItemW, bbItemH);
//				}
//			}
//
//			gl.glPopName();
//			//
//		}
//		// /////////////////////
//		// highlight portals
////		for (PathwayVertexRep portal : portalVertexReps) {
////			bbGroupID++;
////			bubblesetCanvas.addGroup(new Color(1.0f, 0.0f, 0.0f), 6, true);
////			double posX = portal.getLowerLeftCornerX();
////			double posY = portal.getLowerLeftCornerY();
////			double bbItemW = portal.getWidth();
////			double bbItemH = portal.getHeight();
////			bubblesetCanvas.addItem(bbGroupID, posX, posY, bbItemW, bbItemH);
////		}

//		if (allPaths.size() <= selectedPathID)
//			selectedPathID = 0;

		bubblesetCanvas.setSelection(selectedPathID); // the selected set will
													  // be rendered on top of
													  // all others
		Graphics2D g2d = texRenderer.createGraphics();
		if (bbGroupID >= 0) {
			bubblesetCanvas.paint(g2d);
		}
		g2d.dispose();	
	}
	
	public void setSize(int width, int height)
	{
		texRenderer.setSize(width,height);
	}
	
	public void render(GL2 gl, float width, float height)
	{		
		texRenderer.setColor(1.0f, 1.0f, 1.0f, 0.75f);
		bubbleSetsTexture = texRenderer.getTexture();

//		gl.glPushName(generalManager.getViewManager().getPickingManager()
//				.getPickingID(uniqueID, EPickingType.PATHWAY_TEXTURE_SELECTION.name(), 0));
//		
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
		bubbleSetsTexture.enable(gl);
		bubbleSetsTexture.bind(gl);
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0, 1);
			gl.glVertex3f(0.0f, 0.0f, 0.0f);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(width, 0.0f, 0.0f);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(width, height, 0.0f);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(0.0f, height, 0.0f);
		gl.glEnd();
		bubbleSetsTexture.disable(gl);
		
//		gl.glPopName();
	}
	
	public int[] getPxl(int posX, int posY)
	{
		int[] pixels = new int[1];
		Image img = texRenderer.getImage();
		PixelGrabber pxlGrabber = new PixelGrabber(img, posX, posY, 1, 1, pixels, 0, 1);
		try {
			pxlGrabber.grabPixels();
		} catch (InterruptedException e) {
			System.err.println("interrupted waiting for pixels!");
			return null;
		}
		return pixels;
	}
	
	//getTexture(){}
}
