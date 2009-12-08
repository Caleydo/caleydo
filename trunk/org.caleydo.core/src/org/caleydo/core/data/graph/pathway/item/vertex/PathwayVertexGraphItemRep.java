package org.caleydo.core.data.graph.pathway.item.vertex;

import java.io.Serializable;
import java.util.StringTokenizer;

import org.caleydo.core.data.graph.ACaleydoGraphItem;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.EGraphItemProperty;

/**
 * Pathway vertex representation stored in the overall pathway graph.
 * 
 * @author Marc Streit
 */
public class PathwayVertexGraphItemRep
	extends ACaleydoGraphItem
	implements Serializable {

	private static final long serialVersionUID = 1L;

	private String sName;

	private EPathwayVertexShape shape;

	private short[][] shArCoords;

	private short shWidth = 20;

	private short shHeight = 20;

	/**
	 * Constructor.
	 * 
	 * @param sName
	 * @param sShapeType
	 * @param sCoords
	 */
	public PathwayVertexGraphItemRep(final String sName, final String sShapeType, final String sCoords) {
		super(EGraphItemKind.NODE);

		shape = EPathwayVertexShape.valueOf(sShapeType);
		this.sName = sName;

		setCoordsByCommaSeparatedString(sCoords);
	}

	/**
	 * Constructor.
	 * 
	 * @param sName
	 * @param sShapeType
	 * @param shX
	 * @param shY
	 * @param shWidth
	 * @param shHeight
	 */
	public PathwayVertexGraphItemRep(final String sName, final String sShapeType, final short shX,
		final short shY, final short shWidth, final short shHeight) {
		super(EGraphItemKind.NODE);

		if (sShapeType == null || sShapeType.isEmpty())
			throw new IllegalArgumentException("Shape type for pathway element is not specified.");
		shape = EPathwayVertexShape.valueOf(sShapeType);
		this.sName = sName;
		this.shWidth = shWidth;
		this.shHeight = shHeight;

		setRectangularCoords(shX, shY, shWidth, shHeight);
	}

	/**
	 * Example: 213,521,202,515,248,440,261,447,213,521 Currently used for BioCarta input.
	 */
	private void setCoordsByCommaSeparatedString(final String sCoords) {

		StringTokenizer sToken = new StringTokenizer(sCoords, ",");

		shArCoords = new short[sToken.countTokens() / 2][2];

		int iCount = 0;

		while (sToken.hasMoreTokens()) {
			// Filter white spaces
			short shXCoord = Short.valueOf(sToken.nextToken().replace(" ", "")).shortValue();

			if (!sToken.hasMoreTokens())
				return;

			short shYCoord = Short.valueOf(sToken.nextToken().replace(" ", "")).shortValue();

			shArCoords[iCount][0] = shXCoord;
			shArCoords[iCount][1] = shYCoord;

			iCount++;
		}
	}

	private void setRectangularCoords(final short shX, final short shY, final short shWidth,
		final short shHeight) {

		shArCoords = new short[4][2];

		shArCoords[0][0] = shX;
		shArCoords[0][1] = shY;

		shArCoords[1][0] = (short) (shX + shWidth);
		shArCoords[1][1] = shY;

		shArCoords[2][0] = (short) (shX + shWidth);
		shArCoords[2][1] = (short) (shY + shHeight);

		shArCoords[3][0] = shX;
		shArCoords[3][1] = (short) (shY + shHeight);
	}

	public String getName() {

		return sName;
	}

	public EPathwayVertexShape getShapeType() {

		return shape;
	}

	public EPathwayVertexType getType() {
		return ((PathwayVertexGraphItem) this.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).get(0))
			.getType();
	}

	public short[][] getCoords() {

		return shArCoords;
	}

	public short getXOrigin() {

		return shArCoords[0][0];
	}

	public short getYOrigin() {

		return shArCoords[0][1];
	}

	public short getWidth() {

		return shWidth;
	}

	public short getHeight() {

		return shHeight;
	}

	@Override
	public String toString() {

		return sName;
	}
}
