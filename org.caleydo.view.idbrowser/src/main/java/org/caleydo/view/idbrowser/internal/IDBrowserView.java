/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.idbrowser.internal;

import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.GLThreadListenerWrapper;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.idbrowser.internal.serial.SerializedIDBrowserView;
import org.caleydo.view.idbrowser.ui.IDBrowserElement;
import org.caleydo.vis.lineup.ui.RankTableKeyListener;
import org.caleydo.vis.lineup.ui.RankTableUIMouseKeyListener;

/**
 *
 * @author Samuel Gratzl
 *
 */
public class IDBrowserView extends AGLElementView {
	public static final String VIEW_TYPE = "org.caleydo.view.idbrowser";
	public static final String VIEW_NAME = "ID Browser";

	private IGLKeyListener tableKeyListener;
	private IGLKeyListener tableKeyListener2;
	private IGLMouseListener tableMouseListener;

	public IDBrowserView(IGLCanvas glCanvas) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedIDBrowserView();
	}

	@Override
	protected GLElement createRoot() {
		return new IDBrowserElement();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);

		IDBrowserElement r = (IDBrowserElement) getRoot();
		// wrap for having the right thread
		this.tableKeyListener = GLThreadListenerWrapper.wrap(new RankTableKeyListener(r.getTable()));
		this.canvas.addKeyListener(tableKeyListener);
		RankTableUIMouseKeyListener tableUIListener = new RankTableUIMouseKeyListener(r.getBody());
		this.tableKeyListener2 = GLThreadListenerWrapper.wrap((IGLKeyListener) tableUIListener);
		this.tableMouseListener = GLThreadListenerWrapper.wrap((IGLMouseListener) tableUIListener);
		this.canvas.addKeyListener(eventListeners.register(this.tableKeyListener2));
		this.canvas.addMouseListener(eventListeners.register(this.tableMouseListener));
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		canvas.removeKeyListener(tableKeyListener);
		canvas.removeKeyListener(tableKeyListener2);
		canvas.removeMouseListener(tableMouseListener);
		super.dispose(drawable);
	}
}
