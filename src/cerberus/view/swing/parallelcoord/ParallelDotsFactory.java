/**
 * 
 */
package cerberus.view.swing.parallelcoord;

import java.awt.Color;

import java.util.Iterator;
import java.util.Vector;
import java.util.Hashtable;

import org.jgraph.graph.DefaultGraphCell;

import cerberus.view.swing.parallelcoord.AbstractParallelDots;

import cerberus.view.swing.parallelcoord.ParallelDots_Dot;
import cerberus.view.swing.parallelcoord.ParallelDots_Line;

import cerberus.view.swing.graph.CerberusGraphViewer;

/**
 * @author kalkusch
 *
 */
public class ParallelDotsFactory {

//	Vector <ParallelDots_Dot> vecDot;
//	Vector <ParallelDots_Line> vecLine;
	
	private Hashtable <Integer,ParallelDots_Dot> vecDot;
	private Hashtable <Integer,ParallelDots_Line> vecLine;
	
	private int iDotId = 1;
	
	private Hashtable < Integer, DefaultGraphCell> vec_GraphCell;
	
	private final CerberusGraphViewer refGraphViewer;
	 
	/**
	 * 
	 */
	public ParallelDotsFactory(CerberusGraphViewer refGraphViewer) {
		this.refGraphViewer = refGraphViewer;
		vecDot= new Hashtable <Integer,ParallelDots_Dot> ();
		vecLine = new Hashtable <Integer,ParallelDots_Line> ();
		
		vec_GraphCell = new Hashtable < Integer, DefaultGraphCell> ();
	}
	
	public ParallelDots_Line addLine( Integer source ) {
		ParallelDots_Line refSource = vecLine.get( source );
		
		if ( refSource == null ) {
			refSource = new ParallelDots_Line( source.intValue() );
			vecLine.put( source, refSource );
		}
		
		return refSource;
	}
	
	public ParallelDots_Line getLine( Integer source ) {
		return vecLine.get( source );
	}
	
	public ParallelDots_Dot getDot( Integer source ) {
		return vecDot.get( source );
	}
	
	public AbstractParallelDots addDotFromLines( Integer source, 
			Integer target,
			float x, 
			float y ) {
		
		if ( source.intValue() == target.intValue() ) {
			return null;
		}
		
		ParallelDots_Dot dot = containsDot(x,y);
		ParallelDots_Line refSource = vecLine.get( source );
		ParallelDots_Line refTarget = vecLine.get( target );
		
		if ( dot == null ) {
			dot = createDot(x,y);
		}
		
		DefaultGraphCell sourceCell;
		DefaultGraphCell targetCell;
		
		if ( refSource == null ) {
			refSource = new ParallelDots_Line( source.intValue() );
			vecLine.put( source, refSource );
						
			sourceCell = refGraphViewer.createVertex("N" + source.toString(),10,30,50,50,Color.CYAN,true);
			vec_GraphCell.put(source,sourceCell);
		} else {
			sourceCell = vec_GraphCell.get( source );
		}
		
		if ( refTarget == null ) {
			refTarget = new ParallelDots_Line( target.intValue() );
			vecLine.put( target, refTarget );
			
			targetCell = refGraphViewer.createVertex("N" + target.toString(),10,30,50,50,Color.ORANGE,true); 
			vec_GraphCell.put(target,targetCell);
		} else {
			targetCell = vec_GraphCell.get( target );
		}
		
		Integer dotIdINT = new Integer(dot.getDotId());
		
//		if ( ( refSource.addLinkById( dotIdINT ) ) &&
//				( refTarget.addLinkById( dotIdINT ) )) {
//			
//			if (( sourceCell != null ) && ( targetCell != null ) ) {
//				refGraphViewer.createEdge( sourceCell, targetCell );
//			}
//		}
		
		dot.addLinkById(source);
		dot.addLinkById(target);
		
		System.out.println("  sc: " + dot.toString() + 
				"\n      |--> " + refSource.toString() + 
				" -->|" + refTarget.toString() );
					
		return dot;		
	}
	
	public ParallelDots_Dot containsDot( float x, float y ) {
		Iterator <ParallelDots_Dot> iter = 
			vecDot.values().iterator();
		
		while ( iter.hasNext() ) {
			ParallelDots_Dot dot = iter.next();
			if ( dot.equals( x, y ) ) {
				return dot;
			}
		}		
		return null;
	}
	
	public synchronized ParallelDots_Dot createDot(final float x, final float y ) {
		ParallelDots_Dot addedDot = new ParallelDots_Dot(iDotId,x,y);
		vecDot.put( new Integer(iDotId), addedDot );
		iDotId++;		
		return addedDot;
	}
	
	public void clearAll() {
		vecDot.clear();
		vecLine.clear();
		
		refGraphViewer.cleanGraph();
	}

	public void registerAllDots() {
		System.out.println("  --- REG all DOTS! ----" );
		
		Iterator <ParallelDots_Dot> iter = this.vecDot.values().iterator();
		
		while ( iter.hasNext() ) {
			ParallelDots_Dot dot = iter.next();
			
			DefaultGraphCell dotCell = 
				refGraphViewer.createVertex("P" + 
						Integer.toString( dot.getDotId() ) + " " + 
						Integer.toString( dot.getWeight() ),
					10,10,25,10,Color.RED, true);
			
			Iterator <Integer> iterDot = dot.getLinkedIdIterator();
			
			while ( iterDot.hasNext() ) {			
				DefaultGraphCell lineCell = vec_GraphCell.get( iterDot.next() );
								
				if ( lineCell != null) {
					this.refGraphViewer.createEdge( dotCell, lineCell );
				} else {
					System.err.println("ERROR line-cell is null");
				}
				
			}
		}
	}
	
	public String toString() {
		String result;
		
		result = "LINE: " + this.vecLine.toString() + "\n";
		result += "DOT : " + this.vecDot.toString();
		
		return result;
	}
}
