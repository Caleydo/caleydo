/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.util.collection.Pair;

/**
 * @author Christian
 *
 */
@XmlType
public class MetaDataElement {

	public enum AttributeType {
		TEXT, URL;
	}

	private String name;
	private Map<String, Pair<String, AttributeType>> attributes = new LinkedHashMap<>();
	private List<MetaDataElement> elements = new ArrayList<>();

	public MetaDataElement() {
	}

	public MetaDataElement(String name) {
		this.name = name;
	}

	public void addAttribute(String key, String value) {
		attributes.put(key, Pair.make(value, AttributeType.TEXT));
	}

	public void addAttribute(String key, String value, AttributeType type) {
		attributes.put(key, Pair.make(value, type));
	}

	public void addElement(MetaDataElement element) {
		elements.add(element);
	}

	/**
	 * @param name
	 *            setter, see {@link name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name, see {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param attributes
	 *            setter, see {@link attributes}
	 */
	public void setAttributes(Map<String, Pair<String, AttributeType>> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the attributes, see {@link #attributes}
	 */
	public Map<String, Pair<String, AttributeType>> getAttributes() {
		return attributes;
	}

	/**
	 * @param elements
	 *            setter, see {@link elements}
	 */
	public void setElements(List<MetaDataElement> elements) {
		this.elements = elements;
	}

	/**
	 * @return the elements, see {@link #elements}
	 */
	public List<MetaDataElement> getElements() {
		return elements;
	}

}
