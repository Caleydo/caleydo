/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription.ECategoryType;
import org.caleydo.core.data.collection.column.container.CategoricalContainer;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.CharStreams;

/**
 * enum like class for collecting known clinical variables and their mappings
 *
 * @author Samuel Gratzl
 *
 */
public class ClinicalMapping {
	private static final Set<ClinicalMapping> types = new LinkedHashSet<>();

	static {
		ListMultimap<String, CategoryProperty<String>> properties = parseProperties("clinicalMetaMapping.txt");
		types.addAll(readAll("clinicalMapping.txt", properties));
	}

	public static Collection<ClinicalMapping> values() {
		return Collections.unmodifiableCollection(types);
	}

	/**
	 * @param string
	 * @return
	 */
	private static ListMultimap<String, CategoryProperty<String>> parseProperties(String fileName) {
		try (InputStreamReader r = new InputStreamReader(ClinicalMapping.class.getResourceAsStream("/resources/"
				+ fileName))) {
			List<String> lines = CharStreams.readLines(r);
			lines.remove(0);
			ListMultimap<String, CategoryProperty<String>> result = ArrayListMultimap.create();
			for (String line : lines) {
				String[] ls = line.split("\t");
				String key = ls[0];
				String name = ls[1];
				String label = ls.length > 2 ? ls[2] : name;
				Color color = ls.length > 3 ? new Color(ls[3]) : null;
				result.put(key, new CategoryProperty<String>(name, label, color));
			}
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ImmutableListMultimap.of();
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

	private static Collection<ClinicalMapping> readAll(String fileName,
			ListMultimap<String, CategoryProperty<String>> properties) {
		try (InputStreamReader r = new InputStreamReader(ClinicalMapping.class.getResourceAsStream("/resources/"
				+ fileName))) {
			List<String> lines = CharStreams.readLines(r);
			lines.remove(0);
			Collection<ClinicalMapping> result = new ArrayList<>();
			for (String line : lines) {
				String[] ls = line.split("\t");
				ClinicalMapping mapping = new ClinicalMapping(ls[0], ls[1], EDataClass.valueOf(ls[2].toUpperCase()),
						EDataType.valueOf(ls[3]), Arrays.copyOfRange(ls, 4, ls.length), properties.get(ls[0]));
				result.add(mapping);
			}
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	private final String name;
	private final String label;
	private final EDataClass dataClass;
	private final EDataType dataType;
	private final String[] extra;
	private final List<CategoryProperty<String>> properties;

	ClinicalMapping(String name, String label, EDataClass dataClass, EDataType dataType, String[] extra,
			List<CategoryProperty<String>> properties) {
		super();
		this.name = name;
		this.label = label;
		this.dataClass = dataClass;
		this.dataType = dataType;
		this.extra = extra;
		this.properties = properties;
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

	public ColumnDescription create(int i) {
		final DataDescription d = new DataDescription(dataClass, dataType);

		if (dataClass == EDataClass.CATEGORICAL && properties != null && properties.size() > 0) {
			switch (dataType) {
			case STRING:
				CategoricalClassDescription<String> cc = new CategoricalClassDescription<String>();
				cc.setRawDataType(dataType);
				if (extra.length > 0)
					cc.setCategoryType(ECategoryType.valueOf(extra[0]));
				cc.setUnknownCategory(new CategoryProperty<>("NA", Color.NOT_A_NUMBER_COLOR));
				cc.setCategoryProperties(properties);
				d.setCategoricalClassDescription(cc);
				ColorBrewer cb;
				if (extra.length > 1)
					cb = ColorBrewer.valueOf(extra[1]);
				else
					cb = cc.getCategoryType() == ECategoryType.ORDINAL ? CategoricalClassDescription.DEFAULT_SEQUENTIAL_COLOR_SCHEME
							: CategoricalClassDescription.DEFAULT_QUALITATIVE_COLOR_SCHEME;
				cc.applyColorScheme(cb, "NA", false);
				break;
			case INTEGER:
				CategoricalClassDescription<Integer> ci = new CategoricalClassDescription<Integer>();
				ci.setRawDataType(dataType);
				ci.setUnknownCategory(new CategoryProperty<>(CategoricalContainer.UNKNOWN_CATEGORY_INT,
						Color.NOT_A_NUMBER_COLOR));
				if (extra.length > 0)
					ci.setCategoryType(ECategoryType.valueOf(extra[0]));
				for (CategoryProperty<String> prop : properties) {
					ci.addCategoryProperty(new Integer(prop.getCategory()), prop.getCategoryName(), prop.getColor());
				}
				if (extra.length > 1)
					cb = ColorBrewer.valueOf(extra[1]);
				else
					cb = ci.getCategoryType() == ECategoryType.ORDINAL ? CategoricalClassDescription.DEFAULT_SEQUENTIAL_COLOR_SCHEME
							: CategoricalClassDescription.DEFAULT_QUALITATIVE_COLOR_SCHEME;
				ci.applyColorScheme(cb, CategoricalContainer.UNKNOWN_CATEGORY_INT, false);
				d.setCategoricalClassDescription(ci);
				break;
			default:
				break;
			}
		}

		ColumnDescription c = new ColumnDescription(i, d);
		return c;
	}

	public static ColumnDescription createDefault(int i) {
		final DataDescription d = new DataDescription(EDataClass.CATEGORICAL, EDataType.STRING);
		CategoricalClassDescription<String> cc = new CategoricalClassDescription<String>(EDataType.STRING);
		cc.setUnknownCategory(new CategoryProperty<>("NA", Color.NEUTRAL_GREY));
		d.setCategoricalClassDescription(cc);
		ColumnDescription c = new ColumnDescription(i, d);
		return c;
	}
}
