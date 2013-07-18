package Database;

import java.sql.ResultSet;
import java.sql.SQLException;

import DataModel.Notebook;

/**
 * A simple (non-hibernate based) implementation of the database interface.
 * @author Ralph
 *
 */
final class DBAccessImpl implements DBAccess {

	private final static String SCHEMA_NAME = "InnoChallenge";
	
	/**
	 * The sql interface singleton.
	 */
	private SQLInterface sql;
	
	private int previousFSObjectID;
	
	
	DBAccessImpl() throws DBException{
		sql = SQLInterface.getSQLInterface();
		previousFSObjectID = getPreviousFSObjectID();
	}
	
	
	/**
	 * Returns the ID of the last FSObject created.
	 * @return the ID of the last FSObject added
	 * @throws DBException 
	 */
	private int getPreviousFSObjectID() throws DBException {
		String query = "select max(objectID) from " + SCHEMA_NAME + ".FSObjects";
		ResultSet rs = sql.getResultSafe(query);
		try {
			if(rs.next()){
				return rs.getInt(1);
			}
			return 0;
		} catch (SQLException e) {
			throw new DBException("An error occured while communicating with the database", e);
		}
	}


	@Override
	public int addNotebook(int parentNotebookID, Notebook notebook) throws DBAddException {
		try {
			// Make sure the parent notebook exists
			if(!notebookDoesExist(parentNotebookID)){
				throw new DBAddException();
			}
			
			// Add the object to the global file system object directory
			String update = "insert into " + SCHEMA_NAME + ".FSObjects " +
					"(objectID) values (" + (previousFSObjectID + 1) + ")";
			sql.getResultSafe(update);
			previousFSObjectID++;
			
			// Add the notebook to the notebook table
			update = "insert into " + SCHEMA_NAME + ".Notebooks (notebookID, notebookName) "
					+ "values (" + previousFSObjectID + ", '" + notebook.getName() + "')";
			sql.getResultSafe(update);
			
			// Place the notebook under the specified parent notebook
			update = "insert into " + SCHEMA_NAME + ".FSStructure (parent, child) " +
					"values (" + parentNotebookID + ", " + previousFSObjectID + ")";
			
			// Return the ID of the notebook just added
			return previousFSObjectID;
		} 
		catch (SQLException e) {
			throw new DBAddException("Error communicating with database", e);
		}
	}
	
	
	/**
	 * Queries the database for a notebook of the ID number supplied.
	 * @return true if the ID corresponds to a notebook, false otherwise
	 * @throws SQLException 
	 */
	private boolean notebookDoesExist(int id) throws SQLException{
		String query = "select * from " + SCHEMA_NAME + ".Notebooks where notebookID = " + 
				id;
		ResultSet rs = sql.getResultSafe(query);
		
		if(rs.next()){
			return true;
		}
		
		return false;
	}

}
