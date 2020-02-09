DROP SCHEMA IF EXISTS core;

CREATE DATABASE core;
USE core;

CREATE TABLE user (
  id BIGINT AUTO_INCREMENT,
  name VARCHAR(191) NOT NULL,
  class_id VARCHAR(64) NULL,
  email VARCHAR(128) NULL ,
  flag_key BINARY(16) NULL ,
  PRIMARY KEY (id) ,
  INDEX class_id (class_id ASC) ,
  UNIQUE INDEX name_UNIQUE (name ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE classes (
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
	has_flag BOOLEAN,
  	hardcoded_flag BOOLEAN,
	flag VARCHAR(32) NULL,
  PRIMARY KEY (id) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE auth (
  is_enabled BOOLEAN DEFAULT FALSE,
  bad_login_count INT DEFAULT 0,
  is_admin BOOLEAN DEFAULT FALSE,
  suspended_until TIMESTAMP,
  suspension_message VARCHAR(191),
  account_created TIMESTAMP,
  last_login TIMESTAMP,
  last_login_method VARCHAR(10),
  user BIGINT,
  FOREIGN KEY (`user`) REFERENCES user(id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE saml_auth (
  user BIGINT,
  saml_id VARCHAR(40) NOT NULL )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE password_auth (
  user BIGINT,
  login_name VARCHAR(191) NOT NULL UNIQUE,
  hashed_password VARCHAR(191),
  password_expired BOOLEAN DEFAULT TRUE )
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