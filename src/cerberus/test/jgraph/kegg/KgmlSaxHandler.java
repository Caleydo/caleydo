/*
 * Project: GenView
 * 
 * Author: Marc Streit
 * 
 *  creation date: 03-07-2006
 *  
 */
package cerberus.test.jgraph.kegg;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


//import java.lang.Integer;

public class KgmlSaxHandler extends DefaultHandler 
{
	private PathwayGraphBuilder pathwayGraphBuilder;
	
	public KgmlSaxHandler(PathwayGraphBuilder pathwayGraphBuilder)
	{
		this.pathwayGraphBuilder = pathwayGraphBuilder;
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
    	
    	if ("".equals(sElementName)) 
    	{
    		sElementName = sQualifiedName; // namespaceAware = false
    	}
    	
    	//System.out.println("Element name: " +sElementName);
    	
    	if (sElementName.equals("graphics"))
    	{
    		if (attributes != null) 
    		{
    			String sName = "";
    			int iHeight = 0;
    			int iWidth = 0;
    			int iXPosition = 0;
    			int iYPosition = 0;
    		
    			for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) 
    			{
    				String sAttributeName = attributes.getLocalName(iAttributeIndex);
    			
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
    			
//	    			System.out.println("Attribute name: " +sAttributeName);
//	    			System.out.println("Attribute value: " +attributes.getValue(iAttributeIndex));
	    		}
    		
				pathwayGraphBuilder.createCell(sName, iHeight, iWidth, iXPosition, iYPosition);
    		}
    	}
    }

    public void endElement(String namespaceURI,
          String sSimpleName,
          String sQualifiedName)
	throws SAXException
	{
		//emit("</"+sName+">");
	}
}
