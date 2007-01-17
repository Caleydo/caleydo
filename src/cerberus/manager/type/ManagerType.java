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
		
		COMMAND("command manager", 99),
		
		MEMENTO("memento manager", 95),
		
		LOGGER("logger manager", -1),
				
		VIEW_GL_CANVAS("view GL canvas manager", 35),	
		
		VIEW("view manager", 30),
		
		GUI_AWT("Sing GUI manager", 31),
		
		GUI_SWT("SWT GUI manager",ManagerType.VIEW, 32 ),		
		
		GENOME_ID("genome id manager", ManagerType.DATA, 70 ),
		
		D_GUI("distributed GUI", 29),
		
		MENU("view menu manager", ManagerType.VIEW, 39 ),
		
		DATA("abstract data manager", ManagerType.DATA, 50),
		
		SET("set manager", ManagerType.DATA, 51 ),
		
		VIRTUAL_ARRAY("selection manager", ManagerType.DATA, 53 ),
		
		STORAGE("storage manager", ManagerType.DATA, 53 ),
		
		SHAREDOBJECT("shared objects", ManagerType.DATA, 59 ),
				
		SINGELTON("onyl used for singelton", 1 ),
		
		PATHWAY("pathway manager", 60),
		
		PATHWAY_ELEMENT("pathway element manager", ManagerType.PATHWAY, 61),
		
		EVENT_PUBLISHER("event publisher", 80 ),
							
		NONE("no type set", -1);
		

//		/**
//		 * @deprecated repalced by "VIEW"
//		 */
//		GUI_COMPONENT("use 'VIEW' instead!");


		
		/*
		 * Remark describing window toolkit.
		 */
		private final String sRemark;
		
		private final ManagerType parentType;
		
		private final int iIdOffsetType;
		
		/**
		 * Constructor.
		 * 
		 * @param setRemark details on toolkit and version of toolkit.
		 */
		private ManagerType(String setRemark,
				final int iSetIdOffsetType) {
			this.sRemark = setRemark;
			this.parentType = ManagerType.NONE;
			this.iIdOffsetType = iSetIdOffsetType;
		}
		
		/**
		 * Constructor.
		 * 
		 * @param setRemark details on toolkit and version of toolkit.
		 */
		private ManagerType(String setRemark, 
				ManagerType parentType,
				final int iSetIdOffsetType) {
			this.sRemark = setRemark;
			this.parentType = parentType;
			this.iIdOffsetType = iSetIdOffsetType;
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
		
		/**
		 * Get the postfix code of that Manager
		 *
		 * @see cerberus.manager.IGeneralManager#iUniqueId_TypeOffsetMultiplyer
		 * @see cerberus.manager.IGeneralManager
		 *
		 * @return [0..99]
		 */
		public int getId_OffsetType() {
			return iIdOffsetType;
		}
}
