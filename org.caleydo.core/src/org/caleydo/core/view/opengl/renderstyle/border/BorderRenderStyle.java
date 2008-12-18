package org.caleydo.core.view.opengl.renderstyle.border;

import gleem.linalg.Vec4f;
import javax.media.opengl.GL;

public class BorderRenderStyle
	implements IBorderRenderStyle
{

	public static final class BORDER
	{
		public static final int FULL = 0xf;
		public static final int LEFT = 0x1;
		public static final int TOP = 0x2;
		public static final int RIGHT = 0x4;
		public static final int BOTTOM = 0x8;
	}

	protected boolean bBorderLeft = true;
	protected boolean bBorderTop = true;
	protected boolean bBorderRight = true;
	protected boolean bBorderBottom = true;
	protected int iBorderWidth = 1;

	protected Vec4f vBorderColor = new Vec4f(0.0f, 0.0f, 0.0f, 1.0f);

	protected int glList = -1;

	public void setBorderWidth(final int width)
	{
		iBorderWidth = width;
	}

	public void setBorder(int borderpart, boolean onoff)
	{
		if ((borderpart & BORDER.LEFT) == BORDER.LEFT)
			bBorderLeft = onoff;

		if ((borderpart & BORDER.TOP) == BORDER.TOP)
			bBorderTop = onoff;

		if ((borderpart & BORDER.RIGHT) == BORDER.RIGHT)
			bBorderRight = onoff;

		if ((borderpart & BORDER.BOTTOM) == BORDER.BOTTOM)
			bBorderBottom = onoff;

	}

	public void setBorderColor(Vec4f color)
	{
		vBorderColor = color;
	}

	public void init(GL gl)
	{

	}

	public void display(GL gl)
	{
		if (glList < 0)
			return;

		gl.glCallList(glList);
	}

}
