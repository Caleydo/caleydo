/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.widget;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.enroute.correlation.DataCellInfo;
import org.caleydo.view.enroute.correlation.IDataClassifier;
import org.eclipse.swt.widgets.Composite;

/**
 * Base class for classification widgets.
 *
 * @author Christian
 *
 */
public abstract class AClassificationWidget extends Composite {

	protected Set<org.eclipse.swt.graphics.Color> colorRegistry = new HashSet<>();

	protected Set<ICallback<IDataClassifier>> callbacks = new HashSet<>();

	protected List<Color> categoryColors;
	/**
	 * @param parent
	 * @param style
	 */
	public AClassificationWidget(Composite parent, int style, List<Color> categoryColors) {
		super(parent, style);
		this.categoryColors = categoryColors;
	}


	/**
	 * @return The classifier that is used by this widget.
	 */
	public abstract IDataClassifier getClassifier();

	public abstract void updateData(DataCellInfo info);

	public void addCallback(ICallback<IDataClassifier> callback) {
		callbacks.add(callback);
	}

	public void removeCallback(ICallback<IDataClassifier> callback) {
		callbacks.remove(callback);
	}

	protected void notifyOfClassifierChange() {
		for (ICallback<IDataClassifier> callback : callbacks) {
			callback.on(getClassifier());
		}
	}

	@Override
	public void dispose() {
		callbacks.clear();
		for (org.eclipse.swt.graphics.Color c : colorRegistry) {
			c.dispose();
		}
		colorRegistry.clear();
		super.dispose();
	}

}
