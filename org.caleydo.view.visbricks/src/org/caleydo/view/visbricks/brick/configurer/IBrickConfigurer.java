package org.caleydo.view.visbricks.brick.configurer;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.layout.ABrickLayoutConfiguration;
import org.caleydo.view.visbricks.brick.layout.CollapsedBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.CompactHeaderBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.DefaultBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.DetailBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.HeaderBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.sorting.ExternallyProvidedSortingStrategy;
import org.caleydo.view.visbricks.brick.sorting.IBrickSortingStrategy;

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
public interface IBrickConfigurer
{

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
