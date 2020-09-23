DROP SCHEMA IF EXISTS core;

CREATE SCHEMA core;
USE core;

CREATE TABLE user (
  id INT AUTO_INCREMENT,
  display_name VARCHAR(191) NOT NULL UNIQUE,
  class_id INT NULL,
  email VARCHAR(128) NULL,
  is_not_banned BOOLEAN DEFAULT FALSE,
  account_created DATETIME NOT NULL,
  user_key BINARY(16) NOT NULL,
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
	short_name VARCHAR(191) NOT NULL UNIQUE,
 	description VARCHAR(191),
  	is_flag_static BOOLEAN DEFAULT FALSE,
	static_flag VARCHAR(64) NULL,
	module_key BINARY(64) NOT NULL,
	is_open BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (id) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE csrf_attack (
	id INT AUTO_INCREMENT,
	pseudonym VARCHAR(64) NOT NULL,
	module_id INT NOT NULL,
	started TIMESTAMP NOT NULL,
	finished TIMESTAMP,
  PRIMARY KEY (id),
FOREIGN KEY (`module_id`) REFERENCES module(id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE correction (
	id INT AUTO_INCREMENT,
	user_id INT NOT NULL,
	amount INT NOT NULL,
	time TIMESTAMP NOT NULL,
	description VARCHAR(191),
  PRIMARY KEY (id),
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
    valid_or_null  boolean as (if(is_valid = true,true, null)) stored,
    PRIMARY KEY (id),
    UNIQUE KEY (user_id, module_id, valid_or_null),
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

CREATE VIEW ranked_submission AS
WITH ranks as 
(
	SELECT 
		rank() over (partition by module_id order by time) as 'rank',
		id as submission_id,
		user_id,
		module_id,
	 	time,
	 	flag
	FROM submission
	where is_valid=true
)
SELECT 
	submission_id,
	user_id,
	`rank`,
	ranks.module_id,
 	time,
 	flag,
 	base_score.points as base_score,
 	COALESCE(bonus_score.points, 0) as bonus_score,
 	base_score.points + COALESCE(bonus_score.points, 0) as score
FROM ranks
left join
	module_point as bonus_score
	on (
		ranks.module_id = bonus_score.module_id
	and
		`rank` = bonus_score.submission_rank
	) 
inner join 
	module_point as base_score
	on (
		ranks.module_id=base_score.module_id 
	and 
		base_score.submission_rank=0
	);

CREATE VIEW scoreboard AS
SELECT
	rank() over(order by sum(score) desc) as 'rank',
	user_id,
	CAST(sum(score) as SIGNED) as score,
	sum(case when `rank` = 1 then 1 else 0 end) as gold_medals,
	sum(case when `rank` = 2 then 1 else 0 end) as silver_medals,
	sum(case when `rank` = 3 then 1 else 0 end) as bronze_medals 
FROM 
(
	SELECT
		user_id, score, `rank`
	FROM
		ranked_submission
	UNION ALL
	SELECT
		user_id,
		amount as score, 
		0
	FROM
		correction
	UNION ALL
	SELECT
		id
	as
		user_id, 
		0,
		0
	FROM
		user
	) 
	as
	all_scores 
group by user_id order by 'rank' desc;