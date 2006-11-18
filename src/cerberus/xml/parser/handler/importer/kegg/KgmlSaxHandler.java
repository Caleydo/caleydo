package cerberus.xml.parser.handler.importer.kegg;

import java.util.HashMap;

import cerberus.manager.IGeneralManager;
import cerberus.manager.data.pathway.PathwayElementManager;
import cerberus.manager.data.pathway.PathwayManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.xml.parser.handler.IXmlParserHandler;
import cerberus.xml.parser.manager.IXmlParserManager;
import cerberus.xml.parser.handler.AXmlParserHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class KgmlSaxHandler 
extends AXmlParserHandler 
implements IXmlParserHandler {
	
	protected Attributes attributes;
	
	protected String sAttributeName = "";
	
	protected HashMap<Integer, Integer> kgmlIdToElementIdLUT;
	
	protected boolean bVertexExists = false;
	
	/**
	 * Map that stores the KEGG compound names and the internal compound ID
	 */
	protected HashMap<String, Integer> kgmlCompoundNameToElementIdLUT;
	
	public KgmlSaxHandler(  final IGeneralManager refGeneralManager,
			final IXmlParserManager refXmlParserManager)
	{		
		super( refGeneralManager, refXmlParserManager);
		
		kgmlIdToElementIdLUT = new HashMap<Integer, Integer>();
		
		kgmlCompoundNameToElementIdLUT = new HashMap<String, Integer>();
		
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
    	
    	//System.out.println("Element name: " +sElementName);
    	
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
			else if (sElementName.equals("subtype"))
				handleSubtypeTag();
			else if (sElementName.equals("reaction"))
				handleReactionTag();
			else if (sElementName.equals("product"))
				handleReactionProductTag();
			else if (sElementName.equals("substrate"))
				handleReactionSubstrateTag();
		}
		
//        System.out.println("Line number: " +refLocator.getLineNumber());
    }

	public void endElement(String namespaceURI,
          String sSimpleName,
          String sQualifiedName)
	throws SAXException {
		
		//emit("</"+sName+">");
		
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
    	
    	String sTitle = "";
    	String sImageLink = "";
    	String sLink = "";
    	int iPathwayID = 0;

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) 
		{
			 sAttributeName = attributes.getLocalName(iAttributeIndex);
		
			if ("".equals(sAttributeName))
			{
				sAttributeName = attributes.getQName(iAttributeIndex);
			}
				
			if (sAttributeName.equals("name"))
				sTitle = attributes.getValue(iAttributeIndex); 
   			else if (sAttributeName.equals("number"))
  				iPathwayID = new Integer(attributes.getValue(iAttributeIndex)); 
   			else if (sAttributeName.equals("image"))
    			sImageLink = attributes.getValue(iAttributeIndex); 
    		else if (sAttributeName.equals("link"))
    			sLink = attributes.getValue(iAttributeIndex); 
			
   			//System.out.println("Attribute name: " +sAttributeName);
  			//System.out.println("Attribute value: " +attributes.getValue(iAttributeIndex));
   		}
		
		((PathwayManager)(refGeneralManager.getManagerByBaseType(ManagerObjectType.PATHWAY))).
			createPathway(sTitle, sImageLink, sLink, iPathwayID);
    }
    
	/**
	 * Reacts on the elements of the entry tag.
	 * 
	 * An example entry tag looks like this:
	 * <entry id="1" name="ec:1.8.4.1" type="enzyme" 
	 * reaction="rn:R01292" link="http://www.genome.jp/dbget-bin/www_bget?enzyme+1.8.4.1">
	 */
    protected void handleEntryTag() {
    	
    	int iKgmlEntryID = 0;
    	String sName = "";
    	String sType = "";
    	String sLink = "";
    	String sReactionId = "";
	   
    	int iGeneratedElementId = 0;
	
    	for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) 
	   	{
		   sAttributeName = attributes.getLocalName(iAttributeIndex);
	
		   if ("".equals(sAttributeName))
		   {
			   sAttributeName = attributes.getQName(iAttributeIndex);
		   }
			
		   if (sAttributeName.equals("id"))
			   iKgmlEntryID = new Integer(attributes.getValue(iAttributeIndex)); 
		   else if (sAttributeName.equals("name"))
			   sName = attributes.getValue(iAttributeIndex); 
		   else if (sAttributeName.equals("type"))
			   sType = attributes.getValue(iAttributeIndex); 
		   else if (sAttributeName.equals("link"))
			   sLink = attributes.getValue(iAttributeIndex);
		   else if (sAttributeName.equals("reaction"))
			   sReactionId = attributes.getValue(iAttributeIndex);
	   	}
    	
    	// Return if node already exists
    	if (!kgmlIdToElementIdLUT.containsKey(iKgmlEntryID))
    	{
        	iGeneratedElementId = 		
        		((PathwayElementManager)(refGeneralManager.getSingelton().getPathwayElementManager())).
    				createVertex(sName, sType, sLink, sReactionId);
        	
        	kgmlIdToElementIdLUT.put(iKgmlEntryID, iGeneratedElementId);
        	
        	bVertexExists = false;
    	}
    	else
    	{
    		bVertexExists = true;
    	}
    	
    	((PathwayElementManager)(refGeneralManager.getSingelton().getPathwayElementManager())).
			addVertexToPathway(iGeneratedElementId);
    	
    	if (sType.equals("compound"))
    	{
    		kgmlCompoundNameToElementIdLUT.put(sName, iGeneratedElementId);
    	}
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
    	
    	// Don't create a new vertex representation if vertex was already created.
    	if (bVertexExists == true)
    		return;
    	
		String sName = "";
		String sType = "";
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
				sName = attributes.getValue(iAttributeIndex); 
   			else if (sAttributeName.equals("height"))
  				iHeight = new Integer(attributes.getValue(iAttributeIndex)); 
   			else if (sAttributeName.equals("width"))
    			iWidth = new Integer(attributes.getValue(iAttributeIndex)); 
    		else if (sAttributeName.equals("x"))
    			iXPosition = new Integer(attributes.getValue(iAttributeIndex)); 
   			else if (sAttributeName.equals("y"))
   				iYPosition = new Integer(attributes.getValue(iAttributeIndex)); 
   			else if (sAttributeName.equals("type"))
   				sType = attributes.getValue(iAttributeIndex);
   		}

		((PathwayElementManager)(refGeneralManager.getManagerByBaseType(ManagerObjectType.PATHWAY_ELEMENT))).
			createVertexRepresentation(sName, iHeight, iWidth,
				iXPosition, iYPosition, sType);
    }
    
	/**
	 * Reacts on the elements of the relation tag.
	 * 
	 * An example relation tag looks like this:
	 * <relation entry1="28" entry2="32" type="ECrel">
	 */
    protected void handleRelationTag() {
    	
    	int iEntry1 = 0;
    	int iEntry2 = 0;
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
  				iEntry1 = new Integer(attributes.getValue(iAttributeIndex)); 
   			else if (sAttributeName.equals("entry2"))
    			iEntry2 = new Integer(attributes.getValue(iAttributeIndex)); 
			
   			//System.out.println("Attribute name: " +sAttributeName);
   			//System.out.println("Attribute value: " +attributes.getValue(iAttributeIndex));
   		}  	    	
		
		if (!kgmlIdToElementIdLUT.containsKey(iEntry1) || !kgmlIdToElementIdLUT.containsKey(iEntry2))
		{
			return;
		}
		
    	int iElementId1 = kgmlIdToElementIdLUT.get(iEntry1);
    	int iElementId2 = kgmlIdToElementIdLUT.get(iEntry2);

		((PathwayElementManager)(refGeneralManager.getSingelton().getPathwayElementManager())).
			createRelationEdge(iElementId1, iElementId2, sType);
    }
    	
    protected void handleSubtypeTag() {
    	
    	String sName = "";
    	int iCompoundId = 0;
		
		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) 
		{
			 sAttributeName = attributes.getLocalName(iAttributeIndex);
		
			if ("".equals(sAttributeName))
			{
				sAttributeName = attributes.getQName(iAttributeIndex);
			}
				
			if (sAttributeName.equals("name"))
				sName = attributes.getValue(iAttributeIndex); 
   			else if (sAttributeName.equals("value"))
  				iCompoundId = new Integer(attributes.getValue(iAttributeIndex)); 
 
   			//System.out.println("Attribute name: " +sAttributeName);
   			//System.out.println("Attribute value: " +attributes.getValue(iAttributeIndex));
   		}  	
		
		if (sName.equals("compound"))
		{
			//retrieve the internal element ID and add the compound value to the edge
    		((PathwayElementManager)(refGeneralManager.getManagerByBaseType(ManagerObjectType.PATHWAY_ELEMENT))).
				addRelationCompound(kgmlIdToElementIdLUT.get(iCompoundId));
		}
    }
    
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
				sReactionType = attributes.getValue(iAttributeIndex); 
			else if (sAttributeName.equals("name"))
				sReactionName = attributes.getValue(iAttributeIndex); 	
		}  	
		
		((PathwayElementManager)(refGeneralManager.getManagerByBaseType(ManagerObjectType.PATHWAY_ELEMENT))).
			createReactionEdge(sReactionName, sReactionType);
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
				sReactionSubstrateName = attributes.getValue(iAttributeIndex); 
		}  	
    	
    	// If substrate is not included in pathway - ignore!
    	if (kgmlCompoundNameToElementIdLUT.containsKey(sReactionSubstrateName))
    	{
        	// Lookup for internal comound ID
    		int iReactionSubstrateId = 
    			kgmlCompoundNameToElementIdLUT.get(sReactionSubstrateName);
    	
    		((PathwayElementManager)(refGeneralManager.getManagerByBaseType(ManagerObjectType.PATHWAY_ELEMENT))).
				addReactionSubstrate(iReactionSubstrateId);
    	}
	}

	/**
	 * Reacts on the elements of the reaction product tag.
	 * 
	 * An example reaction product tag looks like this:
	 * <product name="cpd:C02291"/>
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
				sReactionProductName = attributes.getValue(iAttributeIndex); 
		}  	

    	// If product is not included in pathway - ignore!
    	if (kgmlCompoundNameToElementIdLUT.containsKey(sReactionProductName))
    	{
    		// Lookup for internal comound ID
    		int iReactionProductId = 
    			kgmlCompoundNameToElementIdLUT.get(sReactionProductName);
    	
    		((PathwayElementManager)(refGeneralManager.getManagerByBaseType(ManagerObjectType.PATHWAY_ELEMENT))).
				addReactionProduct(iReactionProductId);  
    	}
	}

	/**
	 * @see cerberus.xml.parser.handler.IXmlParserHandler#destroyHandler()
	 * @see cerberus.xml.parser.handler.AXmlParserHandler#destroyHandler()
	 * 
	 */
	public void destroyHandler() {
		
		kgmlIdToElementIdLUT = null;
		
		super.destroyHandler();
	}
}
