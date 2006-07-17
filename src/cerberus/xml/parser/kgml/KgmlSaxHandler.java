package cerberus.xml.parser.kgml;

import java.util.HashMap;

import cerberus.pathways.Pathway;
import cerberus.pathways.PathwayManager;
import cerberus.pathways.element.ElementManager;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

//import java.lang.Integer;

public class KgmlSaxHandler extends DefaultHandler 
{
	private Attributes attributes = null;
	private Pathway currentPathway = null;
	
	private HashMap<Integer, Integer> kgmlIdToElementIdLUT;
	
	public KgmlSaxHandler()
	{
		kgmlIdToElementIdLUT = new HashMap<Integer, Integer>(); 
	}
	
    public void startDocument()
    throws SAXException
    {
        System.out.println("Start parsing the document.");
    }

    public void endDocument()
    throws SAXException
    {
       	System.out.println("End parsing the document.");
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
    	
    	System.out.println("Element name: " +sElementName);
    	
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
			else if (sElementName.equals("reaction"))
				handleReactionTag();
		}
    }

    public void endElement(String namespaceURI,
          String sSimpleName,
          String sQualifiedName)
	throws SAXException
	{
		//emit("</"+sName+">");
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
    public void handlePathwayTag()
    {
    	String sTitle = "";
    	String sImageLink = "";
    	String sLink = "";
    	int iPathwayID = 0;
    
		String sAttributeName = "";

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) 
		{
			 sAttributeName = attributes.getLocalName(iAttributeIndex);
		
			if ("".equals(sAttributeName))
			{
				sAttributeName = attributes.getQName(iAttributeIndex);
			}
				
			if (sAttributeName.equals("title"))
				sTitle = attributes.getValue(iAttributeIndex); 
   			else if (sAttributeName.equals("number"))
  				iPathwayID = new Integer(attributes.getValue(iAttributeIndex)); 
   			else if (sAttributeName.equals("image"))
    			sImageLink = attributes.getValue(iAttributeIndex); 
    		else if (sAttributeName.equals("link"))
    			sLink = attributes.getValue(iAttributeIndex); 
			
   			System.out.println("Attribute name: " +sAttributeName);
   			System.out.println("Attribute value: " +attributes.getValue(iAttributeIndex));
   		}
		
		PathwayManager.getInstance().createPathway(sTitle, sImageLink, sLink, iPathwayID);
    }
    
	/**
	 * Reacts on the elements of the entry tag.
	 * 
	 * An example entry tag looks like this:
	 * <entry id="1" name="ec:1.8.4.1" type="enzyme" 
	 * reaction="rn:R01292" link="http://www.genome.jp/dbget-bin/www_bget?enzyme+1.8.4.1">
	 */
    public void handleEntryTag()
    {
    	int iKgmlEntryID = 0;
    	String sName = "";
    	String sType = "";
	   
    	String sAttributeName = "";
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
		
		   System.out.println("Attribute name: " +sAttributeName);
		   System.out.println("Attribute value: " +attributes.getValue(iAttributeIndex));
	   	}

    	iGeneratedElementId = ElementManager.getInstance().createVertex(sName, sType);
    	kgmlIdToElementIdLUT.put(iKgmlEntryID, iGeneratedElementId);
    }
	
	/**
	 * Reacts on the elements of the graphics tag.
	 * 
	 * An example graphics tag looks like this:
	 * <graphics name="1.8.4.1" fgcolor="#000000" 
	 * bgcolor="#FFFFFF" type="rectangle" 
	 * x="142" y="304" width="45" height="17"/>
	 */
    public void handleGraphicsTag()
    {
		String sName = "";
		int iHeight = 0;
		int iWidth = 0;
		int iXPosition = 0;
		int iYPosition = 0;
		
		String sAttributeName = "";
		
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
			
   			System.out.println("Attribute name: " +sAttributeName);
   			System.out.println("Attribute value: " +attributes.getValue(iAttributeIndex));
   		}

		ElementManager.getInstance().createVertexRepresentation(sName, iHeight, iWidth,
				iXPosition, iYPosition);
    }
    
	/**
	 * Reacts on the elements of the relation tag.
	 * 
	 * An example relation tag looks like this:
	 * <relation entry1="28" entry2="32" type="ECrel">
	 */
    public void handleRelationTag()
    {
    	int iEntry1 = 0;
    	int iEntry2 = 0;
    	String sType = "";
    	
		String sAttributeName = "";
		
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
			
   			System.out.println("Attribute name: " +sAttributeName);
   			System.out.println("Attribute value: " +attributes.getValue(iAttributeIndex));
   		}  	
    	
    	int iElementId1 = kgmlIdToElementIdLUT.get(iEntry1); //TODO: exception
    	int iElementId2 = kgmlIdToElementIdLUT.get(iEntry2);
    	ElementManager.getInstance().createEdge(iElementId1, iElementId2, sType);
    }
    	
	/**
	 * Reacts on the elements of the pathway tag.
	 * 
	 * An example reaction tag looks like this:
	 * <reaction name="rn:R01001" type="irreversible">
	 */
    public void handleReactionTag()
    {
    	//TODO: implement method
    }
}
