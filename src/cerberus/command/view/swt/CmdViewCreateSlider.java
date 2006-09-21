package cerberus.command.view.swt;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreateGui;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.slider.SliderViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a slider view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateSlider 
extends ACmdCreateGui 
implements ICommand
{
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateSlider(
			final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler)
	{
		super(refGeneralManager, refParameterHandler);
	}

	/**
	 * Method creates a slider view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException
	{
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		SliderViewRep sliderView = (SliderViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_SLIDER,
							iUniqueTargetId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				sliderView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);

		sliderView.readInAttributes(refParameterHandler);
		
		sliderView.retrieveGUIContainer();
		sliderView.initView();
		sliderView.drawView();
	}
}
