package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.slider.SliderViewRep;

/**
 * Class implementes the command for creating a slider view.
 * 
 * @author Marc Streit
 *
 */
public class CmdViewCreateSlider extends ACmdCreate implements
		ICommand
{
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateSlider(
			IGeneralManager refGeneralManager,
			final LinkedList<String> listAttributes)
	{
		super(refGeneralManager, listAttributes);
	}

	/**
	 * Method creates a slider view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException
	{
		SliderViewRep sliderView = (SliderViewRep) ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW)).createView(
				ManagerObjectType.VIEW_SWT_SLIDER, 
				iUniqueId, iParentContainerId, sLabel);

		sliderView.setAttributes(refVecAttributes);
		sliderView.extractAttributes();
		sliderView.retrieveGUIContainer();
		sliderView.initView();
		sliderView.drawView();
	}
}
