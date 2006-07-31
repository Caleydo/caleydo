package cerberus.view.gui.swt.widget;

import org.eclipse.swt.widgets.Composite;

import cerberus.view.gui.Widget;

public class SWTNativeWidget extends Widget 
{
	Composite refComposite;
	
	//TODO: the parameter should be the parent composite
	public SWTNativeWidget(Composite refComposite)
	{
		this.refComposite = refComposite;
	}
	
	public Composite getSWTWidget()
	{
		return refComposite;
	}
}
