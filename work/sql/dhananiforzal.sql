/*
SQLyog Community v13.0.0 (64 bit)
MySQL - 8.0.13 : Database - dhananiforzal
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`dhananiforzal` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `dhananiforzal`;

/*Table structure for table `account_master` */

DROP TABLE IF EXISTS `account_master`;

CREATE TABLE `account_master` (
  `id` varchar(8) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `expense` decimal(10,2) NOT NULL DEFAULT '0.00',
  `status` tinyint(1) DEFAULT NULL,
  `opb` decimal(10,3) DEFAULT '0.000',
  `amount_type` tinyint(1) DEFAULT NULL,
  `edit_no` decimal(3,0) DEFAULT '0',
  `user_cd` int(11) DEFAULT NULL,
  `time_stamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `account_master` */

insert  into `account_master`(`id`,`name`,`expense`,`status`,`opb`,`amount_type`,`edit_no`,`user_cd`,`time_stamp`) values 
('AM000003','CASH',0.00,0,0.000,NULL,0,1,'2018-12-22 14:38:25'),
('AM000005','CHECK ACCOUNT',0.00,0,0.000,NULL,0,1,'2018-12-22 19:11:06'),
('AM000006','TEST OPB',100.00,0,200.000,0,4,1,'2019-01-07 22:29:07');

/*Table structure for table `appconfig` */

DROP TABLE IF EXISTS `appconfig`;

CREATE TABLE `appconfig` (
  `client_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `issue_date` date DEFAULT NULL,
  `act_date` date DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `act_cd` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `license_no` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `server` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `act1` varchar(4) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT '',
  `act2` varchar(4) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT '',
  `act3` varchar(4) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT '',
  `act4` varchar(4) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT '',
  `user_nm` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `pass` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `months` int(11) DEFAULT NULL,
  `days` int(11) DEFAULT NULL,
  `haddr` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `iaddr` varchar(40) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `laststart` date DEFAULT NULL,
  `def` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT '',
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
('STOCK MANAGEMENT SOF','2018-09-20','2018-09-20','2019-01-01',NULL,'SMS-000001-15-06',NULL,NULL,'0000','1111','2222','3333','LG','LG123',-1,0,0,'90-4C-E5-07-79-87','192.168.0.108','2018-09-19','',0,0,0,1,0,1,0,4);

/*Table structure for table `bank_payment_receipt_details` */

DROP TABLE IF EXISTS `bank_payment_receipt_details`;

CREATE TABLE `bank_payment_receipt_details` (
  `id` varchar(8) NOT NULL,
  `sr_no` int(11) NOT NULL,
  `fk_account_master_id` varchar(8) NOT NULL,
  `amount` decimal(15,5) NOT NULL,
  `chq_dt` date NOT NULL,
  `chq_no` varchar(50) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`sr_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `bank_payment_receipt_details` */

/*Table structure for table `bank_payment_receipt_head` */

DROP TABLE IF EXISTS `bank_payment_receipt_head`;

CREATE TABLE `bank_payment_receipt_head` (
  `id` varchar(8) NOT NULL,
  `voucher_date` date DEFAULT NULL,
  `fk_bank_cd` varchar(8) DEFAULT NULL,
  `total_bal` decimal(15,5) DEFAULT NULL,
  `ctype` int(11) DEFAULT NULL,
  `is_del` int(11) DEFAULT '0',
  `prn_no` int(11) DEFAULT '0',
  `fix_time` varchar(10) DEFAULT NULL,
  `edit_no` decimal(3,0) NOT NULL DEFAULT '0',
  `user_cd` decimal(3,0) DEFAULT NULL,
  `time_stamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `bank_payment_receipt_head` */

/*Table structure for table `cash_payment_receipt_details` */

DROP TABLE IF EXISTS `cash_payment_receipt_details`;

CREATE TABLE `cash_payment_receipt_details` (
  `id` varchar(8) NOT NULL,
  `sr_no` int(10) NOT NULL,
  `fk_account_master_id` varchar(8) DEFAULT NULL,
  `amount` decimal(15,3) DEFAULT '0.000',
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`sr_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `cash_payment_receipt_details` */

/*Table structure for table `cash_payment_receipt_head` */

DROP TABLE IF EXISTS `cash_payment_receipt_head`;

CREATE TABLE `cash_payment_receipt_head` (
  `id` varchar(8) NOT NULL,
  `voucher_date` date DEFAULT NULL,
  `total_bal` decimal(10,3) DEFAULT NULL,
  `fk_account_master_id` varchar(8) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `ctype` int(2) DEFAULT NULL,
  `is_del` int(2) DEFAULT '0',
  `fix_time` varchar(10) DEFAULT NULL,
  `edit_no` decimal(3,0) NOT NULL DEFAULT '0',
  `user_cd` decimal(3,0) DEFAULT NULL,
  `time_stamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `cash_payment_receipt_head` */

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

/*Table structure for table `cmpny_mst` */

DROP TABLE IF EXISTS `cmpny_mst`;

CREATE TABLE `cmpny_mst` (
  `cmpn_cd` varchar(7) NOT NULL,
  `cmpn_code` varchar(2) DEFAULT NULL,
  `branch_code` varchar(3) DEFAULT NULL,
  `cmpn_name` varchar(50) NOT NULL,
  `ac_year` varchar(4) NOT NULL,
  `mnth` varchar(2) NOT NULL,
  `sh_name` varchar(2) DEFAULT NULL,
  `digit` decimal(1,0) NOT NULL,
  `invoice_type` decimal(1,0) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT '',
  `add1` varchar(500) DEFAULT '',
  `add2` varchar(500) DEFAULT '',
  `area_cd` varchar(7) DEFAULT '',
  `city_cd` varchar(7) DEFAULT '',
  `pincode` varchar(6) DEFAULT '',
  `mob_no` varchar(17) DEFAULT '',
  `phone_no` varchar(17) DEFAULT '',
  `licence_no` varchar(30) DEFAULT '',
  `email` varchar(70) DEFAULT '',
  `fax_no` varchar(17) DEFAULT '',
  `pan_no` varchar(20) DEFAULT '',
  `tin_no` varchar(20) DEFAULT '',
  `cst_no` varchar(20) DEFAULT '',
  `tax_no` varchar(20) DEFAULT '',
  `bank_name` varchar(100) DEFAULT '',
  `ac_no` varchar(20) DEFAULT '',
  `branch_name` varchar(100) DEFAULT '',
  `cash_ac_cd` varchar(8) DEFAULT '',
  `lab_inc_ac` varchar(8) DEFAULT '',
  `lab_exp_ac` varchar(8) DEFAULT '',
  `sale_ac` varchar(8) DEFAULT '',
  `purchase_ac` varchar(8) DEFAULT '',
  `mypwd` varchar(10) DEFAULT NULL,
  `contact_person` varchar(200) DEFAULT '',
  `website` varchar(255) DEFAULT '',
  `edit_no` decimal(3,0) NOT NULL DEFAULT '0',
  `user_cd` decimal(3,0) NOT NULL,
  `time_stamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sls_chr_lbl` varchar(255) DEFAULT NULL,
  `delete_pwd` varchar(30) DEFAULT NULL,
  `ifsc_code` varchar(20) DEFAULT NULL,
  `bill_supply_type` smallint(4) DEFAULT NULL,
  `bill_supply` varchar(100) DEFAULT NULL,
  `bill_supply_desc` varchar(100) DEFAULT NULL,
  `is_retail` smallint(4) DEFAULT NULL,
  `multiple_company_data` smallint(4) DEFAULT NULL,
  `corraddress1` varchar(500) DEFAULT NULL,
  `corraddress2` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`cmpn_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `cmpny_mst` */

insert  into `cmpny_mst`(`cmpn_cd`,`cmpn_code`,`branch_code`,`cmpn_name`,`ac_year`,`mnth`,`sh_name`,`digit`,`invoice_type`,`image_path`,`add1`,`add2`,`area_cd`,`city_cd`,`pincode`,`mob_no`,`phone_no`,`licence_no`,`email`,`fax_no`,`pan_no`,`tin_no`,`cst_no`,`tax_no`,`bank_name`,`ac_no`,`branch_name`,`cash_ac_cd`,`lab_inc_ac`,`lab_exp_ac`,`sale_ac`,`purchase_ac`,`mypwd`,`contact_person`,`website`,`edit_no`,`user_cd`,`time_stamp`,`sls_chr_lbl`,`delete_pwd`,`ifsc_code`,`bill_supply_type`,`bill_supply`,`bill_supply_desc`,`is_retail`,`multiple_company_data`,`corraddress1`,`corraddress2`) values 
('C000001','01','001','DHANANI FROZAL','2017','01','SE',2,0,'','ahmedabad','ahmedabad','','','','9727397009','7405116442','','dhameliya.jaydeep@gmail.com','','ACNFS5720D','24060305663ABC','24560305663123','','STATE BANK OF INDIA','30425911901','BAPUNAGAR BR.','AM000003','0','0','0','0','','','',0,1,'2016-01-10 22:04:45','Loading Charge','123','SBI29051693',0,'BILL OF SUPPLY','Composition Taxable Person, Not Eligible to Collect Tax on Supplies',0,0,'ahmedabad','ahmedabad');

/*Table structure for table `dbmst` */

DROP TABLE IF EXISTS `dbmst`;

CREATE TABLE `dbmst` (
  `cmp_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `db_year` varchar(4) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `db_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `dbmonth` smallint(6) DEFAULT '0',
  `dbname` varchar(30) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT '',
  `dbyear` varchar(4) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `dbmst` */

insert  into `dbmst`(`cmp_name`,`db_year`,`db_name`,`dbmonth`,`dbname`,`dbyear`) values 
('DHANANI FROZAL','2018','dhananiforzal',5,'dhananiforzal','2018');

/*Table structure for table `form_mst` */

DROP TABLE IF EXISTS `form_mst`;

CREATE TABLE `form_mst` (
  `form_cd` decimal(5,0) NOT NULL,
  `form_name` varchar(255) DEFAULT NULL,
  `menu_cd` decimal(5,0) DEFAULT NULL,
  PRIMARY KEY (`form_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `form_mst` */

insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values 
(1,'MAIN CATEGORY',1),
(2,'SUB CATEGORY',1),
(3,'SLAB CATEGORY',1),
(4,'ACCOUNT MASTER',1),
(41,'PURCHASE BILL',2),
(42,'SLAB BREAKUP',2),
(43,'SALE BILL',2),
(44,'CASH PAYMENT',2),
(45,'CASH RECEIPT',2),
(46,'BANK PAYMENT',2),
(47,'BANK RECEIPT',2),
(70,'PURCHASE AVERAGE',3),
(71,'WORKABILITY',3),
(72,'STOCK SUMMARY',3);

/*Table structure for table `grade_main` */

DROP TABLE IF EXISTS `grade_main`;

CREATE TABLE `grade_main` (
  `id` varchar(8) NOT NULL,
  `v_date` date DEFAULT NULL,
  `fk_account_master_id` varchar(8) DEFAULT NULL,
  `fk_main_category_id` varchar(8) DEFAULT NULL,
  `fk_sub_category_id` varchar(8) DEFAULT NULL,
  `total_slabqty` decimal(15,5) DEFAULT '0.00000',
  `total_kgs` decimal(15,3) NOT NULL DEFAULT '0.000',
  `total_blockused` decimal(15,3) DEFAULT '0.000',
  `total_usd` decimal(15,3) DEFAULT '0.000',
  `total_inr` decimal(15,3) DEFAULT '0.000',
  `total_block` decimal(15,3) DEFAULT '0.000',
  `fix_time` varchar(8) DEFAULT NULL,
  `edit_no` decimal(3,0) NOT NULL DEFAULT '0',
  `user_cd` int(11) DEFAULT NULL,
  `time_stamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `grade_main` */

insert  into `grade_main`(`id`,`v_date`,`fk_account_master_id`,`fk_main_category_id`,`fk_sub_category_id`,`total_slabqty`,`total_kgs`,`total_blockused`,`total_usd`,`total_inr`,`total_block`,`fix_time`,`edit_no`,`user_cd`,`time_stamp`) values 
('BK000001','2018-12-24','AM000005','MC000001','SC000001',0.00000,40.000,0.000,325.000,650.000,16.000,'22:04:58',1,1,'2018-12-24 22:04:58'),
('BK000002','2018-12-24','AM000005','MC000001','SC000001',0.00000,7.000,0.000,25.000,250.000,7.000,'22:26:05',0,1,'2018-12-24 22:26:05'),
('BK000003','2019-01-07','AM000006','MC000001','SC000001',0.00000,20.000,0.000,50.000,3750.000,12.000,'01:30:36',1,1,'2019-03-17 01:30:36'),
('BK000004','2019-04-14','AM000006','MC000001','SC000001',20.00000,81.000,0.000,11500.000,0.000,11.000,'16:42:23',0,1,'2019-04-14 16:42:23'),
('BK000005','2019-04-19','AM000006','MC000001','SC000001',15.00000,30.000,25.000,1000.000,0.000,154.000,'23:05:44',0,1,'2019-04-19 23:05:44'),
('BK000006','2019-04-19','AM000006','MC000001','SC000001',15.00000,20.000,80.000,0.000,0.000,283.000,'23:08:47',0,1,'2019-04-19 23:08:47'),
('BK000007','2019-04-20','AM000006','MC000001','SC000001',30.00000,20.000,86.000,5000.000,0.000,450.000,'10:59:07',1,1,'2019-04-20 10:59:07');

/*Table structure for table `grade_sub` */

DROP TABLE IF EXISTS `grade_sub`;

CREATE TABLE `grade_sub` (
  `id` varchar(8) NOT NULL,
  `sr_no` int(11) NOT NULL,
  `fk_slab_category_id` varchar(8) DEFAULT NULL,
  `grad_qty` decimal(15,3) DEFAULT NULL,
  `kgs` decimal(15,3) DEFAULT NULL,
  `block_used` decimal(15,3) DEFAULT NULL,
  `rate_usd` decimal(15,3) DEFAULT NULL,
  `total_usd` decimal(15,3) DEFAULT NULL,
  `rate_inr` decimal(15,3) DEFAULT NULL,
  `total_inr` decimal(15,3) DEFAULT NULL,
  `block` decimal(15,3) DEFAULT NULL,
  PRIMARY KEY (`id`,`sr_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `grade_sub` */

insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values 
('BK000001',1,'SL000001',NULL,15.000,NULL,5.000,75.000,10.000,150.000,7.000),
('BK000001',2,'SL000003',NULL,25.000,NULL,10.000,250.000,20.000,500.000,9.000),
('BK000002',1,'SL000001',NULL,3.000,NULL,3.000,9.000,30.000,90.000,3.000),
('BK000002',2,'SL000003',NULL,4.000,NULL,4.000,16.000,40.000,160.000,4.000),
('BK000003',1,'SL000001',2.000,10.000,NULL,1.000,20.000,75.000,1500.000,7.000),
('BK000003',2,'SL000003',3.000,10.000,NULL,1.000,30.000,75.000,2250.000,5.000),
('BK000004',1,'SL000001',10.000,47.000,0.000,10.000,4700.000,0.000,0.000,7.000),
('BK000004',2,'SL000003',10.000,34.000,0.000,20.000,6800.000,0.000,0.000,4.000),
('BK000005',1,'SL000001',10.000,10.000,15.000,0.000,0.000,0.000,0.000,102.000),
('BK000005',2,'SL000003',5.000,20.000,10.000,10.000,1000.000,0.000,0.000,52.000),
('BK000006',1,'SL000001',10.000,10.000,50.000,0.000,0.000,0.000,0.000,189.000),
('BK000006',2,'SL000003',5.000,10.000,30.000,0.000,0.000,0.000,0.000,94.000),
('BK000007',1,'SL000001',10.000,10.000,78.000,10.000,1000.000,0.000,0.000,250.000),
('BK000007',2,'SL000003',20.000,10.000,8.000,20.000,4000.000,0.000,0.000,150.000);

/*Table structure for table `main_category` */

DROP TABLE IF EXISTS `main_category`;

CREATE TABLE `main_category` (
  `id` varchar(8) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `short_name` varchar(5) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `edit_no` decimal(3,0) DEFAULT '0',
  `user_cd` int(11) DEFAULT NULL,
  `time_stamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `main_category` */

insert  into `main_category`(`id`,`name`,`short_name`,`status`,`edit_no`,`user_cd`,`time_stamp`) values 
('MC000001','M1','',0,0,1,'2018-12-24 21:24:31'),
('MC000002','M2','',0,0,1,'2018-12-24 21:24:37'),
('MC000003','M3','M3',0,0,1,'2019-04-14 18:14:04');

/*Table structure for table `manage_email` */

DROP TABLE IF EXISTS `manage_email`;

CREATE TABLE `manage_email` (
  `manage_id` bigint(10) NOT NULL AUTO_INCREMENT,
  `manage_email` varchar(70) DEFAULT NULL,
  `manage_pwd` varchar(70) DEFAULT NULL,
  `manage_port` varchar(5) DEFAULT NULL,
  `manage_host` longtext,
  `manage_mobno` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`manage_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Data for the table `manage_email` */

insert  into `manage_email`(`manage_id`,`manage_email`,`manage_pwd`,`manage_port`,`manage_host`,`manage_mobno`) values 
(1,'dhameliya.jaydeep@yahoo.com','123','587','smtp.mail.yahoo.com','7405116442');

/*Table structure for table `menu_mst` */

DROP TABLE IF EXISTS `menu_mst`;

CREATE TABLE `menu_mst` (
  `menu_cd` decimal(5,0) NOT NULL,
  `menu_name` varchar(255) NOT NULL,
  PRIMARY KEY (`menu_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `menu_mst` */

insert  into `menu_mst`(`menu_cd`,`menu_name`) values 
(1,'MASTER'),
(2,'TRANSACTION'),
(6,'UTILITY');

/*Table structure for table `oldb2_1` */

DROP TABLE IF EXISTS `oldb2_1`;

CREATE TABLE `oldb2_1` (
  `ac_cd` varchar(8) DEFAULT NULL,
  `opb` decimal(15,3) DEFAULT NULL,
  `dr` decimal(15,3) DEFAULT NULL,
  `cr` decimal(15,3) DEFAULT NULL,
  `bal` decimal(15,3) DEFAULT NULL,
  `amount_type` tinyint(1) DEFAULT '0' COMMENT '0-USD, 1-RS'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `oldb2_1` */

insert  into `oldb2_1`(`ac_cd`,`opb`,`dr`,`cr`,`bal`,`amount_type`) values 
('AM000005',0.000,5400.000,24000.000,0.000,0),
('AM000006',200.000,8800.000,32800.000,0.000,0);

/*Table structure for table `oldb2_2` */

DROP TABLE IF EXISTS `oldb2_2`;

CREATE TABLE `oldb2_2` (
  `DOC_REF_NO` varchar(8) DEFAULT NULL,
  `DOC_CD` varchar(8) DEFAULT NULL,
  `DOC_DATE` date DEFAULT NULL,
  `AC_CD` varchar(8) DEFAULT NULL,
  `DRCR` varchar(1) DEFAULT NULL,
  `VAL` decimal(15,5) DEFAULT NULL,
  `PARTICULAR` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `oldb2_2` */

insert  into `oldb2_2`(`DOC_REF_NO`,`DOC_CD`,`DOC_DATE`,`AC_CD`,`DRCR`,`VAL`,`PARTICULAR`) values 
('OPB','OPB','2018-12-22','AM000005','0',0.00000,''),
('PB000001','PB','2018-12-24','AM000005','1',24000.00000,'Purchase Bill'),
('PB000001','PB','2018-12-24','AM000003','0',24000.00000,'Purchase Bill'),
('SL00002','SL','2018-12-30','AM000005','0',3000.00000,'SALES BILL'),
('OPB','OPB','2018-12-30','AM000006','0',200.00000,''),
('SL00003','SL','2019-01-07','AM000005','0',1200.00000,'SALES BILL'),
('SL00004','SL','2019-01-07','AM000005','0',1200.00000,'SALES BILL'),
('PB000002','PB','2019-01-07','AM000006','1',1800.00000,'Purchase Bill'),
('PB000002','PB','2019-01-07','AM000003','0',1800.00000,'Purchase Bill'),
('SL00005','SL','2019-01-07','AM000006','0',1800.00000,'SALES BILL'),
('PB000003','PB','2019-04-14','AM000006','1',31000.00000,'Purchase Bill'),
('PB000003','PB','2019-04-14','AM000003','0',31000.00000,'Purchase Bill'),
('SL00005','SL','2019-04-20','AM000006','0',3000.00000,'SALES BILL'),
('SL00006','SL','2019-04-20','AM000006','0',4000.00000,'SALES BILL');

/*Table structure for table `purchase_bill_details` */

DROP TABLE IF EXISTS `purchase_bill_details`;

CREATE TABLE `purchase_bill_details` (
  `id` varchar(8) NOT NULL,
  `sr_no` int(11) NOT NULL,
  `tali_no` varchar(30) DEFAULT NULL,
  `fk_main_category_id` varchar(8) DEFAULT NULL,
  `fk_sub_category_id` varchar(8) DEFAULT NULL,
  `weight` decimal(15,3) DEFAULT '0.000',
  `rate` decimal(15,3) DEFAULT '0.000',
  `amount` decimal(15,3) DEFAULT '0.000',
  PRIMARY KEY (`id`,`sr_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `purchase_bill_details` */

insert  into `purchase_bill_details`(`id`,`sr_no`,`tali_no`,`fk_main_category_id`,`fk_sub_category_id`,`weight`,`rate`,`amount`) values 
('PB000001',1,NULL,'MC000001','SC000001',200.000,20.000,4000.000),
('PB000002',1,NULL,'MC000001','0',12.000,50.000,600.000),
('PB000003',1,NULL,'MC000001','SC000001',100.000,10.000,1000.000),
('PB000003',2,NULL,'MC000001','SC000002',200.000,0.000,0.000);

/*Table structure for table `purchase_bill_head` */

DROP TABLE IF EXISTS `purchase_bill_head`;

CREATE TABLE `purchase_bill_head` (
  `id` varchar(8) NOT NULL,
  `fk_account_master_id` varchar(8) DEFAULT NULL,
  `v_date` date DEFAULT NULL,
  `expense` decimal(10,3) DEFAULT '0.000',
  `total_expense` decimal(10,3) DEFAULT '0.000',
  `other_expense` decimal(10,3) DEFAULT '0.000',
  `net_amt` decimal(10,3) DEFAULT '0.000',
  `total_weight` decimal(10,3) DEFAULT '0.000',
  `total_amount` decimal(10,3) DEFAULT '0.000',
  `description` varchar(255) DEFAULT NULL,
  `fix_time` varchar(8) DEFAULT NULL,
  `edit_no` decimal(3,0) DEFAULT '0',
  `talli_no` varchar(30) DEFAULT NULL,
  `user_cd` int(11) DEFAULT NULL,
  `time_stamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `purchase_bill_head` */

insert  into `purchase_bill_head`(`id`,`fk_account_master_id`,`v_date`,`expense`,`total_expense`,`other_expense`,`net_amt`,`total_weight`,`total_amount`,`description`,`fix_time`,`edit_no`,`talli_no`,`user_cd`,`time_stamp`) values 
('PB000001','AM000005','2018-12-24',100.000,20000.000,0.000,24000.000,200.000,4000.000,'','21:47:40',0,'12345',1,'2018-12-24 21:47:40'),
('PB000002','AM000006','2019-01-07',100.000,1200.000,0.000,1800.000,12.000,600.000,'','22:31:12',0,'12',1,'2019-01-07 22:31:12'),
('PB000003','AM000006','2019-04-14',100.000,30000.000,0.000,31000.000,300.000,1000.000,'','17:49:17',0,'T0001',1,'2019-04-14 17:49:17');

/*Table structure for table `sale_bill_detail` */

DROP TABLE IF EXISTS `sale_bill_detail`;

CREATE TABLE `sale_bill_detail` (
  `ref_no` varchar(7) NOT NULL,
  `sr_no` int(11) NOT NULL,
  `fk_main_category_id` varchar(11) DEFAULT NULL,
  `fk_sub_category_id` varchar(11) DEFAULT NULL,
  `fk_slab_category_id` varchar(11) NOT NULL,
  `qty` decimal(15,5) DEFAULT NULL,
  `rate` decimal(15,5) NOT NULL,
  `amt` decimal(15,5) NOT NULL,
  PRIMARY KEY (`ref_no`,`sr_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `sale_bill_detail` */

insert  into `sale_bill_detail`(`ref_no`,`sr_no`,`fk_main_category_id`,`fk_sub_category_id`,`fk_slab_category_id`,`qty`,`rate`,`amt`) values 
('SL00001',1,NULL,NULL,'SL000001',10.00000,100.00000,1000.00000),
('SL00002',1,NULL,NULL,'SL000003',15.00000,200.00000,3000.00000),
('SL00003',1,NULL,NULL,'SL000001',12.00000,100.00000,1200.00000),
('SL00004',1,NULL,NULL,'SL000001',12.00000,100.00000,1200.00000),
('SL00005',1,'MC000001','SC000001','SL000001',30.00000,100.00000,3000.00000),
('SL00006',1,'MC000001','SC000001','SL000001',40.00000,100.00000,4000.00000);

/*Table structure for table `sale_bill_head` */

DROP TABLE IF EXISTS `sale_bill_head`;

CREATE TABLE `sale_bill_head` (
  `ref_no` varchar(7) NOT NULL,
  `voucher_date` date DEFAULT NULL,
  `bill_no` varchar(15) NOT NULL,
  `amount_type` int(3) DEFAULT NULL,
  `fk_account_id` varchar(10) NOT NULL,
  `total_qty` decimal(15,5) NOT NULL DEFAULT '0.00000',
  `total_rate` decimal(15,5) NOT NULL DEFAULT '0.00000',
  `total_amt` decimal(15,5) NOT NULL DEFAULT '0.00000',
  `disc_per` decimal(10,3) DEFAULT '0.000',
  `disc_rs` decimal(10,3) DEFAULT '0.000',
  `amount_total` decimal(10,3) DEFAULT '0.000',
  `bill_amount` decimal(10,3) DEFAULT '0.000',
  `adj_amount` decimal(10,3) DEFAULT '0.000',
  `net_amount` decimal(10,3) DEFAULT '0.000',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `p_date` date DEFAULT NULL,
  `chck` int(3) DEFAULT NULL,
  `user_cd` decimal(3,0) NOT NULL,
  `edit_no` decimal(3,0) NOT NULL DEFAULT '0',
  `time_stamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ref_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `sale_bill_head` */

insert  into `sale_bill_head`(`ref_no`,`voucher_date`,`bill_no`,`amount_type`,`fk_account_id`,`total_qty`,`total_rate`,`total_amt`,`disc_per`,`disc_rs`,`amount_total`,`bill_amount`,`adj_amount`,`net_amount`,`remark`,`p_date`,`chck`,`user_cd`,`edit_no`,`time_stamp`) values 
('SL00001','2018-12-26','1',NULL,'AM000005',10.00000,0.00000,1000.00000,0.000,0.000,1000.000,1000.000,0.000,1000.000,'','2018-12-01',0,1,0,'2018-12-26 08:08:04'),
('SL00002','2018-12-30','2',0,'AM000005',15.00000,0.00000,3000.00000,0.000,0.000,3000.000,3000.000,0.000,3000.000,'','2018-12-01',0,1,1,'2018-12-30 18:50:46'),
('SL00003','2019-01-07','3',0,'AM000005',12.00000,0.00000,1200.00000,0.000,0.000,1200.000,1200.000,0.000,1200.000,'',NULL,0,1,0,'2019-01-07 08:34:33'),
('SL00004','2019-01-07','4',1,'AM000005',12.00000,0.00000,1200.00000,0.000,0.000,1200.000,1200.000,0.000,1200.000,'',NULL,0,1,0,'2019-01-07 08:34:46'),
('SL00005','2019-04-20','5',0,'AM000006',30.00000,0.00000,3000.00000,0.000,0.000,3000.000,3000.000,0.000,3000.000,'','2019-04-15',0,1,0,'2019-04-20 10:59:55'),
('SL00006','2019-04-20','6',0,'AM000006',40.00000,0.00000,4000.00000,0.000,0.000,4000.000,4000.000,0.000,4000.000,'','2019-04-16',0,1,1,'2019-04-20 11:01:20');

/*Table structure for table `slab_category` */

DROP TABLE IF EXISTS `slab_category`;

CREATE TABLE `slab_category` (
  `id` varchar(8) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `short_name` varchar(5) DEFAULT NULL,
  `rate` decimal(15,3) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `fk_sub_category_id` varchar(8) DEFAULT NULL,
  `edit_no` decimal(3,0) DEFAULT '0',
  `user_cd` int(11) DEFAULT NULL,
  `time_stamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `slab_category` */

insert  into `slab_category`(`id`,`name`,`short_name`,`rate`,`status`,`fk_sub_category_id`,`edit_no`,`user_cd`,`time_stamp`) values 
('SL000001','SL1','sl',100.000,0,'SC000001',0,1,'2018-12-24 21:41:32'),
('SL000002','SL3','SL',300.000,0,'SC000002',0,1,'2018-12-24 21:43:23'),
('SL000003','SL2','SL',200.000,0,'SC000001',0,1,'2018-12-24 21:43:48'),
('SL000004','SL4','SL',400.000,0,'SC000002',0,1,'2018-12-24 21:44:06'),
('SL000005','SL5','SL',500.000,0,'SC000002',0,1,'2018-12-24 21:44:17'),
('SL000006','SLAB7','SL7',30.000,0,'SC000006',0,1,'2019-04-14 18:56:08'),
('SL000007','SLAB7_2','SL7_2',50.000,0,'SC000006',0,1,'2019-04-14 18:57:04'),
('SL000008','SLAB8','SL8',80.000,0,'SC000007',0,1,'2019-04-14 18:57:23');

/*Table structure for table `stock0_1` */

DROP TABLE IF EXISTS `stock0_1`;

CREATE TABLE `stock0_1` (
  `stk01_cd` bigint(10) NOT NULL AUTO_INCREMENT,
  `fk_slab_category_id` varchar(10) DEFAULT NULL,
  `opb` decimal(10,3) DEFAULT NULL,
  `pur` decimal(10,3) DEFAULT NULL,
  `sal` decimal(10,3) DEFAULT NULL,
  `qty` decimal(10,3) DEFAULT NULL,
  `block` decimal(10,3) DEFAULT NULL,
  `block_used` decimal(10,3) DEFAULT NULL,
  `bal` decimal(10,3) DEFAULT NULL,
  `time_stamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`stk01_cd`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;

/*Data for the table `stock0_1` */

insert  into `stock0_1`(`stk01_cd`,`fk_slab_category_id`,`opb`,`pur`,`sal`,`qty`,`block`,`block_used`,`bal`,`time_stamp`) values 
(3,'SL000002',0.000,0.000,0.000,0.000,0.000,0.000,0.000,'2018-12-24 21:43:29'),
(5,'SL000004',0.000,0.000,0.000,0.000,0.000,0.000,0.000,'2018-12-24 21:44:06'),
(6,'SL000005',0.000,0.000,0.000,0.000,0.000,0.000,0.000,'2018-12-24 21:44:17'),
(7,'SL000001',0.000,28.000,106.000,930.000,250.000,0.000,NULL,'2018-12-24 21:59:33'),
(8,'SL000003',0.000,39.000,18.000,770.000,150.000,0.000,NULL,'2018-12-24 21:59:47'),
(16,'SL000006',0.000,0.000,0.000,0.000,0.000,0.000,0.000,'2019-04-14 18:56:08'),
(17,'SL000007',0.000,0.000,0.000,0.000,0.000,0.000,0.000,'2019-04-14 18:57:04'),
(18,'SL000008',0.000,0.000,0.000,0.000,0.000,0.000,0.000,'2019-04-14 18:57:23');

/*Table structure for table `stock0_2` */

DROP TABLE IF EXISTS `stock0_2`;

CREATE TABLE `stock0_2` (
  `stock2_id` bigint(10) NOT NULL AUTO_INCREMENT,
  `doc_id` varchar(10) DEFAULT NULL,
  `fk_slab_category_id` varchar(10) DEFAULT NULL,
  `trns_id` tinyint(1) DEFAULT '0' COMMENT '1-OPB, 2-GRADE, 3-PURR, 4-SAL, 5-SALR',
  `opb` decimal(10,2) DEFAULT NULL,
  `pur` decimal(10,2) DEFAULT NULL,
  `pur_r` decimal(10,2) DEFAULT NULL,
  `sal` decimal(10,2) DEFAULT NULL,
  `sal_r` decimal(10,2) DEFAULT NULL,
  `qty` decimal(10,2) DEFAULT NULL,
  `block` decimal(10,2) DEFAULT NULL,
  `block_used` decimal(10,2) DEFAULT NULL,
  `time_stamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`stock2_id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=latin1;

/*Data for the table `stock0_2` */

insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values 
(6,NULL,'SL000002',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2018-12-24 21:43:32'),
(8,NULL,'SL000004',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2018-12-24 21:44:06'),
(9,NULL,'SL000005',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2018-12-24 21:44:17'),
(29,NULL,'SL000006',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2019-04-14 18:56:08'),
(30,NULL,'SL000007',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2019-04-14 18:57:04'),
(31,NULL,'SL000008',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2019-04-14 18:57:23'),
(42,'BK000007','SL000001',2,NULL,NULL,NULL,NULL,NULL,100.00,0.00,78.00,'2019-04-20 10:59:07'),
(43,'BK000007','SL000003',2,NULL,NULL,NULL,NULL,NULL,200.00,0.00,8.00,'2019-04-20 10:59:07'),
(44,'SL00005','SL000001',4,NULL,NULL,NULL,30.00,NULL,NULL,NULL,NULL,'2019-04-20 10:59:56'),
(46,'SL00006','SL000001',4,NULL,NULL,NULL,40.00,NULL,NULL,NULL,NULL,'2019-04-20 11:01:20');

/*Table structure for table `sub_category` */

DROP TABLE IF EXISTS `sub_category`;

CREATE TABLE `sub_category` (
  `id` varchar(8) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `short_name` varchar(5) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `fk_main_category_id` varchar(8) NOT NULL,
  `edit_no` decimal(3,0) DEFAULT '0',
  `user_cd` int(11) DEFAULT NULL,
  `time_stamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `sub_category` */

insert  into `sub_category`(`id`,`name`,`short_name`,`status`,`fk_main_category_id`,`edit_no`,`user_cd`,`time_stamp`) values 
('SC000001','S1','',0,'MC000001',0,1,'2018-12-24 21:24:59'),
('SC000002','S2','',0,'MC000001',0,1,'2018-12-24 21:25:14'),
('SC000003','S3','',0,'MC000001',0,1,'2018-12-24 21:25:28'),
('SC000004','S4','',0,'MC000002',0,1,'2018-12-24 21:25:49'),
('SC000005','s5','',0,'MC000002',0,1,'2018-12-24 21:25:59'),
('SC000006','S7','S7',0,'MC000003',0,1,'2019-04-14 18:49:44'),
('SC000007','S8','S8',0,'MC000003',0,1,'2019-04-14 18:49:54');

/*Table structure for table `unt_mst` */

DROP TABLE IF EXISTS `unt_mst`;

CREATE TABLE `unt_mst` (
  `unt_cd` int(10) NOT NULL,
  `unt_name` varchar(50) DEFAULT NULL,
  `unt_symbol` varchar(30) DEFAULT NULL,
  `lower_cd` int(2) DEFAULT '0',
  `upper_cd` int(2) DEFAULT '0',
  `edit_no` decimal(3,0) NOT NULL DEFAULT '0',
  `user_cd` decimal(3,0) DEFAULT NULL,
  `time_stamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`unt_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `unt_mst` */

insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values 
(1,'KILO','KG',2,0,1,1,'2016-02-09 19:09:39'),
(2,'GRAM','GM',0,1,0,1,'2016-02-09 19:09:05'),
(3,'LITER','LT',4,0,0,1,'2016-02-09 19:09:12'),
(4,'MILILITER','ML',0,3,0,1,'2016-02-09 19:09:56'),
(5,'NOS.','NOS.',0,0,2,1,'2016-03-06 16:55:07'),
(6,'NONE','NONE',0,0,0,1,'2016-02-09 19:10:07'),
(7,'GLASSE(S)','GL',0,0,0,1,'2016-02-09 19:10:14'),
(8,'NO(S)','NO(S)',0,0,0,1,'2016-02-09 19:10:19'),
(9,'PKT(S)','PKT(S)',0,0,0,1,'2016-02-09 19:10:26'),
(10,'PCS(S)','PCS(S)',0,0,0,1,'2016-02-09 19:10:31'),
(11,'PLT(S)','PLT(S)',0,0,0,1,'2016-02-09 19:15:33'),
(12,'DOZEN(S)','DOZEN(S)',0,0,0,1,'2016-02-09 19:15:39'),
(13,'MTR(S)','MTR(S)',0,0,2,1,'2017-04-04 11:04:58'),
(14,'DF','EW',0,0,0,1,'2018-03-31 01:01:37');

/*Table structure for table `user_mst` */

DROP TABLE IF EXISTS `user_mst`;

CREATE TABLE `user_mst` (
  `user_cd` int(10) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  `user_photo` blob,
  `isactive` smallint(2) DEFAULT '1',
  `edit_no` smallint(2) DEFAULT '0',
  `modifiedby` int(5) DEFAULT NULL,
  `time_stamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_cd`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Data for the table `user_mst` */

insert  into `user_mst`(`user_cd`,`username`,`password`,`user_photo`,`isactive`,`edit_no`,`modifiedby`,`time_stamp`) values 
(1,'admin','',NULL,1,0,NULL,'2016-02-08 18:40:35');

/*Table structure for table `user_rights` */

DROP TABLE IF EXISTS `user_rights`;

CREATE TABLE `user_rights` (
  `user_cd` decimal(5,0) NOT NULL,
  `form_cd` decimal(5,0) NOT NULL,
  `views` decimal(1,0) DEFAULT '0',
  `edit` decimal(1,0) DEFAULT '0',
  `adds` decimal(1,0) DEFAULT '0',
  `deletes` decimal(1,0) DEFAULT '0',
  `print` decimal(1,0) DEFAULT '0',
  `navigate_view` decimal(1,0) DEFAULT '0',
  PRIMARY KEY (`form_cd`,`user_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `user_rights` */

insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values 
(1,1,1,1,1,1,1,1),
(1,2,1,1,1,1,1,1),
(1,3,1,1,1,1,1,1),
(1,4,1,1,1,1,1,1),
(1,41,1,1,1,1,1,1),
(1,42,1,1,1,1,1,1),
(1,43,1,1,1,1,1,1),
(1,70,1,1,1,1,1,1),
(1,71,1,1,1,1,1,1),
(1,72,1,1,1,1,1,1);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
