package org.caleydo.core.view.swt.data.explorer.model;

public class SelectionModel 
extends AModel {
	
	public SelectionModel(int iId, String sLabel) {

		super(iId, sLabel);
	}

	@Override
	public void accept(IModelVisitor visitor, Object passAlongArgument) {

		visitor.visitSelectionModel(this, passAlongArgument);
	}
}
