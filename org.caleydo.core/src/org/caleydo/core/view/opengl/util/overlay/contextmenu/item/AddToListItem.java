package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.data.selection.delta.VADeltaItem;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;
import org.caleydo.core.manager.event.view.storagebased.PropagationEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Item that adds a selected element to the list heat map for detailed inspection FIXME: this is not
 * implemented yet - only the skeleton is here
 * 
 * @author Alexander Lex
 */
public class AddToListItem
	extends AContextMenuItem {

	public AddToListItem(int iStorageIndex) {
		super();
		setIconTexture(EIconTextures.SAVE_TO_LIST_HEAT_MAP);
		setText("Save to list heat map");
		PropagationEvent event = new PropagationEvent();
		IVirtualArrayDelta delta = new VirtualArrayDelta(EIDType.EXPRESSION_INDEX);		
		delta.add(VADeltaItem.append(iStorageIndex));
		event.setVirtualArrayDelta(delta);
		registerEvent(event);
	}

}
