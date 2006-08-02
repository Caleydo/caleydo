package cerberus.view.gui.swt.data.explorer;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

import cerberus.manager.GeneralManager;
import cerberus.manager.SetManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.ViewInter;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

public class DataExplorerViewRep implements ViewInter
{
	protected final int iNewId;
	protected GeneralManager refGeneralManager;
	protected Composite refSWTContainer;
	
	protected TreeViewer treeViewer;
	
	public DataExplorerViewRep(int iNewId, GeneralManager refGeneralManager)
	{
		this.iNewId = iNewId;
		this.refGeneralManager = refGeneralManager;
				
		retrieveNewGUIContainer();
		initView();
		drawView();
	}
	
	public void initView()
	{
		treeViewer = new TreeViewer(refSWTContainer);
		//treeViewer.setContentProvider(new MovingBoxContentProvider());
		//treeViewer.setLabelProvider(new MovingBoxLabelProvider());
		treeViewer.setInput(getInitalInput());
		treeViewer.expandAll();	
	}

	public void drawView()
	{
		// TODO Auto-generated method stub
		
	}

	public void retrieveNewGUIContainer()
	{
		SWTNativeWidget refSWTNativeWidget = 
			(SWTNativeWidget)refGeneralManager.getSingelton()
		.getSWTGUIManager().createWidget(ManagerObjectType.GUI_SWT_NATIVE_WIDGET);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();
		
	}

	public void retrieveExistingGUIContainer()
	{
		// TODO Auto-generated method stub
		
	}
	
    protected void getInitalInput() {
//        MovingBox root = new MovingBox();
//        MovingBox books = new MovingBox("Books");
//        MovingBox games = new MovingBox("Games");
//        MovingBox books2 = new MovingBox("More books");
//        MovingBox games2 = new MovingBox("More games");
//        
//        root.addBox(books);
//        root.addBox(games);
//        root.addBox(new MovingBox());
//        
//        books.addBox(books2);
//        games.addBox(games2);
//        
//        books.addBook(new Book("The Lord of the Rings", "J.R.R.", "Tolkien"));
//        books.addBoardGame(new BoardGame("Taj Mahal", "Reiner", "Knizia"));
//        books.addBook(new Book("Cryptonomicon", "Neal", "Stephenson"));
//        books.addBook(new Book("Smalltalk, Objects, and Design", "Chamond", "Liu"));
//        books.addBook(new Book("A Game of Thrones", "George R. R.", " Martin"));
//        books.addBook(new Book("The Hacker Ethic", "Pekka", "Himanen"));
//        //books.addBox(new MovingBox());
//        
//        books2.addBook(new Book("The Code Book", "Simon", "Singh"));
//        books2.addBook(new Book("The Chronicles of Narnia", "C. S.", "Lewis"));
//        books2.addBook(new Book("The Screwtape Letters", "C. S.", "Lewis"));
//        books2.addBook(new Book("Mere Christianity ", "C. S.", "Lewis"));
//        games.addBoardGame(new BoardGame("Tigris & Euphrates", "Reiner", "Knizia"));        
//        games.addBoardGame(new BoardGame("La Citta", "Gerd", "Fenchel"));
//        games.addBoardGame(new BoardGame("El Grande", "Wolfgang", "Kramer"));
//        games.addBoardGame(new BoardGame("The Princes of Florence", "Richard", "Ulrich"));
//        games.addBoardGame(new BoardGame("The Traders of Genoa", "Rudiger", "Dorn"));
//        games2.addBoardGame(new BoardGame("Tikal", "M.", "Kiesling"));
//        games2.addBoardGame(new BoardGame("Modern Art", "Reiner", "Knizia"));       
//        return root;
     }



}
