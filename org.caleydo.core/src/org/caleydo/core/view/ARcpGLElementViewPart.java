/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.eclipse.swt.widgets.Composite;

/**
 * simple basic class for a {@link ARcpGLViewPart}
 *
 * @author Samuel Gratzl
 *
 */
public abstract class ARcpGLElementViewPart extends ARcpGLViewPart {
	private static final Logger log = Logger.create(ARcpGLElementViewPart.class);

	private final Class<? extends ASerializedView> serializedViewClass;

	/**
	 * hand in the serialized version of the view
	 *
	 * @param serializedViewClass
	 */
	protected ARcpGLElementViewPart(Class<? extends ASerializedView> serializedViewClass) {
		super(serializedViewClass);
		this.serializedViewClass = serializedViewClass;
	}

	@Override
	public final void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = createView(glCanvas);
		initializeView();
		createPartControlGL();
	}

	/**
	 * factory method for creating the view
	 *
	 * @param canvas
	 * @return
	 */
	protected abstract AGLElementView createView(IGLCanvas canvas);

	@Override
	public void createDefaultSerializedView() {
		try {
			serializedView = serializedViewClass.newInstance();
			determineDataConfiguration(serializedView);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("can't create serialized view", e);
		}

	}
}
