package cerberus.view.swt.data.explorer.model;

public class DeltaEvent 
{
	protected Object actedUpon;
	
	public DeltaEvent(Object receiver) 
	{
		actedUpon = receiver;
	}
	
	public Object receiver() 
	{
		return actedUpon;
	}
}

