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
package org.caleydo.testing.applications.gui.jogl.gears;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

// import javax.media.opengl.*;
// import com.sun.opengl.util.*;

/**
 * Gears.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel)
 * <P>
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */

public class GearsMouse
	implements MouseListener, MouseMotionListener
{

	private GearsMain gearsMain;

	private float view_rotx = 20.0f, view_roty = 30.0f, view_rotz = 0.0f;
	// private float angle = 0.0f;

	private int prevMouseX, prevMouseY;

	private boolean mouseRButtonDown = false;

	public GearsMouse(final GearsMain parentGearsMain)
	{
		this.gearsMain = parentGearsMain;
	}

	public boolean isMouseRButtondown()
	{
		return mouseRButtonDown;
	}

	public float getViewRotX()
	{
		return view_rotx;
	}

	public float getViewRotY()
	{
		return view_roty;
	}

	public float getViewRotZ()
	{
		return view_rotz;
	}

	// Methods required for the implementation of MouseListener
	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
		prevMouseX = e.getX();
		prevMouseY = e.getY();
		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
		{
			mouseRButtonDown = true;
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
		{
			mouseRButtonDown = false;
		}
	}

	public void mouseClicked(MouseEvent e)
	{
	}

	// Methods required for the implementation of MouseMotionListener
	public void mouseDragged(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		Dimension size = e.getComponent().getSize();

		float thetaY = 360.0f * ((float) (x - prevMouseX) / (float) size.width);
		float thetaX = 360.0f * ((float) (prevMouseY - y) / (float) size.height);

		prevMouseX = x;
		prevMouseY = y;

		view_rotx += thetaX;
		view_roty += thetaY;

		System.out.println("dragging...");

		gearsMain.setViewAngles(view_rotx, view_roty, view_rotz);
	}

	public void mouseMoved(MouseEvent e)
	{
	}
}
