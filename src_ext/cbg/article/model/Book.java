package cbg.article.model;

import java.util.ArrayList;
import java.util.List;

public class Book extends Model {
	protected static List newBooks = buildBookList();
	protected static int cursor = 0;
	
	public Book(String title, String authorGivenName, String authorSirName) {
		super(title, authorGivenName, authorSirName);
	}
	
	
	
	
	public static Book newBook() {
		Book newBook = (Book)newBooks.get(cursor);
		cursor = ((cursor + 1) % newBooks.size());
		return newBook;
	}
	
	
	protected static List buildBookList() {
		newBooks = new ArrayList();
		Book[] books = new Book[] {
			new Book("Advanced Java: Idioms, Pitfalls, Styles and Programming Tips", "Chris", "Laffra"),
			new Book("Programming Ruby: A Pragmatic Programmer's Guide", "David", "Thomas"),
			new Book("The Pragmatic Programmer", "Andrew", "Hunt"),
			new Book("Java Virtual Machine", "Jon", "Meyer"),
			new Book("Using Netscape IFC", "Arun", "Rao"),
			new Book("Smalltalk-80", "Adele", "Goldberg"),
			new Book("Cold Mountain", "Charles", "Frazier"),
			new Book("Software Development Using Eiffel", "Richard", "Wiener"),
			new Book("Winter's Heart", "Robert", "Jordan"),
			new Book("Ender's Game", "Orson Scott", "Card"),
			new Book("Castle", "David", "Macaulay"),
			new Book("Cranberry Thanksgiving", "Wende", "Devlin"),
			new Book("The Biggest Bear", "Lynd", "Ward"),
			new Book("The Boxcar Children", "Gertrude Chandler", "Warner"),
			new Book("BASIC Fun with Adventure Games", "Susan Drake", "Lipscomb"),
			new Book("Bridge to Terabithia", "Katherine", "Paterson"),
			new Book("One Renegade Cell", "Robert A.", "Weinberg"),
			new Book("Programming Internet Mail", "David", "Wood"),
			new Book("Refactoring", "Martin", "Fowler"),
			new Book("Effective Java", "Joshua", "Bloch"),
			new Book("Cutting-Edge Java Game Programming", "Neil", "Bartlett"),
			new Book("The C Programming Language", "Brian W.", "Kernighan"),
			new Book("The Design and Analysis of Spatial Data Structures", "Hanan", "Samet"),
			new Book("Object-Oriented Programming", "Brad", "Cox"),
			new Book("Python Essential Reference", "David M.", "Beazley"),
			new Book("The Practical SQL Handbook", "Judith S.", "Bowman"),
			new Book("The Design Patterns Smalltalk Companion", "Sherman R.", "Alpert"),
			new Book("Design Patterns", "Erich", "Gamma"),
			new Book("Gig", "John", "Bowe"),
			new Book("You Can't Be Too Careful", "David Pryce", "Jones"),
			new Book("Go for Beginners", "Kaoru", "Iwamoto"),
			new Book("How to Read a Book", "Mortimer J.", "Adler"),
			new Book("The Message", "Eugene H.", "Peterson"),
			new Book("Beyond Bumper Sticker Ethics", "Steve", "Wilkens"),
			new Book("Life Together", "Dietrich", "Bonhoeffer"),
			new Book("Java 2 Exam Cram", "William", "Brogden")
		};
		
		for (int i = 0; i < books.length; i++) {
			newBooks.add(books[i]);
			
		}
		return newBooks;
	}
	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitBook(this, passAlongArgument);
	}

}
