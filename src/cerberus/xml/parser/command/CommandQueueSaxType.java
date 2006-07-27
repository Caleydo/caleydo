/**
 * 
 */
package cerberus.xml.parser.command;

/**
 * Type of Command Queue "tag's" and "key's"
 * 
 * @author kalkusch
 *
 */
public enum CommandQueueSaxType
{
	COMMAND_QUEUE_OPEN("cmdqueue","type"),
	COMMAND_QUEUE_RUN("cmdqueue","type"),
	
	CMD_ID("cmdqueue","cmdId"),
	CMDQUEUE_ID("cmdqueue","cmdQueueId"),
	
	ON_DEMAND("cmdqueue","process"),
	RUN_QUEUE("cmdqueue","process"),
	
	CMD_THREAD_POOL_ID("cmdqueue","queue_thread"),
	CMD_THREAD_POOL_WAIT_ID("cmdqueue","queue_thread_wait");
	
	/**
	 *  * Tag: Example: <CmdQueue type="COMMAND_QUEUE_OPEN"> <br>
	 * "type" is the Key.<br>
	 * "CmdQueue" is the Tag.<br>
	 * "COMMAND_QUEUE_OPEN" is the attribute.<br>
	 */
	private String sXmlTag;
	
	/**
	 *  * Tag: Example: <CmdQueue type="COMMAND_QUEUE_OPEN"> <br>
	 * "type" is the Key.<br>
	 * "CmdQueue" is the Tag.<br>
	 * "COMMAND_QUEUE_OPEN" is the attribute.<br>
	 */
	private String sXmlKey;
	
	private CommandQueueSaxType( String sXmlTag, 
			String sXmlKey ) 
	{
		this.sXmlTag = sXmlTag;		
		this.sXmlKey = sXmlKey;
	}
	
//	public static final CommandQueueSaxType parse( final String sData) {
//		
//	}
	
	/**
	 *  * Tag: Example: <CmdQueue type="COMMAND_QUEUE_OPEN"> <br>
	 * "type" is the Key.<br>
	 * "CmdQueue" is the Tag.<br>
	 * "COMMAND_QUEUE_OPEN" is the attribute.<br>
	 * 
	 * @return key
	 */
	public String getXmlKey() 
	{
		return this.sXmlKey;
	}
	
	/**
	 * Tag: Example: <CmdQueue type="COMMAND_QUEUE_OPEN"> <br>
	 * "CmdQueue" is the Tag.<br>
	 * "type" is the Key.<br>
	 * "COMMAND_QUEUE_OPEN" is the attribute.
	 * 
	 * @return tag
	 */
	public String getXmlTag() 
	{
		return this.sXmlTag;
	}
}
