package org.caleydo.core.parser.xml.sax.handler.glyph;

import java.awt.Rectangle;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.specialized.genome.pathway.PathwayManager;
import org.caleydo.core.manager.specialized.glyph.EGlyphSettingIDs;
import org.caleydo.core.parser.xml.sax.handler.AXmlParserHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * 
 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler
 * 
 * @author Sauer Stefan
 *
 */
public class GlyphDefinitionSaxHandler 
extends AXmlParserHandler {

	protected Attributes attributes;
	protected String sAttributeName = "";
	
	protected Vector<String> tagHierarchie = new Vector<String>(); 
	
	
	public GlyphDefinitionSaxHandler(IGeneralManager generalManager, 
			IXmlParserManager xmlParserManager) {

		super(generalManager, xmlParserManager);

		setXmlActivationTag("glyphview");
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
			
			if (sElementName.equals("glyphview")) {
				//generalManager.getLogger().log(Level.INFO, "GlyphDefinitionHandler:: el=glyphview");
				tagHierarchie.clear();

			} else if (sElementName.equals("profile")) {
//				generalManager.getLogger().log(Level.INFO, "GlyphDefinitionHandler:: el= "+tagHierarchie.lastElement()+"->profile");
				handleProfileTag();
				
			} else if (sElementName.equals("settings")) {
//				generalManager.getLogger().log(Level.INFO, "GlyphDefinitionHandler:: el= "+tagHierarchie.lastElement()+"->settings");
				handleSettingsTag();
				
			} else if (sElementName.equals("item")) {
//				generalManager.getLogger().log(Level.INFO, "GlyphDefinitionHandler:: el= "+tagHierarchie.lastElement()+"->item");
				handleItemTag();
			}
			
			tagHierarchie.add(sElementName);
		}
	}

	public void endElement(String namespaceURI,
          String sSimpleName,
          String sQualifiedName)
	throws SAXException {
		//emit("</"+sName+">");
		
		String eName = ("".equals(sSimpleName)) ? sQualifiedName : sSimpleName;
		tagHierarchie.remove(tagHierarchie.size()-1);
		
		if (null != eName) {
			if (eName.equals(sOpeningTag)) {
				/**
				 * section (xml block) finished, call callback function from IXmlParserManager
				 */
				xmlParserManager.sectionFinishedByHandler( this );
			}
		}
	}
	
	
	

	private void handleItemTag() {
		String pTag = tagHierarchie.lastElement(); //parent tag
		
		if(pTag.equals("settings")) {
			handleSettings();
		}
		
	}
	
	private void handleSettings() {
		String key = "";
		String value = "";
		
    	for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
        	String sAttributeName = attributes.getLocalName(iAttributeIndex);
    		
    		if(sAttributeName.equals(""))		sAttributeName = attributes.getQName(iAttributeIndex);
    		if(sAttributeName.equals("type"))	key = attributes.getValue(iAttributeIndex);
    		if(sAttributeName.equals("value"))	value=attributes.getValue(iAttributeIndex);
    		
		}
		
		if(key.equals("sort"))
			generalManager.getGlyphManager().addSortColumn(value);
		if(key.equals("topColor"))
			generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.TOPCOLOR , value);
		if (key.equals("boxColor"))
			generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.BOXCOLOR , value);
		if (key.equals("boxHeight"))
			generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.BOXHEIGHT , value);
		if (key.equals("updateSendParameter"))
			generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.UPDATESENDPARAMETER , value);
		if (key.equals("scatterPlotAxisX"))
			generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.SCATTERPLOTX , value);
		if (key.equals("scatterPlotAxisY"))
			generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.SCATTERPLOTY , value);
		
	}
	
	

	private void handleSettingsTag() {
		//should not be needed for now
	}

	private void handleProfileTag() {
		//we meight want to support some profiles in the future
	}

	
	
	/**
	 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler#destroyHandler()
	 * @see org.caleydo.core.parser.xml.sax.handler.AXmlParserHandler#destroyHandler()
	 * 
	 */
	public void destroyHandler() {
		
		super.destroyHandler();
	}
}
