/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

import org.caleydo.core.util.base.ILabelProvider;

public final class SampleType implements ILabelProvider {
	private static final Set<SampleType> types = new LinkedHashSet<>();

	static {
		types.addAll(readAll("sampleType.txt"));
	}

	public static SampleType byShortLetterCode(String name) {
		name = name.toLowerCase();
		for (SampleType type : types) {
			String t = type.getShortLetterCode();
			if (t.equals(name) || name.startsWith(t))
				return type;
		}
		return null;
	}

	public static SampleType byCode(int code) {
		for (SampleType type : types) {
			if (code == type.getCode())
				return type;
		}
		return null;
	}

	private static Collection<SampleType> readAll(String fileName) {
		try (Scanner s = new Scanner(SampleType.class.getResourceAsStream("/resources/" + fileName))) {
			s.useDelimiter("\t").nextLine();
			Collection<SampleType> result = new ArrayList<>();
			while (s.hasNextInt()) {
				result.add(new SampleType(s.nextInt(), s.next(), s.nextLine().trim()));
			}
			return result;
		}
	}

	private final String shortLetterCode;
	private final String label;
	private final int code;

	SampleType(int code, String label, String shortLetterCode) {
		super();
		this.code = code;
		this.label = label;
		this.shortLetterCode = shortLetterCode;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getProviderName() {
		return "SampleType";
	}

	/**
	 * @return the shortLetterCode, see {@link #shortLetterCode}
	 */
	public String getShortLetterCode() {
		return shortLetterCode;
	}

	/**
	 * @return the code, see {@link #code}
	 */
	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return shortLetterCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((shortLetterCode == null) ? 0 : shortLetterCode.hashCode());
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
		SampleType other = (SampleType) obj;
		if (shortLetterCode == null) {
			if (other.shortLetterCode != null)
				return false;
		} else if (!shortLetterCode.equals(other.shortLetterCode))
			return false;
		return true;
	}

}
