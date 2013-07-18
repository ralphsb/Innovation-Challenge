package Database;

import DataModel.Note;
import DataModel.Notebook;

/**
 * External users of this package should use this interface to make calls to the
 * database. No arbitrary SQL should ever be executed
 * @author Ralph
 *
 */
public interface DBAccess {

	/**
	 * Adds a notebook as a child of the specified notebook. As of this writing, this
	 * method is NOT designed to recursively add whatever children the notebook may have
	 * @param parentNotebookID the ID number of the parent notebook
	 * @param notebook the new notebook
	 * @return the ID number of the notebook you just added
	 * @throws DBAddException
	 */
	public int addNotebook(int parentNotebookID, Notebook notebook) throws DBAddException;
	
	/**
	 * Gets the file system object with the inputed ID number
	 * @param notebookID the id of the notebook
	 * @return the notebook or null if none exists
	 */
	public Notebook getNotebook(int notebookID) throws DBException;
	
	/**
	 * Adds the inputed note to the database. Disregards whatever is in the id field of the
	 * note, and returns the id number under which the note was added
	 * @param parentNotebookID the id of the parent notebook
	 * @param note the note to add
	 * @return the id number the note was added under
	 * @throws DBException 
	 */
	public int addNote(int parentNotebookID, Note note) throws DBException;
	
	/**
	 * Gets the note that corresponds with the inputed note id number
	 * @param noteID the id number of the note
	 * @return the note that corresponds to that id or null if none can be found
	 * @throws DBException
	 */
	public Note getNote(int noteID) throws DBException;
	
	
	/**
	 * Singleton provided for convenience.
	 * @author Ralph
	 *
	 */
	public class Singlton{
		
		private static DBAccess instance;
		
		public static DBAccess getInstance() throws DBException{
			if(instance == null){
				instance = new DBAccessImpl();
			}
			return instance;
		}
	}
	
}
