/**
 * 
 */
package cerberus.view.swing.graph.parser;

import org.xml.sax.Attributes;

import cerberus.view.swing.graph.DualNode;
import cerberus.view.swing.graph.SingleIndexDataNode;
import cerberus.view.swing.graph.NodeAttributes;

/**
 * @author java
 *
 */
public class NodeSaxDefaultHandler extends AbstractSaxDefaultHandler {

	protected DualNode rootNode = null;
	
	protected DualNode currentNode = null;
	
	private boolean bGraphSection = false;
	
	private boolean bDataSection = false;
	
	protected String sGraphSectionTag = "graph";
	
	protected String sNodeSectionTag = "Node";
	protected String sDataSectionTag = "Data";
	
	/**
	 * 
	 */
	public NodeSaxDefaultHandler() {
		super();
	}

	/**
	 * @param bEnableHaltOnParsingError
	 */
	public NodeSaxDefaultHandler(boolean bEnableHaltOnParsingError) {
		super(bEnableHaltOnParsingError);
	}
	
	public DualNode getRootNode () {
		return this.rootNode;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.parser.AbstractSaxDefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if ( qName.equalsIgnoreCase( sGraphSectionTag )) {
			if ( bGraphSection ) {
				this.appandErrorMsg("open <" + qName + 
						"> inside opened section!");
			} else {
				bGraphSection = true;
			}
			return;
		}		

		if ( bDataSection) {
			appandErrorMsg("<" + sDataSectionTag + 
					"> must not contain nested tags! Ignore node.");
			return;
		}
		
		if ( bGraphSection ) {
			
			/**
			 * <node>
			 */
			if ( qName.equalsIgnoreCase( sNodeSectionTag ) ) {
				
				DualNode insertNode = new DualNode();
				
				if ( attributes.getLength() > 0 ) {
					
					NodeAttributes attrib = new NodeAttributes();
					
					String param = attributes.getValue("label");
					if ( param != null ) {
						attrib.label = param;	
					}
					
					param = attributes.getValue("style");					
					if ( param!= null ) {
						attrib.renderStyle = parseStringToInt( param );	
					}
					
					insertNode.setAttributes( attrib );
				}
				
				if ( currentNode == null) {
					currentNode = insertNode;
									
					if ( rootNode == null ) {
						rootNode = currentNode;					
					} else {
						currentNode.setParent( rootNode );
					}
				} else {
					
					System.out.println("  INSERT: " + currentNode.toString() );
					
					if ( currentNode.isLeftNodeSet() ) {
						if ( currentNode.isRightNodeSet() ) {
							this.appandErrorMsg("Error while opening <" +
									sNodeSectionTag + 
									"> because both two nodes have been assigned! SKIP Node!");
						} else {
							currentNode.addChild( insertNode );
							insertNode.setParent( currentNode );
							currentNode = insertNode;
							return;
						}
					} else {
						currentNode.addChild( insertNode );
						insertNode.setParent( currentNode );
						currentNode = insertNode;
						return;
					}
				} //end: if ( currentNode == null) {..}else{..
				
			} // end: if ( qName.equalsIgnoreCase( sNodeSectionTag ) )
			else if ( qName.equalsIgnoreCase( sDataSectionTag ) ) {
				
				/**
				 * <data>
				 */
				/* avoid <data> node as root node! */
				if ( currentNode == null) {
					appandErrorMsg("<" + sDataSectionTag + 
							"> must not be a root node! RootNode must be a <" +
							sNodeSectionTag + "> node!");
				}
				
				int iIndex = parseStringToInt( 
						attributes.getValue("index") );
				
				if ( iIndex < 0 ) {
					appandErrorMsg("<" + sDataSectionTag + 
							"> value of key index must be a positive value");
				}
				
				SingleIndexDataNode dataNode = 
					new SingleIndexDataNode(iIndex);
				
				bDataSection = true;
				
				if ( ! currentNode.isLeftNodeSet() ) {
					currentNode.addChild( dataNode );
				}
				else if ( ! currentNode.isRightNodeSet() ) {
					currentNode.addChild( dataNode );
				}
				else  
				{
					appandErrorMsg("Parent <" + sNodeSectionTag
							+ "> has already two child nodes!");
					dataNode = null;
					return;
				}
				
				dataNode.setParent( currentNode );
				
			}//end: else if ( qName.equalsIgnoreCase( sDateSectionTag ) ) {...}
			
		} //end: if ( bGraphSection ) {

	}

	/* (non-Javadoc)
	 * @see cerberus.view.swing.graph.parser.AbstractSaxDefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) {
		if ( qName.equalsIgnoreCase( sGraphSectionTag )) {
			if ( bGraphSection ) {
				bGraphSection = false;
			} else {
				this.appandErrorMsg("close </" + qName + 
						"> without opening section!");				
			}
			
			return;
		}
		
		if ( bGraphSection ) {
			if ( qName.equalsIgnoreCase( sNodeSectionTag ) ) {
				if ( currentNode == null ) {
					appandErrorMsg("close </" +
							sNodeSectionTag +
							"> before creating a node!");
					return;		
				}
				else {				
					currentNode = (DualNode) currentNode.getParent();
				}
			}
			else if ( qName.equalsIgnoreCase( sDataSectionTag ) ) {
				
				if ( bDataSection ) {
					bDataSection = false;
					return;
				} else {
					appandErrorMsg("close </" +
							sDataSectionTag +
							"> before opening the tag!");
					return;		
				}
				
			}
		} //end: if ( bGraphSection ) {..
	}

}
