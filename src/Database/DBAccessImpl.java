package Database;

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
	public int addNotebook(int parentNotebookID, String name) throws DBAddException {
		// TODO Auto-generated method stub
		return 0;
	}

}
