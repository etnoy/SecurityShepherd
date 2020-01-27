
DROP SCHEMA IF EXISTS `core` ;

CREATE DATABASE core;
USE core;
CREATE  TABLE users (
  `id` VARCHAR(64) NOT NULL ,
  `classId` VARCHAR(64) NULL ,
  `name` VARCHAR(191) NOT NULL ,
  `password` VARCHAR(191) ,
  `role` VARCHAR(32) NOT NULL ,
  `suspendedUntil` DATETIME DEFAULT NULL,
  `email` VARCHAR(128) NULL ,
  `loginType` VARCHAR(32) NULL ,
  `tempPassword` BOOLEAN  NULL DEFAULT FALSE ,
  `tempUsername` BOOLEAN  NULL DEFAULT FALSE ,
  `score` INT NOT NULL DEFAULT 0 ,
  `goldMedals` INT NOT NULL DEFAULT 0 ,
  `silverMedals` INT NOT NULL DEFAULT 0 ,
  `bronzeMedals` INT NOT NULL DEFAULT 0 ,
  `badSubmissionCount` INT NOT NULL DEFAULT 0,
  `badLoginCount` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`id`) ,
  INDEX `classId` (`classId` ASC) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;