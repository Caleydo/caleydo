package cbg.article.model;
public abstract class Model {
	protected MovingBox parent;
	protected String name;
	protected String authorGivenName, authorSirName;	
	protected IDeltaListener listener = NullDeltaListener.getSoleInstance();
	
	protected void fireAdd(Object added) {
		listener.add(new DeltaEvent(added));
	}

	protected void fireRemove(Object removed) {
		listener.remove(new DeltaEvent(removed));
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public MovingBox getParent() {
		return parent;
	}
	
	/* The receiver should visit the toVisit object and
	 * pass along the argument. */
	public abstract void accept(IModelVisitor visitor, Object passAlongArgument);
	
	public String getName() {
		return name;
	}
	
	public void addListener(IDeltaListener listener) {
		this.listener = listener;
	}
	
	public Model(String title, String authorGivenName, String authorSirName) {
		this.name = title;
		this.authorGivenName = authorGivenName;
		this.authorSirName = authorSirName;
	}
	
	public Model() {
	}	
	
	public void removeListener(IDeltaListener listener) {
		if(this.listener.equals(listener)) {
			this.listener = NullDeltaListener.getSoleInstance();
		}
	}

	public String authorGivenName() {
		return authorGivenName;
	}


	public String authorSirName() {
		return authorSirName;
	}

	public String getTitle() {
		return name;
	}


}
