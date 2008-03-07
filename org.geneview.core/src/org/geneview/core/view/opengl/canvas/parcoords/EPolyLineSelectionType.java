package org.geneview.core.view.opengl.canvas.parcoords;


public enum EPolyLineSelectionType
{
	NORMAL ("NORMAL"),
	SELECTION ("SELECTION"),
	MOUSE_OVER ("MOUSE_OVER"),
	DESELECTED ("DESELECTED");
	
	private String sType;
	//private static ArrayList<String> alSelectionType;
	
	EPolyLineSelectionType(String sType)
	{
		this.sType = sType;
	}
	
	public String getString()
	{
		return sType;
	}
	
//	public static ArrayList<String> getSelectionTypes()
//	{
////		if(alSelectionType == null)
////		{
//			alSelectionType = new ArrayList<String>();
////			for(EPolyLineSelectionType selectionType : EPolyLineSelectionType.values())
////			{
//			//	alSelectionType.add(selectionType.getSelectionType());
////			}
//			// FIXXME
//			alSelectionType.add("NORMAL");
//			alSelectionType.add("SELECTION");
//			alSelectionType.add("MOUSE_OVER");
//			alSelectionType.add("DESELECTED");
//			return alSelectionType;
////		}
////		else
////			return alSelectionType;
//		
//	}
}
