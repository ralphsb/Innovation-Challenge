package Database;

/**
 * External users of this package should use this interface to make calls to the
 * database. No arbitrary SQL should ever be executed
 * @author Ralph
 *
 */
public interface DBAccess {

	
	
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
