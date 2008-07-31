package org.caleydo.core.view.swt.mixer;

import java.util.ArrayList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;

/**
 * Class implements a slider mixer representation. The slider stack can contain
 * arbitrary slider elements that are accessible by a index. Class is prepared
 * to work together with a connected MIDI device.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class MixerViewRep
	extends AView
	implements IView
{

	protected ArrayList<Slider> sliderList;

	protected int iNumberOfSliders = 0;

	public MixerViewRep(IGeneralManager generalManager, int iViewId, int iParentContainerId,
			String sLabel)
	{

		super(generalManager, iViewId, iParentContainerId, sLabel, ViewType.SWT_MIXER);
	}

	/**
	 * In the init method the sliders are created. The sliders are added to the
	 * slider list. Minimum slider value = 0. Maximum slider value = 100. We use
	 * a fill layout to fill the available space optimally.
	 * 
	 * @see org.caleydo.core.view.IView#initView()
	 */
	protected void initViewSwtComposit(Composite swtContainer)
	{

		sliderList = new ArrayList<Slider>();
		swtContainer.setLayout(new FillLayout(SWT.HORIZONTAL));

		Slider tmpSlider = null;
		for (int iSliderIndex = 0; iSliderIndex < iNumberOfSliders; iSliderIndex++)
		{
			tmpSlider = new Slider(swtContainer, SWT.VERTICAL);
			tmpSlider.setMinimum(0);
			tmpSlider.setMaximum(100);
			tmpSlider.setSelection(50);
			// tmpSlider.setSize((int)((float)iWidth/iNumberOfSliders),
			// iHeight);
			sliderList.add(tmpSlider);
		}
	}

	public void drawView()
	{

	}

	/**
	 * Method sets the slider to the value of iSliderValue by a given slider
	 * index in the matrix slider stack. If the value is smaller than the
	 * minimum value or bigger than the max value the value is clipped to min or
	 * max respectively.
	 * 
	 * @param iSliderIndex
	 * @param iSliderValue
	 */
	public void setSliderValueByIndex(int iSliderIndex, int iSliderValue)
	{

		try
		{
			sliderList.get(iSliderIndex).setSelection(iSliderValue);

		}
		catch (Exception e)
		{
			throw new CaleydoRuntimeException("Mixer Slider with index " + iSliderIndex
					+ " does not exist!");
		}
	}

	/**
	 * Method returns a slider value by a given slider index in the matrix
	 * slider stack.
	 * 
	 * @param iSliderIndex
	 * @return
	 */
	public int getSliderValueByIndex(int iSliderIndex)
	{

		try
		{
			return sliderList.get(iSliderIndex).getSelection();

		}
		catch (Exception e)
		{
			throw new CaleydoRuntimeException("Mixer Slider with index " + iSliderIndex
					+ " does not exist!");
		}
	}

	/**
	 * Method return the number of sliders in the mixer view.
	 * 
	 * @return Number of sliders in the mixer.
	 */
	public int getSliderDimension()
	{

		return iNumberOfSliders;
	}

	public void setAttributes(int iWidth, int iHeight, int iNumberOfSliders)
	{

		super.setAttributes(iWidth, iHeight);

		this.iNumberOfSliders = iNumberOfSliders;
	}
}
