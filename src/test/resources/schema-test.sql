DROP SCHEMA IF EXISTS `core`;

CREATE DATABASE core;
USE core;

CREATE TABLE users (
  `id` VARCHAR(64) NOT NULL,
  `name` VARCHAR(191) NOT NULL,
  `class_id` VARCHAR(64) NULL,
  `password` VARCHAR(191),
  `role` VARCHAR(32) NOT NULL ,
  `suspended_until` DATETIME DEFAULT NULL,
  `email` VARCHAR(128) NULL ,
  `login_type` VARCHAR(32) NULL ,
  `temporary_password` BOOLEAN  NULL DEFAULT FALSE ,
  `temporary_username` BOOLEAN  NULL DEFAULT FALSE ,
  `score` INT NOT NULL DEFAULT 0 ,
  `gold_medals` INT NOT NULL DEFAULT 0 ,
  `silver_medals` INT NOT NULL DEFAULT 0 ,
  `bronze_medals` INT NOT NULL DEFAULT 0 ,
  `bad_submission_count` INT NOT NULL DEFAULT 0,
  `bad_login_count` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`id`) ,
  INDEX `class_id` (`class_id` ASC) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE groups (
  `id` VARCHAR(64) NOT NULL ,
  `name` VARCHAR(191) NOT NULL UNIQUE,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;