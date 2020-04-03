DROP SCHEMA IF EXISTS sqlinjection cascade;
CREATE SCHEMA IF NOT EXISTS sqlinjection;

CREATE TABLE sqlinjection.users (
  id VARCHAR(64) PRIMARY KEY,
  name VARCHAR(255),
  comment VARCHAR(255));

INSERT INTO sqlinjection.users values (1337, 'Jonathan Jogenfors', 'System Author');
INSERT INTO sqlinjection.users values (12345, 'Niklas Johansson', 'Teacher');
INSERT INTO sqlinjection.users values (123456, 'Jan-Åke Larsson', 'Professor');
INSERT INTO sqlinjection.users values (1234567, 'Guilherme B. Xavier', 'Examiner');
INSERT INTO sqlinjection.users values (12345678, 'OR 1=1', 'You''re close! Surround the query with single quotes so that your code is interpreted');
