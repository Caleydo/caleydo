package org.caleydo.core.view.opengl.miniview.slider;

import gleem.linalg.Vec4f;
import java.awt.Font;
import java.util.ArrayList;
import javax.media.opengl.GL;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.opengl.miniview.AGLMiniView;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import com.sun.opengl.util.j2d.TextRenderer;

/**
 * OpenGL Slider Distribution Mini View
 * 
 * @author Stefan Sauer
 */
public class GLDistributionMiniView
	extends AGLMiniView
{
	public enum BORDER
	{
		FULL,
		LEFT,
		RIGHT,
		TOP,
		BOTTOM;
	}

	public enum ALIGN
	{
		LEFT,
		RIGHT,
		CENTER;
	}

	private boolean bBorderLeft = true;
	private boolean bBorderTop = true;
	private boolean bBorderRight = true;
	private boolean bBorderBottom = true;
	private ALIGN alignment = ALIGN.LEFT;

	private int iBorderWidth = 1;
	private Vec4f vBorderColor = new Vec4f(0.0f, 0.0f, 0.0f, 1.0f);
	private Vec4f vDistributionColor = new Vec4f(0.0f, 0.75f, 0.0f, 1.0f);
	private Vec4f vDistributionSelectedColor = new Vec4f(0.0f, 0.5f, 0.0f, 1.0f);

	private ArrayList<Float> alNormalicedDistribution = null;
	private ArrayList<Float> alNormalicedSelectedDistribution = null;

	TextRenderer textRenderer = null;

	public GLDistributionMiniView(PickingJoglMouseListener pickingTriggerMouseAdapter, final int iViewID,
			final int iDistributionID)
	{
		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 16), false);
		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.setSmoothing(false);

		fWidth = 1.0f;
		fHeight = 1.0f;
	}

	public void setHeight(final float fHeight)
	{
		this.fHeight = fHeight;
	}

	public void setNormalicedDistribution(final ArrayList<Float> values)
	{
		alNormalicedDistribution = values;
	}

	public void setDistribution(final ArrayList<Float> values)
	{

		// alAxisScaleNominal = new ArrayList<String>();
		// float realinc = 1000 / (values.size());
		// float inccounter = 0;
		// int counter = 0;
		// for (int i = 0; i < 1000; ++i)
		// {
		// alAxisScaleNominal.add(values.get(counter));
		// if (inccounter >= realinc)
		// {
		// inccounter = 0;
		// counter++;
		// }
		// ++inccounter;
		// }

		throw (new CaleydoRuntimeException("not implemented yet"));
	}

	public void setNormalicedSelectedDistribution(final ArrayList<Float> values)
	{
		alNormalicedSelectedDistribution = values;
	}

	public void setSelectedDistribution(final ArrayList<Float> values)
	{
		throw (new CaleydoRuntimeException("not implemented yet"));
	}

	public void setBorderWidth(final int width)
	{
		iBorderWidth = width;
	}

	public void setBorder(BORDER borderpart, boolean onoff)
	{
		switch (borderpart)
		{
			case LEFT:
				bBorderLeft = onoff;
				break;
			case TOP:
				bBorderTop = onoff;
				break;
			case RIGHT:
				bBorderRight = onoff;
				break;
			case BOTTOM:
				bBorderBottom = onoff;
				break;
			case FULL:
			default:
				bBorderLeft = onoff;
				bBorderTop = onoff;
				bBorderRight = onoff;
				bBorderBottom = onoff;
				break;
		}
	}

	public void setDistributionAlign(ALIGN align)
	{
		alignment = align;
	}

	@Override
	public void render(GL gl, float fXOrigin, float fYOrigin, float fZOrigin)
	{
		gl.glPushMatrix();
		// draw helplines
		// GLHelperFunctions.drawAxis(gl);

		drawBorder(gl);
		drawDistribution(gl);

		gl.glPopMatrix();
	}

	private void drawBorder(GL gl)
	{
		gl.glPushMatrix();
		gl.glLineWidth(iBorderWidth);

		if (bBorderLeft)
		{
			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2),
					vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, fHeight, 0);
			gl.glEnd();
		}

		gl.glTranslatef(0f, fHeight, 0f);

		if (bBorderTop)
		{
			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2),
					vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(fWidth, 0, 0);
			gl.glEnd();
		}

		gl.glTranslatef(fWidth, 0f, 0f);

		if (bBorderRight)
		{
			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2),
					vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, -fHeight, 0);
			gl.glEnd();
		}

		gl.glTranslatef(0f, -fHeight, 0f);

		if (bBorderBottom)
		{
			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2),
					vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(-fWidth, 0, 0);
			gl.glEnd();
		}

		gl.glPopMatrix();
	}

	private void drawDistribution(GL gl)
	{
		if (alNormalicedDistribution != null)
		{

			float increment = fHeight / alNormalicedDistribution.size();

			gl.glPushMatrix();
			gl.glTranslatef(0, 0, -0.01f);

			gl.glColor4f(vDistributionColor.get(0), vDistributionColor.get(1),
					vDistributionColor.get(2), vDistributionColor.get(3));

			for (float value : alNormalicedDistribution)
			{
				float posx = 0;
				float width = fWidth * value;
				if (alignment == ALIGN.RIGHT)
					posx = fWidth - width;
				if (alignment == ALIGN.CENTER)
					posx = (fWidth / 2.0f) - (width / 2.0f);

				gl.glBegin(GL.GL_QUADS);
				gl.glNormal3i(0, 1, 0);
				gl.glVertex3f(posx, 0, 0);
				gl.glVertex3f(posx, increment, 0);
				gl.glVertex3f(posx + width, increment, 0);
				gl.glVertex3f(posx + width, 0, 0);
				gl.glEnd();

				gl.glTranslatef(0, increment, 0);
			}
			gl.glTranslatef(0, 0, 0.01f);
			gl.glPopMatrix();
		}

		if (alNormalicedSelectedDistribution != null)
		{
			float increment = fHeight / alNormalicedSelectedDistribution.size();

			gl.glPushMatrix();
			// gl.glTranslatef(0, 0, -0.75f);

			gl.glColor4f(vDistributionSelectedColor.get(0), vDistributionSelectedColor.get(1),
					vDistributionSelectedColor.get(2), vDistributionSelectedColor.get(3));

			for (float value : alNormalicedSelectedDistribution)
			{
				float posx = 0;
				float width = fWidth * value;
				if (alignment == ALIGN.RIGHT)
					posx = fWidth - width;
				if (alignment == ALIGN.CENTER)
					posx = (fWidth / 2.0f) - (width / 2.0f);

				gl.glBegin(GL.GL_QUADS);
				gl.glNormal3i(0, 1, 0);
				gl.glVertex3f(posx, 0, 0);
				gl.glVertex3f(posx, increment, 0);
				gl.glVertex3f(posx + width, increment, 0);
				gl.glVertex3f(posx + width, 0, 0);
				gl.glEnd();

				gl.glTranslatef(0, increment, 0);
			}

			gl.glPopMatrix();
		}
	}

}
