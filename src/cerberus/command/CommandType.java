/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command;

import cerberus.command.CommandTypeGroup;

/**
 * List of all Command's
 * 
 * Desing Pattern "Command"
 * 
 * @author Michael Kalkusch
 *
 */
public enum CommandType {

	// --- General Application ---
	APPLICATION_COMMAND_MACRO(CommandTypeGroup.APPLICATION,"INTERNAL: Indicate a macro command"),
	APPLICATION_COMMAND_FACTORY(CommandTypeGroup.APPLICATION,"INTERNAL: Indicate a command factory without allocated command"),
	APPLICATION_EXIT(CommandTypeGroup.APPLICATION,"INTERNAL: Exit from an aplication. Terminates the application."),

	
	// ... Host ...
	HOST_QUIT(CommandTypeGroup.HOST,"Close host application"),
	
	
	// ---  Data Collection ---
	DATA_COLLECTION_LOAD(CommandTypeGroup.DATA_COLLECTION,"Load a data collection from file or network, consisting of data sets and selections"),
	DATA_COLLECTION_SAVE(CommandTypeGroup.DATA_COLLECTION,"Save a data collection to file or network, consisting of data sets and selections"),

	
	// ---  Data ISet ---
	DATASET_LOAD(CommandTypeGroup.DATASET,"Load a data set from file or network"),
	DATASET_RELOAD(CommandTypeGroup.DATASET,"Re-Load a data set from file or network"),
	DATASET_SAVE(CommandTypeGroup.DATASET,"Save a data set to file or network"),
	
    // ---  Errors ---
	ERROR_UNKOWN_COMMAND(CommandTypeGroup.ERROR,"Error caused by unkown command type"),
			
	
	// --- Manager ---
	MANAGER_MEMENTO_EMPTY(CommandTypeGroup.MANAGER,"INTERNAL: Removes all stored Mementos"),
	MANAGER_MEMENTO_SAVE(CommandTypeGroup.MANAGER,"INTERNAL: Stores all stored Mementos to file or network"),	
	MANAGER_MEMENTO_LOAD(CommandTypeGroup.MANAGER,"INTERNAL: Loads stored Mementos from file or network"),
	
	// --- Comamdn Queue ---
	COMMAND_QUEUE_OPEN(CommandTypeGroup.COMMAND,"Creates a new command qroup"),
	COMMAND_QUEUE_CLOSE(CommandTypeGroup.COMMAND,"Closes a command qroup, it is set read only now"),
	COMMAND_QUEUE_RUN(CommandTypeGroup.COMMAND,"Execute a command qroup"),
	
	// --- IVirtualArray ---
	SELECT_NEW(CommandTypeGroup.SELECT,"Create a new selection"),
	SELECT_DEL(CommandTypeGroup.SELECT,"Deletes a selection"),
	SELECT_ADD(CommandTypeGroup.SELECT,"Add a selection to another selection"),
	
	SELECT_LOAD(CommandTypeGroup.SELECT,"Load a selection from file or network"),
	SELECT_SAVE(CommandTypeGroup.SELECT,"Save a selection to file or network"),
	SELECT_SET_UPDATE(CommandTypeGroup.SELECT,"Update an existing IVirtualArray without changing its type."),
	SELECT_CHANGE_TYPE(CommandTypeGroup.SELECT,"Change the type of an existing IVirtualArray."),
	
	
	// ... IVirtualArray Parameters ...
	SELECT_SET_OFFSET(CommandTypeGroup.SELECT_VALUE,"ISet offset of selection (int)"),
	SELECT_SET_SIZE(CommandTypeGroup.SELECT_VALUE,"ISet size of selection"),
	SELECT_SET_INC(CommandTypeGroup.SELECT_VALUE,"ISet increment of selection (int)"),
	SELECT_SET_REPEAT(CommandTypeGroup.SELECT_VALUE,"ISet repeat of selection"),
	
	
	// ... IVirtualArray States ...
	SELECT_SHOW(CommandTypeGroup.SELECT_VALUE,"Show a selection, enable show/hide"),
	SELECT_HIDE(CommandTypeGroup.SELECT_VALUE,"Hide a selection, disable show/hide"),
	SELECT_LOCK(CommandTypeGroup.SELECT_VALUE,"Lock a selection, enable lock/unlock"),
	SELECT_UNLOCK(CommandTypeGroup.SELECT_VALUE,"Unlock a selection, disable lock/unlock"),
	
	
	// ... Server ...
	SRV_QUIT(CommandTypeGroup.SERVER,"Close server application"),
	SRV_START(CommandTypeGroup.SERVER,"Start server services"),
	SRV_STOP(CommandTypeGroup.SERVER,"Stop server services"),
	SRV_RELOAD(CommandTypeGroup.SERVER,
			"Stop server services, Reload configuration, Start server services"),	
			
	// ... System commands ...
	SYSTEM_EXIT(CommandTypeGroup.SYSTEM,"Close application"),
	SYSTEM_NOP(CommandTypeGroup.SYSTEM,"no operation"),
	SYSTEM_NEW_FRAME(CommandTypeGroup.SYSTEM,"new Frame"),
	
	WINDOW_POPUP_CREDITS(CommandTypeGroup.WINDOW,"About window wiht credits"),
	WINDOW_POPUP_INFO(CommandTypeGroup.WINDOW,"General info in a popup window"),
	WINDOW_POPUP_ERROR_MSG(CommandTypeGroup.WINDOW,"Error message popup window"),
	WINDOW_POPUP_PROGRESS_BAR(CommandTypeGroup.WINDOW,"Progress bar"),
	WINDOW_NEW_INTERNAL_FRAME(CommandTypeGroup.WINDOW,"Opens a new internal frame"),
	WINDOW_IFRAME_OPEN_HEATMAP2D(CommandTypeGroup.WINDOW,"Opens a new Heatmap inside an internal Frame"),
	WINDOW_IFRAME_OPEN_HISTOGRAM2D(CommandTypeGroup.WINDOW,"Opens a new Histogram inside an internal Frame"),
	WINDOW_IFRAME_OPEN_SCATTERPLOT2D(CommandTypeGroup.WINDOW,"Opens a new Scatterplot inside an internal Frame"),
	WINDOW_IFRAME_NEW_INTERNAL_FRAME(CommandTypeGroup.WINDOW,"Opens a new internal Frame"),
	WINDOW_IFRAME_OPEN_SELECTION(CommandTypeGroup.WINDOW,"Opens a new internal Frame for selection"),
	WINDOW_IFRAME_OPEN_STORAGE(CommandTypeGroup.WINDOW,"Opens a new internal Frame for storage"),
	WINDOW_IFRAME_OPEN_SET(CommandTypeGroup.WINDOW,"Opens a new internal Frame for set"),
	WINDOW_IFRAME_OPEN_JOGL_CANVAS(CommandTypeGroup.WINDOW,"Opens a new internal Frame with a Jogl canvas"),
	WINDOW_IFRAME_OPEN_JOGL_HISTOGRAM(CommandTypeGroup.WINDOW,"Opens a new internal Frame with a Jogl Histogram"),	
	WINDOW_IFRAME_OPEN_JOGL_HEATMAP(CommandTypeGroup.WINDOW,"Opens a new internal Frame with a Jogl Heatmap"),
	WINDOW_IFRAME_OPEN_JOGL_SCATTERPLOT(CommandTypeGroup.WINDOW,"Opens a new internal Frame with a Jogl Scatterplot"),	
	WINDOW_SET_ACTIVE_FRAME(CommandTypeGroup.WINDOW,"Sets a new active Frame"),
	WINDOW_NEW(CommandTypeGroup.WINDOW,"Create a new window"),
	WINDOW_OPEN(CommandTypeGroup.WINDOW,"Opens a new window"),
	WINDOW_CLOSE(CommandTypeGroup.WINDOW,"Close an existing window"),
	WINDOW_MINIMIZE(CommandTypeGroup.WINDOW,"Minimize an existing window"),
	WINDOW_MAXIMIZE(CommandTypeGroup.WINDOW,"Maximize an existing window");
	
	
	

	/**
	 * Brief description, what the command does.
	 */
	private final String sDescription;
	
	/**
	 * Group the command belongs to.
	 * 
	 * Each command is part of a group handling similar things.
	 */
	private final CommandTypeGroup enumGroup;
	
	CommandType( CommandTypeGroup enumSetGroup, String sSetDescription ) {
		sDescription = sSetDescription;
		enumGroup = enumSetGroup;
	}
	
	/**
	 * Get brief description of what the command does.
	 * 
	 * @return brief description of the command
	 */
	public String getDescription() {
		return sDescription;
	}
	
	/**
	 * Get the groupId.
	 * 
	 * Each command belongs to a group of commands doing similar things.
	 * 
	 * @return groupID
	 */
	public CommandTypeGroup getGroup() {
		return enumGroup;
	}
	
}
