package org.caleydo.view.visbricks.brick.layout;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.ui.AContainedViewRenderer;

/**
 * Classes implementing this interface are intended to configure the different
 * {@link ABrickLayoutTemplate}'s dependent on the kind of data that shall be
 * displayed within a brick (double dispatch). A modified version of the visitor
 * pattern is used to achieve this goal. The configurations should always
 * include setting the valid view types for the templates and setting a default
 * view type. The classes are also supposed to set the appropriate views for the
 * brick.
 * 
 * @author Christian Partl
 * 
 */
public interface IBrickConfigurer {

	/**
	 * Configures the specified layoutTemplate.
	 * 
	 * @param layoutTemplate
	 */
	public void configure(CentralBrickLayoutTemplate layoutTemplate);

	/**
	 * Configures the specified layoutTemplate.
	 * 
	 * @param layoutTemplate
	 */
	public void configure(CompactBrickLayoutTemplate layoutTemplate);
	
	/**
	 * Configures the specified layoutTemplate.
	 * 
	 * @param layoutTemplate
	 */
	public void configure(CompactCentralBrickLayoutTemplate layoutTemplate);

	/**
	 * Configures the specified layoutTemplate.
	 * 
	 * @param layoutTemplate
	 */
	public void configure(DefaultBrickLayoutTemplate layoutTemplate);
	
	/**
	 * Configures the specified layoutTemplate.
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
	public void setBrickViews(GLBrick brick, GL2 gl,
			GLMouseListener glMouseListener, ABrickLayoutTemplate brickLayout);

}
