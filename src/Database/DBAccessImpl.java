package Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import DataModel.FSObject;
import DataModel.FSObjectImpl;
import DataModel.FSObjectType;
import DataModel.Note;
import DataModel.Notebook;
import DataModel.NotebookImpl;
import DataModel.Permission;
import DataModel.Tag;
import DataModel.User;
import DataModel.UserEntity;

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
	public synchronized int addNotebook(int parentNotebookID, Notebook notebook) throws DBAddException {
		try {
			// Make sure the parent notebook exists
			if(!notebookDoesExist(parentNotebookID)){
				throw new DBAddException();
			}
			
			// Add the object to the global file system object directory
			String update = "insert into " + SCHEMA_NAME + ".FSObjects " +
					"(objectID, objectName, typeName) values (" + (previousFSObjectID + 1) + 
					", '" + notebook.getName() + "', 'notebook')";
			sql.getResultSafe(update);
			previousFSObjectID++;
			
			// Add the notebook to the notebook table
			update = "insert into " + SCHEMA_NAME + ".Notebooks (notebookID) "
					+ "values (" + previousFSObjectID + ")";
			sql.getResultSafe(update);
			
			// Place the notebook under the specified parent notebook
			update = "insert into " + SCHEMA_NAME + ".FSStructure (parent, child) " +
					"values (" + parentNotebookID + ", " + previousFSObjectID + ")";
			sql.getResultSafe(update);
			
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


	@Override
	public synchronized Notebook getNotebook(int notebookID) throws DBException {
		// Get the details of the notebook
		String query = "select notebookID, objectName " +
				"from " + SCHEMA_NAME + ".Notebooks, " +
				SCHEMA_NAME + ".FSObjects " +
				"where notebookID = " + notebookID + " and " +
				"objectID = notebookID";
		
		ResultSet rs = sql.getResultSafe(query);
		
		String notebookName = null;
		try{
			if(!rs.next()){
				return null;
			}
			notebookName = rs.getString(2);
		}
		catch(SQLException e){
			throw new DBException(e);
		}
		
		// Get the notebook's children
		query = "select objectID, typeName, objectName " +
				"from " + SCHEMA_NAME + ".FSObjects fso, " +
				SCHEMA_NAME + ".FSStructure fss " +
				"where fss.parent = " + notebookID + " and " +
				"fss.child = fso.objectID " +
				"order by objectName";
		
		rs = sql.getResultSafe(query);
		List<FSObject> children = new LinkedList<FSObject>();
		try{
			while(rs.next()){
				int id = rs.getInt(1);
				FSObjectType type = FSObjectType.fromString(rs.getString(2));
				String name = rs.getString(3);
				FSObject obj = new FSObjectImpl(id, name, type);
				
				children.add(obj);
			}
		}
		catch(SQLException e){
			throw new DBException("Error communicating with DB", e);
		}
		
		// Create and return the new Notebook
		return new NotebookImpl(notebookID, notebookName, children);
	}


	@Override
	public synchronized int addNote(int parentNotebookID, Note note, 
			Map<UserEntity, Permission> perms) throws DBAddException, DBException {
		try{
			if(!notebookDoesExist(parentNotebookID)){
				throw new DBAddException("Parent notebook does not exist");
			}
		}
		catch(SQLException e){
			throw new DBException("Error communicating with DB", e);
		}
		
		int tempID = previousFSObjectID + 1;
		
		// Insert the note into the FSObject directory
		insertNoteIntoFSObjectTable(tempID, note);
		
		// Insert the note into the Notes table
		insertNoteIntoNotesTable(tempID, note);
		
		// Insert the note into the FSStructure table
		insertNoteIntoFSStructureTable(parentNotebookID, tempID);
		
		// Insert the note metadata into the NoteMetadata table
		insertNoteMetaData(tempID, note);
		
		// Insert the tags into the tags table
		insertNoteTagsIntoTable(tempID, note);
		
		// Insert the perms into the perms table
		insertNotePermsIntoTable(tempID, perms);
		
		previousFSObjectID++;
		return tempID;
	}


	/**
	 * Inserts the note into the FSObjectTable
	 * @param id the id of the note
	 * @param note the note
	 */
	private void insertNoteIntoFSObjectTable(int id, Note note){
		String update = "insert into " + SCHEMA_NAME + ".FSObjects (objectID, objectName, " +
				"typeName) values (" + id + ", '" + note.getName() + "', " +
				"note)";
		sql.getResultSafe(update);
	}
	
	
	/**
	 * Inserts the note into the notes table
	 * @param id the id of the note
	 * @param note the note
	 */
	private void insertNoteIntoNotesTable(int id, Note note){
		String createdDate = null;
		String modifiedDate = null;
		String content = makeSafeString(note.getContent());
		String update = "insert into " + SCHEMA_NAME + ".Notes (noteID, author, created, " +
				"lastModified, content) values (" + id + ", " + note.getAuthor().getID() + 
				", STR_TO_DATE('" + createdDate + "', '%Y-%m-%d %H:%i'), " +
				" STR_TO_DATE('" + modifiedDate + "','%Y-%m-%d %H:%i'), '" + 
				content + "')";
		
		sql.getResultSafe(update);
	}
	
	
	/**
	 * Inserts the note into the FSStructure table
	 * @param parentID the parent notebook's id (does NOT check for validity)
	 * @param id the id number of the note
	 */
	private void insertNoteIntoFSStructureTable(int parentID, int id){
		String update = "insert into " + SCHEMA_NAME + ".FSStructure (parent, child) values " +
				"(" + parentID + ", " + id + ")";
		
		sql.getResultSafe(update);
	}
	
	
	/**
	 * Inserts the note's metadata into the metadata table
	 * @param id the notes id
	 * @param note the note
	 */
	private void insertNoteMetaData(int id, Note note){
		for(Entry<String, String> entry: note.getMetadata().entrySet()){
			String update = "insert into " + SCHEMA_NAME + ".NoteMetaData (noteID, " +
					"fieldName, fieldData) values ('" + entry.getKey() + "', '" + 
					entry.getValue() + "')";
			
			sql.getResultSafe(update);
		}
	}
	
	
	/**
	 * Inserts the note's tags into the tags table
	 * @param id the note's id
	 * @param note the note
	 */
	private void insertNoteTagsIntoTable(int id, Note note){
		for(Tag t: note.getTags()){
			String update;
			
			// Try to add the tag to the tag database. Ignore the exception that
			// occurs if there is a duplicate tag
			try{
				update = "insert into " + SCHEMA_NAME + ".Tags (tag) values ('" + 
						t.getName() + "')";
				sql.getResultUnsafe(update);
			}
			catch(SQLException e){
			}
			
			update = "insert into " + SCHEMA_NAME + ".NoteTags (noteID, tag) values " +
					"(" + id + ", '" + t.getName() + "')";
			
			sql.getResultSafe(update);
		}
	}
	
	
	/**
	 * Inserts the perms into the perms table
	 * @param id the id of the note
	 * @param perms the map containing all the perms
	 */
	private void insertNotePermsIntoTable(int id, Map<UserEntity, Permission> perms) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public synchronized Note getNote(int noteID) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public synchronized void addUser(User u) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public synchronized User getUser(int id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public synchronized Permission getPermission(int noteID, int userID) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**Removes quotes from strings to avoid having the SQL syntax be invalidated.
	 * @param s the string to clean
	 * @return the clean string
	 */
	private String makeSafeString(String s){
		String temp = s.replace("'", "");
		return temp.replace("\"", "");
	}

}
