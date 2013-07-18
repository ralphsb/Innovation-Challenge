drop table NotePermissions;
drop table Permissions;
drop table NoteTags;
drop table TagSubscribers;
drop table Tags;
drop table NoteMetaData;
drop table Notes;
drop table FSStructure;
drop table Notebooks;
drop table FSObjects;
drop table UserGroupMappings;
drop table UserGroups;
drop table Users;
drop table UserEntities;


create table UserEntities(
	userEntityID int,
	
	primary key (userEntityID)
);

create table Users(
	userID int,
	userName varchar(100),
	pass varchar(100),
	
	primary key (userID),
	foreign key (userID) references UserEntities(userEntityID) on delete cascade
);

create table UserGroups(
	groupID int,
	groupName varchar(100),
	
	primary key (groupID),
	foreign key (groupID) references UserEntities(userEntityID) on delete cascade
);

create table UserGroupMappings(
	userID int,
	groupID int,
	
	primary key (userID, groupID),
	foreign key (userID) references Users(userID) on delete cascade,
	foreign key (groupID) references UserGroups(groupID) on delete cascade
);

create table FSObjects(
	objectID int,
	
	primary key (objectID)
);

create table Notebooks(
	notebookID int,
	notebookName varchar(100),
	
	primary key (notebookID),
	foreign key(notebookID) references FSObject(objectID)
);

create table FSStructure(
	parent int,
	child int,

	primary key(child),
	foreign key(parent) references Notebooks(notebookID) on delete cascade,
	foreign key(child) references FSObjects(objectID) on delete cascade
);

create table Notes(
	noteID int,
	author varchar(50),
	created datetime,
	content text,

	primary key (noteID),
	foreign key (noteID) references FSObjects(objectID) on delete cascade
);

create table NoteMetaData(
	noteID int,
	fieldName varchar(100),
	fieldData varchar(200),
	
	primary key (noteID, fieldName),
	foreign key (noteID) references Notes(noteID) on delete cascade
);

create table Tags(
	tag varchar(100),
	
	primary key (tag)
);

create table TagSubscribers(
	tag varchar(100),
	userEntityID int,
	
	primary key (tag, userEntityID),
	foreign key (tag) references Tags(tag) on delete cascade,
	foreign key (userEntityID) references UserEntities(userEntityID) on delete cascade
);

create table NoteTags(
	noteID int,
	tag varchar(100),

	primary key (noteID, tag),
	foreign key (noteID) references Notes(noteID) on delete cascade,
	foreign key (tag) references Tags(tag) on delete cascade
);

create table Permissions(
	permission varchar(10),
	
	primary key (permission)
);

create table NotePermissions(
	noteID int,
	userEntityID int,
	permission varchar(10),

	primary key (noteID, userEntityID),
	foreign key (noteID) references Notes(noteID) on delete cascade,
	foreign key (UserEntityID) references UserEntities(userEntityID) on delete cascade,
	foreign key (permission) references Permissions(permission) on delete cascade
);

