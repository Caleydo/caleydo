/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.type;


/**
 * Group of Managers.
 * 
 * @see cerberus.manager.type.BaseManagerType
 * 
 * @author Michael Kalkusch
 *
 */
public enum BaseManagerGroupType {

		COMMAND(),
		FABRIK(),
		STORAGE(),
		SELECTION(),
		SET(),
		VIEW(),
		GUI_COMPONENT(),
		MEMENTO(),
		MENU();
}
