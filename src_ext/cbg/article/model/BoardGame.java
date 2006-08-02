package cbg.article.model;
public class BoardGame extends Model {
	
	public BoardGame(String title, String authorGivenName, String authorSirName) {
		super(title, authorGivenName, authorSirName);
	}
	
	
	
	
	
	
	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitBoardgame(this, passAlongArgument);
	}

}
