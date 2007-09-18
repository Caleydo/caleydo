/**
 * 
 */
package cerberus.view.swing.parallelcoord;

import  cerberus.view.swing.parallelcoord.AbstractParallelDots;

/**
 * @author Michael Kalkusch
 *
 */
public class ParallelDots_Line extends AbstractParallelDots {
	
	/**
	 * 
	 */
	public ParallelDots_Line( final int iDotId ) {
		super(iDotId,true);
		
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

}
 