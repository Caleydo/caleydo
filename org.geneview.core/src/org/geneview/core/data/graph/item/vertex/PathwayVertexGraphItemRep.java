package org.geneview.core.data.graph.item.vertex;

import java.util.StringTokenizer;

import org.geneview.core.util.system.StringConversionTool;
import org.geneview.util.graph.EGraphItemKind;
import org.geneview.util.graph.EGraphItemProperty;
import org.geneview.util.graph.item.GraphItem;

public class PathwayVertexGraphItemRep 
extends GraphItem {

	private String sName;
	
	private EPathwayVertexShape shape;
	
	private short[][] shArCoords;
	
	private short shWidth = 20;
	
	private short shHeight = 20;

	/**
	 * Constructor. 
	 * 
	 * @param iId
	 * @param sName
	 * @param sShapeType
	 * @param sCoords
	 */
	public PathwayVertexGraphItemRep(
			final int iId,
			final String sName,
			final String sShapeType,
			final String sCoords) {

		super(iId, EGraphItemKind.NODE);
		
		shape = EPathwayVertexShape.valueOf(sShapeType);
		this.sName = sName;
		
		setCoordsByCommaSeparatedString(sCoords);
	}
	
	/**
	 * 
	 * Constructor.
	 * 
	 * @param iId
	 * @param sName
	 * @param sShapeType
	 * @param shX
	 * @param shY
	 * @param shWidth
	 * @param shHeight
	 */
	public PathwayVertexGraphItemRep(
			final int iId,
			final String sName,
			final String sShapeType,
			final short shX,
			final short shY,
			final short shWidth,
			final short shHeight) {
		
		super(iId, EGraphItemKind.NODE);
		
		shape = EPathwayVertexShape.valueOf(sShapeType);
		this.sName = sName;
		this.shWidth = shWidth;
		this.shHeight = shHeight;
		
		setRectangularCoords(shX, shY, shWidth, shHeight);
	}
	
	/**
	 * Example: 213,521,202,515,248,440,261,447,213,521
	 * Currently used for BioCarta input.
	 */
	private void setCoordsByCommaSeparatedString(final String sCoords) {
		
		StringTokenizer sToken = 
			new StringTokenizer(sCoords, ",");

		shArCoords = new short[sToken.countTokens() / 2][2];
		
		int iCount = 0;
		
		while(sToken.hasMoreTokens()) 
		{			
			short shXCoord = (short) StringConversionTool.convertStringToInt( 
					sToken.nextToken(), 0);
			
			if (!sToken.hasMoreTokens())
				return;
			
			short shYCoord = (short) StringConversionTool.convertStringToInt( 
					sToken.nextToken(), 0);
			
			shArCoords[iCount][0] = shXCoord;
			shArCoords[iCount][1] = shYCoord;
			          
			iCount++;         
		}
	}
	
	private void setRectangularCoords(final short shX, 
			final short shY, 
			final short shWidth,
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
	
	public PathwayVertexGraphItem getPathwayVertexGraphItem() {
		
		return ((PathwayVertexGraphItem)this.getAllItemsByProp(
				EGraphItemProperty.ALIAS_PARENT).toArray()[0]);
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
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		return sName;
	}
}
