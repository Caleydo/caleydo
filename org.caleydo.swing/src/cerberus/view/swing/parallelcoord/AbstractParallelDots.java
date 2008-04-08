/**
 * 
 */
package cerberus.view.swing.parallelcoord;

import java.util.Vector;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class AbstractParallelDots {

	protected final int iDotId;

	protected boolean bIsNodeNotEdge;
	
	protected Vector<Integer> vecDotId;
	
	protected boolean bIsMarked;
		
	/**
	 * 
	 */
	public AbstractParallelDots( final int iDotId,
			final boolean bIsNodeNotEdge ) {
		this.iDotId = iDotId;
		vecDotId = new Vector <Integer> ();
		this.bIsNodeNotEdge = bIsNodeNotEdge;
	}
	
	protected final void setIsNodeNotEdge(boolean setIsNodeNotEdge ) {
		bIsNodeNotEdge = setIsNodeNotEdge;
	}	
	
	public final int getDotId() {
		return this.iDotId;
	}
	
	public final int getWeight() {
		return this.vecDotId.size();
	}
	
	public final boolean isNode() {
		return bIsNodeNotEdge;
	}
	
	public final boolean isMarked() {
		return this.bIsMarked;
	}
	
	public final void setIsMarked( boolean bSetIsMarked ) {
		this.bIsMarked = bSetIsMarked;
	}
	
	public String toString() {
		String result = "(";			
		
		if ( this.bIsNodeNotEdge ) {
			result += "N";
		} else {
			result += "E";
		}
		
		if ( this.bIsMarked ) {
			result += "m";
		} else {
			result += "-";
		}
		
		result += Integer.toString( iDotId ) + 
			"->" +
			vecDotId.toString() + ")";
		
		return result;
	}
	
	abstract public boolean containsLinkById( final Integer idotId );

	abstract public boolean addLinkById( final Integer idotId );
}
