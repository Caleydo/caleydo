/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import org.caleydo.core.event.AEvent;

/**
 * Tells a data cell to show the data classification that is used for correlation calculation.
 *
 * @author Christian
 *
 */
public class ShowDataClassificationEvent extends AEvent {

	protected final boolean isFirstCell;
	protected final DataCellInfo info;
	protected final IDataClassifier classifier;

	/**
	 * @param dataCellID
	 * @param classificationPredicate
	 */
	public ShowDataClassificationEvent(DataCellInfo info, IDataClassifier classifier, boolean isFirstCell) {
		this.info = info;
		this.classifier = classifier;
		this.isFirstCell = isFirstCell;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @return the info, see {@link #info}
	 */
	public DataCellInfo getInfo() {
		return info;
	}

	/**
	 * @return the classifier, see {@link #classifier}
	 */
	public IDataClassifier getClassifier() {
		return classifier;
	}

	/**
	 * @return the isFirstCell, see {@link #isFirstCell}
	 */
	public boolean isFirstCell() {
		return isFirstCell;
	}

}
