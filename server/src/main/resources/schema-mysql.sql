DROP SCHEMA IF EXISTS core;

CREATE SCHEMA core;
USE core;

CREATE TABLE user (
  id INT AUTO_INCREMENT,
  display_name VARCHAR(191) NOT NULL UNIQUE,
  class_id INT NULL,
  email VARCHAR(128) NULL,
  user_key BINARY(16) NULL,
  PRIMARY KEY (id) ,
  INDEX class_id (class_id ASC) ,
  UNIQUE INDEX display_name_UNIQUE (display_name ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE class (
  id INT AUTO_INCREMENT,
  name VARCHAR(191) NOT NULL UNIQUE,
  PRIMARY KEY (id) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE module (
	id INT AUTO_INCREMENT,
	name VARCHAR(191) NOT NULL UNIQUE,
 	description VARCHAR(191),
 	short_name VARCHAR(191),
	is_flag_enabled BOOLEAN,
  	is_flag_exact BOOLEAN,
	flag VARCHAR(64) NULL,
	is_open BOOLEAN,
  PRIMARY KEY (id) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE module_points (
	id INT AUTO_INCREMENT,
	module_id INT,
	submission_rank INT,
	points INT,
  PRIMARY KEY (id),
  UNIQUE KEY (module_id, submission_rank),
  FOREIGN KEY (`module_id`) REFERENCES module(id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE user_auth (
  id INT AUTO_INCREMENT,
  user_id INT NOT NULL,
  is_enabled BOOLEAN DEFAULT FALSE,
  bad_login_count INT DEFAULT 0,
  is_admin BOOLEAN DEFAULT FALSE NOT NULL,
  account_created DATETIME NULL DEFAULT NULL,
  suspension_message VARCHAR(191),
  suspended_until DATETIME NULL DEFAULT NULL,
  last_login DATETIME NULL DEFAULT NULL,
  last_login_method VARCHAR(10),
 PRIMARY KEY (id) ,
 FOREIGN KEY (user_id) REFERENCES user(id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE saml_auth (
  id INT AUTO_INCREMENT,
  user_id INT,
  saml_id VARCHAR(40) NOT NULL,
      PRIMARY KEY (id) ,
    FOREIGN KEY (user_id) REFERENCES user(id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE password_auth (
  id INT AUTO_INCREMENT,
  user_id INT,
  login_name VARCHAR(191) NOT NULL UNIQUE,
  hashed_password VARCHAR(191),
  is_password_non_expired BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (id) ,
  FOREIGN KEY (user_id) REFERENCES user(id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE submission (
	id INT AUTO_INCREMENT,
    user_id INT NOT NULL,
    module_id INT NOT NULL, 
    time DATETIME NULL DEFAULT NULL,
    is_valid BOOLEAN,
    flag VARCHAR(191),
    PRIMARY KEY (id),
    FOREIGN KEY (`user_id`) REFERENCES user(id),
    FOREIGN KEY (`module_id`) REFERENCES module(id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE configuration (
  id INT AUTO_INCREMENT,
  config_key VARCHAR(191) NOT NULL UNIQUE,
  value VARCHAR(191) NOT NULL,
  PRIMARY KEY (id) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;
