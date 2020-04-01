DROP SCHEMA IF EXISTS core;

CREATE SCHEMA core;
USE core;

CREATE TABLE user (
  id INT AUTO_INCREMENT,
  display_name VARCHAR(191) NOT NULL UNIQUE,
  class_id INT NULL,
  email VARCHAR(128) NULL,
  user_key BINARY(16) NULL,
  PRIMARY KEY (id),
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
	is_flag_enabled BOOLEAN DEFAULT FALSE,
  	is_flag_exact BOOLEAN DEFAULT FALSE,
	flag VARCHAR(64) NULL,
	is_open BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (id) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE scoreboard (
	id INT AUTO_INCREMENT,
	user_id INT NOT NULL UNIQUE,
	user_rank INT NOT NULL,
	score INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (`user_id`) REFERENCES user(id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE score (
	id INT AUTO_INCREMENT,
	module_id INT NOT NULL,
	user_rank INT NOT NULL,
	user_id INT NOT NULL,
	amount INT NOT NULL,
	time TIMESTAMP NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (user_id, module_id),
  FOREIGN KEY (`module_id`) REFERENCES module(id),
  FOREIGN KEY (`user_id`) REFERENCES user(id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE module_point (
	id INT AUTO_INCREMENT,
	module_id INT NOT NULL,
	submission_rank INT NOT NULL,
	points INT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (module_id, submission_rank),
  FOREIGN KEY (`module_id`) REFERENCES module(id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE user_auth (
  id INT AUTO_INCREMENT,
  user_id INT UNIQUE NOT NULL,
  is_enabled BOOLEAN DEFAULT FALSE,
  bad_login_count INT DEFAULT 0,
  is_admin BOOLEAN DEFAULT FALSE NOT NULL,
  account_created DATETIME NULL DEFAULT NULL,
  suspension_message VARCHAR(191),
  suspended_until DATETIME NULL DEFAULT NULL,
  last_login DATETIME NULL DEFAULT NULL,
  last_login_method VARCHAR(10) DEFAULT NULL,
 PRIMARY KEY (id) ,
 FOREIGN KEY (user_id) REFERENCES user(id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE saml_auth (
  id INT AUTO_INCREMENT,
  user_id INT NOT NULL UNIQUE,
  saml_id VARCHAR(40) NOT NULL UNIQUE,
  PRIMARY KEY (id) ,
  FOREIGN KEY (user_id) REFERENCES user(id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE password_auth (
  id INT AUTO_INCREMENT,
  user_id INT NOT NULL UNIQUE,
  login_name VARCHAR(191) NOT NULL UNIQUE,
  hashed_password VARCHAR(191) NOT NULL,
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
    is_valid BOOLEAN NOT NULL,
    flag VARCHAR(191) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (`user_id`) REFERENCES user(id),
    FOREIGN KEY (`module_id`) REFERENCES module(id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE configuration (
  id INT AUTO_INCREMENT,
  config_key VARCHAR(191) NOT NULL UNIQUE,
  value VARCHAR(191) NOT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;
