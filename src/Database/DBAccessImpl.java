package Database;

import DataModel.Notebook;

/**
 * A simple (non-hibernate based) implementation of the database interface.
 * @author Ralph
 *
 */
class DBAccessImpl implements DBAccess {

	/**
	 * The sql interface singleton.
	 */
	private SQLInterface sql;
	
	
	DBAccessImpl(){
		sql = SQLInterface.getSQLInterface();
	}
	
	@Override
	public int addNotebook(int parentNotebookID, Notebook notebook) throws DBAddException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	/**
	 * Queries the database for a notebook of the ID number supplied.
	 * @return true if the ID corresponds to a notebook, false otherwise
	 */
	private boolean notebookDoesExist(int id){
		// TODO
		return false;
	}

}
