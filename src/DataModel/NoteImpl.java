package DataModel;

import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

public final class NoteImpl implements Note {

	private final String name;
	
	private final int id;
	
	private final User author;
	
	private final DateTime created, modified;
	
	private final Map<String, String> metadata;
	
	private final Set<Tag> tags;
	
	private final String content;
	
	
	private NoteImpl(String name, int id, User author, DateTime created, DateTime modified,
			Map<String, String> metadata, Set<Tag> tags, String content){
		this.name = name;
		this.id = id;
		this.author = author;
		this.created = created;
		this.modified = modified;
		this.metadata = metadata;
		this.tags = tags;
		this.content = content;
	}
	
	
	@Override
	public FSObjectType getType() {
		return FSObjectType.NOTE;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public User getAuthor() {
		return author;
	}

	@Override
	public DateTime getCreated() {
		return created;
	}

	@Override
	public DateTime getLastModified() {
		return modified;
	}

	@Override
	public Map<String, String> getMetadata() {
		return metadata;
	}

	@Override
	public Set<Tag> getTags() {
		return tags;
	}

	@Override
	public String getContent() {
		return content;
	}
	
	
	public static class NoteBuilder{
		private String name;
		
		private int id;
		
		private User author;
		
		private DateTime created, modified;
		
		private Map<String, String> metadata;
		
		private Set<Tag> tags;
		
		private String content;
		
		
		public NoteBuilder() {

		}

		public NoteBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public NoteBuilder setId(int id) {
			this.id = id;
			return this;
		}

		public NoteBuilder setAuthor(User author) {
			this.author = author;
			return this;
		}

		public NoteBuilder setCreated(DateTime created) {
			this.created = created;
			return this;
		}

		public NoteBuilder setModified(DateTime modified) {
			this.modified = modified;
			return this;
		}

		public NoteBuilder setMetadata(Map<String, String> metadata) {
			this.metadata = metadata;
			return this;
		}

		public NoteBuilder setTags(Set<Tag> tags) {
			this.tags = tags;
			return this;
		}

		public NoteBuilder setContent(String content) {
			this.content = content;
			return this;
		}
		
		public Note build(){
			return new NoteImpl(name, id, author, created, modified, metadata, tags, content);
		}
	}

}
