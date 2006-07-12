package cerberus.view.swing.graph;

import java.awt.geom.Point2D;

import javax.media.opengl.GL;

import cerberus.view.swing.graph.NodeAttributes;
import cerberus.view.swing.graph.NodeIndexDataInterface;

public interface NodeInterface extends NodeIndexDataInterface {

	public Point2D.Float getVertex();
	
	public void setVertex( Point2D.Float set );
	
	public static final String INDENT_PER_LEVEL = "| ";

	public void startChildIteration();
	
	public NodeInterface getNextChild();
	
	public NodeInterface getParent();
	
	public boolean hasChild(final NodeInterface testChild);
	
	public boolean hasParent();
	
	public boolean addChild(NodeInterface addChild);
	
	public boolean removeChild(NodeInterface addChild);
	
	public boolean removeAllChildren();
	
	public boolean setParent(final NodeInterface parent);
	
	public int size();
	
	public void renderGL( GL gl, float fz );
	
	public boolean hasAttributes();
	
	public NodeAttributes getAttributes();
	
	public void setAttributes(NodeAttributes attributes);
	
	public String toStringRecursively(String indent);
}
