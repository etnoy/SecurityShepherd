DROP SCHEMA IF EXISTS core;

CREATE DATABASE core;
USE core;

CREATE TABLE user (
  id BIGINT AUTO_INCREMENT,
  display_name VARCHAR(191) NOT NULL UNIQUE,
  class_id BIGINT NULL,
  email VARCHAR(128) NULL,
  flag_key BINARY(16) NULL,
  PRIMARY KEY (id) ,
  INDEX class_id (class_id ASC) ,
  UNIQUE INDEX display_name_UNIQUE (display_name ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE class (
  id BIGINT AUTO_INCREMENT,
  name VARCHAR(191) NOT NULL UNIQUE,
  PRIMARY KEY (id) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE module (
	id BIGINT AUTO_INCREMENT,
	name VARCHAR(191) NOT NULL UNIQUE,
 	description VARCHAR(191),
 	short_name VARCHAR(191),
	flag_enabled BOOLEAN,
  	exact_flag BOOLEAN,
	flag VARCHAR(64) NULL,
	is_open BOOLEAN,
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