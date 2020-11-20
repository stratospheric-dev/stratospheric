create table PERSON
(
	ID BIGSERIAL not null primary key,
	EMAIL VARCHAR(255) UNIQUE,
	NAME VARCHAR(255)
);

create table TODO
(
	ID BIGSERIAL not null primary key,
	DESCRIPTION VARCHAR(255),
	DUE_DATE DATE,
	PRIORITY INTEGER,
	STATUS VARCHAR(255),
	TITLE VARCHAR(255),
	OWNER_ID BIGINT,
	constraint FK_TODO_OWNER
		foreign key (OWNER_ID) references PERSON (ID)
);

create table NOTE
(
	ID BIGSERIAL not null primary key,
	CONTENT VARCHAR(255),
	TODO_ID BIGINT,
	constraint FK_NOTE_TODO
		foreign key (TODO_ID) references TODO (ID)
);

create table PERSON_OWNED_TODOS
(
	PERSON_ID BIGINT not null,
	OWNED_TODOS_ID BIGINT not null
		constraint UK_OWNED_TODOS_ID
			unique,
	constraint FK_PERSON_OWNED_TODOS_PERSON
		foreign key (PERSON_ID) references PERSON (ID),
	constraint FK_PERSON_OWNED_TODOS_TODO
		foreign key (OWNED_TODOS_ID) references TODO (ID)
);

create table REMINDER
(
	ID BIGSERIAL not null primary key,
	DUE_DATE DATE,
	TODO_ID BIGINT,
	constraint FK_REMINDER_TODO
		foreign key (TODO_ID) references TODO (ID)
);

create table TODO_COLLABORATION
(
	TODO_ID BIGINT not null,
	COLLABORATOR_ID BIGINT not null,
	constraint FK_TODO_COLLABORATION_TODO
		foreign key (TODO_ID) references TODO (ID),
	constraint FK_TODO_COLLABORATION_COLLABORATOR
		foreign key (COLLABORATOR_ID) references PERSON (ID)
);

create table TODO_COLLABORATION_REQUEST
(
	ID BIGSERIAL not null primary key,
	COLLABORATOR_ID BIGINT,
	TODO_ID BIGINT,
	TOKEN VARCHAR(255),
	constraint FK_TODO_COLLABORATION_REQUEST_COLLABORATOR
		foreign key (COLLABORATOR_ID) references PERSON (ID),
	constraint FK_TODO_COLLABORATION_REQUEST_TODO
		foreign key (TODO_ID) references TODO (ID)
);
