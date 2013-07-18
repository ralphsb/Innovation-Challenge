package Database;

import DataModel.Notebook;

/**
 * External users of this package should use this interface to make calls to the
 * database. No arbitrary SQL should ever be executed
 * @author Ralph
 *
 */
public interface DBAccess {

	/**
	 * Adds a notebook as a child of the specified notebook
	 * @param parentNotebookID the ID number of the parent notebook
	 * @param notebook the new notebook
	 * @return the ID number of the notebook you just added
	 * @throws DBAddException
	 */
	public int addNotebook(int parentNotebookID, Notebook notebook) throws DBAddException;
	
	
	/**
	 * Singleton provided for convenience.
	 * @author Ralph
	 *
	 */
	public class Singlton{
		
		private static DBAccess instance;
		
		public static DBAccess getInstance(){
			if(instance == null){
				instance = new DBAccessImpl();
			}
			return instance;
		}
	}
	
}
