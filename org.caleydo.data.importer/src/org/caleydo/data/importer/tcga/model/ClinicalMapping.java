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
