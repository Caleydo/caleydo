package cerberus.command.view.swt;

import java.util.StringTokenizer;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.swt.slider.SelectionSliderViewRep;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a slider view 
 * that is able to change selection data.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateSelectionSlider 
extends ACmdCreate_IdTargetLabelParentXY 
implements ICommand {
	
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateSelectionSlider(
			final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler) {
		
		super(refGeneralManager, refParameterHandler);
		
		setAttributes(refParameterHandler);
	}

	/**
	 * Method creates a slider view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		SelectionSliderViewRep sliderView = (SelectionSliderViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_SELECTION_SLIDER,
							iUniqueTargetId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				sliderView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);

		sliderView.readInAttributes(refParameterHandler);
		
		sliderView.extractAttributes();
		sliderView.retrieveGUIContainer();
		sliderView.initView();
		sliderView.drawView();
	}

	protected void setAttributes( final IParameterHandler refParameterHandler ) {
		
		refParameterHandler.setValueAndTypeAndDefault("iSelectionId",
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_DETAIL.getXmlKey() ),
				IParameterHandler.ParameterHandlerType.INT,
				"0");
		
		refParameterHandler.setValueAndTypeAndDefault("sSelectionFieldName",
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey() ),
				IParameterHandler.ParameterHandlerType.STRING,
				"offset");
	}
	
	public void undoCommand() throws CerberusRuntimeException {

		// TODO Auto-generated method stub
	}
}
