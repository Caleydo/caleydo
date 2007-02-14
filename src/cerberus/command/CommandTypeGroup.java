/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command;

/**
 * Enumeration of Command groups.
 * 
 * A comamnd group is a list of commands, that are related to each other.
 * 
 * @see CommandType
 * @author Michael Kalkusch
 *
 * @deprecated use CommandQueueSaxType
 */
public enum CommandTypeGroup {
	
	
	
	APPLICATION("EXIT,COMMAND_FACTORY"),
	DATASET("LOAD,SAVE"),
	DATA_COLLECTION("LOAD,SAVE"),
	ERROR(""),
	GUI("..."),
	HOST("QUIT"),
	MANAGER("MEMENTO,..."),
	SELECT("NEW, DEL, ADD, ..."),
	SELECT_VALUE("SHOW,HIDE,LCOK,UNLOCK,OFFSET,SIZE,INC,REPEAT, ..."),
	SERVER("START,STOP,RELOAD,QUIT"),
	SYSTEM("EXIT"),
	COMMAND("COMMAND"),
	WINDOW("NEW,OPEN,CLOSE,MINIMIZE,MAXIMAIZE");
	
	private String sListOfCommands;
	
	private CommandTypeGroup(String sSetListOfCommands) {
		sListOfCommands = sSetListOfCommands;
	}
	
	public String getListOfCommands() {
		return sListOfCommands;
	}
	

}
