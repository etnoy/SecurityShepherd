DROP SCHEMA IF EXISTS core;

CREATE DATABASE core;
USE core;

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT,
  name VARCHAR(191) NOT NULL,
  class_id VARCHAR(64) NULL,
  email VARCHAR(128) NULL ,
  solution_key BINARY(16) NULL ,
  PRIMARY KEY (id) ,
  INDEX class_id (class_id ASC) ,
  UNIQUE INDEX name_UNIQUE (name ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE groups (
  id BIGINT AUTO_INCREMENT,
  name VARCHAR(191) NOT NULL UNIQUE,
  PRIMARY KEY (id) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE modules (
	id BIGINT AUTO_INCREMENT,
	name VARCHAR(191) NOT NULL UNIQUE,
 	description VARCHAR(191),
 	short_name VARCHAR(191),
	solution_key BINARY(16) NULL ,
	fixed_solution_key BOOLEAN,
  PRIMARY KEY (id) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE auth (
  is_enabled BOOLEAN DEFAULT FALSE,
  bad_login_count INT DEFAULT 0,
  is_admin BOOLEAN DEFAULT FALSE,
  suspended_until TIMESTAMP,
  suspension_message VARCHAR(191),
  last_login TIMESTAMP,
  user BIGINT,
  FOREIGN KEY (`user`) REFERENCES users(id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE auth_saml (
  user BIGINT,
  saml_id VARCHAR(40) NOT NULL )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE auth_password (
  user BIGINT,
  hashed_password VARCHAR(191) NOT NULL,
  password_expired BOOLEAN DEFAULT FALSE )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE submissions (
	id BIGINT AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    module_id BIGINT NOT NULL,
    time TIMESTAMP,
    valid BOOLEAN,
    submitted_flag VARCHAR(191),
      PRIMARY KEY (id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;