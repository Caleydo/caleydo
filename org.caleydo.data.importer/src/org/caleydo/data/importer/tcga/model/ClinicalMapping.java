/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;

/**
 * enum like class for collecting known clinical variables and their mappings
 *
 * @author Samuel Gratzl
 *
 */
public class ClinicalMapping {
	private static final Set<ClinicalMapping> types = new LinkedHashSet<>();

	static {
		types.addAll(readAll("clinicalMapping.txt"));
	}

	public static Collection<ClinicalMapping> values() {
		return Collections.unmodifiableCollection(types);
	}

	public static ClinicalMapping byName(String name) {
		name = name.toLowerCase();
		for (ClinicalMapping type : types) {
			String t = type.getName().toLowerCase();
			if (t.equals(name))
				return type;
		}
		return null;
	}

	private static Collection<ClinicalMapping> readAll(String fileName) {
		try (Scanner s = new Scanner(ClinicalMapping.class.getResourceAsStream("/resources/" + fileName))) {
			s.useDelimiter("\t").nextLine();
			Collection<ClinicalMapping> result = new ArrayList<>();
			while (s.hasNext()) {
				result.add(new ClinicalMapping(s.next(), s.next(), EDataClass.valueOf(s.next().trim().toUpperCase()),
						EDataType
						.valueOf(s.nextLine().trim().toUpperCase())));
			}
			return result;
		}
	}

	private final String name;
	private final String label;
	private final EDataClass dataClass;
	private final EDataType dataType;

	ClinicalMapping(String name, String label, EDataClass dataClass, EDataType dataType) {
		super();
		this.name = name;
		this.label = label;
		this.dataClass = dataClass;
		this.dataType = dataType;
	}

	public String getLabel() {
		return label;
	}

	public String getName() {
		return name;
	}

	public EDataClass getDataClass() {
		return dataClass;
	}

	public EDataType getDataType() {
		return dataType;
	}
}
