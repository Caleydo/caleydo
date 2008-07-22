package org.caleydo.core.parser.xml.sax.handler.glyph;

import java.util.Vector;
import java.util.logging.Level;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.specialized.glyph.EGlyphSettingIDs;
import org.caleydo.core.parser.xml.sax.handler.AXmlParserHandler;
import org.caleydo.core.view.opengl.canvas.glyph.GlyphAttributeType;
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
	
	private GlyphAttributeType gatActualColumn = null;

	
	
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
//				handleSettingsTag();
				
			} else if (sElementName.equals("item")) {
//				generalManager.getLogger().log(Level.INFO, "GlyphDefinitionHandler:: el= "+tagHierarchie.lastElement()+"->item");
				handleItemTag();
				
			} else if (sElementName.equals("data")) {
//				generalManager.getLogger().log(Level.INFO, "GlyphDefinitionHandler:: el= "+tagHierarchie.lastElement()+"->data");
			} else if (sElementName.equals("column")) {
//				generalManager.getLogger().log(Level.INFO, "GlyphDefinitionHandler:: el= "+tagHierarchie.lastElement()+"->column");
				handleColumnBeginTag();
			} else if (sElementName.equals("nominal")) {
//			generalManager.getLogger().log(Level.INFO, "GlyphDefinitionHandler:: el= "+tagHierarchie.lastElement()+"->column");
				handleNominalTag();
			} else if (sElementName.equals("int")) {
//				generalManager.getLogger().log(Level.INFO, "GlyphDefinitionHandler:: el= "+tagHierarchie.lastElement()+"->column");
				handleIntTag();
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
		  if (eName.equals("column")) {
			handleColumnEndTag();
		  }
			
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
	
	


	private void handleProfileTag() {
		//we meight want to support some profiles in the future
	}


	private void handleColumnBeginTag() {
//		String type  = "";
		String col   = "";
		String label = "";
		int colnum = 0;
		
    	for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
        	String sAttributeName = attributes.getLocalName(iAttributeIndex);
    		
    		if(sAttributeName.equals(""))		sAttributeName = attributes.getQName(iAttributeIndex);
//    		if(sAttributeName.equals("type"))	type  = attributes.getValue(iAttributeIndex);
    		if(sAttributeName.equals("colnum"))	col   = attributes.getValue(iAttributeIndex);
    		if(sAttributeName.equals("label"))	label = attributes.getValue(iAttributeIndex);
		}
    	
    	try {
    		colnum = Integer.parseInt(col);
    	} catch(NumberFormatException ex) {
			gatActualColumn = null;
			generalManager.getLogger().log(Level.SEVERE, "GlyphSaxDefinitionHandler:: colnumber is not an integer! (" + label + ")");
			return;
    	}
    	
		gatActualColumn = new GlyphAttributeType(generalManager, label, colnum);

		/*
    	if(type.equals("ordinal")) {
//    		gatActualColumn.setDoesAutomaticAttribute(true);
    	}*/
    	
    	
    	
    	
		
	}
	
	private void handleColumnEndTag() {
		if(gatActualColumn != null)
			generalManager.getGlyphManager().addColumnAttributeType(gatActualColumn);
	}
	
	
	private void handleNominalTag() {
//	     <nominal string="X"  group="0" numeric="0.0" />
		String pTag = tagHierarchie.lastElement(); //parent tag
		
		if(!pTag.equals("column")) {
			generalManager.getLogger().log(Level.WARNING, "GlyphSaxDefinitionHandler::handleNominalTag() - nominal tag not in column tag embeded");
			return;
		}
		
		gatActualColumn.setDoesAutomaticAttribute(false);
		
		String st = "";
		String gr = "";
		int   igr = -1;
		String nu = "";
		float fnu = 0.0f;
		
    	for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
        	String sAttributeName = attributes.getLocalName(iAttributeIndex);
    		
    		if(sAttributeName.equals(""))			sAttributeName = attributes.getQName(iAttributeIndex);
    		if(sAttributeName.equals("string"))		st = attributes.getValue(iAttributeIndex);
    		if(sAttributeName.equals("group"))		gr = attributes.getValue(iAttributeIndex);
    		if(sAttributeName.equals("numeric"))	nu = attributes.getValue(iAttributeIndex);
		}
    	
    	// if numeric is not set its the same as the group number
    	if(nu.equals("")) nu = gr;
		
    	// convert numbers
    	try {
    		fnu = Float.parseFloat(nu);
    	} catch(NumberFormatException ex) {
			generalManager.getLogger().log(Level.SEVERE, "GlyphSaxDefinitionHandler::handleNominalTag() nominal numeric is not an float!");
			return;
    	}
    	try {
    		igr = Integer.parseInt(gr);
    	} catch(NumberFormatException ex) {
			generalManager.getLogger().log(Level.SEVERE, "GlyphSaxDefinitionHandler::handleNominalTag() group is not an integer!");
			return;
    	}

    	// add this parameter
    	gatActualColumn.addAttribute(igr, st, fnu);
		
	}

	private void handleIntTag() {
//     <int min="1900" max="2008" interval="1" />
		String pTag = tagHierarchie.lastElement(); //parent tag
		
		if(!pTag.equals("column")) {
			generalManager.getLogger().log(Level.WARNING, "GlyphSaxDefinitionHandler::handleNominalTag() - int tag not in column tag embeded");
			return;
		}
		
		gatActualColumn.setDoesAutomaticAttribute(false);

		String smin = "";
		String smax = "";
		String sint = "";
		int imin = 0;
		int imax = 0;
		int iint = 0;
		
    	for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++) {
        	String sAttributeName = attributes.getLocalName(iAttributeIndex);
    		
    		if(sAttributeName.equals(""))			sAttributeName = attributes.getQName(iAttributeIndex);
    		if(sAttributeName.equals("min"))		smin = attributes.getValue(iAttributeIndex);
    		if(sAttributeName.equals("max"))		smax = attributes.getValue(iAttributeIndex);
    		if(sAttributeName.equals("interval"))	sint = attributes.getValue(iAttributeIndex);
		}
    	
    	try {
    		imin = Integer.parseInt(smin);
    		imax = Integer.parseInt(smax);
    		iint = Integer.parseInt(sint);
    	} catch(NumberFormatException ex) {
			generalManager.getLogger().log(Level.SEVERE, "GlyphSaxDefinitionHandler::handleIntTag() given data is not an integer!");
			return;
    	}
    	
    	for(int i=imin; i<imax; i+=iint) {
        	gatActualColumn.addAttribute(i, Integer.toString(i) , (float)i);
    	}

    	
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
