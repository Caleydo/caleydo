package org.caleydo.core.util.mapping.color;

import java.util.ArrayList;
import java.util.Collections;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.conversion.ConversionTools;
import org.eclipse.jface.preference.PreferenceStore;

/**
 * Color mapping. The class is initialized with a list of inflection points and an associated color. A color
 * mapping for values between 0 and 1 based on the provided points is accessible.
 * 
 * @author Alexander Lex
 */
@XmlType
public class ColorMapping {

	ArrayList<float[]> colorList;
	ArrayList<ColorMarkerPoint> markerPoints;
	EColorMappingType colorMappingType;

	float[] notANumberColor = { 0, 0, 1 };

	@XmlTransient
	public static final int COLOR_DEPTH = 256;

	/**
	 * Default no-arg constructor, especially needed for xml-serialization.
	 */
	public ColorMapping() {

	}

	/**
	 * <p>
	 * Constructor. Provide a list of {@link ColorMarkerPoint} where the first has the smallest value, and
	 * each next point has a bigger value. These color points work as inflection points. Between two adjacent
	 * points the colors are interpolated.
	 * </p>
	 * <p>
	 * Additionally the color marker points have spreads - which signal an area of constant color. For example
	 * if a marker point has a value of 0.5 and a left spread of 0.1 and a right spread of 0.2 then the region
	 * between 0.4 and 0.7 is in the constant color of the marker point. Only at the end of the spreads the
	 * interpolation to the next color begins.
	 * </p>
	 * 
	 * @param alMarkerPoints
	 * @throws IllegalArgumentException
	 *             if values in marker points are not increasing, or if fvalue > 1 || fvalue < 0
	 */
	protected ColorMapping(EColorMappingType colorMappingType, ArrayList<ColorMarkerPoint> alMarkerPoints) {
		this.colorMappingType = colorMappingType;
		init(alMarkerPoints);
	}

	protected ColorMapping(EColorMappingType colorMappingType) {
		this.colorMappingType = colorMappingType;
		initiFromPreferenceStore();
	}

	/**
	 * Reset the color mapping, same principles as constructor {@link ColorMapping#ColorMapping(ArrayList)}
	 * 
	 * @param alMarkerPoints
	 */
	protected void resetColorMapping(ArrayList<ColorMarkerPoint> alMarkerPoints) {
		init(alMarkerPoints);
	}

	private void init(ArrayList<ColorMarkerPoint> alMarkerPoints) {
		this.markerPoints = alMarkerPoints;
		colorList = new ArrayList<float[]>(COLOR_DEPTH);
		for (int iCount = 0; iCount < COLOR_DEPTH; iCount++) {
			colorList.add(new float[3]);
		}

		setUpMapping();
		notANumberColor =
			ConversionTools.getFloatColorFromString(GeneralManager.get().getPreferenceStore()
				.getString(PreferenceConstants.NAN_COLOR));
	}

	/**
	 * Initializes a gene expression color mapping from values stored in the preference store. Sets all
	 * display list to dirty to have immediate effect.
	 */
	public void initiFromPreferenceStore() {
		PreferenceStore store = GeneralManager.get().getPreferenceStore();
		int iNumberOfMarkerPoints =
			store.getInt(colorMappingType + "_" + PreferenceConstants.NUMBER_OF_COLOR_MARKER_POINTS);

		ArrayList<ColorMarkerPoint> alMarkerPoints = new ArrayList<ColorMarkerPoint>();
		for (int iCount = 1; iCount <= iNumberOfMarkerPoints; iCount++) {
			float colorMarkerValue =
				store
					.getFloat(colorMappingType + "_" + PreferenceConstants.COLOR_MARKER_POINT_VALUE + iCount);
			String color =
				store.getString(colorMappingType + "_" + PreferenceConstants.COLOR_MARKER_POINT_COLOR
					+ iCount);
			float fLeftSpread =
				store.getFloat(colorMappingType + "_" + PreferenceConstants.COLOR_MARKER_POINT_LEFT_SPREAD
					+ iCount);
			float fRightSpread =
				store.getFloat(colorMappingType + "_" + PreferenceConstants.COLOR_MARKER_POINT_RIGHT_SPREAD
					+ iCount);

			ColorMarkerPoint point =
				new ColorMarkerPoint(colorMarkerValue, ConversionTools.getFloatColorFromString(color));

			if (Float.compare(fLeftSpread, 0.0f) > 0)
				point.setLeftSpread(fLeftSpread);
			if (Float.compare(fRightSpread, 0.0f) > 0)
				point.setRightSpread(fRightSpread);

			alMarkerPoints.add(point);
		}

		init(alMarkerPoints);

	}

	/**
	 * Writes the color values of the current mapping to the preference store
	 */
	public void writeToPrefStore() {

		PreferenceStore store = GeneralManager.get().getPreferenceStore();
		int iCount = 1;
		for (ColorMarkerPoint point : markerPoints) {
			store.setValue(colorMappingType + "_" + PreferenceConstants.COLOR_MARKER_POINT_VALUE + iCount,
				point.getValue());
			store.setValue(colorMappingType + "_" + PreferenceConstants.COLOR_MARKER_POINT_RIGHT_SPREAD
				+ iCount, point.getRightSpread());
			store.setValue(colorMappingType + "_" + PreferenceConstants.COLOR_MARKER_POINT_LEFT_SPREAD
				+ iCount, point.getLeftSpread());
			iCount++;
			store.setValue(colorMappingType + "_" + PreferenceConstants.NUMBER_OF_COLOR_MARKER_POINTS,
				markerPoints.size());
		}
	}

	/**
	 * Initialize the color mapping
	 * 
	 * @param markerPoints
	 *            the marker points
	 */
	private void setUpMapping() {
		Collections.sort(markerPoints);
		ArrayList<ColorMarkerPoint> alFinalMarkerPoints = considerSpread();
		float fSrcValue, fDestValue;

		for (int iCount = 0; iCount < alFinalMarkerPoints.size() - 1; iCount++) {
			fSrcValue = alFinalMarkerPoints.get(iCount).getValue();
			fDestValue = alFinalMarkerPoints.get(iCount + 1).getValue();

			if (fDestValue < fSrcValue)
				throw new IllegalArgumentException("Marker points values have to be increasing in size, "
					+ "but this was not the case");

			float[] fSrcColor = alFinalMarkerPoints.get(iCount).getColor();
			float[] fDestColor = alFinalMarkerPoints.get(iCount + 1).getColor();

			int iSrcIndex = (int) (fSrcValue * (COLOR_DEPTH - 1));
			int iDestIndex = (int) (fDestValue * (COLOR_DEPTH - 1));

			int iColorRange = iDestIndex - iSrcIndex;

			for (int iInnerCount = 0; iInnerCount <= iColorRange; iInnerCount++) {
				float[] fColor = colorList.get(iSrcIndex + iInnerCount);
				float fDivisor = (float) iColorRange / (float) iInnerCount;
				fColor[0] = fSrcColor[0] + (fDestColor[0] - fSrcColor[0]) / fDivisor;
				fColor[1] = fSrcColor[1] + (fDestColor[1] - fSrcColor[1]) / fDivisor;
				fColor[2] = fSrcColor[2] + (fDestColor[2] - fSrcColor[2]) / fDivisor;
			}
		}
	}

	/**
	 * Return the mapped color for any value between 0 and 1
	 * 
	 * @param fValue
	 * @return float array with length 3, RGB
	 * @throws IllegalArgumentException
	 *             if fvalue > 1 || fvalue < 0
	 */
	public float[] getColor(float fValue) {
		if (Float.isNaN(fValue))
			return notANumberColor;

		if (fValue > 1 || fValue < 0)
			throw new IllegalArgumentException("Invalid value in fValue. Has to be between 0 and 1");

		return colorList.get((int) (fValue * (COLOR_DEPTH - 1)));
	}

	/**
	 * Get the marker points on which the color mapping is based
	 * 
	 * @return the list of marker points
	 */
	public ArrayList<ColorMarkerPoint> getMarkerPoints() {
		return markerPoints;
	}

	/**
	 * Returns the list of marker points, but with spread converted to a separate marker point. This means
	 * that a marker point at 0.5 with a left spread of 0.1 will result in two marker points, one with 0.4 and
	 * one with 0.5 of the same color in this list
	 * 
	 * @return the list of marker points without spreads but points for spreads
	 */
	public ArrayList<ColorMarkerPoint> getConvertedMarkerPoints() {
		return considerSpread();
	}

	public void update() {
		setUpMapping();
	}

	/**
	 * Converts the spread in color marker points to separate color points, which are easier to map later.
	 * Does some checking and error handling.
	 * 
	 * @return the list with all the marker points instead of spreads
	 */
	private ArrayList<ColorMarkerPoint> considerSpread() {
		ArrayList<ColorMarkerPoint> alFinalColorMarkerPoints = new ArrayList<ColorMarkerPoint>();

		for (ColorMarkerPoint point : markerPoints) {
			if (point.hasLeftSpread()) {
				float fLeftValue = point.getValue() - point.getLeftSpread();
				alFinalColorMarkerPoints.add(new ColorMarkerPoint(fLeftValue, point.getColor()));
			}
			alFinalColorMarkerPoints.add(point);
			if (point.hasRightSpread()) {
				float fRightValue = point.getValue() + point.getRightSpread();
				alFinalColorMarkerPoints.add(new ColorMarkerPoint(fRightValue, point.getColor()));
			}
		}

		return alFinalColorMarkerPoints;
	}

	public ArrayList<float[]> getColorList() {
		return colorList;
	}

	public void setColorList(ArrayList<float[]> colorList) {
		this.colorList = colorList;
	}

	public EColorMappingType getColorMappingType() {
		return colorMappingType;
	}

	public void setColorMappingType(EColorMappingType colorMappingType) {
		this.colorMappingType = colorMappingType;
	}

	public float[] getNotANumberColor() {
		return notANumberColor;
	}

	public void setNotANumberColor(float[] notANumberColor) {
		this.notANumberColor = notANumberColor;
	}

	public void setMarkerPoints(ArrayList<ColorMarkerPoint> markerPoints) {
		this.markerPoints = markerPoints;
	}

}
