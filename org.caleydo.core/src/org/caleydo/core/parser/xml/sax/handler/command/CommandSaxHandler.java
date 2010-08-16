package org.caleydo.core.parser.xml.sax.handler.command;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.parser.parameter.IParameterHandler.ParameterHandlerType;
import org.caleydo.core.parser.parameter.ParameterHandler;
import org.caleydo.core.parser.xml.sax.handler.AXmlParserHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Create commands
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CommandSaxHandler
	extends AXmlParserHandler {

	/* XML Tags */
	private final String sTag_Command = ECommandType.TAG_CMD.getXmlKey();
	/* END: XML Tags */

	/**
	 * Since the opening tag is handled by the external handler this fal is set to true by default.
	 */
	private boolean bCommandBuffer_isActive = false;

	/**
	 * <Application > <CommandBuffer> <Cmd /> <Cmd /> </CommandBuffer> </Application>
	 */
	public CommandSaxHandler() {
		super();
		setXmlActivationTag("CommandBuffer");
	}

	/**
	 * Read values of class: iCurrentFrameId
	 * 
	 * @param attrs
	 * @param bIsExternalFrame
	 */
	protected ICommand readCommandData(final Attributes attrs, boolean bIsExternalFrame) {

		ICommand lastCommand = null;

		IParameterHandler phAttributes = new ParameterHandler();

		phAttributes.setValueBySaxAttributes(attrs, ECommandType.TAG_LABEL.getXmlKey(),
			ECommandType.TAG_LABEL.getDefault(), ParameterHandlerType.STRING);

		phAttributes.setValueBySaxAttributes(attrs, ECommandType.TAG_UNIQUE_ID.getXmlKey(),
			ECommandType.TAG_UNIQUE_ID.getDefault(), ParameterHandlerType.INT);

		phAttributes.setValueBySaxAttributes(attrs, ECommandType.TAG_TYPE.getXmlKey(),
			ECommandType.TAG_TYPE.getDefault(), ParameterHandlerType.STRING);

		phAttributes.setValueBySaxAttributes(attrs, ECommandType.TAG_ATTRIBUTE1.getXmlKey(),
			ECommandType.TAG_ATTRIBUTE1.getDefault(), ParameterHandlerType.STRING);

		phAttributes.setValueBySaxAttributes(attrs, ECommandType.TAG_ATTRIBUTE2.getXmlKey(),
			ECommandType.TAG_ATTRIBUTE2.getDefault(), ParameterHandlerType.STRING);

		phAttributes.setValueBySaxAttributes(attrs, ECommandType.TAG_ATTRIBUTE3.getXmlKey(),
			ECommandType.TAG_ATTRIBUTE3.getDefault(), ParameterHandlerType.STRING);

		phAttributes.setValueBySaxAttributes(attrs, ECommandType.TAG_ATTRIBUTE4.getXmlKey(),
			ECommandType.TAG_ATTRIBUTE4.getDefault(), ParameterHandlerType.STRING);

		phAttributes.setValueBySaxAttributes(attrs, ECommandType.TAG_ATTRIBUTE5.getXmlKey(),
			ECommandType.TAG_ATTRIBUTE5.getDefault(), ParameterHandlerType.STRING);
		
		phAttributes.setValueBySaxAttributes(attrs, ECommandType.TAG_POS_GL_ORIGIN.getXmlKey(),
			ECommandType.TAG_POS_GL_ORIGIN.getDefault(), ParameterHandlerType.VEC3F);

		phAttributes.setValueBySaxAttributes(attrs, ECommandType.TAG_POS_GL_ROTATION.getXmlKey(),
			ECommandType.TAG_POS_GL_ROTATION.getDefault(), ParameterHandlerType.VEC4F);

		phAttributes.setValueBySaxAttributes(attrs, ECommandType.TAG_DETAIL.getXmlKey(),
			ECommandType.TAG_DETAIL.getDefault(), ParameterHandlerType.STRING);

		lastCommand = generalManager.getCommandManager().createCommand(phAttributes);

		return lastCommand;
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(Stringt, Stringt, Stringt, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes attrs)
		throws SAXException {

		String eName = "".equals(localName) ? qName : localName;

		if (null != eName) {

			if (eName.equals(sOpeningTag)) {
				/* <sFrameStateTag> */
				if (bCommandBuffer_isActive)
					throw new SAXException("<" + sOpeningTag + "> already opened!");
				else {
					bCommandBuffer_isActive = true;
					return;
				}

			} // end: if (eName.equals(sFrameStateTag)) {
			else if (eName.equals(sTag_Command)) {

				if (bCommandBuffer_isActive) {
					/**
					 * <CommandBuffer> ... <Cmd ...>
					 */

					// readCommandQueueData( attrs, true );
					ICommand lastCommand = readCommandData(attrs, true);

					if (lastCommand == null) {
						// generalManager.logMsg(
						// "Command: can not execute command due to error while parsing. skip it."
						// ,
						// LoggerType.VERBOSE );
					}

				}
				else
					throw new SAXException("<" + sTag_Command + "> opens without <" + sOpeningTag
						+ "> being opened!");
			}
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(Stringt, Stringt, Stringt)
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

		String eName = "".equals(localName) ? qName : localName;

		if (null != eName) {
			if (eName.equals(sOpeningTag)) {

				/* </CommandBuffer> */
				if (bCommandBuffer_isActive) {
					bCommandBuffer_isActive = false;

					/**
					 * section (xml block) finished, call callback function from IXmlParserManager
					 */
					xmlParserManager.sectionFinishedByHandler(this);

					return;
				}
				else
					throw new SAXException("<" + sOpeningTag + "> was already closed.");

			}
			else if (eName.equals(sTag_Command)) {

				/* </cmd> */
				if (!bCommandBuffer_isActive)
					throw new SAXException("<" + sTag_Command + "> opens without " + sOpeningTag
						+ " being opened.");

			}
		}

	}

	/**
	 * Cleanup called by Manager after Handler is not used any more.
	 */
	@Override
	public void destroyHandler() {
		super.destroyHandler();
	}
}
