package cbg.article.model;



public interface IModelVisitor {
	public void visitMovingBox(MovingBox box, Object passAlongArgument);
	public void visitBook(Book book, Object passAlongArgument);
	public void visitBoardgame(BoardGame boardgame, Object passAlongArgument);
}
