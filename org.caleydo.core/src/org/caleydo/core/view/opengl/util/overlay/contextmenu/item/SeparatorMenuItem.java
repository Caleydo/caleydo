package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenuItem;
import org.eclipse.swt.SWT;

/**
 * Separator context menu item.
 * 
 * @author Marc Streit
 */
public class SeparatorMenuItem
	extends ContextMenuItem {

	public SeparatorMenuItem() {
		setStyle(SWT.SEPARATOR);
	}

}
