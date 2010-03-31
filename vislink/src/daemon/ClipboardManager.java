package daemon;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ClipboardManager {
	
	private String get(Clipboard clipboard){
		String selectionString = ""; 
		DataFlavor flavor = DataFlavor.stringFlavor;
		if (clipboard.isDataFlavorAvailable(flavor))
		{
			try
			{  
				selectionString = (String) clipboard.getData(flavor);
			}
			catch (UnsupportedFlavorException e)
			{  
				System.out.println("Unsupported flavor exception: " + e); 
			}
			catch (IOException e)
			{  
				System.out.println("IOException: "+e); 
			}
		}
		return selectionString; 
	}
	
	/**
	 * Returns the string currently in the system's selection clipboard
	 * (when selecting a text with the mouse). 
	 * @return Returns the clipboard selection string or an empty string. 
	 */
	public String getSelection(){
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemSelection();
		return this.get(clipboard); 
	}
	
	/**
	 * Returns the string currently in the system's clipboard 
	 * (when selecting a text and copying it to the clipboard). 
	 * @return Returns the clipboard string or an empty string. 
	 */
	public String getClipboard(){
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		return this.get(clipboard); 
	}

}
