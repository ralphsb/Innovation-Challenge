package DataModel;

import java.util.Set;

import org.joda.time.DateTime;

/**
 * An interface used to represent a note
 * @author Ralph
 *
 */
public interface Note extends FSObject {

	public User getAuthor();
	
	public DateTime getCreated();
	
	public DateTime getLastModified();
	
	public String getMetadataField(String field);
	
	public Set<Tag> getTags();
	
	public String getContent();
	
}
