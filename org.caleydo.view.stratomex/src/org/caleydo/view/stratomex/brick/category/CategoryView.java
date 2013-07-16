/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.category;

import javax.media.opengl.GL2;

import org.caleydo.core.event.view.RedrawViewEvent;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;

/**
 * Rendering the histogram.
 *
 * @author Alexander Lex
 */
public class CategoryView extends ATableBasedView {
	public static String VIEW_TYPE = "org.caleydo.view.histogram";

	public static String VIEW_NAME = "Categorical View";

	float renderWidth;

	/**
	 * Constructor.
	 *
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public CategoryView(IGLCanvas glCanvas, final ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, VIEW_TYPE, VIEW_NAME);
	}

	@Override
	public void init(GL2 gl) {

	}

	@Override
	public void initLocal(GL2 gl) {

		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		this.glMouseListener = glMouseListener;

		init(gl);

	}

	@Override
	public void initData() {
		super.initData();

	}

	@Override
	public void displayLocal(GL2 gl) {

		if (!lazyMode)
			pickingManager.handlePicking(this, gl);

		display(gl);

	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);

	}

	@Override
	public void display(GL2 gl) {
		if (!lazyMode)
			checkForHits(gl);
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {

	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (redrawViewListener != null) {
			eventPublisher.removeListener(redrawViewListener);
			redrawViewListener = null;
		}
	}

	@Override
	public int getMinPixelHeight() {
		// TODO: Calculate depending on content
		return 100;
	}

	@Override
	public int getMinPixelWidth() {
		// TODO: Calculate depending on content
		return 150;
	}

	@Override
	public int getMinPixelHeight(EDetailLevel detailLevel) {
		switch (detailLevel) {
		case HIGH:
			return getMinPixelHeight();
		case MEDIUM:
			return getMinPixelHeight();
		case LOW:
			return getMinPixelHeight();
		default:
			return 50;
		}
	}

	@Override
	public int getMinPixelWidth(EDetailLevel detailLevel) {
		switch (detailLevel) {
		case HIGH:
			return 100;
		case MEDIUM:
			return 100;
		case LOW:
			return 100;
		default:
			return 100;
		}
	}


	@Override
	public ASerializedView getSerializableRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		// TODO Auto-generated method stub

	}

}
