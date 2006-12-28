/**
 * 
 */
package cerberus.view.swing.graph;

import java.awt.geom.Point2D;

import javax.media.opengl.GL;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class AbstractNode implements NodeInterface {

	protected NodeInterface parent = null;
	
	protected NodeAttributes attributes = null;
	
	protected Point2D.Float vertex = new Point2D.Float();
	
	/**
	 * 
	 */
	public AbstractNode() {
		
	}

	public final Point2D.Float getVertex() {
		return vertex;
	}
	
	public void setVertex( Point2D.Float set ) {
		this.vertex = set;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#getParent()
	 */
	public final NodeInterface getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#hasParent()
	 */
	public final boolean hasParent() {
		return (parent != null);
	}



	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.NodeInterface#setParent(cerberus.view.swing.graph.NodeInterface)
	 */
	public final boolean setParent(NodeInterface parent) {
		
		if ( parent == null ) {
			return false;
		}
		
		this.parent = parent;		
		return true;
	}
	
	public final boolean hasAttributes() {
		if ( attributes ==  null ) {
			return false;
		}
		return true;
	}
	
	public final NodeAttributes getAttributes() {
		return this.attributes;
	}
	
	public final void setAttributes(NodeAttributes attributes) {
		this.attributes = attributes;
	}
	
	public final void renderGL( GL gl, final float fz) {
		
		if ( parent != null ) {
			Point2D.Float parentVertex = parent.getVertex();
			
			gl.glBegin( GL.GL_LINE_STRIP );
			gl.glVertex3f(vertex.x, vertex.y,fz);
			gl.glVertex3f(vertex.x,parentVertex.y,fz);
			gl.glVertex3f(parentVertex.x,parentVertex.y,fz);
			gl.glEnd();
		}
	}

}
