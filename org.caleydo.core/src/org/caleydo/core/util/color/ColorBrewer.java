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
package org.caleydo.core.util.color;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.util.color.mapping.ColorMapper;
import org.caleydo.core.util.color.mapping.ColorMarkerPoint;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

/**
 * This product includes color specifications and designs developed by Cynthia Brewer (http://colorbrewer.org/).
 *
 * @author Samuel Gratzl
 *
 */
public enum ColorBrewer {
	YlGn(EColorSchemeType.SEQUENTIAL),
	YlGnBu(EColorSchemeType.SEQUENTIAL),
	GnBu(EColorSchemeType.SEQUENTIAL),
	BuGn(EColorSchemeType.SEQUENTIAL),
	PuBuGn(EColorSchemeType.SEQUENTIAL),
	PuBu(EColorSchemeType.SEQUENTIAL),
	BuPu(EColorSchemeType.SEQUENTIAL),
	RdPu(EColorSchemeType.SEQUENTIAL),
	PuRd(EColorSchemeType.SEQUENTIAL),
	OrRd(EColorSchemeType.SEQUENTIAL),
	YlOrRd(EColorSchemeType.SEQUENTIAL),
	Accent(EColorSchemeType.QUALITATIVE),
	YlOrBr(EColorSchemeType.SEQUENTIAL),
	Purples(EColorSchemeType.SEQUENTIAL),
	Blues(EColorSchemeType.SEQUENTIAL),
	Greens(EColorSchemeType.SEQUENTIAL),
	Oranges(EColorSchemeType.SEQUENTIAL),
	Reds(EColorSchemeType.SEQUENTIAL),
	Greys(EColorSchemeType.SEQUENTIAL),
	BrBG(EColorSchemeType.DIVERGING),
	PuOr(EColorSchemeType.DIVERGING),
	PRGn(EColorSchemeType.DIVERGING),
	PiYG(EColorSchemeType.DIVERGING),
	RdBu(EColorSchemeType.DIVERGING),
	RdGy(EColorSchemeType.DIVERGING),
	RdYlBu(EColorSchemeType.DIVERGING),
	Spectral(EColorSchemeType.DIVERGING),
	RdYlGn(EColorSchemeType.DIVERGING),
	Dark2(EColorSchemeType.QUALITATIVE),
	Paired(EColorSchemeType.QUALITATIVE),
	Pastel1(EColorSchemeType.QUALITATIVE),
	Pastel2(EColorSchemeType.QUALITATIVE),
	Set1(EColorSchemeType.QUALITATIVE),
	Set2(EColorSchemeType.QUALITATIVE),
	Set3(EColorSchemeType.QUALITATIVE);

	/**
	 * The type of this color scheme.
	 */
	private EColorSchemeType type;

	private ColorBrewer(EColorSchemeType type) {
		this.type = type;
	}

	/**
	 * @return the type, see {@link #type}
	 */
	public EColorSchemeType getType() {
		return type;
	}

	private static final Table<ColorBrewer, Integer, List<Color>> data = TreeBasedTable.create();

	static {
		Map<Integer, List<Color>> set;
		set = data.row(ColorBrewer.YlGn);
		add(set, "#f7fcb9", "#addd8e", "#31a354");
		add(set, "#ffffcc", "#c2e699", "#78c679", "#238443");
		add(set, "#ffffcc", "#c2e699", "#78c679", "#31a354", "#006837");
		add(set, "#ffffcc", "#d9f0a3", "#addd8e", "#78c679", "#31a354", "#006837");
		add(set, "#ffffcc", "#d9f0a3", "#addd8e", "#78c679", "#41ab5d", "#238443", "#005a32");
		add(set, "#ffffe5", "#f7fcb9", "#d9f0a3", "#addd8e", "#78c679", "#41ab5d", "#238443", "#005a32");
		add(set, "#ffffe5", "#f7fcb9", "#d9f0a3", "#addd8e", "#78c679", "#41ab5d", "#238443", "#006837", "#004529");
		set = data.row(ColorBrewer.YlGnBu);
		add(set, "#edf8b1", "#7fcdbb", "#2c7fb8");
		add(set, "#ffffcc", "#a1dab4", "#41b6c4", "#225ea8");
		add(set, "#ffffcc", "#a1dab4", "#41b6c4", "#2c7fb8", "#253494");
		add(set, "#ffffcc", "#c7e9b4", "#7fcdbb", "#41b6c4", "#2c7fb8", "#253494");
		add(set, "#ffffcc", "#c7e9b4", "#7fcdbb", "#41b6c4", "#1d91c0", "#225ea8", "#0c2c84");
		add(set, "#ffffd9", "#edf8b1", "#c7e9b4", "#7fcdbb", "#41b6c4", "#1d91c0", "#225ea8", "#0c2c84");
		add(set, "#ffffd9", "#edf8b1", "#c7e9b4", "#7fcdbb", "#41b6c4", "#1d91c0", "#225ea8", "#253494", "#081d58");
		set = data.row(ColorBrewer.GnBu);
		add(set, "#e0f3db", "#a8ddb5", "#43a2ca");
		add(set, "#f0f9e8", "#bae4bc", "#7bccc4", "#2b8cbe");
		add(set, "#f0f9e8", "#bae4bc", "#7bccc4", "#43a2ca", "#0868ac");
		add(set, "#f0f9e8", "#ccebc5", "#a8ddb5", "#7bccc4", "#43a2ca", "#0868ac");
		add(set, "#f0f9e8", "#ccebc5", "#a8ddb5", "#7bccc4", "#4eb3d3", "#2b8cbe", "#08589e");
		add(set, "#f7fcf0", "#e0f3db", "#ccebc5", "#a8ddb5", "#7bccc4", "#4eb3d3", "#2b8cbe", "#08589e");
		add(set, "#f7fcf0", "#e0f3db", "#ccebc5", "#a8ddb5", "#7bccc4", "#4eb3d3", "#2b8cbe", "#0868ac", "#084081");
		set = data.row(ColorBrewer.BuGn);
		add(set, "#e5f5f9", "#99d8c9", "#2ca25f");
		add(set, "#edf8fb", "#b2e2e2", "#66c2a4", "#238b45");
		add(set, "#edf8fb", "#b2e2e2", "#66c2a4", "#2ca25f", "#006d2c");
		add(set, "#edf8fb", "#ccece6", "#99d8c9", "#66c2a4", "#2ca25f", "#006d2c");
		add(set, "#edf8fb", "#ccece6", "#99d8c9", "#66c2a4", "#41ae76", "#238b45", "#005824");
		add(set, "#f7fcfd", "#e5f5f9", "#ccece6", "#99d8c9", "#66c2a4", "#41ae76", "#238b45", "#005824");
		add(set, "#f7fcfd", "#e5f5f9", "#ccece6", "#99d8c9", "#66c2a4", "#41ae76", "#238b45", "#006d2c", "#00441b");
		set = data.row(ColorBrewer.PuBuGn);
		add(set, "#ece2f0", "#a6bddb", "#1c9099");
		add(set, "#f6eff7", "#bdc9e1", "#67a9cf", "#02818a");
		add(set, "#f6eff7", "#bdc9e1", "#67a9cf", "#1c9099", "#016c59");
		add(set, "#f6eff7", "#d0d1e6", "#a6bddb", "#67a9cf", "#1c9099", "#016c59");
		add(set, "#f6eff7", "#d0d1e6", "#a6bddb", "#67a9cf", "#3690c0", "#02818a", "#016450");
		add(set, "#fff7fb", "#ece2f0", "#d0d1e6", "#a6bddb", "#67a9cf", "#3690c0", "#02818a", "#016450");
		add(set, "#fff7fb", "#ece2f0", "#d0d1e6", "#a6bddb", "#67a9cf", "#3690c0", "#02818a", "#016c59", "#014636");
		set = data.row(ColorBrewer.PuBu);
		add(set, "#ece7f2", "#a6bddb", "#2b8cbe");
		add(set, "#f1eef6", "#bdc9e1", "#74a9cf", "#0570b0");
		add(set, "#f1eef6", "#bdc9e1", "#74a9cf", "#2b8cbe", "#045a8d");
		add(set, "#f1eef6", "#d0d1e6", "#a6bddb", "#74a9cf", "#2b8cbe", "#045a8d");
		add(set, "#f1eef6", "#d0d1e6", "#a6bddb", "#74a9cf", "#3690c0", "#0570b0", "#034e7b");
		add(set, "#fff7fb", "#ece7f2", "#d0d1e6", "#a6bddb", "#74a9cf", "#3690c0", "#0570b0", "#034e7b");
		add(set, "#fff7fb", "#ece7f2", "#d0d1e6", "#a6bddb", "#74a9cf", "#3690c0", "#0570b0", "#045a8d", "#023858");
		set = data.row(ColorBrewer.BuPu);
		add(set, "#e0ecf4", "#9ebcda", "#8856a7");
		add(set, "#edf8fb", "#b3cde3", "#8c96c6", "#88419d");
		add(set, "#edf8fb", "#b3cde3", "#8c96c6", "#8856a7", "#810f7c");
		add(set, "#edf8fb", "#bfd3e6", "#9ebcda", "#8c96c6", "#8856a7", "#810f7c");
		add(set, "#edf8fb", "#bfd3e6", "#9ebcda", "#8c96c6", "#8c6bb1", "#88419d", "#6e016b");
		add(set, "#f7fcfd", "#e0ecf4", "#bfd3e6", "#9ebcda", "#8c96c6", "#8c6bb1", "#88419d", "#6e016b");
		add(set, "#f7fcfd", "#e0ecf4", "#bfd3e6", "#9ebcda", "#8c96c6", "#8c6bb1", "#88419d", "#810f7c", "#4d004b");
		set = data.row(ColorBrewer.RdPu);
		add(set, "#fde0dd", "#fa9fb5", "#c51b8a");
		add(set, "#feebe2", "#fbb4b9", "#f768a1", "#ae017e");
		add(set, "#feebe2", "#fbb4b9", "#f768a1", "#c51b8a", "#7a0177");
		add(set, "#feebe2", "#fcc5c0", "#fa9fb5", "#f768a1", "#c51b8a", "#7a0177");
		add(set, "#feebe2", "#fcc5c0", "#fa9fb5", "#f768a1", "#dd3497", "#ae017e", "#7a0177");
		add(set, "#fff7f3", "#fde0dd", "#fcc5c0", "#fa9fb5", "#f768a1", "#dd3497", "#ae017e", "#7a0177");
		add(set, "#fff7f3", "#fde0dd", "#fcc5c0", "#fa9fb5", "#f768a1", "#dd3497", "#ae017e", "#7a0177", "#49006a");
		set = data.row(ColorBrewer.PuRd);
		add(set, "#e7e1ef", "#c994c7", "#dd1c77");
		add(set, "#f1eef6", "#d7b5d8", "#df65b0", "#ce1256");
		add(set, "#f1eef6", "#d7b5d8", "#df65b0", "#dd1c77", "#980043");
		add(set, "#f1eef6", "#d4b9da", "#c994c7", "#df65b0", "#dd1c77", "#980043");
		add(set, "#f1eef6", "#d4b9da", "#c994c7", "#df65b0", "#e7298a", "#ce1256", "#91003f");
		add(set, "#f7f4f9", "#e7e1ef", "#d4b9da", "#c994c7", "#df65b0", "#e7298a", "#ce1256", "#91003f");
		add(set, "#f7f4f9", "#e7e1ef", "#d4b9da", "#c994c7", "#df65b0", "#e7298a", "#ce1256", "#980043", "#67001f");
		set = data.row(ColorBrewer.OrRd);
		add(set, "#fee8c8", "#fdbb84", "#e34a33");
		add(set, "#fef0d9", "#fdcc8a", "#fc8d59", "#d7301f");
		add(set, "#fef0d9", "#fdcc8a", "#fc8d59", "#e34a33", "#b30000");
		add(set, "#fef0d9", "#fdd49e", "#fdbb84", "#fc8d59", "#e34a33", "#b30000");
		add(set, "#fef0d9", "#fdd49e", "#fdbb84", "#fc8d59", "#ef6548", "#d7301f", "#990000");
		add(set, "#fff7ec", "#fee8c8", "#fdd49e", "#fdbb84", "#fc8d59", "#ef6548", "#d7301f", "#990000");
		add(set, "#fff7ec", "#fee8c8", "#fdd49e", "#fdbb84", "#fc8d59", "#ef6548", "#d7301f", "#b30000", "#7f0000");
		set = data.row(ColorBrewer.YlOrRd);
		add(set, "#ffeda0", "#feb24c", "#f03b20");
		add(set, "#ffffb2", "#fecc5c", "#fd8d3c", "#e31a1c");
		add(set, "#ffffb2", "#fecc5c", "#fd8d3c", "#f03b20", "#bd0026");
		add(set, "#ffffb2", "#fed976", "#feb24c", "#fd8d3c", "#f03b20", "#bd0026");
		add(set, "#ffffb2", "#fed976", "#feb24c", "#fd8d3c", "#fc4e2a", "#e31a1c", "#b10026");
		add(set, "#ffffcc", "#ffeda0", "#fed976", "#feb24c", "#fd8d3c", "#fc4e2a", "#e31a1c", "#b10026");
		add(set, "#ffffcc", "#ffeda0", "#fed976", "#feb24c", "#fd8d3c", "#fc4e2a", "#e31a1c", "#bd0026", "#800026");
		set = data.row(ColorBrewer.YlOrBr);
		add(set, "#fff7bc", "#fec44f", "#d95f0e");
		add(set, "#ffffd4", "#fed98e", "#fe9929", "#cc4c02");
		add(set, "#ffffd4", "#fed98e", "#fe9929", "#d95f0e", "#993404");
		add(set, "#ffffd4", "#fee391", "#fec44f", "#fe9929", "#d95f0e", "#993404");
		add(set, "#ffffd4", "#fee391", "#fec44f", "#fe9929", "#ec7014", "#cc4c02", "#8c2d04");
		add(set, "#ffffe5", "#fff7bc", "#fee391", "#fec44f", "#fe9929", "#ec7014", "#cc4c02", "#8c2d04");
		add(set, "#ffffe5", "#fff7bc", "#fee391", "#fec44f", "#fe9929", "#ec7014", "#cc4c02", "#993404", "#662506");
		set = data.row(ColorBrewer.Purples);
		addLinear(set, "#efedf5", "#bcbddc", "#756bb1");
		addLinear(set, "#f2f0f7", "#cbc9e2", "#9e9ac8", "#6a51a3");
		addLinear(set, "#f2f0f7", "#cbc9e2", "#9e9ac8", "#756bb1", "#54278f");
		addLinear(set, "#f2f0f7", "#dadaeb", "#bcbddc", "#9e9ac8", "#756bb1", "#54278f");
		addLinear(set, "#f2f0f7", "#dadaeb", "#bcbddc", "#9e9ac8", "#807dba", "#6a51a3", "#4a1486");
		addLinear(set, "#fcfbfd", "#efedf5", "#dadaeb", "#bcbddc", "#9e9ac8", "#807dba", "#6a51a3", "#4a1486");
		addLinear(set, "#fcfbfd", "#efedf5", "#dadaeb", "#bcbddc", "#9e9ac8", "#807dba", "#6a51a3", "#54278f",
				"#3f007d");
		set = data.row(ColorBrewer.Blues);
		addLinear(set, "#deebf7", "#9ecae1", "#3182bd");
		addLinear(set, "#eff3ff", "#bdd7e7", "#6baed6", "#2171b5");
		addLinear(set, "#eff3ff", "#bdd7e7", "#6baed6", "#3182bd", "#08519c");
		addLinear(set, "#eff3ff", "#c6dbef", "#9ecae1", "#6baed6", "#3182bd", "#08519c");
		addLinear(set, "#eff3ff", "#c6dbef", "#9ecae1", "#6baed6", "#4292c6", "#2171b5", "#084594");
		addLinear(set, "#f7fbff", "#deebf7", "#c6dbef", "#9ecae1", "#6baed6", "#4292c6", "#2171b5", "#084594");
		addLinear(set, "#f7fbff", "#deebf7", "#c6dbef", "#9ecae1", "#6baed6", "#4292c6", "#2171b5", "#08519c",
				"#08306b");
		set = data.row(ColorBrewer.Greens);
		addLinear(set, "#e5f5e0", "#a1d99b", "#31a354");
		addLinear(set, "#edf8e9", "#bae4b3", "#74c476", "#238b45");
		addLinear(set, "#edf8e9", "#bae4b3", "#74c476", "#31a354", "#006d2c");
		addLinear(set, "#edf8e9", "#c7e9c0", "#a1d99b", "#74c476", "#31a354", "#006d2c");
		addLinear(set, "#edf8e9", "#c7e9c0", "#a1d99b", "#74c476", "#41ab5d", "#238b45", "#005a32");
		addLinear(set, "#f7fcf5", "#e5f5e0", "#c7e9c0", "#a1d99b", "#74c476", "#41ab5d", "#238b45", "#005a32");
		addLinear(set, "#f7fcf5", "#e5f5e0", "#c7e9c0", "#a1d99b", "#74c476", "#41ab5d", "#238b45", "#006d2c",
				"#00441b");
		set = data.row(ColorBrewer.Oranges);
		addLinear(set, "#fee6ce", "#fdae6b", "#e6550d");
		addLinear(set, "#feedde", "#fdbe85", "#fd8d3c", "#d94701");
		addLinear(set, "#feedde", "#fdbe85", "#fd8d3c", "#e6550d", "#a63603");
		addLinear(set, "#feedde", "#fdd0a2", "#fdae6b", "#fd8d3c", "#e6550d", "#a63603");
		addLinear(set, "#feedde", "#fdd0a2", "#fdae6b", "#fd8d3c", "#f16913", "#d94801", "#8c2d04");
		addLinear(set, "#fff5eb", "#fee6ce", "#fdd0a2", "#fdae6b", "#fd8d3c", "#f16913", "#d94801", "#8c2d04");
		addLinear(set, "#fff5eb", "#fee6ce", "#fdd0a2", "#fdae6b", "#fd8d3c", "#f16913", "#d94801", "#a63603",
				"#7f2704");
		set = data.row(ColorBrewer.Reds);
		addLinear(set, "#fee0d2", "#fc9272", "#de2d26");
		addLinear(set, "#fee5d9", "#fcae91", "#fb6a4a", "#cb181d");
		addLinear(set, "#fee5d9", "#fcae91", "#fb6a4a", "#de2d26", "#a50f15");
		addLinear(set, "#fee5d9", "#fcbba1", "#fc9272", "#fb6a4a", "#de2d26", "#a50f15");
		addLinear(set, "#fee5d9", "#fcbba1", "#fc9272", "#fb6a4a", "#ef3b2c", "#cb181d", "#99000d");
		addLinear(set, "#fff5f0", "#fee0d2", "#fcbba1", "#fc9272", "#fb6a4a", "#ef3b2c", "#cb181d", "#99000d");
		addLinear(set, "#fff5f0", "#fee0d2", "#fcbba1", "#fc9272", "#fb6a4a", "#ef3b2c", "#cb181d", "#a50f15",
				"#67000d");
		set = data.row(ColorBrewer.Greys);
		addLinear(set, "#f0f0f0", "#bdbdbd", "#636363");
		addLinear(set, "#f7f7f7", "#cccccc", "#969696", "#525252");
		addLinear(set, "#f7f7f7", "#cccccc", "#969696", "#636363", "#252525");
		addLinear(set, "#f7f7f7", "#d9d9d9", "#bdbdbd", "#969696", "#636363", "#252525");
		addLinear(set, "#f7f7f7", "#d9d9d9", "#bdbdbd", "#969696", "#737373", "#525252", "#252525");
		addLinear(set, "#ffffff", "#f0f0f0", "#d9d9d9", "#bdbdbd", "#969696", "#737373", "#525252", "#252525");
		addLinear(set, "#ffffff", "#f0f0f0", "#d9d9d9", "#bdbdbd", "#969696", "#737373", "#525252", "#252525",
				"#000000");
		set = data.row(ColorBrewer.PuOr);
		add(set, "#f1a340", "#f7f7f7", "#998ec3");
		add(set, "#e66101", "#fdb863", "#b2abd2", "#5e3c99");
		add(set, "#e66101", "#fdb863", "#f7f7f7", "#b2abd2", "#5e3c99");
		add(set, "#b35806", "#f1a340", "#fee0b6", "#d8daeb", "#998ec3", "#542788");
		add(set, "#b35806", "#f1a340", "#fee0b6", "#f7f7f7", "#d8daeb", "#998ec3", "#542788");
		add(set, "#b35806", "#e08214", "#fdb863", "#fee0b6", "#d8daeb", "#b2abd2", "#8073ac", "#542788");
		add(set, "#b35806", "#e08214", "#fdb863", "#fee0b6", "#f7f7f7", "#d8daeb", "#b2abd2", "#8073ac", "#542788");
		add(set, "#7f3b08", "#b35806", "#e08214", "#fdb863", "#fee0b6", "#d8daeb", "#b2abd2", "#8073ac", "#542788",
				"#2d004b");
		add(set, "#7f3b08", "#b35806", "#e08214", "#fdb863", "#fee0b6", "#f7f7f7", "#d8daeb", "#b2abd2", "#8073ac",
				"#542788", "#2d004b");
		set = data.row(ColorBrewer.BrBG);
		add(set, "#d8b365", "#f5f5f5", "#5ab4ac");
		add(set, "#a6611a", "#dfc27d", "#80cdc1", "#018571");
		add(set, "#a6611a", "#dfc27d", "#f5f5f5", "#80cdc1", "#018571");
		add(set, "#8c510a", "#d8b365", "#f6e8c3", "#c7eae5", "#5ab4ac", "#01665e");
		add(set, "#8c510a", "#d8b365", "#f6e8c3", "#f5f5f5", "#c7eae5", "#5ab4ac", "#01665e");
		add(set, "#8c510a", "#bf812d", "#dfc27d", "#f6e8c3", "#c7eae5", "#80cdc1", "#35978f", "#01665e");
		add(set, "#8c510a", "#bf812d", "#dfc27d", "#f6e8c3", "#f5f5f5", "#c7eae5", "#80cdc1", "#35978f", "#01665e");
		add(set, "#543005", "#8c510a", "#bf812d", "#dfc27d", "#f6e8c3", "#c7eae5", "#80cdc1", "#35978f", "#01665e",
				"#003c30");
		add(set, "#543005", "#8c510a", "#bf812d", "#dfc27d", "#f6e8c3", "#f5f5f5", "#c7eae5", "#80cdc1", "#35978f",
				"#01665e", "#003c30");
		set = data.row(ColorBrewer.PRGn);
		add(set, "#af8dc3", "#f7f7f7", "#7fbf7b");
		add(set, "#7b3294", "#c2a5cf", "#a6dba0", "#008837");
		add(set, "#7b3294", "#c2a5cf", "#f7f7f7", "#a6dba0", "#008837");
		add(set, "#762a83", "#af8dc3", "#e7d4e8", "#d9f0d3", "#7fbf7b", "#1b7837");
		add(set, "#762a83", "#af8dc3", "#e7d4e8", "#f7f7f7", "#d9f0d3", "#7fbf7b", "#1b7837");
		add(set, "#762a83", "#9970ab", "#c2a5cf", "#e7d4e8", "#d9f0d3", "#a6dba0", "#5aae61", "#1b7837");
		add(set, "#762a83", "#9970ab", "#c2a5cf", "#e7d4e8", "#f7f7f7", "#d9f0d3", "#a6dba0", "#5aae61", "#1b7837");
		add(set, "#40004b", "#762a83", "#9970ab", "#c2a5cf", "#e7d4e8", "#d9f0d3", "#a6dba0", "#5aae61", "#1b7837",
				"#00441b");
		add(set, "#40004b", "#762a83", "#9970ab", "#c2a5cf", "#e7d4e8", "#f7f7f7", "#d9f0d3", "#a6dba0", "#5aae61",
				"#1b7837", "#00441b");
		set = data.row(ColorBrewer.PiYG);
		add(set, "#e9a3c9", "#f7f7f7", "#a1d76a");
		add(set, "#d01c8b", "#f1b6da", "#b8e186", "#4dac26");
		add(set, "#d01c8b", "#f1b6da", "#f7f7f7", "#b8e186", "#4dac26");
		add(set, "#c51b7d", "#e9a3c9", "#fde0ef", "#e6f5d0", "#a1d76a", "#4d9221");
		add(set, "#c51b7d", "#e9a3c9", "#fde0ef", "#f7f7f7", "#e6f5d0", "#a1d76a", "#4d9221");
		add(set, "#c51b7d", "#de77ae", "#f1b6da", "#fde0ef", "#e6f5d0", "#b8e186", "#7fbc41", "#4d9221");
		add(set, "#c51b7d", "#de77ae", "#f1b6da", "#fde0ef", "#f7f7f7", "#e6f5d0", "#b8e186", "#7fbc41", "#4d9221");
		add(set, "#8e0152", "#c51b7d", "#de77ae", "#f1b6da", "#fde0ef", "#e6f5d0", "#b8e186", "#7fbc41", "#4d9221",
				"#276419");
		add(set, "#8e0152", "#c51b7d", "#de77ae", "#f1b6da", "#fde0ef", "#f7f7f7", "#e6f5d0", "#b8e186", "#7fbc41",
				"#4d9221", "#276419");
		set = data.row(ColorBrewer.RdBu);
		add(set, "#ef8a62", "#f7f7f7", "#67a9cf");
		add(set, "#ca0020", "#f4a582", "#92c5de", "#0571b0");
		add(set, "#ca0020", "#f4a582", "#f7f7f7", "#92c5de", "#0571b0");
		add(set, "#b2182b", "#ef8a62", "#fddbc7", "#d1e5f0", "#67a9cf", "#2166ac");
		add(set, "#b2182b", "#ef8a62", "#fddbc7", "#f7f7f7", "#d1e5f0", "#67a9cf", "#2166ac");
		add(set, "#b2182b", "#d6604d", "#f4a582", "#fddbc7", "#d1e5f0", "#92c5de", "#4393c3", "#2166ac");
		add(set, "#b2182b", "#d6604d", "#f4a582", "#fddbc7", "#f7f7f7", "#d1e5f0", "#92c5de", "#4393c3", "#2166ac");
		add(set, "#67001f", "#b2182b", "#d6604d", "#f4a582", "#fddbc7", "#d1e5f0", "#92c5de", "#4393c3", "#2166ac",
				"#053061");
		add(set, "#67001f", "#b2182b", "#d6604d", "#f4a582", "#fddbc7", "#f7f7f7", "#d1e5f0", "#92c5de", "#4393c3",
				"#2166ac", "#053061");
		set = data.row(ColorBrewer.RdGy);
		add(set, "#ef8a62", "#ffffff", "#999999");
		add(set, "#ca0020", "#f4a582", "#bababa", "#404040");
		add(set, "#ca0020", "#f4a582", "#ffffff", "#bababa", "#404040");
		add(set, "#b2182b", "#ef8a62", "#fddbc7", "#e0e0e0", "#999999", "#4d4d4d");
		add(set, "#b2182b", "#ef8a62", "#fddbc7", "#ffffff", "#e0e0e0", "#999999", "#4d4d4d");
		add(set, "#b2182b", "#d6604d", "#f4a582", "#fddbc7", "#e0e0e0", "#bababa", "#878787", "#4d4d4d");
		add(set, "#b2182b", "#d6604d", "#f4a582", "#fddbc7", "#ffffff", "#e0e0e0", "#bababa", "#878787", "#4d4d4d");
		add(set, "#67001f", "#b2182b", "#d6604d", "#f4a582", "#fddbc7", "#e0e0e0", "#bababa", "#878787", "#4d4d4d",
				"#1a1a1a");
		add(set, "#67001f", "#b2182b", "#d6604d", "#f4a582", "#fddbc7", "#ffffff", "#e0e0e0", "#bababa", "#878787",
				"#4d4d4d", "#1a1a1a");
		set = data.row(ColorBrewer.RdYlBu);
		add(set, "#fc8d59", "#ffffbf", "#91bfdb");
		add(set, "#d7191c", "#fdae61", "#abd9e9", "#2c7bb6");
		add(set, "#d7191c", "#fdae61", "#ffffbf", "#abd9e9", "#2c7bb6");
		add(set, "#d73027", "#fc8d59", "#fee090", "#e0f3f8", "#91bfdb", "#4575b4");
		add(set, "#d73027", "#fc8d59", "#fee090", "#ffffbf", "#e0f3f8", "#91bfdb", "#4575b4");
		add(set, "#d73027", "#f46d43", "#fdae61", "#fee090", "#e0f3f8", "#abd9e9", "#74add1", "#4575b4");
		add(set, "#d73027", "#f46d43", "#fdae61", "#fee090", "#ffffbf", "#e0f3f8", "#abd9e9", "#74add1", "#4575b4");
		add(set, "#a50026", "#d73027", "#f46d43", "#fdae61", "#fee090", "#e0f3f8", "#abd9e9", "#74add1", "#4575b4",
				"#313695");
		add(set, "#a50026", "#d73027", "#f46d43", "#fdae61", "#fee090", "#ffffbf", "#e0f3f8", "#abd9e9", "#74add1",
				"#4575b4", "#313695");
		set = data.row(ColorBrewer.Spectral);
		add(set, "#fc8d59", "#ffffbf", "#99d594");
		add(set, "#d7191c", "#fdae61", "#abdda4", "#2b83ba");
		add(set, "#d7191c", "#fdae61", "#ffffbf", "#abdda4", "#2b83ba");
		add(set, "#d53e4f", "#fc8d59", "#fee08b", "#e6f598", "#99d594", "#3288bd");
		add(set, "#d53e4f", "#fc8d59", "#fee08b", "#ffffbf", "#e6f598", "#99d594", "#3288bd");
		add(set, "#d53e4f", "#f46d43", "#fdae61", "#fee08b", "#e6f598", "#abdda4", "#66c2a5", "#3288bd");
		add(set, "#d53e4f", "#f46d43", "#fdae61", "#fee08b", "#ffffbf", "#e6f598", "#abdda4", "#66c2a5", "#3288bd");
		add(set, "#9e0142", "#d53e4f", "#f46d43", "#fdae61", "#fee08b", "#e6f598", "#abdda4", "#66c2a5", "#3288bd",
				"#5e4fa2");
		add(set, "#9e0142", "#d53e4f", "#f46d43", "#fdae61", "#fee08b", "#ffffbf", "#e6f598", "#abdda4", "#66c2a5",
				"#3288bd", "#5e4fa2");
		set = data.row(ColorBrewer.RdYlGn);
		add(set, "#fc8d59", "#ffffbf", "#91cf60");
		add(set, "#d7191c", "#fdae61", "#a6d96a", "#1a9641");
		add(set, "#d7191c", "#fdae61", "#ffffbf", "#a6d96a", "#1a9641");
		add(set, "#d73027", "#fc8d59", "#fee08b", "#d9ef8b", "#91cf60", "#1a9850");
		add(set, "#d73027", "#fc8d59", "#fee08b", "#ffffbf", "#d9ef8b", "#91cf60", "#1a9850");
		add(set, "#d73027", "#f46d43", "#fdae61", "#fee08b", "#d9ef8b", "#a6d96a", "#66bd63", "#1a9850");
		add(set, "#d73027", "#f46d43", "#fdae61", "#fee08b", "#ffffbf", "#d9ef8b", "#a6d96a", "#66bd63", "#1a9850");
		add(set, "#a50026", "#d73027", "#f46d43", "#fdae61", "#fee08b", "#d9ef8b", "#a6d96a", "#66bd63", "#1a9850",
				"#006837");
		add(set, "#a50026", "#d73027", "#f46d43", "#fdae61", "#fee08b", "#ffffbf", "#d9ef8b", "#a6d96a", "#66bd63",
				"#1a9850", "#006837");
		set = data.row(ColorBrewer.Accent);
		addRange(set, 3, "#7fc97f", "#beaed4", "#fdc086", "#ffff99", "#386cb0", "#f0027f", "#bf5b17", "#666666");
		set = data.row(ColorBrewer.Dark2);
		addRange(set, 3, "#1b9e77", "#d95f02", "#7570b3", "#e7298a", "#66a61e", "#e6ab02", "#a6761d", "#666666");
		set = data.row(ColorBrewer.Paired);
		addRange(set, 3, "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00",
				"#cab2d6", "#6a3d9a", "#ffff99", "#b15928");
		set = data.row(ColorBrewer.Pastel1);
		addRange(set, 3, "#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4", "#fed9a6", "#ffffcc", "#e5d8bd", "#fddaec",
				"#f2f2f2");
		set = data.row(ColorBrewer.Pastel2);
		addRange(set, 3, "#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4", "#e6f5c9", "#fff2ae", "#f1e2cc", "#cccccc");
		set = data.row(ColorBrewer.Set1);
		addRange(set, 3, "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", "#ff7f00", "#ffff33", "#a65628", "#f781bf",
				"#999999");
		set = data.row(ColorBrewer.Set2);
		addRange(set, 3, "#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3", "#a6d854", "#ffd92f", "#e5c494", "#b3b3b3");
		set = data.row(ColorBrewer.Set3);
		addRange(set, 3, "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", "#80b1d3", "#fdb462", "#b3de69", "#fccde5",
				"#d9d9d9", "#bc80bd", "#ccebc5", "#ffed6f");
	}

	private static void add(Map<Integer, List<Color>> set, String... colors) {
		List<Color> col = new ArrayList<>(colors.length);
		for (String c : colors)
			col.add(Color.decode(c));
		set.put(colors.length, col);
	}

	/**
	 * add special linear range colors where {@link Color#darker()} and {@link Color#brighter()} use colors from the
	 * same set
	 *
	 * @param set
	 * @param colors
	 */
	private static void addLinear(Map<Integer, List<Color>> set, String... colors) {
		List<Color> col = new ArrayList<>(colors.length);
		int i = 0;
		for (String c : colors)
			col.add(BrewerColor.decode(c, col, i++));
		set.put(colors.length, col);
	}

	private static void addRange(Map<Integer, List<Color>> set, int start, String... colors) {
		List<Color> col = new ArrayList<>(colors.length);
		for (String c : colors)
			col.add(Color.decode(c));
		for (int i = start; i < colors.length; ++i)
			set.put(i, col.subList(0, i));
		set.put(colors.length, col);
	}

	public Set<Integer> getSizes() {
		return data.row(this).keySet();
	}

	public int getMaxSize() {
		int max = Integer.MIN_VALUE;
		for (Integer size : data.row(this).keySet()) {
			if (size > max)
				max = size;
		}
		return max;
	}

	public int getMinSize() {
		int min = Integer.MAX_VALUE;
		for (Integer size : data.row(this).keySet()) {
			if (size < min)
				min = size;
		}
		return min;
	}

	public ColorMapper asColorMapper(int size) {
		List<Color> colors = data.get(this, size);
		List<ColorMarkerPoint> colorMarkerPoints = new ArrayList<>(colors.size());

		int colorCount = 0;
		double mappingValueDistance = 1d / (colors.size() - 1);
		float nextMappingValue = 0;

		float spread = 0.05f;
		if (type == EColorSchemeType.QUALITATIVE)
			spread = 1.0f / colors.size();
		for (Color color : colors) {
			if (colorCount == colors.size() - 1)
				nextMappingValue = 1;
			ColorMarkerPoint point = new ColorMarkerPoint(nextMappingValue,
					(org.caleydo.core.util.color.Color) Colors.of(color));

			// set spread only for first and last
			if (colorCount == 0 || (type == EColorSchemeType.QUALITATIVE && colorCount != colors.size() - 1)) {
				point.setRightSpread(spread);
			}
			if (colorCount == colors.size() - 1) {
				point.setLeftSpread(spread);

			}
			colorMarkerPoints.add(point);
			nextMappingValue += mappingValueDistance;
			colorCount++;
		}

		return new ColorMapper(colorMarkerPoints);
	}

	public static Set<ColorBrewer> getSets(int size) {
		return data.column(size).keySet();
	}

	public List<Color> get(int size) {
		return data.get(this, size);
	}

	public Color get(int size, int index) {
		return get(size).get(index);
	}

	public static Set<ColorBrewer> getSets(int size, EColorSchemeType type) {
		Set<ColorBrewer> set = getSets(size);
		Set<ColorBrewer> result = new HashSet<>();
		for (ColorBrewer scheme : set) {
			if (scheme.getType() == type) {
				result.add(scheme);
			}
		}

		return result;
	}

	public static Set<ColorBrewer> getSets(EColorSchemeType type) {
		Set<ColorBrewer> set = new HashSet<>();
		for (ColorBrewer scheme : values()) {
			if (scheme.getType() == type) {
				set.add(scheme);
			}
		}
		return set;
	}

	/**
	 * special color version, where {@link #darker()} and {@link #brighter()} are just lookups in a color set
	 *
	 * @author Samuel Gratzl
	 *
	 */
	private static class BrewerColor extends Color {
		private static final long serialVersionUID = -6536215871696621355L;
		private final int index;
		private final List<Color> set;

		public BrewerColor(int r, int g, int b, int a, List<Color> set, int index) {
			super(r, g, b, a);
			this.set = set;
			this.index = index;
		}

		public static BrewerColor decode(String nm, List<Color> set, int index) {
			Color base = Color.decode(nm);
			return new BrewerColor(base.getRed(), base.getGreen(), base.getBlue(), base.getAlpha(), set, index);
		}

		@Override
		public Color darker() {
			if (index >= set.size())
				return super.darker();
			return set.get(index + 1);
		}

		@Override
		public Color brighter() {
			if (index <= 0)
				return super.brighter();
			return set.get(index - 1);
		}
	}
}
