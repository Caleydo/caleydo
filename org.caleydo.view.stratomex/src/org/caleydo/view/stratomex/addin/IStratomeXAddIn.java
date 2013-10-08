/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.stratomex.addin;

import java.util.Collection;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.column.BlockAdapter;
import org.caleydo.view.stratomex.column.BrickColumn;

/**
 * @author Samuel Gratzl
 *
 */
public interface IStratomeXAddIn {

	/**
	 * @param columns
	 */
	void addColumns(List<BlockAdapter> columns);

	/**
	 *
	 */
	void postDisplay();

	/**
	 * @return
	 */
	boolean canShowDetailBrick();

	/**
	 *
	 */
	void registerPickingListeners();

	/**
	 * @param brickColumn
	 */
	void addedBrickColumn(BrickColumn brickColumn);

	/**
	 * @return
	 */
	boolean isEmpty();

	/**
	 * @return
	 */
	Collection<? extends String> addEmptyStrings();

	/**
	 * @param gl
	 * @param xStart
	 * @param leftCenterBrickTop
	 * @param x
	 * @param f
	 * @param iD
	 */
	void renderOptionTrigger(GL2 gl, float xStart, float leftCenterBrickTop, float x, float f, int iD);

	/**
	 * @param stratomeX
	 */
	void stampTo(GLStratomex stratomeX);

}
