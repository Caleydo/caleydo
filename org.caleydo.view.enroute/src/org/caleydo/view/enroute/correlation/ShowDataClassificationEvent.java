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

	protected final int dataCellID;

	protected final IDataClassifier classifier;

	/**
	 * @param dataCellID
	 * @param classificationPredicate
	 */
	public ShowDataClassificationEvent(int dataCellID, IDataClassifier classifier) {
		super();
		this.dataCellID = dataCellID;
		this.classifier = classifier;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @return the dataCellID, see {@link #dataCellID}
	 */
	public int getDataCellID() {
		return dataCellID;
	}

	/**
	 * @return the classifier, see {@link #classifier}
	 */
	public IDataClassifier getClassifier() {
		return classifier;
	}

}
