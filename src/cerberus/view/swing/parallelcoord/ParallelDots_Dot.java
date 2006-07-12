/**
 * 
 */
package cerberus.view.swing.parallelcoord;

import java.awt.geom.Point2D;

import java.util.Iterator;
//import java.util.Enumeration;

import  cerberus.view.swing.parallelcoord.AbstractParallelDots;

/**
 * @author kalkusch
 *
 */
public class ParallelDots_Dot extends AbstractParallelDots {

	protected static final float fEpsilon = 0.00001f;
	
	protected Point2D.Float vertex;

	/**
	 * 
	 */
//	public ParallelDots_Dot( final int iDotId ) {	
//		super(iDotId,false);
//		vertex = new  Point2D.Float(-1,-1);
//	}
	
	public ParallelDots_Dot( final int iDotId, float x, float y ) {
		super(iDotId,true);	
		vertex = new Point2D.Float(x,y);
	}
	
	public boolean containsLinkById( final Integer idotId ) {
		return vecDotId.contains( idotId );
	}
	
	public boolean addLinkById( final Integer idotId ) {
		if ( ! vecDotId.contains( idotId ) ) {
			vecDotId.add( idotId );
			return true;
		}
		return false;
	}

	public final Point2D.Float getVertex() {
		return new Point2D.Float(vertex.x, vertex.y );
	}
	
	public final void setVertex( float x, float y) {
		this.vertex.x = x;
		this.vertex.y = y;
	}

//	public final boolean equals(Point2D.Float testVertex) {
//		if (( this.vertex.x == testVertex.x) &&
//				(this.vertex.y == testVertex.y) ){
//			return true;
//		}
//		return false;
//	}
	
	private static final boolean equalEpsilon(final float a, final float b ) {
		if (( b < (a + fEpsilon) ) && ( b > (a - fEpsilon))) {
			return true;
		}
		return false;
	}
	
	public final boolean equals(final float x, final float y) {
		if (( equalEpsilon( this.vertex.x, x )) &&
				( equalEpsilon( this.vertex.y, y )) ){
			return true;
		}
		return false;
	}
	
//	public final boolean equals(ParallelDots_Dot testDot) {		
//		return this.equals( testDot.getVertex() );
//	}
//	
//	public final ParallelDots_Dot equalDot(Point2D.Float testVertex) {
//		if (( this.vertex.x == testVertex.x) &&
//				(this.vertex.y == testVertex.y) ){
//			return this;
//		}
//		return null;
//	}
	
	public final ParallelDots_Dot equalDot(float x, float y) {
		if ( equals(x,y) ) {
			return this;
		}		
		return null;
	}
	
	public Iterator <Integer> getLinkedIdIterator() {
		Iterator <Integer> iter = this.vecDotId.iterator();
		
		return iter;
	}
	
//	public final ParallelDots_Dot equalDot(ParallelDots_Dot testDot) {		
//		return this.equalDot( testDot.getVertex() );
//	}

}
