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
 *
 */
public enum ManagerType {

		/**
		 * @deprecated replaced by "NONE"
		 */
		FABRIK("use 'NONE' instead!"),

		/**
		 * @deprecated repalced by "VIEW"
		 */
		GUI_COMPONENT("use 'VIEW' instead!"),

		
		COMMAND("command manager"),
		
		MEMENTO("memento manager"),
		
		LOGGER("logger manager"),
				
		VIEW("view manager"),				
		
		GUI_SWT("SWT GUI manager"),
		
		GUI_SWING("Sing GUI manager"),
		
		MENU("view menu manager", ManagerType.VIEW ),
		
		SHAREDOBJECT("shared objects"),
		
		DATA("abstract data manager"),
		
		SET("set manager", ManagerType.DATA ),
		
		SELECTION("selection manager", ManagerType.DATA ),
		
		STORAGE("storage manager", ManagerType.DATA ),
		
		PATHWAY("pathway manager", ManagerType.DATA ),
				
		NONE("no type set");
		
		/**
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
		private ManagerType(String setRemark, 
				ManagerType parentType) {
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
