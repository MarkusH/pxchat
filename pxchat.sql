DROP DATABASE IF EXISTS `pxchat`;
CREATE DATABASE `pxchat` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

USE `pxchat`;

DROP TABLE IF EXISTS `servers`;
CREATE TABLE `servers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `time` int(20) NOT NULL,
  `address` varchar(255) NOT NULL,
  `port` varchar(10) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE` (`address`,`port`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE USER 'pxchat'@'localhost' IDENTIFIED BY 'pxchat';
GRANT SELECT, INSERT, UPDATE, DELETE ON  `pxchat` . * TO  'pxchat'@'localhost';
FLUSH PRIVILEGES;