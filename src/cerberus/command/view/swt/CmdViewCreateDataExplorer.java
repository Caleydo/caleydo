package cerberus.command.view.swt;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.AViewRep.ViewType;
import cerberus.view.gui.swt.data.explorer.DataExplorerViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a data explorer view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateDataExplorer 
extends ACmdCreate_IdTargetLabelParentXY 
implements ICommand {
	
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 */
	public CmdViewCreateDataExplorer(
			final IGeneralManager refGeneralManager) {
		
		super(refGeneralManager);
	}

	/**
	 * Method creates a data explorer view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		DataExplorerViewRep dataExplorerView = (DataExplorerViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_DATA_EXPLORER,
							iUniqueTargetId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				dataExplorerView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);
		
		viewManager.addDataExplorerViewRep(dataExplorerView);
		
		dataExplorerView.setViewType(ViewType.DATA_EXPLORER);
		
		dataExplorerView.setAttributes(iWidthX, iHeightY);
		dataExplorerView.retrieveGUIContainer();
		dataExplorerView.initView();
		dataExplorerView.drawView();
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {

		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);
	}
	
	public void undoCommand() throws CerberusRuntimeException {
		
		// TODO Auto-generated method stub	
	}
}
