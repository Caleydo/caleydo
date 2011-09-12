package org.caleydo.core.util.color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;

/**
 * Manager for lists of colors.
 * 
 * @author Partl
 */
public class ColorManager {

	public static String DATA_DOMAIN_COLORS = "dataDomainColors";
	
	private volatile static ColorManager instance;

	private Map<String, List<Pair<Color, Boolean>>> colorLists =
		new HashMap<String, List<Pair<Color, Boolean>>>();

	{
		List<Pair<Color, Boolean>> dataDomainColorList = new ArrayList<Pair<Color, Boolean>>();

		// Colors from colorbrewer qualitative Set3
		dataDomainColorList.add(new Pair<Color, Boolean>(new Color(141f / 255f, 211f / 255f, 199f / 255f),
			false));
		dataDomainColorList.add(new Pair<Color, Boolean>(new Color(1f, 1f, 179f / 255f), false));
		dataDomainColorList.add(new Pair<Color, Boolean>(new Color(190f / 255f, 186f / 255f, 218f / 255f),
			false));
		dataDomainColorList.add(new Pair<Color, Boolean>(new Color(251f / 255f, 128f / 255f, 114f / 255f),
			false));
		dataDomainColorList.add(new Pair<Color, Boolean>(new Color(128f / 255f, 177f / 255f, 211f / 255f),
			false));
		dataDomainColorList.add(new Pair<Color, Boolean>(new Color(253f / 255f, 180f / 255f, 98f / 255f),
			false));
		dataDomainColorList.add(new Pair<Color, Boolean>(new Color(179f / 255f, 222f / 255f, 105f / 255f),
			false));
		dataDomainColorList.add(new Pair<Color, Boolean>(new Color(252f / 255f, 205f / 255f, 229f / 255f),
			false));
		dataDomainColorList.add(new Pair<Color, Boolean>(new Color(217f / 255f, 217f / 255f, 217f / 255f),
			false));
		dataDomainColorList.add(new Pair<Color, Boolean>(new Color(188f / 255f, 128f / 255f, 189f / 255f),
			false));
		dataDomainColorList.add(new Pair<Color, Boolean>(new Color(204f / 255f, 235f / 255f, 197f / 255f),
			false));
		dataDomainColorList.add(new Pair<Color, Boolean>(new Color(1f, 237f / 255f, 111f / 255f), false));

		// 141, 211, 199;
		// 255, 255, 179;
		// 190, 186, 218;
		// 251, 128, 114;
		// 128, 177, 211;
		// 253, 180, 98;
		// 179, 222, 105;
		// 252, 205, 229;
		// 217, 217, 217;
		// 188, 128, 189;
		// 204, 235, 197;
		// 255, 237, 111;

		colorLists.put(DATA_DOMAIN_COLORS, dataDomainColorList);
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

	public void addColorToList(String colorListID, Color color, boolean isColorMarked) {
		List<Pair<Color, Boolean>> list = colorLists.get(colorListID);

		if (list == null)
			return;

		list.add(new Pair<Color, Boolean>(color, isColorMarked));
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
