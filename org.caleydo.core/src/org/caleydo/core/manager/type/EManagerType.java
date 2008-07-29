package org.caleydo.core.manager.type;

/**
 * Group of Managers.
 * 
 * @see org.caleydo.core.manager.type.EManagerObjectType
 * 
 * @author Michael Kalkusch
 *
 */
public enum EManagerType {
		
		COMMAND("command manager", 99),
		
		DATA("abstract data manager", EManagerType.DATA, 50),
		
		DATA_GENOME_ID("genome id manager", EManagerType.DATA, 70 ),
				
		DATA_PATHWAY("pathway manager", EManagerType.DATA, 60),
		
		DATA_PATHWAY_ELEMENT("pathway element manager", EManagerType.DATA_PATHWAY, 61),
		
		DATA_GLYPH("glyph data manager", EManagerType.DATA, 40 ),
		
		DATA_SET("set manager", EManagerType.DATA, 51 ),		
		
		DATA_STORAGE("storage manager", EManagerType.DATA, 53 ),
		
		DATA_VIEWDATA("viewdata manager", EManagerType.DATA, 54 ),		
		
		DATA_VIRTUAL_ARRAY("virtual array manager", EManagerType.DATA, 55 ),
		
		DATA_SELECTION("selection manager", EManagerType.DATA, 56 ),		
		
		EVENT_PUBLISHER("event publisher", 80 ),
		
		LOGGER("logger manager", -1),
		
		MEMENTO("memento manager", 95),									
		
		NONE("no type set", -1),
		
		SYSTEM("run system or thread requests",7),
		
		VIEW("view manager", 30),
		
		// @deprecated
		VIEW_GUI_AWT("Sing GUI manager",EManagerType.VIEW, 31),
		
		// @deprecated
		VIEW_GUI_SWT("SWT GUI manager",EManagerType.VIEW, 32 ),	
		
		PICKING_MANAGER("Manage Picking for all Views", EManagerType.VIEW, 43),
		
		SELECTION_MANAGER("Manage selections for all views", EManagerType.VIEW, 44),
		
		VIEW_DISTRIBUTE_GUI("distributed GUI", 29);


		
		/*
		 * Remark describing window toolkit.
		 */
		private final String sRemark;
		
//		private final ManagerType parentType;
		
		private final int iIdOffsetType;
		
		/**
		 * Constructor.
		 * 
		 * @param setRemark details on toolkit and version of toolkit.
		 */
		private EManagerType(String setRemark,
				final int iSetIdOffsetType) {
			this.sRemark = setRemark;
//			this.parentType = ManagerType.NONE;
			this.iIdOffsetType = iSetIdOffsetType;
		}
		
		/**
		 * Constructor.
		 * 
		 * @param setRemark details on toolkit and version of toolkit.
		 */
		private EManagerType(String setRemark, 
				EManagerType parentType,
				final int iSetIdOffsetType) {
			this.sRemark = setRemark;
//			this.parentType = parentType;
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
		
//		/**
//		 * Return parent type or NONE if it is parent type.
//		 * 
//		 * @return paretn type or none
//		 */
//		public ManagerType getParentType() {
//			return this.parentType;	
//		}
		
//		/**
//		 * Return TRUE if type is a parent type.
//		 * 
//		 * @return true if this type is a parent type
//		 */
//		public boolean isParentType() {
//			return (parentType == ManagerType.NONE) ? true : false;	
//		}
		
		/**
		 * Get the postfix code of that Manager
		 *
		 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_TypeOffsetMultiplyer
		 * @see org.caleydo.core.manager.IGeneralManager
		 *
		 * @return [0..99]
		 */
		public int getId_OffsetType() {
			return iIdOffsetType;
		}
}
