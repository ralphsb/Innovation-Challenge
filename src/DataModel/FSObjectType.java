package DataModel;

public enum FSObjectType {

	NOTEBOOK,
	NOTE;
	
	public static FSObjectType fromString(String type){
		if(type.toLowerCase().equals("note")){
			return NOTE;
		}
		else{
			return NOTEBOOK;
		}
	}
}
