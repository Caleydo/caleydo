package org.caleydo.core.parser.xml;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.parser.parameter.ParameterHandler;
import org.caleydo.core.parser.parameter.ParameterHandler.ParameterHandlerType;
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

	private final String sTag_Command = CommandType.TAG_CMD.getXmlKey();

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

		ParameterHandler phAttributes = new ParameterHandler();

		phAttributes.setValueBySaxAttributes(attrs, CommandType.TAG_LABEL.getXmlKey(),
			CommandType.TAG_LABEL.getDefault(), ParameterHandlerType.STRING);

		phAttributes.setValueBySaxAttributes(attrs, CommandType.TAG_UNIQUE_ID.getXmlKey(),
			CommandType.TAG_UNIQUE_ID.getDefault(), ParameterHandlerType.INT);

		phAttributes.setValueBySaxAttributes(attrs, CommandType.TAG_TYPE.getXmlKey(),
			CommandType.TAG_TYPE.getDefault(), ParameterHandlerType.STRING);

		phAttributes.setValueBySaxAttributes(attrs, CommandType.TAG_ATTRIBUTE1.getXmlKey(),
			CommandType.TAG_ATTRIBUTE1.getDefault(), ParameterHandlerType.STRING);

		phAttributes.setValueBySaxAttributes(attrs, CommandType.TAG_ATTRIBUTE2.getXmlKey(),
			CommandType.TAG_ATTRIBUTE2.getDefault(), ParameterHandlerType.STRING);

		phAttributes.setValueBySaxAttributes(attrs, CommandType.TAG_ATTRIBUTE3.getXmlKey(),
			CommandType.TAG_ATTRIBUTE3.getDefault(), ParameterHandlerType.STRING);

		phAttributes.setValueBySaxAttributes(attrs, CommandType.TAG_ATTRIBUTE4.getXmlKey(),
			CommandType.TAG_ATTRIBUTE4.getDefault(), ParameterHandlerType.STRING);

		phAttributes.setValueBySaxAttributes(attrs, CommandType.TAG_ATTRIBUTE5.getXmlKey(),
			CommandType.TAG_ATTRIBUTE5.getDefault(), ParameterHandlerType.STRING);
		
		phAttributes.setValueBySaxAttributes(attrs, CommandType.TAG_ATTRIBUTE6.getXmlKey(),
			CommandType.TAG_ATTRIBUTE6.getDefault(), ParameterHandlerType.STRING);

		phAttributes.setValueBySaxAttributes(attrs, CommandType.TAG_DETAIL.getXmlKey(),
			CommandType.TAG_DETAIL.getDefault(), ParameterHandlerType.STRING);

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
					 * section (xml block) finished, call callback function from XmlParserManager
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
