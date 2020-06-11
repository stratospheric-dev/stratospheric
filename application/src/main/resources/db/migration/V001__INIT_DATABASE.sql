/*
    Currently H2 until we have a PostgreSQL instance
 */
CREATE TABLE todo (
    id BIGINT AUTO_INCREMENT,
    title VARCHAR(255)
);

INSERT INTO todo (title) VALUES ('Setup infrastructure in AWS');
INSERT INTO todo (title) VALUES ('Secure application');
INSERT INTO todo (title) VALUES('Write book');
INSERT INTO todo (title) VALUES('Release it');
