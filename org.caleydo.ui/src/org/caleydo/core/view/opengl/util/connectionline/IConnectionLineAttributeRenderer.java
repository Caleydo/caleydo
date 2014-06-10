/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.core.view.opengl.util.connectionline;

import gleem.linalg.Vec3f;

import java.util.List;

import javax.media.opengl.GL2;

/**
 * @author Christian Interface for all attributes that can be added to a
 *         connection line (see {@link ConnectionLineRenderer}).
 * 
 */
public interface IConnectionLineAttributeRenderer {

	/**
	 * Rendering method for the attribute.
	 * 
	 * @param gl
	 * @param linePoints Points of the connection line.
	 */
	public void render(GL2 gl, List<Vec3f> linePoints);
}
