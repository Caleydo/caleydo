package org.caleydo.view.visbricks.brick;

import org.caleydo.core.manager.picking.Pick;

public interface IPickingListener {
	
	public void clicked(Pick pick);
	
	public void doubleClicked(Pick pick);
	
	public void rightClicked(Pick pick);
	
	public void mouseOver(Pick pick);
	
	public void dragged(Pick pick);

}
