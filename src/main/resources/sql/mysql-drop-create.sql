-- Drop database and user

DROP DATABASE IF EXISTS moany;
DROP USER IF EXISTS 'moany'@'localhost';
DROP USER IF EXISTS 'moany'@'%';

-- Create database and user
CREATE DATABASE moany DEFAULT CHARACTER SET = utf8 DEFAULT COLLATE = utf8_general_ci;
CREATE USER 'moany'@'localhost' IDENTIFIED BY 'password';
CREATE USER 'moany'@'%' IDENTIFIED BY 'password';
grant all privileges on moany.* to 'moany'@'localhost';
grant all privileges on moany.* to 'moany'@'%';
flush privileges;
