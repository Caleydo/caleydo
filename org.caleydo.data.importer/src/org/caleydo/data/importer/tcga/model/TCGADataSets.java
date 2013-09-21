/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga.model;

import java.util.ArrayList;

import org.caleydo.core.io.ProjectDescription;
import org.caleydo.view.tourguide.api.external.ScoreParseSpecification;

/**
 * similar to {@link ProjectDescription} but for {@link TCGADataSet}s
 *
 * @author Samuel Gratzl
 *
 */
public class TCGADataSets extends ArrayList<TCGADataSet> {
	private static final long serialVersionUID = -5914726220369369026L;

	private String label;

	private ScoreParseSpecification mutsigParser;

	/**
	 * @param label
	 */
	public TCGADataSets(String label) {
		super();
		this.label = label;
	}

	/**
	 * @param mutsigParser
	 *            setter, see {@link mutsigParser}
	 */
	public void setMutsigParser(ScoreParseSpecification mutsigParser) {
		this.mutsigParser = mutsigParser;
	}


	/**
	 * @return the mutsigParser, see {@link #mutsigParser}
	 */
	public ScoreParseSpecification getMutsigParser() {
		return mutsigParser;
	}

	/**
	 * @param label
	 *            setter, see {@link label}
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the label, see {@link #label}
	 */
	public String getLabel() {
		return label;
	}
}
