package org.geneview.core.parser.xml.sax.handler.kegg;

import java.util.ArrayList;
import java.util.Random;

import org.geneview.core.data.graph.core.PathwayGraph;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IXmlParserManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.data.pathway.EPathwayDatabaseType;
import org.geneview.core.parser.xml.sax.handler.AXmlParserHandler;
import org.geneview.util.graph.IGraphItem;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *	Class is able to parse BioCarta pathway files. 
 *  The creation of the pathway objects is triggered.
 * 
 * @author Marc Streit
 * 
 */
public class BioCartaPathwayImageMapSaxHandler 
extends AXmlParserHandler {

	private final static String BIOCARTA_EXTERNAL_URL_PATHWAY =
		"http://cgap.nci.nih.gov/Pathways/BioCarta/";
	
	private final static String BIOCARTA_EXTERNAL_URL_VERTEX =
		"http://cgap.nci.nih.gov";
	
	private Attributes attributes;
	
	private String sAttributeName = "";
	
	private boolean bReadTitle = false;
	
	private PathwayGraph currentPathway;
	
	private String sTitle = "";
	
	public BioCartaPathwayImageMapSaxHandler(IGeneralManager refGeneralManager, 
			IXmlParserManager refXmlParserManager) {

		super(refGeneralManager, refXmlParserManager);

		setXmlActivationTag("span");
	}	

    public void startElement(String namespaceURI,
            String sSimpleName,
            String sQualifiedName,
            Attributes attributes)
    throws SAXException	{
    	
    	String sElementName = sSimpleName;
    	this.attributes = attributes;
    	
    	if ("".equals(sElementName)) 
    	{
    		sElementName = sQualifiedName; // namespaceAware = false
    	}
    	    	
		if (attributes != null) 
		{
			if (sElementName.equals("b"))
				handleTitleTag();
			else if (sElementName.equals("img"))
				handleImageLinkTag();
			else if (sElementName.equals("area"))
				handleAreaTag();
		}
	}
	
	public void endElement(String namespaceURI,
	          String sSimpleName,
	          String sQualifiedName)
		throws SAXException {
			
			String sName = ("".equals(sSimpleName)) ? sQualifiedName : sSimpleName;
			
			if (sName.equals("map")) 
			{
				// Early abort parsing current file
				refXmlParserManager.sectionFinishedByHandler( this );
			}
		}
    
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		if (!bReadTitle)
			return;
	
		for (int iCharIndex = start; iCharIndex < length; iCharIndex++)
		{
			sTitle += ch[iCharIndex];
		}
		
//		refGeneralManager.getSingelton().logMsg( 
//				"Load 'BIOCARTA pathway titled: " + sTitle,
//					LoggerType.VERBOSE );
		
		bReadTitle = false;
	}
	
	/**
	 * Reacts on the elements of the <b> tag.
	 * 
	 */
	protected void handleTitleTag() {
		
		if (sTitle.isEmpty())
			bReadTitle = true;
	}
	
	/**
	 * Reacts on the elements of the <b> tag.
	 * 
	 */
	protected void handleImageLinkTag() {
		
		String sName = "";
    	String sImageLink = "";

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) 
		{
			 sAttributeName = attributes.getLocalName(iAttributeIndex);
		
			if ("".equals(sAttributeName))
			{
				sAttributeName = attributes.getQName(iAttributeIndex);
			}
				
			if (sAttributeName.equals("src"))
			{
				sImageLink = attributes.getValue(iAttributeIndex); 
			}
   			else if (sAttributeName.equals("name"))
   			{
   				sName = attributes.getValue(iAttributeIndex); 
   			}
		} 	
		
		if (sImageLink.isEmpty() || sName.isEmpty())
			return;
		
		// FIXME: generate appropriate BioCarta IDs
		Random rand = new Random();
		currentPathway = refGeneralManager.getSingelton().getPathwayManager().
			createPathway(EPathwayDatabaseType.BIOCARTA, 
					rand.nextInt(), "<name>", sTitle, 
					sImageLink, BIOCARTA_EXTERNAL_URL_PATHWAY + sName);
		
		sTitle = "";
	}

	private void handleAreaTag() {
		
		String sName = "<unknown>";
    	String sCoords = "";
    	String sShape = "";
    	String sExternalLink = "";

		for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) 
		{
			 sAttributeName = attributes.getLocalName(iAttributeIndex);
		
			if ("".equals(sAttributeName))
			{
				sAttributeName = attributes.getQName(iAttributeIndex);
			}
				
			if (sAttributeName.equals("shape"))
			{
				sShape = attributes.getValue(iAttributeIndex); 
			}
   			else if (sAttributeName.equals("coords"))
   			{
   				sCoords = attributes.getValue(iAttributeIndex); 
   			}
   			else if (sAttributeName.equals("href"))
   			{
   				sExternalLink = attributes.getValue(iAttributeIndex); 
   			
   				if (sExternalLink.contains("BCID="))
   				{
	   				// Create name from link
	   				sName = sExternalLink.substring(
	   						sExternalLink.lastIndexOf("BCID=") + 5, 
	   						sExternalLink.length());
   				}
   			}
		} 	
		
		IGraphItem vertex = refGeneralManager.getSingelton().getPathwayItemManager()
			.createVertex(sName, "other", 
					BIOCARTA_EXTERNAL_URL_VERTEX +  sExternalLink, "");
		
		refGeneralManager.getSingelton()
			.getPathwayItemManager().createVertexRep(
			currentPathway, 
			vertex, 
			sName, 
			sShape, 
			sCoords);
	}
	
	/**
	 * @see org.geneview.core.parser.xml.sax.handler.IXmlParserHandler#destroyHandler()
	 * @see org.geneview.core.parser.xml.sax.handler.AXmlParserHandler#destroyHandler()
	 * 
	 */
	public void destroyHandler() {
		
		super.destroyHandler();
	}
}
