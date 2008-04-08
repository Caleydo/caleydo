package org.caleydo.core.view.opengl.util.infoarea;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.manager.IGeneralManager;

import com.sun.opengl.util.j2d.Overlay;

/**
 * 
 * Class implements the overlay info area.
 * 
 * @author Marc Streit
 *
 */
public class GLInfoOverlayRenderer 
{
	private final static int MAX_OVERLAY_HEIGHT = 300;
	private final static int OVERLAY_WIDTH = 800;
	private final static int LINE_HEIGHT = 20;
	
	private Overlay glOverlay;
	
	private Font font;
		
	private ArrayList<String> sAlContent;
	
	/**
	 * Constructor.
	 */
	public GLInfoOverlayRenderer(final IGeneralManager generalManager) 
	{
		
		font = new Font("Courier", Font.BOLD, 16);
		sAlContent = new ArrayList<String>();
	}
	
	public void init(final GLAutoDrawable drawable) 
	{
		glOverlay = new Overlay(drawable);
	}
	
	/**
	 * Set the data to be rendered.
	 * 
	 * @param iUniqueID
	 * @param eInputDataTypes
	 * @param pickedPoint
	 */
	public void setData(final ArrayList<String> sAlContent) 
	{
		this.sAlContent = sAlContent;
	}
	
	public void render(final GLAutoDrawable drawable) 
	{
		GL gl = drawable.getGL();

		int iLineCount = 0;
		
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		
		int panelHeight = 0;
		if (sAlContent.isEmpty())
			panelHeight = 0;
		else
			panelHeight = (sAlContent.size() + 1) * LINE_HEIGHT;
		
		if (panelHeight > MAX_OVERLAY_HEIGHT)
			panelHeight = MAX_OVERLAY_HEIGHT;

		
		if (drawable.getWidth() == 0 || drawable.getHeight() == 0)
			return;
		
		Graphics2D g2d = glOverlay.createGraphics();
		g2d.setComposite(AlphaComposite.Src);

		// Flush info area
		g2d.setColor(new Color(1,1,1,0));
		g2d.fillRect((viewport[2] - OVERLAY_WIDTH) / 2, 0, OVERLAY_WIDTH, MAX_OVERLAY_HEIGHT);
				
		g2d.setColor(new Color(0, 0, 0, 0.3f));
		g2d.fillRect((viewport[2] - OVERLAY_WIDTH) / 2, 0, OVERLAY_WIDTH, panelHeight);

		g2d.setColor(Color.DARK_GRAY);
		g2d.drawRect((viewport[2] - OVERLAY_WIDTH) / 2, 0, OVERLAY_WIDTH, panelHeight);

		g2d.setColor(Color.WHITE);
		g2d.setFont(font);

		Iterator<String> iterContentCreator = sAlContent.iterator();
		
		while(iterContentCreator.hasNext())
		{
			iLineCount++;
			g2d.drawString(iterContentCreator.next(), 
					(viewport[2] - OVERLAY_WIDTH) / 2 + 10, iLineCount * LINE_HEIGHT);
		}
		
		//render all the overlay to the screen
		glOverlay.markDirty((viewport[2] - OVERLAY_WIDTH) / 2, 0, OVERLAY_WIDTH, MAX_OVERLAY_HEIGHT);
		glOverlay.drawAll();
		g2d.dispose();
	}
}
