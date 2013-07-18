package Test;

import DataModel.Notebook;
import Database.DBAccess;
import Database.DBAddException;
import Database.DBException;

public class Test {

	public static void main(String[] args) throws DBAddException, DBException {
		//Notebook book = new NotebookImpl(-1, "My notebook", null);
		//DBAccess.Singlton.getInstance().addNotebook(1, book);
		
		Notebook book = DBAccess.Singlton.getInstance().getNotebook(1);
		System.out.println(book);
	}

}
