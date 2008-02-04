/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.manager.type;

/**
 * Group of Managers.
 * 
 * @see org.geneview.core.manager.type.ManagerObjectType
 * 
 * @author Michael Kalkusch
 *
 */
public enum ManagerType {
		
		COMMAND("command manager", 99),
		
		DATA("abstract data manager", ManagerType.DATA, 50),
		
		DATA_GENOME_ID("genome id manager", ManagerType.DATA, 70 ),
				
		DATA_PATHWAY("pathway manager", ManagerType.DATA, 60),
		
		DATA_PATHWAY_ELEMENT("pathway element manager", ManagerType.DATA_PATHWAY, 61),
		
		DATA_SET("set manager", ManagerType.DATA, 51 ),		
		
		DATA_STORAGE("storage manager", ManagerType.DATA, 53 ),
		
		DATA_VIEWDATA("viewdata manager", ManagerType.DATA, 53 ),		
		
		DATA_VIRTUAL_ARRAY("selection manager", ManagerType.DATA, 53 ),		
		
		EVENT_PUBLISHER("event publisher", 80 ),
		
		LOGGER("logger manager", -1),
		
		MEMENTO("memento manager", 95),		
		
		MENU("view menu manager", ManagerType.VIEW, 39 ),							
		
		NONE("no type set", -1),
		
		SINGELTON("onyl used for singelton", 1 ),
		
		SYSTEM("run system or thread requests",7),
		
		VIEW("view manager", 30),
		
		// @deprecated
		VIEW_GUI_AWT("Sing GUI manager",ManagerType.VIEW, 31),
		
		// @deprecated
		VIEW_GUI_SWT("SWT GUI manager",ManagerType.VIEW, 32 ),	
		
		PICKING_MANAGER("Manage Picking for all Views", ManagerType.VIEW, 43),
		
		VIEW_DISTRIBUTE_GUI("distributed GUI", 29);


		
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
		 * @see org.geneview.core.manager.IGeneralManager#iUniqueId_TypeOffsetMultiplyer
		 * @see org.geneview.core.manager.IGeneralManager
		 *
		 * @return [0..99]
		 */
		public int getId_OffsetType() {
			return iIdOffsetType;
		}
}
