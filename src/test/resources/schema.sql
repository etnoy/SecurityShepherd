
DROP SCHEMA IF EXISTS `core` ;

CREATE DATABASE core;
USE core;
CREATE  TABLE users (
  `userId` VARCHAR(64) NOT NULL ,
  `classId` VARCHAR(64) NULL ,
  `userName` VARCHAR(32) NOT NULL ,
  `userPass` VARCHAR(191) ,
  `userRole` VARCHAR(32) NOT NULL ,
  `ssoName` VARCHAR(191) ,
  `badLoginCount` INT NOT NULL DEFAULT 0 ,
  `suspendedUntil` DATETIME DEFAULT '1000-01-01 00:00:00' ,
  `userAddress` VARCHAR(128) NULL ,
  `loginType` VARCHAR(32) NULL ,
  `tempPassword` TINYINT(1)  NULL DEFAULT FALSE ,
  `tempUsername` TINYINT(1)  NULL DEFAULT FALSE ,
  `userScore` INT NOT NULL DEFAULT 0 ,
  `goldMedalCount` INT NOT NULL DEFAULT 0 ,
  `silverMedalCount` INT NOT NULL DEFAULT 0 ,
  `bronzeMedalCount` INT NOT NULL DEFAULT 0 ,
  `badSubmissionCount` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`userId`) ,
  INDEX `classId` (`classId` ASC) ,
  UNIQUE INDEX `userName_UNIQUE` (`userName` ASC) ,
  UNIQUE INDEX `ssoName_UNIQUE` (`ssoName` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;