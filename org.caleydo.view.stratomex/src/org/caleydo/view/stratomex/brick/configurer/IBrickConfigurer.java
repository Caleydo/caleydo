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
package org.caleydo.view.stratomex.brick.configurer;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.layout.ABrickLayoutConfiguration;
import org.caleydo.view.stratomex.brick.layout.CollapsedBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.CompactHeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.DefaultBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.DetailBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.HeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.TitleOnlyHeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.sorting.IBrickSortingStrategy;

/**
 * 
 * Classes implementing this interface are intended to configure the different
 * {@link ABrickLayoutConfiguration}'s dependent on the kind of data that shall
 * be displayed within a brick (double dispatch).
 * <p>
 * The configurations should always include setting the valid view types for the
 * templates and setting a default view type. The classes are also supposed to
 * set the appropriate views for the brick.
 * </p>
 * *
 * <p>
 * A modified version of the visitor pattern is used to achieve this goal.
 * </p>
 * 
 * @author Christian Partl
 * 
 */
public interface IBrickConfigurer {

	/**
	 * Configures the specified {@link HeaderBrickLayoutTemplate} for the type
	 * of data set of this configurer.
	 * 
	 * @param layoutTemplate
	 */
	public void configure(HeaderBrickLayoutTemplate layoutTemplate);

	/**
	 * Same as {@link #configure(HeaderBrickLayoutTemplate)} for
	 * {@link CompactBrickLayoutTemplate}
	 * 
	 * @param layoutTemplate
	 */
	public void configure(CollapsedBrickLayoutTemplate layoutTemplate);

	/**
	 * Same as {@link #configure(HeaderBrickLayoutTemplate)} for
	 * {@link CompactHeaderBrickLayoutTemplate}
	 * 
	 * @param layoutTemplate
	 */
	public void configure(CompactHeaderBrickLayoutTemplate layoutTemplate);

	/**
	 * Configures the specified {@link TitleOnlyHeaderBrickLayoutTemplate} for the type
	 * of data set of this configurer.
	 * 
	 * @param layoutTemplate
	 */
	public void configure(TitleOnlyHeaderBrickLayoutTemplate layoutTemplate);
	
	/**
	 * Same as {@link #configure(HeaderBrickLayoutTemplate)} for
	 * {@link DefaultBrickLayoutTemplate}
	 * 
	 * @param layoutTemplate
	 */
	public void configure(DefaultBrickLayoutTemplate layoutTemplate);

	/**
	 * Same as {@link #configure(HeaderBrickLayoutTemplate)} for
	 * {@link DetailBrickLayoutTemplate}
	 * 
	 * @param layoutTemplate
	 */
	public void configure(DetailBrickLayoutTemplate layoutTemplate);

	/**
	 * Sets the views and {@link AContainedViewRenderer}s in the brick that are
	 * appropriate for the kind of data.
	 * 
	 * @param brick
	 * @param gl
	 * @param glMouseListener
	 * @param brickLayout
	 */
	public void setBrickViews(GLBrick brick, GL2 gl, GLMouseListener glMouseListener,
			ABrickLayoutConfiguration brickLayout);

	/**
	 * Returns the brick sorting strategy for this particular configurer.
	 * 
	 * @return the sorting strategy that needs to be applied to the dimension
	 *         group.
	 */
	public IBrickSortingStrategy getBrickSortingStrategy();

	/**
	 * Returns a boolean value telling whether the type of data configured with
	 * this configurer should use a default width for all dimension groups and
	 * all bricks therein for the not
	 * 
	 * @return
	 */
	public boolean useDefaultWidth();

	/**
	 * Returns the default width in pixel that should be used if
	 * {@link #useDefaultWidth()} is true for all bricks except for the
	 * {@link CompactHeaderBrickLayoutTemplate}
	 */
	public int getDefaultWidth();

}
