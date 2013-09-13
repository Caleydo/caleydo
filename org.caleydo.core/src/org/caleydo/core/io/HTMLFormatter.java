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
public class HTMLFormatter implements IMetaDataFormatter {

	private static final int MAX_HEADER_DEPTH = 4;

	@Override
	public String format(MetaDataElement metaData) {
		return format(metaData, "");
	}

	public String format(MetaDataElement metaData, String title) {
		StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html>").append(System.lineSeparator()).append("<html>")
				.append(System.lineSeparator()).append("<head>").append(System.lineSeparator());
		addTag(builder, "title", title);
		builder.append("</head>").append(System.lineSeparator()).append("<body>").append(System.lineSeparator());
		format(metaData, builder, 1);
		builder.append("</body>").append(System.lineSeparator()).append("</html>").append(System.lineSeparator());

		return builder.toString();
	}

	private void format(MetaDataElement metaData, StringBuilder builder, int currentHeaderDepth) {
		String name = metaData.getName();
		Map<String, Pair<String, AttributeType>> attributes = metaData.getAttributes();
		List<MetaDataElement> elements = metaData.getElements();
		if (name != null) {
			if ((currentHeaderDepth != MAX_HEADER_DEPTH)
					&& ((attributes != null && !attributes.isEmpty()) || (elements != null && !elements.isEmpty()))) {
				addTag(builder, "h" + currentHeaderDepth, name);
				currentHeaderDepth++;
			} else {
				builder.append(name).append("<br/>");
			}
			builder.append(System.lineSeparator());
		}

		if (attributes != null && !attributes.isEmpty()) {
			builder.append("<p>");
			for (Entry<String, Pair<String, AttributeType>> entry : attributes.entrySet()) {
				builder.append(entry.getKey()).append(": ");
				if (entry.getValue().getSecond() == AttributeType.URL) {
					addTag(builder, "a", entry.getValue().getFirst(), "href=\"" + entry.getValue().getFirst() + "\"");
				} else {
					builder.append(entry.getValue().getFirst());
				}
				builder.append("<br/>").append(System.lineSeparator());
			}
			builder.append("</p>");
		}

		if (elements != null && !elements.isEmpty()) {
			for (MetaDataElement element : elements) {
				format(element, builder, currentHeaderDepth);
			}
		}
	}

	private void addTag(StringBuilder builder, String tag, String content) {
		builder.append("<").append(tag).append(">").append(content).append("</").append(tag).append(">");
	}

	private void addTag(StringBuilder builder, String tag, String content, String... attributes) {
		builder.append("<").append(tag);
		for (String attribute : attributes) {
			builder.append(" ").append(attribute);
		}

		builder.append(">").append(content).append("</").append(tag).append(">");
	}

}
