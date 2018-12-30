/*
SQLyog Community v13.0.0 (64 bit)
MySQL - 5.1.45-community : Database - dhanani_main
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`dhanani_main` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `dhanani_main`;

/*Table structure for table `appconfig` */

DROP TABLE IF EXISTS `appconfig`;

CREATE TABLE `appconfig` (
  `client_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `issue_date` date DEFAULT NULL,
  `act_date` date DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `act_cd` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `license_no` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `email` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `server` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `act1` varchar(4) COLLATE utf8_unicode_ci DEFAULT '',
  `act2` varchar(4) COLLATE utf8_unicode_ci DEFAULT '',
  `act3` varchar(4) COLLATE utf8_unicode_ci DEFAULT '',
  `act4` varchar(4) COLLATE utf8_unicode_ci DEFAULT '',
  `user_nm` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pass` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `months` int(11) DEFAULT NULL,
  `days` int(11) DEFAULT NULL,
  `haddr` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `iaddr` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `laststart` date DEFAULT NULL,
  `def` varchar(20) COLLATE utf8_unicode_ci DEFAULT '',
  `app_limit` smallint(6) DEFAULT '0',
  `cur_limit` smallint(6) DEFAULT '0',
  `tmode` smallint(6) DEFAULT '0',
  `proj_mode` smallint(6) DEFAULT '0',
  `mlt_mac` smallint(6) DEFAULT '0',
  `mac_lmt` int(10) DEFAULT '1',
  `is_tax` smallint(6) DEFAULT NULL,
  `theme_cd` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `appconfig` */

insert  into `appconfig`(`client_id`,`issue_date`,`act_date`,`due_date`,`act_cd`,`license_no`,`email`,`server`,`act1`,`act2`,`act3`,`act4`,`user_nm`,`pass`,`status`,`months`,`days`,`haddr`,`iaddr`,`laststart`,`def`,`app_limit`,`cur_limit`,`tmode`,`proj_mode`,`mlt_mac`,`mac_lmt`,`is_tax`,`theme_cd`) values 
('STOCK MANAGEMENT SOF','2018-09-20','2018-09-20','2019-01-31',NULL,'SMS-000001-15-06',NULL,NULL,'0000','1111','2222','3333','LG','LG123',-1,0,0,'90-4C-E5-07-79-87','192.168.0.108','2018-12-30','',0,0,0,1,0,1,0,4);

/*Table structure for table `change_themes` */

DROP TABLE IF EXISTS `change_themes`;

CREATE TABLE `change_themes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `theme_name` varchar(255) DEFAULT NULL,
  `theme_link` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;

/*Data for the table `change_themes` */

insert  into `change_themes`(`id`,`theme_name`,`theme_link`) values 
(1,'TextureLookAndFeel ','com.jtattoo.plaf.texture.TextureLookAndFeel'),
(2,'SmartLookAndFeel','com.jtattoo.plaf.smart.SmartLookAndFeel'),
(3,'NoireLookAndFeel','com.jtattoo.plaf.noire.NoireLookAndFeel'),
(4,'AcrylLookAndFeel','com.jtattoo.plaf.acryl.AcrylLookAndFeel'),
(5,'AeroLookAndFeel','com.jtattoo.plaf.aero.AeroLookAndFeel'),
(6,'AluminiumLookAndFeel','com.jtattoo.plaf.aluminium.AluminiumLookAndFeel'),
(7,'BernsteinLookAndFeel','com.jtattoo.plaf.bernstein.BernsteinLookAndFeel'),
(8,'FastLookAndFeel','com.jtattoo.plaf.fast.FastLookAndFeel'),
(9,'GraphiteLookAndFeel','com.jtattoo.plaf.graphite.GraphiteLookAndFeel'),
(10,'HiFiLookAndFeel','com.jtattoo.plaf.hifi.HiFiLookAndFeel'),
(11,'LunaLookAndFeel','com.jtattoo.plaf.luna.LunaLookAndFeel'),
(12,'McWinLookAndFeel ','com.jtattoo.plaf.mcwin.McWinLookAndFeel'),
(13,'MintLookAndFeel','com.jtattoo.plaf.mint.MintLookAndFeel');

/*Table structure for table `dbmst` */

DROP TABLE IF EXISTS `dbmst`;

CREATE TABLE `dbmst` (
  `cmp_name` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `db_year` varchar(4) COLLATE utf8_unicode_ci NOT NULL,
  `db_name` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `dbmonth` smallint(6) DEFAULT '0',
  `dbname` varchar(30) COLLATE utf8_unicode_ci DEFAULT '',
  `dbyear` varchar(4) COLLATE utf8_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `dbmst` */

insert  into `dbmst`(`cmp_name`,`db_year`,`db_name`,`dbmonth`,`dbname`,`dbyear`) values 
('DHANANI FROZAL','2018','dhananiforzal',5,'dhananiforzal','2018');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
