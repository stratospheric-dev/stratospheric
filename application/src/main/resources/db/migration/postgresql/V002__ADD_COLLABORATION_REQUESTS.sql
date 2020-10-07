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
