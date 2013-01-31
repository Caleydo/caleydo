/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.data.importer.tcga.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

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

	public static TumorType byName(String name) {
		name = name.toLowerCase();
		for (TumorType type : types) {
			String t = type.getName().toLowerCase();
			if (t.equals(name) || name.startsWith(t))
				return type;
		}
		return null;
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
}
