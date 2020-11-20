create table PERSON
(
	ID serial not null primary key,
	EMAIL VARCHAR(255) UNIQUE,
	NAME VARCHAR(255)
);

create table TODO
(
	ID serial not null primary key,
	DESCRIPTION VARCHAR(255),
	DUE_DATE DATE,
	PRIORITY INTEGER,
	STATUS INTEGER,
	TITLE VARCHAR(255),
	OWNER_ID BIGINT,
	constraint FK_TODO_OWNER
		foreign key (OWNER_ID) references PERSON (ID)
);

create table NOTE
(
	ID serial not null primary key,
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
	ID serial not null primary key,
	DUE_DATE DATE,
	TODO_ID BIGINT,
	constraint FK_REMINDER_TODO
		foreign key (TODO_ID) references TODO (ID)
);

create table TODO_COLLABORATORS
(
	COLLABORATIVE_TODOS_ID BIGINT not null,
	COLLABORATORS_ID BIGINT not null,
	constraint FK_TODO_COLLABORATORS_TODO
		foreign key (COLLABORATIVE_TODOS_ID) references TODO (ID),
	constraint FK_TODO_COLLABORATORS_COLLABORATOR
		foreign key (COLLABORATORS_ID) references PERSON (ID)
);

create table TODO_COLLABORATION_REQUEST
(
	ID serial not null primary key,
	PERSON_ID BIGINT,
	TODO_ID BIGINT,
	constraint FK_TODO_COLLABORATION_REQUEST_PERSON
		foreign key (PERSON_ID) references PERSON (ID),
	constraint FK_TODO_COLLABORATION_REQUEST_TODO
		foreign key (TODO_ID) references TODO (ID)
);

INSERT INTO PERSON (EMAIL, NAME) VALUES ('info@stratospheric.dev', 'Duke');

INSERT INTO TODO (TITLE, DUE_DATE, STATUS, PRIORITY, OWNER_ID) VALUES ('Setup infrastructure in AWS', '2020-31-12', 'OPEN', 1, 1);
INSERT INTO TODO (TITLE, DUE_DATE, STATUS, PRIORITY, OWNER_ID) VALUES ('Secure application', 'OPEN', '2020-31-12', 'OPEN', 1, 1);
INSERT INTO TODO (TITLE, DUE_DATE, STATUS, PRIORITY, OWNER_ID) VALUES ('Write book', '2020-31-12', 'OPEN', 1, 1);
INSERT INTO TODO (TITLE, DUE_DATE, STATUS, PRIORITY, OWNER_ID) VALUES ('Release it', '2020-31-12', 'OPEN', 1, 1);
