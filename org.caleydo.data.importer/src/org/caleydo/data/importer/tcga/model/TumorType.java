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
import java.util.regex.Pattern;

import org.caleydo.core.util.base.ILabelProvider;

/**
 * TCGA tumor types as listed here:
 * https://tcga-data.nci.nih.gov/tcga/tcgaHome2.jsp
 *
 * @author Marc Streit
 *
 */
public class TumorType implements ILabelProvider {
	private static final Set<TumorType> types = new LinkedHashSet<>();

	static {
		types.addAll(readAll("diseaseStudy.txt"));
		types.addAll(readAll("diseaseStudySpecial.txt"));
	}

	/**
	 * returns all known {@link TumorType}s
	 *
	 * @return
	 */
	public static Collection<TumorType> values() {
		return Collections.unmodifiableCollection(types);
	}

	public static Collection<TumorType> getNormalTumorTypes() {
		Collection<TumorType> r = new ArrayList<>(types.size());
		for (TumorType t : types) {
			if (t.isSpecialType())
				continue;
			r.add(t);
		}
		return r;
	}

	public static TumorType byName(String name) {
		name = name.toLowerCase();
		for (TumorType type : types) {
			String t = type.getName().toLowerCase();
			if (t.equals(name))
				return type;
		}
		return null;
	}

	public static Collection<TumorType> byNameMatches(Pattern pattern) {
		Collection<TumorType> r = new ArrayList<>(types.size());
		for (TumorType type : types) {
			if (pattern.matcher(type.getName()).matches())
				r.add(type);
		}
		return r;
	}

	private static Collection<TumorType> readAll(String fileName) {
		try (Scanner s = new Scanner(TumorType.class.getResourceAsStream("/resources/" + fileName))) {
			s.useDelimiter("\t").nextLine();
			Collection<TumorType> result = new ArrayList<>();
			while (s.hasNext()) {
				result.add(new TumorType(s.next(), s.nextLine().trim()));
			}
			return result;
		}
	}

	private final String name;
	private final String label;

	TumorType(String name, String label) {
		super();
		this.name = name;
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * by convention types with a "-" in it are special ones
	 * @return
	 */
	public boolean isSpecialType() {
		return name.contains("-");
	}

	public String getBaseName() {
		if (!isSpecialType())
			return name;
		return name.substring(0, name.indexOf('-'));
	}
	@Override
	public String getProviderName() {
		return "TumorType";
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TumorType other = (TumorType) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * @param type
	 * @return
	 */
	public static TumorType createDummy(String type) {
		return new TumorType(type, type);
	}
}
