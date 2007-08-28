package cerberus.parser.xml.sax.handler.kegg;

import java.util.HashMap;

import org.geneview.graph.EGraphItemProperty;
import org.geneview.graph.IGraph;
import org.geneview.graph.IGraphItem;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import cerberus.data.graph.item.edge.PathwayReactionEdgeGraphItem;
import cerberus.data.graph.item.vertex.PathwayVertexGraphItem;
import cerberus.data.graph.item.vertex.PathwayVertexGraphItemRep;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IXmlParserManager;
import cerberus.parser.xml.sax.handler.AXmlParserHandler;
import cerberus.parser.xml.sax.handler.IXmlParserHandler;

public class KgmlSaxHandler 
extends AXmlParserHandler 
implements IXmlParserHandler {
	
	private Attributes attributes;
	
	private String sAttributeName = "";
	
	private HashMap<Integer, IGraphItem> hashKgmlEntryIdToVertexRepId;
	
	private HashMap<String, IGraphItem> hashKgmlNameToVertexRepId;
	
	private HashMap<String, IGraphItem> hashKgmlReactionIdToVertexRepId;	
	
	private IGraph currentPathway;
	
	private IGraphItem currentVertex;
	
	private IGraphItem currentReactionSubstrateEdgeRep;
	
	private IGraphItem currentReactionProductEdgeRep;
	
	private int iCurrentEntryId;
	
	public KgmlSaxHandler(  final IGeneralManager refGeneralManager,
			final IXmlParserManager refXmlParserManager)
	{		
		super( refGeneralManager, refXmlParserManager);
		
		hashKgmlEntryIdToVertexRepId = new HashMap<Integer, IGraphItem>();
			
		hashKgmlNameToVertexRepId = new HashMap<String, IGraphItem>();
		
		hashKgmlReactionIdToVertexRepId = new HashMap<String, IGraphItem>();
		
		setXmlActivationTag("pathway");
	}
	
    public void startElement(String namespaceURI,
            String sSimpleName,
            String sQualifiedName,
            Attributes attributes)
    throws SAXException	
    {
    	String sElementName = sSimpleName;
    	this.attributes = attributes;
    	
    	if ("".equals(sElementName)) 
    	{
    		sElementName = sQualifiedName; // namespaceAware = false
    	}
    	
		if (attributes != null) 
		{
			if (sElementName.equals("pathway"))
				handlePathwayTag();
			else if (sElementName.equals("entry"))
				handleEntryTag();
			else if (sElementName.equals("graphics"))
				handleGraphicsTag();
			else if (sElementName.equals("relation"))
				handleRelationTag();
//			else if (sElementName.equals("subtype"))
//				handleSubtypeTag();
			else if (sElementName.equals("reaction"))
				handleReactionTag();
			else if (sElementName.equals("product"))
				handleReactionProductTag();
			else if (sElementName.equals("substrate"))
				handleReactionSubstrateTag();
		}
    }

	public void endElement(String namespaceURI,
          String sSimpleName,
          String sQualifiedName)
	throws SAXException {
		
		String eName = ("".equals(sSimpleName)) ? sQualifiedName : sSimpleName;
		
		if (null != eName) {
			if (eName.equals(sOpeningTag)) {	
				/**
				 * section (xml block) finished, call callback function from IXmlParserManager
				 */
				refXmlParserManager.sectionFinishedByHandler( this );
			}
		}
	}
    
	/**
	 * Reacts on the elements of the pathway tag.
	 * 
	 * An example pathway tag looks like this:
	 *     <pathway name="path:map00271" 
     *	org="map" number="00271" 
     *	title="Methionine metabolism" 
     *	image="http://www.genome.jp/kegg/pathway/map/map00271.gif" 
     *	link="http://www.genome.jp/dbget-bin/show_pathway?map00271">
	 */
    protected void handlePathwayTag() {
    	
    	String sName = "";
    	String sTitle = "";
    	String sImageLink = "";
    	String sExternalLink = "";
    	int iKeggId = 0;

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) 
		{
			 sAttributeName = attributes.getLocalName(iAttributeIndex);
		
			if ("".equals(sAttributeName))
			{
				sAttributeName = attributes.getQName(iAttributeIndex);
			}
				
			if (sAttributeName.equals("name"))
			{
				sName = attributes.getValue(iAttributeIndex); 
			}
			else if (sAttributeName.equals("title"))
			{
				sTitle = attributes.getValue(iAttributeIndex); 
			}
			else if (sAttributeName.equals("number"))
			{
				iKeggId = new Integer(attributes.getValue(iAttributeIndex)); 
			}
   			else if (sAttributeName.equals("image"))
   			{
   				sImageLink = attributes.getValue(iAttributeIndex); 
   			}
    		else if (sAttributeName.equals("link"))
    		{
    			sExternalLink = attributes.getValue(iAttributeIndex); 
    		}			
   		}
		
		currentPathway = refGeneralManager.getSingelton().getPathwayManager().
			createPathway(iKeggId, sName, sTitle, sImageLink, sExternalLink);
    }
    
	/**
	 * Reacts on the elements of the entry tag.
	 * 
	 * An example entry tag looks like this:
	 * <entry id="1" name="ec:1.8.4.1" type="enzyme" 
	 * reaction="rn:R01292" link="http://www.genome.jp/dbget-bin/www_bget?enzyme+1.8.4.1">
	 */
    protected void handleEntryTag() {
    	
    	int iEntryId = 0;
    	String sName = "";
    	String sType = "";
    	String sExternalLink = "";
    	String sReactionId = "";
	
    	for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) 
	   	{
		   sAttributeName = attributes.getLocalName(iAttributeIndex);
	
		   if ("".equals(sAttributeName))
		   {
			   sAttributeName = attributes.getQName(iAttributeIndex);
		   }
			
		   if (sAttributeName.equals("id"))
		   {
			   iEntryId = new Integer(attributes.getValue(iAttributeIndex)); 
		   }
		   else if (sAttributeName.equals("name"))
		   {
			   sName = attributes.getValue(iAttributeIndex); 
		   }
		   else if (sAttributeName.equals("type"))
		   {
			   sType = attributes.getValue(iAttributeIndex); 
		   }
		   else if (sAttributeName.equals("link"))
		   {
			   sExternalLink = attributes.getValue(iAttributeIndex);
		   }
		   else if (sAttributeName.equals("reaction"))
		   {
			   sReactionId = attributes.getValue(iAttributeIndex);
		   }
	   	}
    	
    	iCurrentEntryId = iEntryId;
    	currentVertex = refGeneralManager.getSingelton().getPathwayItemManager()
    		.createVertex(sName, sType, sExternalLink, sReactionId);
    }
	
	/**
	 * Reacts on the elements of the graphics tag.
	 * 
	 * An example graphics tag looks like this:
	 * <graphics name="1.8.4.1" fgcolor="#000000" 
	 * bgcolor="#FFFFFF" type="rectangle" 
	 * x="142" y="304" width="45" height="17"/>
	 */
    protected void handleGraphicsTag() {
    	
		String sName = "";
		String sShapeType = "";
		int iHeight = 0;
		int iWidth = 0;
		int iXPosition = 0;
		int iYPosition = 0;

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) 
		{
			 sAttributeName = attributes.getLocalName(iAttributeIndex);
		
			if ("".equals(sAttributeName))
			{
				sAttributeName = attributes.getQName(iAttributeIndex);
			}
				
			if (sAttributeName.equals("name"))
			{
				sName = attributes.getValue(iAttributeIndex); 
			}
   			else if (sAttributeName.equals("height"))
   			{
   				iHeight = new Integer(attributes.getValue(iAttributeIndex)); 
   			}
   			else if (sAttributeName.equals("width"))
   			{
   				iWidth = new Integer(attributes.getValue(iAttributeIndex)); 
   			}
    		else if (sAttributeName.equals("x"))
    		{
    			iXPosition = new Integer(attributes.getValue(iAttributeIndex)); 
    		}
   			else if (sAttributeName.equals("y"))
   			{
   				iYPosition = new Integer(attributes.getValue(iAttributeIndex)); 
   			}
   			else if (sAttributeName.equals("type"))
   			{
   				sShapeType = attributes.getValue(iAttributeIndex);
   			}
   		}

		IGraphItem vertexRep = refGeneralManager.getSingelton()
			.getPathwayItemManager().createVertexRep(
				currentPathway, 
				currentVertex, 
				sName, 
				sShapeType, 
				iHeight, 
				iWidth, 
				iXPosition, 
				iYPosition);
    	
    	hashKgmlEntryIdToVertexRepId.put(iCurrentEntryId, vertexRep);
    	hashKgmlNameToVertexRepId.put(((PathwayVertexGraphItem)currentVertex).getName(), vertexRep);
    	hashKgmlReactionIdToVertexRepId.put(((PathwayVertexGraphItem)currentVertex).getReactionId(), vertexRep);
    }
    
	/**
	 * Reacts on the elements of the relation tag.
	 * 
	 * An example relation tag looks like this:
	 * <relation entry1="28" entry2="32" type="ECrel">
	 */
    protected void handleRelationTag() {
    	
    	int iSourceVertexId = 0;
    	int iTargetVertexId = 0;
    	String sType = "";
		
		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) 
		{
			 sAttributeName = attributes.getLocalName(iAttributeIndex);
		
			if ("".equals(sAttributeName))
			{
				sAttributeName = attributes.getQName(iAttributeIndex);
			}
				
			if (sAttributeName.equals("type"))
				sType = attributes.getValue(iAttributeIndex); 
   			else if (sAttributeName.equals("entry1"))
  				iSourceVertexId = new Integer(attributes.getValue(iAttributeIndex)); 
   			else if (sAttributeName.equals("entry2"))
    			iTargetVertexId = new Integer(attributes.getValue(iAttributeIndex)); 
			
   			//System.out.println("Attribute name: " +sAttributeName);
   			//System.out.println("Attribute value: " +attributes.getValue(iAttributeIndex));
   		}  	    	

		IGraphItem graphItemIn = hashKgmlEntryIdToVertexRepId.get(iSourceVertexId);
		IGraphItem graphItemOut = hashKgmlEntryIdToVertexRepId.get(iTargetVertexId);
		
		// Create edge (data)
		IGraphItem relationEdge = refGeneralManager.getSingelton().getPathwayItemManager().
			createRelationEdge(((PathwayVertexGraphItemRep)graphItemIn).getPathwayVertexGraphItem(), 
					((PathwayVertexGraphItemRep)graphItemOut).getPathwayVertexGraphItem(), sType);
    
		// Create edge representation
		refGeneralManager.getSingelton().getPathwayItemManager().
			createRelationEdgeRep(currentPathway, relationEdge, graphItemIn, graphItemOut);
		
    }
//    	
//    protected void handleSubtypeTag() {
//    	
//    	String sName = "";
//    	int iCompoundId = 0;
//		
//		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) 
//		{
//			 sAttributeName = attributes.getLocalName(iAttributeIndex);
//		
//			if ("".equals(sAttributeName))
//			{
//				sAttributeName = attributes.getQName(iAttributeIndex);
//			}
//				
//			if (sAttributeName.equals("name"))
//				sName = attributes.getValue(iAttributeIndex); 
//   			else if (sAttributeName.equals("value"))
//   			{
//   				// TODO: handle special case of value "-->" in signalling pathways
//   				if (attributes.getValue(iAttributeIndex).contains("-") || 
//   						attributes.getValue(iAttributeIndex).contains("=") ||
//   						attributes.getValue(iAttributeIndex).contains("+") ||
//   						attributes.getValue(iAttributeIndex).contains(":") ||
//   						attributes.getValue(iAttributeIndex).contains("."))
//   					iCompoundId = 0;
//   				else
//   	  				iCompoundId = new Integer(attributes.getValue(iAttributeIndex)); 
//   			}
//
// 
//   			//System.out.println("Attribute name: " +sAttributeName);
//   			//System.out.println("Attribute value: " +attributes.getValue(iAttributeIndex));
//   		}  	
//		
//		if (sName.equals("compound"))
//		{
//			//retrieve the internal element ID and add the compound value to the edge
//    		refGeneralManager.getSingelton().getPathwayElementManager().
//				addRelationCompound(kgmlIdToElementIdLUT.get(iCompoundId));
//		}
//    }
//    
	/**
	 * Reacts on the elements of the reaction tag.
	 * 
	 * An example reaction tag looks like this:
	 * <reaction name="rn:R01001" type="irreversible">
	 */
    protected void handleReactionTag() {
    	
    	String sReactionName = "";
    	String sReactionType = "";
    	
		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) 
		{
			 sAttributeName = attributes.getLocalName(iAttributeIndex);
		
			if ("".equals(sAttributeName))
			{
				sAttributeName = attributes.getQName(iAttributeIndex);
			}
				
			if (sAttributeName.equals("type"))
			{
				sReactionType = attributes.getValue(iAttributeIndex); 				
			}
			else if (sAttributeName.equals("name"))
			{
				sReactionName = attributes.getValue(iAttributeIndex); 	
			}
		}  	
		
		currentReactionSubstrateEdgeRep = refGeneralManager.getSingelton().getPathwayItemManager().
			createReactionEdge(currentPathway, sReactionName, sReactionType);

		currentReactionProductEdgeRep = refGeneralManager.getSingelton().getPathwayItemManager().
			createReactionEdge(currentPathway, sReactionName, sReactionType);
    
    }
    
	/**
	 * Reacts on the elements of the reaction substrate tag.
	 * 
	 * An example reaction substrate tag looks like this:
	 * <substrate name="cpd:C01118"/>
	 */
    protected void handleReactionSubstrateTag() {
    	
    	String sReactionSubstrateName = "";
    	
    	for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) 
		{
			 sAttributeName = attributes.getLocalName(iAttributeIndex);
		
			if ("".equals(sAttributeName))
			{
				sAttributeName = attributes.getQName(iAttributeIndex);
			}
				
			if (sAttributeName.equals("name"))
			{
				sReactionSubstrateName = attributes.getValue(iAttributeIndex); 
			}
		}  	
    	
    	IGraphItem graphItemIn = 
    		hashKgmlNameToVertexRepId.get(sReactionSubstrateName);
    	
    	IGraphItem graphItemOut =
    		hashKgmlReactionIdToVertexRepId.get(
    				((PathwayReactionEdgeGraphItem)currentReactionSubstrateEdgeRep.getAllItemsByProp(
        					EGraphItemProperty.ALIAS_PARENT).toArray()[0]).getReactionId());
    	
    	if (graphItemIn == null || graphItemOut == null)
    	{
    		return;
    	}
    	
   		currentReactionSubstrateEdgeRep.addItemDoubleLinked(graphItemIn,
				EGraphItemProperty.INCOMING);

		currentReactionSubstrateEdgeRep.addItemDoubleLinked(graphItemOut,
				EGraphItemProperty.OUTGOING);

		IGraphItem tmpReactionEdge = (PathwayReactionEdgeGraphItem) currentReactionSubstrateEdgeRep
				.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).toArray()[0];

		tmpReactionEdge.addItemDoubleLinked(
				(IGraphItem) graphItemIn.getAllItemsByProp(
						EGraphItemProperty.ALIAS_PARENT).toArray()[0],
				EGraphItemProperty.INCOMING);

		tmpReactionEdge.addItemDoubleLinked(
				(IGraphItem) graphItemOut.getAllItemsByProp(
						EGraphItemProperty.ALIAS_PARENT).toArray()[0],
				EGraphItemProperty.OUTGOING);
    }

	/**
	 * Reacts on the elements of the reaction product tag.
	 * 
	 * An example reaction product tag looks like this: <product
	 * name="cpd:C02291"/>
	 */
	protected void handleReactionProductTag() {
		
    	String sReactionProductName = "";
    	
    	for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) 
		{
			 sAttributeName = attributes.getLocalName(iAttributeIndex);
		
			if ("".equals(sAttributeName))
			{
				sAttributeName = attributes.getQName(iAttributeIndex);
			}
				
			if (sAttributeName.equals("name"))
			{
				sReactionProductName = attributes.getValue(iAttributeIndex); 				
			}
		}  	

    	// Compound
    	IGraphItem graphItemOut = 
    		hashKgmlNameToVertexRepId.get(sReactionProductName);
    	
    	// Enzyme
    	IGraphItem graphItemIn =
    		hashKgmlReactionIdToVertexRepId.get(
    				((PathwayReactionEdgeGraphItem)currentReactionProductEdgeRep.getAllItemsByProp(
        					EGraphItemProperty.ALIAS_PARENT).toArray()[0]).getReactionId());
    	
    	if (graphItemIn == null || graphItemOut == null)
    	{
    		return;
    	}
   	
    	currentReactionProductEdgeRep.addItemDoubleLinked(graphItemIn, 
    			EGraphItemProperty.INCOMING);
 
    	currentReactionProductEdgeRep.addItemDoubleLinked(graphItemOut,
    			EGraphItemProperty.OUTGOING);
    	
    	IGraphItem tmpReactionEdge = (PathwayReactionEdgeGraphItem)currentReactionProductEdgeRep.getAllItemsByProp(
				EGraphItemProperty.ALIAS_PARENT).toArray()[0];

    	tmpReactionEdge.addItemDoubleLinked(
    			(IGraphItem)graphItemIn.getAllItemsByProp(
    					EGraphItemProperty.ALIAS_PARENT).toArray()[0],
				EGraphItemProperty.INCOMING);

    	tmpReactionEdge.addItemDoubleLinked(
    			(IGraphItem)graphItemOut.getAllItemsByProp(
    					EGraphItemProperty.ALIAS_PARENT).toArray()[0],
				EGraphItemProperty.OUTGOING);
    }

	/**
	 * @see cerberus.parser.handler.IXmlParserHandler#destroyHandler()
	 * @see cerberus.parser.handler.AXmlParserHandler#destroyHandler()
	 * 
	 */
	public void destroyHandler() {
		
		super.destroyHandler();
	}
}
