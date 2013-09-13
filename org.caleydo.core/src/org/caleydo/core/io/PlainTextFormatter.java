/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.caleydo.core.io.MetaDataElement.AttributeType;
import org.caleydo.core.util.collection.Pair;

/**
 * @author Christian
 *
 */
public class PlainTextFormatter implements IMetaDataFormatter {

	@Override
	public String format(MetaDataElement metaData) {
		StringBuilder builder = new StringBuilder();
		format(metaData, builder, "");

		return builder.toString();
	}

	private void format(MetaDataElement metaData, StringBuilder builder, String preceedingSpaces) {
		String name = metaData.getName();
		if (name != null) {
			builder.append(preceedingSpaces).append(name).append(System.lineSeparator());
		}
		String newPreceedingSpaces = preceedingSpaces + "  ";

		Map<String, Pair<String, AttributeType>> attributes = metaData.getAttributes();
		if (attributes != null && !attributes.isEmpty()) {
			for (Entry<String, Pair<String, AttributeType>> entry : attributes.entrySet()) {
				builder.append(newPreceedingSpaces).append(entry.getKey()).append(": ")
						.append(entry.getValue().getFirst()).append(System.lineSeparator());
			}
		}

		List<MetaDataElement> elements = metaData.getElements();

		if (elements != null && !elements.isEmpty()) {
			for (MetaDataElement element : elements) {
				format(element, builder, newPreceedingSpaces);
			}
		}
	}
}
