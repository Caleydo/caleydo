/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.color;

import static org.caleydo.core.util.collection.Pair.make;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;

/**
 * Manager for lists of colors.
 *
 * @author Christian Partl
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ColorManager {

	public static String QUALITATIVE_COLORS = "qualitativeColors";

	private volatile static ColorManager instance;

	private Map<String, List<Pair<Color, Boolean>>> colorLists = new HashMap<>();

	{
		List<Pair<Color, Boolean>> qualitativeColorList = new ArrayList<>();

		qualitativeColorList.add(make(new Color("06799F"), false));
		qualitativeColorList.add(make(new Color("C60017"), false));
		qualitativeColorList.add(make(new Color("00654e"), false));
		qualitativeColorList.add(make(new Color("D2006B"), false));
		qualitativeColorList.add(make(new Color("A65500"), false));
		qualitativeColorList.add(make(new Color("60016D"), false));
		qualitativeColorList.add(make(new Color("A6A000"), false));

		// Colors from colorbrewer qualitative Set3
		// qualitativeColorList.add(make(new Color(141, 211, 199), false));
		// qualitativeColorList.add(make(new Color(179, 222, 105), false));
		// qualitativeColorList.add(make(new Color(128, 177, 211), false));
		// qualitativeColorList.add(make(new Color(190, 186, 218), false));
		//
		// qualitativeColorList.add(make(new Color(252, 205, 229), false));
		//
		// qualitativeColorList.add(make(new Color(188, 128, 189), false));
		// qualitativeColorList.add(make(new Color(204, 235, 197), false));
		// qualitativeColorList.add(make(new Color(255, 237, 111), false));
		// qualitativeColorList.add(make(new Color(251, 128, 114), false));
		// qualitativeColorList.add(make(new Color(253, 180, 98), false));

		colorLists.put(QUALITATIVE_COLORS, qualitativeColorList);
	}

	public static ColorManager get() {
		if (instance == null) {
			synchronized (GeneralManager.class) {
				if (instance == null) {
					instance = new ColorManager();
				}
			}
		}
		return instance;
	}

	public List<Color> getColorList(String colorListID) {
		List<Pair<Color, Boolean>> list = colorLists.get(colorListID);

		if (list == null)
			return null;

		List<Color> colorList = new ArrayList<Color>(list.size());
		for (Pair<Color, Boolean> colorItem : list) {
			colorList.add(colorItem.getFirst());
		}

		return colorList;
	}

	public List<Color> getMarkedColorsOfList(String colorListID, boolean isColorMarked) {
		List<Pair<Color, Boolean>> list = colorLists.get(colorListID);

		if (list == null)
			return null;

		List<Color> colorList = new ArrayList<Color>(list.size());
		for (Pair<Color, Boolean> colorItem : list) {
			if (colorItem.getSecond() == isColorMarked) {
				colorList.add(colorItem.getFirst());
			}
		}

		return colorList;
	}

	public Color getFirstMarkedColorOfList(String colorListID, boolean isColorMarked) {
		List<Pair<Color, Boolean>> list = colorLists.get(colorListID);

		if (list == null)
			return null;

		for (Pair<Color, Boolean> colorItem : list) {
			if (colorItem.getSecond() == isColorMarked) {
				return colorItem.getFirst();
			}
		}

		return null;
	}

	public void markColor(String colorListID, Color color, boolean mark) {
		List<Pair<Color, Boolean>> list = colorLists.get(colorListID);

		if (list == null)
			return;

		for (Pair<Color, Boolean> colorItem : list) {
			if (colorItem.getFirst() == color) {
				colorItem.setSecond(mark);
			}
		}
	}

	public void unmarkAllColors(String colorListID) {
		List<Pair<Color, Boolean>> list = colorLists.get(colorListID);

		if (list == null)
			return;

		for (Pair<Color, Boolean> colorItem : list) {
			colorItem.setSecond(false);
		}
	}

	public void addColorToList(String colorListID, Color color, boolean isColorMarked) {
		List<Pair<Color, Boolean>> list = colorLists.get(colorListID);

		if (list == null)
			return;

		list.add(make(color, isColorMarked));
	}

	public boolean doesColorListExist(String colorListID) {
		return colorLists.containsKey(colorListID);
	}

	public void addNewColorList(String colorListID) throws IllegalArgumentException {

		if (colorLists.containsKey(colorListID))
			throw new IllegalArgumentException("Color list with the specified ID already exists");

		List<Pair<Color, Boolean>> colorList = new ArrayList<Pair<Color, Boolean>>();
		colorLists.put(colorListID, colorList);
	}
}
