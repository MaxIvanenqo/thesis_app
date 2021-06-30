-- MySQL Script generated by MySQL Workbench
-- pią, 26 lut 2021, 23:01:05
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`users` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `identificator` VARCHAR(45) NOT NULL,
  `contacts` JSON NULL,
  `last_seen` DATETIME NOT NULL,
  `is_online` TINYINT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`personal_data`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`personal_data` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `email_address` VARCHAR(45) NOT NULL,
  `full_name` VARCHAR(100) NOT NULL,
  `users_id` INT UNSIGNED NOT NULL,
  `user_photo` MEDIUMTEXT NOT NULL,
  `description` MEDIUMTEXT NULL,
  `phone_number` VARCHAR(45) NULL,
  PRIMARY KEY (`id`, `users_id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `fk_personal_data_users_idx` (`users_id` ASC) VISIBLE,
  CONSTRAINT `fk_personal_data_users`
    FOREIGN KEY (`users_id`)
    REFERENCES `mydb`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`certificates`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`certificates` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `key` MEDIUMTEXT NOT NULL,
  `access_code` VARCHAR(255) NOT NULL,
  `users_id` INT UNSIGNED NOT NULL,
  `last_use` DATETIME NOT NULL DEFAULT NOW(),
  `device_name` VARCHAR(255) NOT NULL,
  `blocked` TINYINT NOT NULL,
  PRIMARY KEY (`id`, `users_id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `fk_certificates_users1_idx` (`users_id` ASC) VISIBLE,
  CONSTRAINT `fk_certificates_users1`
    FOREIGN KEY (`users_id`)
    REFERENCES `mydb`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`messages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`messages` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `certificates_address_id` INT UNSIGNED NOT NULL,
  `certificates_sender_id` INT UNSIGNED NOT NULL,
  `timestamp` DATETIME NOT NULL DEFAULT NOW(),
  `encrypted_message` LONGTEXT NOT NULL,
  `delivered` TINYINT NOT NULL,
  `protected_key` MEDIUMTEXT NOT NULL,
  `attachment` LONGTEXT NULL,
  `address_id` INT NOT NULL,
  `sender_id` INT NOT NULL,
  PRIMARY KEY (`id`, `certificates_address_id`, `certificates_sender_id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `fk_messages_certificates1_idx` (`certificates_address_id` ASC) VISIBLE,
  INDEX `fk_messages_certificates2_idx` (`certificates_sender_id` ASC) VISIBLE,
  CONSTRAINT `fk_messages_certificates1`
    FOREIGN KEY (`certificates_address_id`)
    REFERENCES `mydb`.`certificates` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_messages_certificates2`
    FOREIGN KEY (`certificates_sender_id`)
    REFERENCES `mydb`.`certificates` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;