package cerberus.view.swt.data.explorer.model;

public interface IDeltaListener 
{
	public void add(DeltaEvent event);
	public void remove(DeltaEvent event);
}
