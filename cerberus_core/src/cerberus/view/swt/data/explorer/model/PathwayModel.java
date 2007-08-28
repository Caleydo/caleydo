package cerberus.view.swt.data.explorer.model;

public class PathwayModel 
extends AModel {

	public PathwayModel(int iId, String sLabel) {

		super(iId, sLabel);
	}

	@Override
	public void accept(IModelVisitor visitor, Object passAlongArgument) {

		visitor.visitPathwayModel(this, passAlongArgument);
	}

}
