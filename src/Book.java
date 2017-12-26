import java.sql.Date;

/**
 * This object represents the data associated with a book in the library.
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class Book {
	
	/** Book fields */
	public String title, author, ISBN, placeOfPublication, subject, publisher;
	public int edition, copyrightYear, shelfNumber, copyNumber;
	public double cost;
	public boolean isReserved, isCheckedOut, isOnHold;
	
	/**
	 * Construct a new instance of a Book object
	 * @param title Title of the book
	 * @param Author Author of the book
	 * @param ISBN ISBN number of the book
	 * @param cost Cost of the book
	 * @param placeOfPublication Where the book was published
	 * @param subject The subject of the book
	 * @param shelfNumber The shelf number where the book is located in library
	 * @param isReserved Is this book reserved?
	 * @param edition The edition of the book
	 * @param publisher The publisher of the book
	 * @param copyrightYear The year this book was copyrighted
	 */
	public Book(String title, String ISBN, boolean isReserved, int copyNumber, boolean isCheckedOut, boolean isOnHold) {
		this.title = title;
		this.ISBN = ISBN;
		this.isReserved = isReserved;
		this.copyNumber = copyNumber;
		this.isCheckedOut = isCheckedOut;
		this.isOnHold = isOnHold;
	}
	
	/**
	 * Print out the book details
	 * @return Lots of information about the book
	 */
	public String toString() {
		String s = "Book: " + title;
		s += "\n\tISBN:       " + ISBN;
		s += "\n\tisReserved: " + isReserved;
		s += "\n\tCopyNumber: " + copyNumber;
		s += "\n\tCheckedOut: " + isCheckedOut;
		s += "\n\tOnHold:     " + isOnHold;
		return s;
	}
}
