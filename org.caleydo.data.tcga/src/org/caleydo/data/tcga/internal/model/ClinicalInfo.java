/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.data.tcga.internal.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Samuel Gratzl
 *
 */
public class ClinicalInfo {
	private int count;
	private List<String> parameters = new ArrayList<>();

	/**
	 * @param count
	 *            setter, see {@link count}
	 */
	public void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * @return the count, see {@link #count}
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param parameters
	 *            setter, see {@link parameters}
	 */
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the parameters, see {@link #parameters}
	 */
	public List<String> getParameters() {
		return parameters;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClinicalInfo [count=");
		builder.append(count);
		builder.append(", parameters=");
		builder.append(parameters);
		builder.append("]");
		return builder.toString();
	}
}

