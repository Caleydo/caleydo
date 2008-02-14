package org.geneview.core.view.opengl.canvas.parcoords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.util.exception.GeneViewRuntimeExceptionType;


/**
 * 
 * @author Alexander Lex
 * 
 * Rewrite with hashmap<enum, hashmap> ?
 *
 */


public class PolylineSelectionManager 
{
	
	private enum RenderMode
	{
		NORMAL,
		SELECTION,
		MOUSE_OVER,
		DESELECTED
	}

	
	private HashMap<Integer, Boolean> hashNormalPolylines;
	private HashMap<Integer, Boolean> hashSelectedPolylines;
	private HashMap<Integer, Boolean> hashMouseOverPolylines;
	private HashMap<Integer, Boolean> hashDeselectedPolylines;
	
	
	public PolylineSelectionManager() 
	{	
		 hashNormalPolylines = new HashMap<Integer, Boolean>();
		 hashSelectedPolylines = new HashMap<Integer, Boolean>();
		 hashMouseOverPolylines = new HashMap<Integer, Boolean>();
		 hashDeselectedPolylines = new HashMap<Integer, Boolean>();
	}
	
	public Boolean initialAdd(int iPolylineID)
	{		
		return hashNormalPolylines.put(iPolylineID, true);
	}
	
	public void clearAll()
	{
		 hashNormalPolylines.clear();
		 hashSelectedPolylines.clear();
		 hashMouseOverPolylines.clear();
		 hashDeselectedPolylines.clear();
	}
	
	public Set<Integer> getNormalPolylines()
	{
		return hashNormalPolylines.keySet();
	}
	
	public Set<Integer> getSelectedPolylines()
	{
		return hashSelectedPolylines.keySet();
	}	
	
	public Set<Integer> getMouseOverPolylines()
	{
		return hashMouseOverPolylines.keySet();
	}
	
	public Set<Integer> getDeselectedPolylines()
	{
		return hashDeselectedPolylines.keySet();
	}
	
	public void addSelection(int iPolylineID)
	{
		
		checkAndRemoveAllOthers(iPolylineID, RenderMode.SELECTION);			
		hashSelectedPolylines.put(iPolylineID, true);
	}
	
	public void removeSelection(int iPolylineID)
	{
		if(hashSelectedPolylines.remove(iPolylineID) == null)
		{
			throw new GeneViewRuntimeException(
					"PolylineSelectionManger: tried to remove selection from polyline that was not selected",
					GeneViewRuntimeExceptionType.VIEW);			
		}
		
		hashNormalPolylines.put(iPolylineID, true);		
	}
	
	public void clearSelection()
	{
		hashNormalPolylines.putAll(hashSelectedPolylines);
		hashSelectedPolylines.clear();
	}
	
	public void addMouseOver(int iPolylineID)
	{
		checkAndRemoveAllOthers(iPolylineID, RenderMode.MOUSE_OVER);			
		hashMouseOverPolylines.put(iPolylineID, true);
	}
	
	public void removeMouseOver(int iPolylineID)
	{
		if(hashMouseOverPolylines.remove(iPolylineID) == null)
		{
			throw new GeneViewRuntimeException(
					"PolylineSelectionManger: tried to remove mouse over from polyline that was not in mouse over",
					GeneViewRuntimeExceptionType.VIEW);			
		}
		
		hashNormalPolylines.put(iPolylineID, true);		
	}
	
	public void clearMouseOver()
	{
		hashNormalPolylines.putAll(hashMouseOverPolylines);
		hashMouseOverPolylines.clear();
	}
	
	public void addDeselection(int iPolylineID)
	{
		checkAndRemoveAllOthers(iPolylineID, RenderMode.DESELECTED);			
		hashDeselectedPolylines.put(iPolylineID, true);
	}
	
	public void removeDeselection(int iPolylineID)
	{
		if(hashDeselectedPolylines.remove(iPolylineID) == null)
		{
			throw new GeneViewRuntimeException(
					"PolylineSelectionManger: tried to remove deselected polyline that was not deselected",
					GeneViewRuntimeExceptionType.VIEW);			
		}
		
		hashDeselectedPolylines.put(iPolylineID, true);		
	}
	
	public void clearDeselection()
	{
		hashNormalPolylines.putAll(hashDeselectedPolylines);
		hashDeselectedPolylines.clear();
	}
	
	
	private boolean checkAndRemoveAllOthers(int iPolylineID, RenderMode eRenderMode)
	{
		short countIsRemoved = 0;
		if (eRenderMode != RenderMode.NORMAL)
		{		
			if(hashNormalPolylines.get(iPolylineID) != null)
			{
				hashNormalPolylines.remove(iPolylineID);
				countIsRemoved++;
			}
		}
		
		if (eRenderMode != RenderMode.SELECTION)
		{		
			if(hashSelectedPolylines.get(iPolylineID) != null)
			{
				hashSelectedPolylines.remove(iPolylineID);
				countIsRemoved++;
			}
		}
		
		if (eRenderMode != RenderMode.MOUSE_OVER)
		{		
			if(hashMouseOverPolylines.get(iPolylineID) != null)
			{
				hashMouseOverPolylines.remove(iPolylineID);
				countIsRemoved++;
			}
		}
		
		if (eRenderMode != RenderMode.DESELECTED)
		{		
			if(hashDeselectedPolylines.get(iPolylineID) != null)
			{
				hashDeselectedPolylines.remove(iPolylineID);
				countIsRemoved++;
			}
		}
		
		if(countIsRemoved > 1)
		{
			throw new GeneViewRuntimeException(
					"PolylineSelectionManager: more polylines removed than allowed to exist",
					GeneViewRuntimeExceptionType.VIEW);
		}
		else if(countIsRemoved < 1)
		{
			if (checkIfInHashMap(iPolylineID, eRenderMode))
				return false;
			else
			{
				throw new GeneViewRuntimeException(
						"PolylineSelectionManager: polyline to be removed does not exist",
						GeneViewRuntimeExceptionType.VIEW);
			}			
		}
		else
		{
			return true;
		}		
	}
	
	private boolean checkIfInHashMap(int iPolylineID, RenderMode eRenderMode)
	{
		boolean isInHashMap = false;
		
		if (eRenderMode == RenderMode.NORMAL)
		{		
			if(hashNormalPolylines.get(iPolylineID) != null)
			{
				isInHashMap = true;
			}
		}		
		else if (eRenderMode == RenderMode.SELECTION)
		{		
			if(hashSelectedPolylines.get(iPolylineID) != null)
			{
				isInHashMap = true;
			}
		}
		
		if (eRenderMode == RenderMode.MOUSE_OVER)
		{		
			if(hashMouseOverPolylines.get(iPolylineID) != null)
			{
				isInHashMap = true;
			}
		}
		
		if (eRenderMode == RenderMode.DESELECTED)
		{		
			if(hashDeselectedPolylines.get(iPolylineID) != null)
			{
				isInHashMap = true;
			}
		}
		
		return isInHashMap;
	}
	
}
