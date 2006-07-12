/**
 * 
 */
package cerberus.view.swing.graph;

import java.awt.Color;

import cerberus.view.swing.graph.NodeRenderingStyle;

/**
 * @author java
 *
 */
public class NodeAttributes {

	public boolean bIsVisible = true;
	
	public Color color = null;
	
	public NodeRenderingStyle style = null;
	
	public int renderStyle = 0;
	
	public String label;
	
	/**
	 * 
	 */
	public NodeAttributes() {
		
	}
	
	public boolean hasenderingStyleChanged() {
		if ( style == null ) {
			return false;
		}
		return true;
	}
	
	public String toString() {
		String result = "(" + label;
		
		if( ! this.bIsVisible ) {
			result += " hide";
		}
		if ( color != null ) {
			result += " " +color.toString();
		} 
		if ( style != null ) {
			result += " style";
		} 
		if ( renderStyle != 0 ) {
			result += " sty=" + renderStyle;
		} 
		result += ")";
		
		return result;
	}

}
