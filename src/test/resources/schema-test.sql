DROP SCHEMA IF EXISTS core;

CREATE DATABASE core;
USE core;

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT,
  name VARCHAR(191) NOT NULL,
  class_id VARCHAR(64) NULL,
  role VARCHAR(32) NOT NULL ,
  email VARCHAR(128) NULL ,
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
      shortname VARCHAR(191),
  PRIMARY KEY (id) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE auth_data (
  is_enabled BOOLEAN DEFAULT FALSE,
  bad_login_count INT DEFAULT 0,
  suspended_until TIMESTAMP,
  last_login TIMESTAMP,
  user BIGINT,
  FOREIGN KEY (`user`) REFERENCES users(id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE auth_data_saml (
  user BIGINT,
  saml_id VARCHAR(40) NOT NULL )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE auth_data_password (
  user BIGINT,
  hashed_password VARCHAR(40) NOT NULL,
  password_expired BOOLEAN DEFAULT FALSE )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


CREATE TABLE submissions (
	id BIGINT AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    module_id BIGINT NOT NULL,
    time TIMESTAMP,
    valid BOOLEAN,
    result VARCHAR(191),
      PRIMARY KEY (id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;