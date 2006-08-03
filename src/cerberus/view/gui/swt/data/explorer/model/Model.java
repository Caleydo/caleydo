package cerberus.view.gui.swt.data.explorer.model;

public abstract class Model 
{	
	protected SetModel parent;
	protected int iID;
	protected String sLabel;	
	
	protected IDeltaListener listener = NullDeltaListener.getSoleInstance();
	
	public Model() 
	{
	}
	
	public Model(int iId, String sLabel) 
	{
		this.iID = iID;
		this.sLabel = sLabel;
	}
	
	protected void fireAdd(Object added) 
	{
		listener.add(new DeltaEvent(added));
	}

	protected void fireRemove(Object removed) 
	{
		listener.remove(new DeltaEvent(removed));
	}
	
	public void setLabel(String sLabel) 
	{
		this.sLabel = sLabel;
	}
	
	public String getLabel() 
	{
		return sLabel;
	}	
	
	public SetModel getParent() 
	{
		return parent;
	}
	
//	/* The receiver should visit the toVisit object and
//	 * pass along the argument. */
	public abstract void accept(IModelVisitor visitor, Object passAlongArgument);
//	
//	public String getName() {
//		return name;
//	}
	
	public void addListener(IDeltaListener listener) 
	{
		this.listener = listener;
	}
		
	
	public void removeListener(IDeltaListener listener) 
	{
		if(this.listener.equals(listener)) 
		{
			this.listener = NullDeltaListener.getSoleInstance();
		}
	}
}