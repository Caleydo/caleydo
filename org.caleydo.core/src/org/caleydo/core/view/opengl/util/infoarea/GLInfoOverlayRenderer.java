package org.caleydo.core.view.opengl.util.infoarea;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import org.caleydo.core.data.view.rep.renderstyle.infoarea.AInfoOverlayRenderStyle;
import org.caleydo.core.manager.IGeneralManager;
import com.sun.opengl.util.j2d.Overlay;

/**
 * Class implements the overlay info area.
 * 
 * @author Marc Streit
 */
public class GLInfoOverlayRenderer
{

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
			panelHeight = (sAlContent.size() + 1) * AInfoOverlayRenderStyle.LINE_HEIGHT;

		if (panelHeight > AInfoOverlayRenderStyle.MAX_OVERLAY_HEIGHT)
			panelHeight = AInfoOverlayRenderStyle.MAX_OVERLAY_HEIGHT;

		if (drawable.getWidth() == 0 || drawable.getHeight() == 0)
			return;

		Graphics2D g2d = glOverlay.createGraphics();
		g2d.setComposite(AlphaComposite.Src);

		int iXPos = (viewport[2] - AInfoOverlayRenderStyle.OVERLAY_WIDTH) / 2;
		int iYPos = 0;

		// Flush info area
		g2d.setColor(new Color(1, 1, 1, 0));
		g2d.fillRect(iXPos, iYPos, AInfoOverlayRenderStyle.OVERLAY_WIDTH,
				AInfoOverlayRenderStyle.MAX_OVERLAY_HEIGHT);

		g2d.setColor(AInfoOverlayRenderStyle.backgroundColor);
		g2d.fillRect(iXPos, iYPos, AInfoOverlayRenderStyle.OVERLAY_WIDTH, panelHeight);

		g2d.setColor(AInfoOverlayRenderStyle.borderColor);
		g2d.drawRect(iXPos, iYPos, AInfoOverlayRenderStyle.OVERLAY_WIDTH - 1, panelHeight);

		g2d.setColor(AInfoOverlayRenderStyle.fontColor);
		g2d.setFont(font);

		Iterator<String> iterContentCreator = sAlContent.iterator();

		while (iterContentCreator.hasNext())
		{
			iLineCount++;
			g2d.drawString(iterContentCreator.next(),
					(viewport[2] - AInfoOverlayRenderStyle.OVERLAY_WIDTH) / 2 + 10, iLineCount
							* AInfoOverlayRenderStyle.LINE_HEIGHT);
		}

		// render all the overlay to the screen
		glOverlay.markDirty((viewport[2] - AInfoOverlayRenderStyle.OVERLAY_WIDTH) / 2, 0,
				AInfoOverlayRenderStyle.OVERLAY_WIDTH,
				AInfoOverlayRenderStyle.MAX_OVERLAY_HEIGHT);
		glOverlay.drawAll();
		g2d.dispose();
	}
}
