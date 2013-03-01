/**
 * 
 */
package setvis;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;
import java.util.List;

import setvis.gui.CanvasComponent;
import setvis.shape.AbstractShapeGenerator;
import setvis.SetOutline;
import setvis.bubbleset.BubbleSet;
import setvis.shape.BSplineShapeGenerator;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.texture.Texture;

import org.caleydo.core.util.color.ColorManager;

public class BubbleSetGLRenderer {

	private Texture bubbleSetsTexture;
	private TextureRenderer texRenderer=null;
	
	private SetOutline setOutline;
	private AbstractShapeGenerator shaper;
	private CanvasComponent bubblesetCanvas;
	private int numberOfGroups=-1;
	private int outlineThickness=1;
	private int outlineThicknessSelection=3;

	public BubbleSetGLRenderer(){
		setOutline = new BubbleSet(100, 20, 3, 10.0, 7.0, 0.5, 2.5, 15.0, 8);
		((BubbleSet) setOutline).useVirtualEdges(false);
		shaper = new BSplineShapeGenerator(setOutline);
		bubblesetCanvas = new CanvasComponent(shaper);
		bubblesetCanvas.setDefaultView();
		// we will adapt the dimensions in each frame
	}
	
	public void init(final GL2 gl){
		texRenderer = new TextureRenderer(1280, 768, true);
	}
	
	public int getNumberOfGroups(){return numberOfGroups;}
	
	public void clearBubbleSet(){
		numberOfGroups = bubblesetCanvas.getGroupCount() - 1;
		while (bubblesetCanvas.getGroupCount() > 0) {
			bubblesetCanvas.setCurrentGroup(numberOfGroups);
			bubblesetCanvas.removeCurrentGroup();
			numberOfGroups--;
		}
	}
	
	void setOutlineThickness(int thickness, boolean selection){
		if(selection)
			outlineThickness=thickness;
		else
			outlineThicknessSelection=thickness;
	}

	public void addToLastGroup(ArrayList<Rectangle2D> items, ArrayList<Line2D> edges){
		addToGroup(items, edges, numberOfGroups);
	}
	
	public void addToGroup(ArrayList<Rectangle2D> items, ArrayList<Line2D> edges, int groupID){
		// add items
		for(Rectangle2D node : items){
			bubblesetCanvas.addItem(numberOfGroups, node.getMinX(), node.getMinY(), node.getWidth(), node.getHeight());
		}		
		//add edges
		if(edges!=null){
			for(Line2D edge : edges){
				bubblesetCanvas.addEdge(numberOfGroups, edge.getX1(), edge.getY1(), edge.getX2(), edge.getY2());
			}
		}
	}
	
	public int addGroup(ArrayList<Rectangle2D> items, ArrayList<Line2D> edges, Color colorValue){
		if(items==null)
			return -1;

		// new group
		numberOfGroups++;		
		int groupID=numberOfGroups;
		
		//use user defined color value
		if(colorValue!=null){
			bubblesetCanvas.addGroup(colorValue, outlineThickness, true);
		}
		//use color from look up table
		else{ 
			List<org.caleydo.core.util.color.Color> colorTable = (ColorManager.get()).getColorList("qualitativeColors");
			int colorID;
			// avoid the last two colors because they are close to orange
			// which is the selection color
			if (groupID < colorTable.size() - 2)
				colorID = groupID;
			else
				colorID = colorTable.size() - 1;
			org.caleydo.core.util.color.Color c = colorTable.get(colorID);
			bubblesetCanvas.addGroup(new Color(c.r,c.g,c.b), outlineThickness, true);
		}
					
		addToGroup(items, edges, numberOfGroups);
		//
		
		return numberOfGroups;
	}
	
	public void update(GL2 gl, float[] selectionColor, int selectionID)
	{
		if(selectionColor!=null && selectionID>=0 && selectionID <= numberOfGroups){
			bubblesetCanvas.setSelection(selectionID); // the selected set will
			bubblesetCanvas.setSelectionColor(selectionColor);
		}
 
		Graphics2D g2d = texRenderer.createGraphics();
		if (numberOfGroups >= 0) {
			bubblesetCanvas.paint(g2d);
		}
		g2d.dispose();	
	}
	
	public void setSize(int width, int height){
		texRenderer.setSize(width,height);
	}
	
	public void renderPxl(GL2 gl, float pxlWidth, float pxlHeight)
	{		
		texRenderer.setColor(1.0f, 1.0f, 1.0f, 0.75f);
		bubbleSetsTexture = texRenderer.getTexture();
		
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		bubbleSetsTexture.enable(gl);
		bubbleSetsTexture.bind(gl);
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(0.0f, 0.0f, 0.0f);
			
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(pxlWidth, 0.0f, 0.0f);
			
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(pxlWidth, pxlHeight, 0.0f);
			
			gl.glTexCoord2f(0, 1);
			gl.glVertex3f(0.0f, pxlHeight, 0.0f);
		gl.glEnd();
		bubbleSetsTexture.disable(gl);		
	}
	
	public void render(GL2 gl, float glWidth, float glHeight)
	{		
		texRenderer.setColor(1.0f, 1.0f, 1.0f, 0.75f);
		bubbleSetsTexture = texRenderer.getTexture();
		
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		bubbleSetsTexture.enable(gl);
		bubbleSetsTexture.bind(gl);
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0, 1);
			gl.glVertex3f(0.0f, 0.0f, 0.0f);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(glWidth, 0.0f, 0.0f);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(glWidth, glHeight, 0.0f);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(0.0f, glHeight, 0.0f);
		gl.glEnd();
		bubbleSetsTexture.disable(gl);		
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
	

}
