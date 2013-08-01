/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.util.color.mapping.ColorMapper;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * This product includes color specifications and designs developed by Cynthia Brewer (http://colorbrewer.org/).
 *
 * @author Samuel Gratzl
 *
 */
public enum AlexColorPalette implements IColorPalette {
	Greens(EColorSchemeType.SEQUENTIAL),
	Blues(EColorSchemeType.SEQUENTIAL),
	Reds(EColorSchemeType.SEQUENTIAL),
	Purples(EColorSchemeType.SEQUENTIAL),
	Browns(EColorSchemeType.SEQUENTIAL),
	Turkis(EColorSchemeType.SEQUENTIAL),
	Magentas(EColorSchemeType.SEQUENTIAL),
	Tans(EColorSchemeType.SEQUENTIAL),

	Dark(EColorSchemeType.QUALITATIVE),
	MediumDark(EColorSchemeType.QUALITATIVE),
	Medium(EColorSchemeType.QUALITATIVE),
	MediumLight(EColorSchemeType.QUALITATIVE),
	Light(EColorSchemeType.QUALITATIVE);

	/**
	 * The type of this color scheme.
	 */
	private final EColorSchemeType type;

	private AlexColorPalette(EColorSchemeType type) {
		this.type = type;
	}

	/**
	 * @return the type, see {@link #type}
	 */
	@Override
	public EColorSchemeType getType() {
		return type;
	}

	private static final List<List<Color>> colors;

	static {
		colors = new ArrayList<>(8);
		colors.add(create(Greens, "#3C8F27", "#59AA4A", "#75C568", "#91E185", "#AEFEA2"));
		colors.add(create(Blues, "#18658B", "#427DA3", "#6196BD", "#7EB1D8", "#9BCCF4"));
		colors.add(create(Reds, "#830A10", "#9E3033", "#BA4D4F", "#D6686A", "#F38485"));
		colors.add(create(Purples, "#55198E", "#6B3FA2", "#835CBA", "#9C77D4", "#B693EF"));
		colors.add(create(Browns, "#864F16", "#A1683A", "#BC8159", "#D89C75", "#F5B791"));
		colors.add(create(Turkis, "#008E68", "#3CA882", "#5EC49D", "#7DDFB8", "#98FCD4"));
		colors.add(create(Magentas, "#8C1D95", "#A445AD", "#BD63C7", "#D881E2", "#F39EFD"));
		colors.add(create(Tans, "#877928", "#A1934A", "#BCAD69", "#D7C986", "#F4E5A3"));
	}

	private static List<Color> create(AlexColorPalette palette, String... colors) {
		List<Color> c = new ArrayList<>(colors.length);
		for (int i = 0; i < colors.length; ++i)
			c.add(new AlexColorPaletteColor(colors[i], palette, i));
		return ImmutableList.copyOf(c);
	}

	@Override
	public String getLabel() {
		return name();
	}

	@Override
	public SortedSet<Integer> getSizes() {
		return ImmutableSortedSet.of(colors.get(0).size(), colors.size());
	}

	@Override
	public ColorMapper asColorMapper(int size) {
		return ColorPalettes.asColorMapper(get(size), type);
	}

	public static Set<AlexColorPalette> getSets(int size) {
		if (size == colors.get(0).size())
			return EnumSet.range(Greens, Tans);
		if (size == colors.size()) {
			return EnumSet.range(Dark, Light);
		}
		return Collections.emptySet();
	}

	public List<Color> get() {
		// sequential one
		if (EnumSet.range(Greens, Tans).contains(this))
			return colors.get(this.ordinal());
		else {// qualitative one
			final int brightness = this.ordinal() - Dark.ordinal();
			return Lists.transform(colors, new Function<List<Color>, Color>() {
				@Override
				public Color apply(List<Color> in) {
					return in.get(brightness);
				}
			});
		}
	}

	@Override
	public List<Color> get(int size) {
		return get();
	}

	@Override
	public Color get(int size, int index) {
		return get().get(index);
	}

	public static Set<AlexColorPalette> getSets(int size, EColorSchemeType type) {
		return Sets.filter(getSets(size), type.isOf());
	}

	public static Set<AlexColorPalette> getSets(EColorSchemeType type) {
		return Sets.filter(ImmutableSet.copyOf(values()), type.isOf());
	}

	/**
	 * custom version of a color with special darker / brighter meaning
	 *
	 * @author Samuel Gratzl
	 *
	 */
	@XmlType
	public static class AlexColorPaletteColor extends Color {
		@XmlAttribute
		private int index;
		@XmlAttribute
		private AlexColorPalette palette;

		public AlexColorPaletteColor() {

		}

		public AlexColorPaletteColor(String hexColor, AlexColorPalette palette, int index) {
			super(hexColor);
			this.palette = palette;
			this.index = index;
		}

		private List<Color> getSet() {
			return palette.get();
		}

		@Override
		public Color darker() {
			if (index <= 0)
				return super.darker();
			List<Color> set = getSet();
			return set.get(index - 1);
		}

		@Override
		public Color brighter() {
			List<Color> set = getSet();
			if (index >= set.size() - 1)
				return super.brighter();
			return set.get(index + 1);
		}
	}
}
