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
 * @see cerberus.manager.type.ManagerObjectType
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public enum ManagerType {

	SET("set manager", ManagerType.DATA),

	VIRTUAL_ARRAY("selection manager", ManagerType.DATA),

	STORAGE("storage manager", ManagerType.DATA),

	PATHWAY("pathway manager", ManagerType.DATA),

	PATHWAY_ELEMENT("pathway element manager", ManagerType.DATA),

	GENOME_ID("genome id manager", ManagerType.DATA),

	COMMAND("command manager"),

	LOGGER("logger manager"),

	VIEW_GL_CANVAS("view GL canvas manager"),

	GUI_SWT("SWT GUI manager"),

	GUI_SWING("Sing GUI manager"),

	DATA("abstract data manager"),

	EVENT_PUBLISHER("event publisher"),

	/**
	 * @deprecated replaced by "NONE"
	 */
	FABRIK("use 'NONE' instead!"),

	/**
	 * @deprecated repalced by "VIEW"
	 */
	GUI_COMPONENT("use 'VIEW' instead!"),

	/**
	 * @deprecated replaced by "VIEW_GL_CANVAS"
	 */
	VIEW("use 'VIEW_GL_CANVAS' instead!"),

	/**
	 * @deprecated
	 */
	MEMENTO("memento manager"),

	/**
	 * @deprecated
	 */
	MENU("view menu manager", ManagerType.VIEW),

	/**
	 * @deprecated
	 */
	SHAREDOBJECT("shared objects"),

	NONE("no type set");

	/*
	 * Remark describing window toolkit.
	 */
	private final String sRemark;

	private final ManagerType parentType;

	/**
	 * Constructor.
	 * 
	 * @param setRemark details on toolkit and version of toolkit.
	 */
	private ManagerType(String setRemark) {

		this.sRemark = setRemark;
		this.parentType = ManagerType.NONE;
	}

	/**
	 * Constructor.
	 * 
	 * @param setRemark details on toolkit and version of toolkit.
	 */
	private ManagerType(String setRemark, ManagerType parentType) {

		this.sRemark = setRemark;
		this.parentType = parentType;
	}

	/**
	 * Details on toolkit and required version of toolkit.
	 * 
	 * @return toolkit description adn version.
	 */
	public String getCommand() {

		return this.sRemark;
	}

	/**
	 * Return parent type or NONE if it is parent type.
	 * 
	 * @return paretn type or none
	 */
	public ManagerType getParentType() {

		return this.parentType;
	}

	/**
	 * Return TRUE if type is a parent type.
	 * 
	 * @return true if this type is a parent type
	 */
	public boolean isParentType() {

		return (parentType == ManagerType.NONE) ? true : false;
	}
}
