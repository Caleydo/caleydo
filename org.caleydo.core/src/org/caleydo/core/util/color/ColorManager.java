/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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

		for (Color c : AlexColorPalette.Medium.get()) {
			qualitativeColorList.add(make(c, false));
		}

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
