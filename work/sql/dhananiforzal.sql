/*
SQLyog Community v13.1.1 (64 bit)
MySQL - 5.0.67-community-nt : Database - dhananiforzal
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`dhananiforzal` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `dhananiforzal`;

/*Table structure for table `account_master` */

DROP TABLE IF EXISTS `account_master`;

CREATE TABLE `account_master` (
  `id` varchar(8) NOT NULL,
  `name` varchar(255) default NULL,
  `expense` decimal(10,2) NOT NULL default '0.00',
  `status` tinyint(1) default NULL,
  `opb` decimal(10,3) default '0.000',
  `amount_type` tinyint(1) default NULL,
  `fk_group_id` varchar(8) default NULL,
  `lock_date` date default NULL,
  `edit_no` decimal(3,0) default '0',
  `user_cd` int(11) default NULL,
  `time_stamp` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `account_master` */

insert  into `account_master`(`id`,`name`,`expense`,`status`,`opb`,`amount_type`,`fk_group_id`,`lock_date`,`edit_no`,`user_cd`,`time_stamp`) values ('AM000003','CASH',0.00,0,0.000,NULL,NULL,'2018-01-01',0,1,'2018-12-22 14:38:25');
insert  into `account_master`(`id`,`name`,`expense`,`status`,`opb`,`amount_type`,`fk_group_id`,`lock_date`,`edit_no`,`user_cd`,`time_stamp`) values ('AM000005','CHECK ACCOUNT',0.00,0,0.000,NULL,NULL,'2018-01-01',0,1,'2018-12-22 19:11:06');
insert  into `account_master`(`id`,`name`,`expense`,`status`,`opb`,`amount_type`,`fk_group_id`,`lock_date`,`edit_no`,`user_cd`,`time_stamp`) values ('AM000006','TEST OPB',100.00,0,200.000,0,NULL,'2018-01-01',4,1,'2019-01-07 22:29:07');

/*Table structure for table `appconfig` */

DROP TABLE IF EXISTS `appconfig`;

CREATE TABLE `appconfig` (
  `client_id` varchar(20) collate utf8_unicode_ci default NULL,
  `issue_date` date default NULL,
  `act_date` date default NULL,
  `due_date` date default NULL,
  `act_cd` varchar(20) collate utf8_unicode_ci default NULL,
  `license_no` varchar(20) collate utf8_unicode_ci default NULL,
  `email` varchar(50) collate utf8_unicode_ci default NULL,
  `server` varchar(100) collate utf8_unicode_ci default NULL,
  `act1` varchar(4) collate utf8_unicode_ci default '',
  `act2` varchar(4) collate utf8_unicode_ci default '',
  `act3` varchar(4) collate utf8_unicode_ci default '',
  `act4` varchar(4) collate utf8_unicode_ci default '',
  `user_nm` varchar(50) collate utf8_unicode_ci default NULL,
  `pass` varchar(20) collate utf8_unicode_ci default NULL,
  `status` int(11) default NULL,
  `months` int(11) default NULL,
  `days` int(11) default NULL,
  `haddr` varchar(20) collate utf8_unicode_ci default NULL,
  `iaddr` varchar(40) collate utf8_unicode_ci default NULL,
  `laststart` date default NULL,
  `def` varchar(20) collate utf8_unicode_ci default '',
  `app_limit` smallint(6) default '0',
  `cur_limit` smallint(6) default '0',
  `tmode` smallint(6) default '0',
  `proj_mode` smallint(6) default '0',
  `mlt_mac` smallint(6) default '0',
  `mac_lmt` int(10) default '1',
  `is_tax` smallint(6) default NULL,
  `theme_cd` int(11) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `appconfig` */

insert  into `appconfig`(`client_id`,`issue_date`,`act_date`,`due_date`,`act_cd`,`license_no`,`email`,`server`,`act1`,`act2`,`act3`,`act4`,`user_nm`,`pass`,`status`,`months`,`days`,`haddr`,`iaddr`,`laststart`,`def`,`app_limit`,`cur_limit`,`tmode`,`proj_mode`,`mlt_mac`,`mac_lmt`,`is_tax`,`theme_cd`) values ('STOCK MANAGEMENT SOF','2018-09-20','2018-09-20','2019-01-01',NULL,'SMS-000001-15-06',NULL,NULL,'0000','1111','2222','3333','LG','LG123',-1,0,0,'90-4C-E5-07-79-87','192.168.0.108','2018-09-19','',0,0,0,1,0,1,0,4);

/*Table structure for table `bank_payment_receipt_details` */

DROP TABLE IF EXISTS `bank_payment_receipt_details`;

CREATE TABLE `bank_payment_receipt_details` (
  `id` varchar(8) NOT NULL,
  `sr_no` int(11) NOT NULL,
  `fk_account_master_id` varchar(8) NOT NULL,
  `amount` decimal(15,5) NOT NULL,
  `chq_dt` date NOT NULL,
  `chq_no` varchar(50) default NULL,
  `remark` varchar(255) default NULL,
  PRIMARY KEY  (`id`,`sr_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `bank_payment_receipt_details` */

/*Table structure for table `bank_payment_receipt_head` */

DROP TABLE IF EXISTS `bank_payment_receipt_head`;

CREATE TABLE `bank_payment_receipt_head` (
  `id` varchar(8) NOT NULL,
  `voucher_date` date default NULL,
  `fk_bank_cd` varchar(8) default NULL,
  `total_bal` decimal(15,5) default NULL,
  `ctype` int(11) default NULL,
  `is_del` int(11) default '0',
  `prn_no` int(11) default '0',
  `fix_time` varchar(10) default NULL,
  `edit_no` decimal(3,0) NOT NULL default '0',
  `user_cd` decimal(3,0) default NULL,
  `time_stamp` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `bank_payment_receipt_head` */

/*Table structure for table `cash_payment_receipt_details` */

DROP TABLE IF EXISTS `cash_payment_receipt_details`;

CREATE TABLE `cash_payment_receipt_details` (
  `id` varchar(8) NOT NULL,
  `sr_no` int(10) NOT NULL,
  `fk_account_master_id` varchar(8) default NULL,
  `amount` decimal(15,3) default '0.000',
  `remark` varchar(255) default NULL,
  PRIMARY KEY  (`id`,`sr_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `cash_payment_receipt_details` */

/*Table structure for table `cash_payment_receipt_head` */

DROP TABLE IF EXISTS `cash_payment_receipt_head`;

CREATE TABLE `cash_payment_receipt_head` (
  `id` varchar(8) NOT NULL,
  `voucher_date` date default NULL,
  `total_bal` decimal(10,3) default NULL,
  `fk_account_master_id` varchar(8) default NULL,
  `remarks` varchar(255) default NULL,
  `ctype` int(2) default NULL,
  `is_del` int(2) default '0',
  `fix_time` varchar(10) default NULL,
  `edit_no` decimal(3,0) NOT NULL default '0',
  `user_cd` decimal(3,0) default NULL,
  `time_stamp` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `cash_payment_receipt_head` */

/*Table structure for table `change_themes` */

DROP TABLE IF EXISTS `change_themes`;

CREATE TABLE `change_themes` (
  `id` int(11) NOT NULL auto_increment,
  `theme_name` varchar(255) default NULL,
  `theme_link` varchar(500) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;

/*Data for the table `change_themes` */

insert  into `change_themes`(`id`,`theme_name`,`theme_link`) values (1,'TextureLookAndFeel ','com.jtattoo.plaf.texture.TextureLookAndFeel');
insert  into `change_themes`(`id`,`theme_name`,`theme_link`) values (2,'SmartLookAndFeel','com.jtattoo.plaf.smart.SmartLookAndFeel');
insert  into `change_themes`(`id`,`theme_name`,`theme_link`) values (3,'NoireLookAndFeel','com.jtattoo.plaf.noire.NoireLookAndFeel');
insert  into `change_themes`(`id`,`theme_name`,`theme_link`) values (4,'AcrylLookAndFeel','com.jtattoo.plaf.acryl.AcrylLookAndFeel');
insert  into `change_themes`(`id`,`theme_name`,`theme_link`) values (5,'AeroLookAndFeel','com.jtattoo.plaf.aero.AeroLookAndFeel');
insert  into `change_themes`(`id`,`theme_name`,`theme_link`) values (6,'AluminiumLookAndFeel','com.jtattoo.plaf.aluminium.AluminiumLookAndFeel');
insert  into `change_themes`(`id`,`theme_name`,`theme_link`) values (7,'BernsteinLookAndFeel','com.jtattoo.plaf.bernstein.BernsteinLookAndFeel');
insert  into `change_themes`(`id`,`theme_name`,`theme_link`) values (8,'FastLookAndFeel','com.jtattoo.plaf.fast.FastLookAndFeel');
insert  into `change_themes`(`id`,`theme_name`,`theme_link`) values (9,'GraphiteLookAndFeel','com.jtattoo.plaf.graphite.GraphiteLookAndFeel');
insert  into `change_themes`(`id`,`theme_name`,`theme_link`) values (10,'HiFiLookAndFeel','com.jtattoo.plaf.hifi.HiFiLookAndFeel');
insert  into `change_themes`(`id`,`theme_name`,`theme_link`) values (11,'LunaLookAndFeel','com.jtattoo.plaf.luna.LunaLookAndFeel');
insert  into `change_themes`(`id`,`theme_name`,`theme_link`) values (12,'McWinLookAndFeel ','com.jtattoo.plaf.mcwin.McWinLookAndFeel');
insert  into `change_themes`(`id`,`theme_name`,`theme_link`) values (13,'MintLookAndFeel','com.jtattoo.plaf.mint.MintLookAndFeel');

/*Table structure for table `cmpny_mst` */

DROP TABLE IF EXISTS `cmpny_mst`;

CREATE TABLE `cmpny_mst` (
  `cmpn_cd` varchar(7) NOT NULL,
  `cmpn_code` varchar(2) default NULL,
  `branch_code` varchar(3) default NULL,
  `cmpn_name` varchar(50) NOT NULL,
  `ac_year` varchar(4) NOT NULL,
  `mnth` varchar(2) NOT NULL,
  `sh_name` varchar(2) default NULL,
  `digit` decimal(1,0) NOT NULL,
  `invoice_type` decimal(1,0) default NULL,
  `image_path` varchar(255) default '',
  `add1` varchar(500) default '',
  `add2` varchar(500) default '',
  `area_cd` varchar(7) default '',
  `city_cd` varchar(7) default '',
  `pincode` varchar(6) default '',
  `mob_no` varchar(17) default '',
  `phone_no` varchar(17) default '',
  `licence_no` varchar(30) default '',
  `email` varchar(70) default '',
  `fax_no` varchar(17) default '',
  `pan_no` varchar(20) default '',
  `tin_no` varchar(20) default '',
  `cst_no` varchar(20) default '',
  `tax_no` varchar(20) default '',
  `bank_name` varchar(100) default '',
  `ac_no` varchar(20) default '',
  `branch_name` varchar(100) default '',
  `cash_ac_cd` varchar(8) default '',
  `lab_inc_ac` varchar(8) default '',
  `lab_exp_ac` varchar(8) default '',
  `sale_ac` varchar(8) default '',
  `purchase_ac` varchar(8) default '',
  `mypwd` varchar(10) default NULL,
  `contact_person` varchar(200) default '',
  `website` varchar(255) default '',
  `edit_no` decimal(3,0) NOT NULL default '0',
  `user_cd` decimal(3,0) NOT NULL,
  `time_stamp` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `sls_chr_lbl` varchar(255) default NULL,
  `delete_pwd` varchar(30) default NULL,
  `ifsc_code` varchar(20) default NULL,
  `bill_supply_type` smallint(4) default NULL,
  `bill_supply` varchar(100) default NULL,
  `bill_supply_desc` varchar(100) default NULL,
  `is_retail` smallint(4) default NULL,
  `multiple_company_data` smallint(4) default NULL,
  `corraddress1` varchar(500) default NULL,
  `corraddress2` varchar(500) default NULL,
  PRIMARY KEY  (`cmpn_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `cmpny_mst` */

insert  into `cmpny_mst`(`cmpn_cd`,`cmpn_code`,`branch_code`,`cmpn_name`,`ac_year`,`mnth`,`sh_name`,`digit`,`invoice_type`,`image_path`,`add1`,`add2`,`area_cd`,`city_cd`,`pincode`,`mob_no`,`phone_no`,`licence_no`,`email`,`fax_no`,`pan_no`,`tin_no`,`cst_no`,`tax_no`,`bank_name`,`ac_no`,`branch_name`,`cash_ac_cd`,`lab_inc_ac`,`lab_exp_ac`,`sale_ac`,`purchase_ac`,`mypwd`,`contact_person`,`website`,`edit_no`,`user_cd`,`time_stamp`,`sls_chr_lbl`,`delete_pwd`,`ifsc_code`,`bill_supply_type`,`bill_supply`,`bill_supply_desc`,`is_retail`,`multiple_company_data`,`corraddress1`,`corraddress2`) values ('C000001','01','001','DHANANI FROZAL','2017','01','SE',2,0,'','ahmedabad','ahmedabad','','','','9727397009','7405116442','','dhameliya.jaydeep@gmail.com','','ACNFS5720D','24060305663ABC','24560305663123','','STATE BANK OF INDIA','30425911901','BAPUNAGAR BR.','AM000003','0','0','0','0','','','',0,1,'2016-01-10 22:04:45','Loading Charge','123','SBI29051693',0,'BILL OF SUPPLY','Composition Taxable Person, Not Eligible to Collect Tax on Supplies',0,0,'ahmedabad','ahmedabad');

/*Table structure for table `dbmst` */

DROP TABLE IF EXISTS `dbmst`;

CREATE TABLE `dbmst` (
  `cmp_name` varchar(100) collate utf8_unicode_ci NOT NULL,
  `db_year` varchar(4) collate utf8_unicode_ci NOT NULL,
  `db_name` varchar(30) collate utf8_unicode_ci NOT NULL,
  `dbmonth` smallint(6) default '0',
  `dbname` varchar(30) collate utf8_unicode_ci default '',
  `dbyear` varchar(4) collate utf8_unicode_ci default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `dbmst` */

insert  into `dbmst`(`cmp_name`,`db_year`,`db_name`,`dbmonth`,`dbname`,`dbyear`) values ('DHANANI FROZAL','2018','dhananiforzal',5,'dhananiforzal','2018');

/*Table structure for table `form_mst` */

DROP TABLE IF EXISTS `form_mst`;

CREATE TABLE `form_mst` (
  `form_cd` decimal(5,0) NOT NULL,
  `form_name` varchar(255) default NULL,
  `menu_cd` decimal(5,0) default NULL,
  PRIMARY KEY  (`form_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `form_mst` */

insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (1,'MAIN CATEGORY',1);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (2,'SUB CATEGORY',1);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (3,'SLAB CATEGORY',1);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (4,'ACCOUNT MASTER',1);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (5,'GROUP MASTER',1);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (41,'PURCHASE BILL',2);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (42,'SLAB BREAKUP',2);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (43,'SALE BILL',2);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (44,'CASH PAYMENT',2);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (45,'CASH RECEIPT',2);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (46,'BANK PAYMENT',2);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (47,'BANK RECEIPT',2);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (70,'PURCHASE AVERAGE',3);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (71,'WORKABILITY',3);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (72,'STOCK SUMMARY',3);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (73,'COLLECTION REPORT',3);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (74,'GROUP SUMMARY',3);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (75,'GENERAL LEDGER',3);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (111,'COMPANY SETTING',4);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (112,'MANAGE USER',4);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (113,'USER RIGHTS',4);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (114,'MANAGE EMAIL',4);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (115,'CHANGE PASSWORD',4);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (116,'QUICK OPEN',4);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (117,'BACK UP',4);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (118,'CHECK PRINT',4);
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values (119,'CHANGE THEMES',4);

/*Table structure for table `grade_main` */

DROP TABLE IF EXISTS `grade_main`;

CREATE TABLE `grade_main` (
  `id` varchar(8) NOT NULL,
  `v_date` date default NULL,
  `fk_account_master_id` varchar(8) default NULL,
  `fk_main_category_id` varchar(8) default NULL,
  `fk_sub_category_id` varchar(8) default NULL,
  `total_slabqty` decimal(15,5) default '0.00000',
  `total_kgs` decimal(15,3) NOT NULL default '0.000',
  `total_blockused` decimal(15,3) default '0.000',
  `total_usd` decimal(15,3) default '0.000',
  `total_inr` decimal(15,3) default '0.000',
  `total_block` decimal(15,3) default '0.000',
  `fix_time` varchar(8) default NULL,
  `edit_no` decimal(3,0) NOT NULL default '0',
  `user_cd` int(11) default NULL,
  `time_stamp` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `grade_main` */

insert  into `grade_main`(`id`,`v_date`,`fk_account_master_id`,`fk_main_category_id`,`fk_sub_category_id`,`total_slabqty`,`total_kgs`,`total_blockused`,`total_usd`,`total_inr`,`total_block`,`fix_time`,`edit_no`,`user_cd`,`time_stamp`) values ('BK000001','2018-12-24','AM000005','MC000001','SC000001',0.00000,40.000,0.000,325.000,650.000,16.000,'22:04:58',1,1,'2018-12-24 22:04:58');
insert  into `grade_main`(`id`,`v_date`,`fk_account_master_id`,`fk_main_category_id`,`fk_sub_category_id`,`total_slabqty`,`total_kgs`,`total_blockused`,`total_usd`,`total_inr`,`total_block`,`fix_time`,`edit_no`,`user_cd`,`time_stamp`) values ('BK000002','2018-12-24','AM000005','MC000001','SC000001',0.00000,7.000,0.000,25.000,250.000,7.000,'22:26:05',0,1,'2018-12-24 22:26:05');
insert  into `grade_main`(`id`,`v_date`,`fk_account_master_id`,`fk_main_category_id`,`fk_sub_category_id`,`total_slabqty`,`total_kgs`,`total_blockused`,`total_usd`,`total_inr`,`total_block`,`fix_time`,`edit_no`,`user_cd`,`time_stamp`) values ('BK000003','2019-01-07','AM000006','MC000001','SC000001',0.00000,20.000,0.000,50.000,3750.000,12.000,'01:30:36',1,1,'2019-03-17 01:30:36');
insert  into `grade_main`(`id`,`v_date`,`fk_account_master_id`,`fk_main_category_id`,`fk_sub_category_id`,`total_slabqty`,`total_kgs`,`total_blockused`,`total_usd`,`total_inr`,`total_block`,`fix_time`,`edit_no`,`user_cd`,`time_stamp`) values ('BK000004','2019-04-14','AM000006','MC000001','SC000001',20.00000,81.000,0.000,11500.000,0.000,11.000,'16:42:23',0,1,'2019-04-14 16:42:23');
insert  into `grade_main`(`id`,`v_date`,`fk_account_master_id`,`fk_main_category_id`,`fk_sub_category_id`,`total_slabqty`,`total_kgs`,`total_blockused`,`total_usd`,`total_inr`,`total_block`,`fix_time`,`edit_no`,`user_cd`,`time_stamp`) values ('BK000005','2019-04-19','AM000006','MC000001','SC000001',15.00000,30.000,25.000,1000.000,0.000,154.000,'23:05:44',0,1,'2019-04-19 23:05:44');
insert  into `grade_main`(`id`,`v_date`,`fk_account_master_id`,`fk_main_category_id`,`fk_sub_category_id`,`total_slabqty`,`total_kgs`,`total_blockused`,`total_usd`,`total_inr`,`total_block`,`fix_time`,`edit_no`,`user_cd`,`time_stamp`) values ('BK000006','2019-04-19','AM000006','MC000001','SC000001',15.00000,20.000,80.000,0.000,0.000,283.000,'23:08:47',0,1,'2019-04-19 23:08:47');
insert  into `grade_main`(`id`,`v_date`,`fk_account_master_id`,`fk_main_category_id`,`fk_sub_category_id`,`total_slabqty`,`total_kgs`,`total_blockused`,`total_usd`,`total_inr`,`total_block`,`fix_time`,`edit_no`,`user_cd`,`time_stamp`) values ('BK000007','2019-04-20','AM000006','MC000001','SC000001',30.00000,20.000,86.000,5000.000,0.000,450.000,'10:59:07',1,1,'2019-04-20 10:59:07');
insert  into `grade_main`(`id`,`v_date`,`fk_account_master_id`,`fk_main_category_id`,`fk_sub_category_id`,`total_slabqty`,`total_kgs`,`total_blockused`,`total_usd`,`total_inr`,`total_block`,`fix_time`,`edit_no`,`user_cd`,`time_stamp`) values ('BK000008','2019-05-17','AM000005','MC000004','SC000008',2.00000,5.000,0.000,15.000,15.000,0.000,'23:45:37',0,1,'2019-05-17 23:45:37');

/*Table structure for table `grade_sub` */

DROP TABLE IF EXISTS `grade_sub`;

CREATE TABLE `grade_sub` (
  `id` varchar(8) NOT NULL,
  `sr_no` int(11) NOT NULL,
  `fk_slab_category_id` varchar(8) default NULL,
  `grad_qty` decimal(15,3) default NULL,
  `kgs` decimal(15,3) default NULL,
  `block_used` decimal(15,3) default NULL,
  `rate_usd` decimal(15,3) default NULL,
  `total_usd` decimal(15,3) default NULL,
  `rate_inr` decimal(15,3) default NULL,
  `total_inr` decimal(15,3) default NULL,
  `block` decimal(15,3) default NULL,
  PRIMARY KEY  (`id`,`sr_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `grade_sub` */

insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000001',1,'SL000001',NULL,15.000,NULL,5.000,75.000,10.000,150.000,7.000);
insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000001',2,'SL000003',NULL,25.000,NULL,10.000,250.000,20.000,500.000,9.000);
insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000002',1,'SL000001',NULL,3.000,NULL,3.000,9.000,30.000,90.000,3.000);
insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000002',2,'SL000003',NULL,4.000,NULL,4.000,16.000,40.000,160.000,4.000);
insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000003',1,'SL000001',2.000,10.000,NULL,1.000,20.000,75.000,1500.000,7.000);
insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000003',2,'SL000003',3.000,10.000,NULL,1.000,30.000,75.000,2250.000,5.000);
insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000004',1,'SL000001',10.000,47.000,0.000,10.000,4700.000,0.000,0.000,7.000);
insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000004',2,'SL000003',10.000,34.000,0.000,20.000,6800.000,0.000,0.000,4.000);
insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000005',1,'SL000001',10.000,10.000,15.000,0.000,0.000,0.000,0.000,102.000);
insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000005',2,'SL000003',5.000,20.000,10.000,10.000,1000.000,0.000,0.000,52.000);
insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000006',1,'SL000001',10.000,10.000,50.000,0.000,0.000,0.000,0.000,189.000);
insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000006',2,'SL000003',5.000,10.000,30.000,0.000,0.000,0.000,0.000,94.000);
insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000007',1,'SL000001',10.000,10.000,78.000,10.000,1000.000,0.000,0.000,250.000);
insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000007',2,'SL000003',20.000,10.000,8.000,20.000,4000.000,0.000,0.000,150.000);
insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000008',1,'SL000009',1.000,2.000,0.000,3.000,6.000,3.000,6.000,0.000);
insert  into `grade_sub`(`id`,`sr_no`,`fk_slab_category_id`,`grad_qty`,`kgs`,`block_used`,`rate_usd`,`total_usd`,`rate_inr`,`total_inr`,`block`) values ('BK000008',2,'SL000010',1.000,3.000,0.000,3.000,9.000,3.000,9.000,0.000);

/*Table structure for table `group_master` */

DROP TABLE IF EXISTS `group_master`;

CREATE TABLE `group_master` (
  `id` varchar(8) NOT NULL,
  `name` varchar(50) default NULL,
  `head` varchar(5) default NULL,
  `head_grp` varchar(8) default NULL,
  `acc_eff` int(5) default NULL,
  `side` int(5) default '2',
  `edit_no` decimal(3,0) NOT NULL default '0',
  `user_cd` decimal(3,0) default NULL,
  `time_stamp` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `group_master` */

insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000001','SUNDRY DEBTORS','0','',2,1,0,1,'2014-02-17 16:47:41');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000002','SUNDRY CREDITORS','0','',2,0,0,1,'2014-02-17 16:47:41');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000003','CAPITAL','0','',2,0,0,1,'2014-02-17 16:47:41');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000004','ASSETS','0','',2,1,0,1,'2014-02-17 16:47:41');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000005','DIRECT EXPENSES','0','',0,0,0,1,'2014-02-17 16:47:41');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000006','DIRECT INCOME','0','',0,1,0,1,'2014-02-17 16:47:41');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000007','CURRENT LIABILITIES','0','',2,0,0,1,'2014-02-17 16:47:41');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000008','CURRENT ASSETS','0','',2,1,0,1,'2014-02-17 16:47:41');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000009','INDIRECT EXPENSES','0','',1,0,0,1,'2014-02-17 16:47:41');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000010','INDIRECT INCOME','0','',1,1,0,1,'2014-02-17 16:47:41');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000011','LOANS & LIABILITIES','0','',2,0,0,1,'2014-02-17 16:47:41');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000012','CASH','0','',2,1,0,1,'2014-02-17 16:47:41');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000013','BANK','0','',2,1,0,1,'2014-02-17 16:47:41');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000014','LOANS & ASSETS','0','',2,1,0,1,'2014-02-17 16:47:41');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000015','CUSTOMER','1','G0000001',2,2,0,1,'2016-02-09 19:02:41');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000016','KARIGAR','1','G0000002',2,2,0,1,'2016-02-21 01:19:37');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000017','VEPARI','1','G0000001',2,2,1,1,'2016-03-06 15:51:56');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000018','COURIER','1','G0000001',2,2,0,1,'2016-03-06 16:06:37');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000019','TRANSPORTATION CHARGES','1','G0000012',2,2,0,1,'2016-03-06 16:25:55');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000020','COMMISSION EXP.','1','G0000002',2,2,2,1,'2017-04-02 00:17:40');
insert  into `group_master`(`id`,`name`,`head`,`head_grp`,`acc_eff`,`side`,`edit_no`,`user_cd`,`time_stamp`) values ('G0000021','SUPPLIER','1','G0000002',2,2,3,1,'2018-03-08 00:36:59');

/*Table structure for table `main_category` */

DROP TABLE IF EXISTS `main_category`;

CREATE TABLE `main_category` (
  `id` varchar(8) NOT NULL,
  `name` varchar(255) default NULL,
  `short_name` varchar(5) default NULL,
  `status` tinyint(1) default NULL,
  `edit_no` decimal(3,0) default '0',
  `user_cd` int(11) default NULL,
  `time_stamp` timestamp NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `main_category` */

insert  into `main_category`(`id`,`name`,`short_name`,`status`,`edit_no`,`user_cd`,`time_stamp`) values ('MC000001','M1','',0,0,1,'2018-12-24 21:24:31');
insert  into `main_category`(`id`,`name`,`short_name`,`status`,`edit_no`,`user_cd`,`time_stamp`) values ('MC000002','M2','',0,0,1,'2018-12-24 21:24:37');
insert  into `main_category`(`id`,`name`,`short_name`,`status`,`edit_no`,`user_cd`,`time_stamp`) values ('MC000003','M3','M3',0,0,1,'2019-04-14 18:14:04');
insert  into `main_category`(`id`,`name`,`short_name`,`status`,`edit_no`,`user_cd`,`time_stamp`) values ('MC000004','1','1',0,0,1,'2019-05-17 23:39:54');

/*Table structure for table `manage_email` */

DROP TABLE IF EXISTS `manage_email`;

CREATE TABLE `manage_email` (
  `manage_id` bigint(10) NOT NULL auto_increment,
  `manage_email` varchar(70) default NULL,
  `manage_pwd` varchar(70) default NULL,
  `manage_port` varchar(5) default NULL,
  `manage_host` longtext,
  `manage_mobno` varchar(20) default NULL,
  PRIMARY KEY  (`manage_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Data for the table `manage_email` */

insert  into `manage_email`(`manage_id`,`manage_email`,`manage_pwd`,`manage_port`,`manage_host`,`manage_mobno`) values (1,'dhameliya.jaydeep@yahoo.com','123','587','smtp.mail.yahoo.com','7405116442');

/*Table structure for table `menu_mst` */

DROP TABLE IF EXISTS `menu_mst`;

CREATE TABLE `menu_mst` (
  `menu_cd` decimal(5,0) NOT NULL,
  `menu_name` varchar(255) NOT NULL,
  PRIMARY KEY  (`menu_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `menu_mst` */

insert  into `menu_mst`(`menu_cd`,`menu_name`) values (1,'MASTER');
insert  into `menu_mst`(`menu_cd`,`menu_name`) values (2,'TRANSACTION');
insert  into `menu_mst`(`menu_cd`,`menu_name`) values (3,'REPORT');
insert  into `menu_mst`(`menu_cd`,`menu_name`) values (4,'UTILITY');

/*Table structure for table `oldb2_1` */

DROP TABLE IF EXISTS `oldb2_1`;

CREATE TABLE `oldb2_1` (
  `ac_cd` varchar(8) default NULL,
  `grp_cd` varchar(8) default NULL,
  `opb` decimal(15,3) default NULL,
  `dr` decimal(15,3) default NULL,
  `cr` decimal(15,3) default NULL,
  `bal` decimal(15,3) default NULL,
  `amount_type` tinyint(1) default '0' COMMENT '0-USD, 1-RS'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `oldb2_1` */

insert  into `oldb2_1`(`ac_cd`,`grp_cd`,`opb`,`dr`,`cr`,`bal`,`amount_type`) values ('AM000005',NULL,0.000,5412.000,24001.000,2.000,0);
insert  into `oldb2_1`(`ac_cd`,`grp_cd`,`opb`,`dr`,`cr`,`bal`,`amount_type`) values ('AM000006',NULL,200.000,8800.000,32800.000,0.000,0);

/*Table structure for table `oldb2_2` */

DROP TABLE IF EXISTS `oldb2_2`;

CREATE TABLE `oldb2_2` (
  `doc_ref_no` varchar(8) default NULL,
  `doc_cd` varchar(8) default NULL,
  `doc_date` date default NULL,
  `ac_cd` varchar(8) default NULL,
  `drcr` varchar(1) default NULL,
  `val` decimal(15,5) default NULL,
  `particular` varchar(100) character set utf8 collate utf8_unicode_ci default NULL,
  `fix_time` varchar(10) default NULL,
  `opp_ac_cd` varchar(8) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `oldb2_2` */

insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('OPB','OPB','2018-12-22','AM000005','0',0.00000,'',NULL,'');
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('PB000001','PB','2018-12-24','AM000005','1',24000.00000,'Purchase Bill',NULL,'');
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('PB000001','PB','2018-12-24','AM000003','0',24000.00000,'Purchase Bill',NULL,'');
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('SL00002','SL','2018-12-30','AM000005','0',3000.00000,'SALES BILL',NULL,'');
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('OPB','OPB','2018-12-30','AM000006','0',200.00000,'',NULL,'');
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('SL00003','SL','2019-01-07','AM000005','0',1200.00000,'SALES BILL',NULL,'');
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('SL00004','SL','2019-01-07','AM000005','0',1200.00000,'SALES BILL',NULL,'');
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('PB000002','PB','2019-01-07','AM000006','1',1800.00000,'Purchase Bill',NULL,'');
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('PB000002','PB','2019-01-07','AM000003','0',1800.00000,'Purchase Bill',NULL,'');
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('SL00005','SL','2019-01-07','AM000006','0',1800.00000,'SALES BILL',NULL,'');
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('PB000003','PB','2019-04-14','AM000006','1',31000.00000,'Purchase Bill',NULL,'');
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('PB000003','PB','2019-04-14','AM000003','0',31000.00000,'Purchase Bill',NULL,'');
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('SL00005','SL','2019-04-20','AM000006','0',3000.00000,'SALES BILL',NULL,'');
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('SL00006','SL','2019-04-20','AM000006','0',4000.00000,'SALES BILL',NULL,'');
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('PB000004','PB','2019-05-17','AM000005','1',1.00000,'Purchase Bill',NULL,NULL);
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('PB000004','PB','2019-05-17','AM000003','0',1.00000,'Purchase Bill',NULL,NULL);
insert  into `oldb2_2`(`doc_ref_no`,`doc_cd`,`doc_date`,`ac_cd`,`drcr`,`val`,`particular`,`fix_time`,`opp_ac_cd`) values ('SL00007','SL','2019-05-17','AM000005','0',12.00000,'SALES BILL',NULL,NULL);

/*Table structure for table `purchase_bill_details` */

DROP TABLE IF EXISTS `purchase_bill_details`;

CREATE TABLE `purchase_bill_details` (
  `id` varchar(8) NOT NULL,
  `sr_no` int(11) NOT NULL,
  `tali_no` varchar(30) default NULL,
  `fk_main_category_id` varchar(8) default NULL,
  `fk_sub_category_id` varchar(8) default NULL,
  `weight` decimal(15,3) default '0.000',
  `rate` decimal(15,3) default '0.000',
  `amount` decimal(15,3) default '0.000',
  PRIMARY KEY  (`id`,`sr_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `purchase_bill_details` */

insert  into `purchase_bill_details`(`id`,`sr_no`,`tali_no`,`fk_main_category_id`,`fk_sub_category_id`,`weight`,`rate`,`amount`) values ('PB000001',1,NULL,'MC000001','SC000001',200.000,20.000,4000.000);
insert  into `purchase_bill_details`(`id`,`sr_no`,`tali_no`,`fk_main_category_id`,`fk_sub_category_id`,`weight`,`rate`,`amount`) values ('PB000002',1,NULL,'MC000001','0',12.000,50.000,600.000);
insert  into `purchase_bill_details`(`id`,`sr_no`,`tali_no`,`fk_main_category_id`,`fk_sub_category_id`,`weight`,`rate`,`amount`) values ('PB000003',1,NULL,'MC000001','SC000001',100.000,10.000,1000.000);
insert  into `purchase_bill_details`(`id`,`sr_no`,`tali_no`,`fk_main_category_id`,`fk_sub_category_id`,`weight`,`rate`,`amount`) values ('PB000003',2,NULL,'MC000001','SC000002',200.000,0.000,0.000);
insert  into `purchase_bill_details`(`id`,`sr_no`,`tali_no`,`fk_main_category_id`,`fk_sub_category_id`,`weight`,`rate`,`amount`) values ('PB000004',1,NULL,'MC000004','SC000008',1.000,1.000,1.000);

/*Table structure for table `purchase_bill_head` */

DROP TABLE IF EXISTS `purchase_bill_head`;

CREATE TABLE `purchase_bill_head` (
  `id` varchar(8) NOT NULL,
  `fk_account_master_id` varchar(8) default NULL,
  `v_date` date default NULL,
  `expense` decimal(10,3) default '0.000',
  `total_expense` decimal(10,3) default '0.000',
  `other_expense` decimal(10,3) default '0.000',
  `net_amt` decimal(10,3) default '0.000',
  `total_weight` decimal(10,3) default '0.000',
  `total_amount` decimal(10,3) default '0.000',
  `description` varchar(255) default NULL,
  `fix_time` varchar(8) default NULL,
  `edit_no` decimal(3,0) default '0',
  `talli_no` varchar(30) default NULL,
  `user_cd` int(11) default NULL,
  `time_stamp` timestamp NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `purchase_bill_head` */

insert  into `purchase_bill_head`(`id`,`fk_account_master_id`,`v_date`,`expense`,`total_expense`,`other_expense`,`net_amt`,`total_weight`,`total_amount`,`description`,`fix_time`,`edit_no`,`talli_no`,`user_cd`,`time_stamp`) values ('PB000001','AM000005','2018-12-24',100.000,20000.000,0.000,24000.000,200.000,4000.000,'','21:47:40',0,'12345',1,'2018-12-24 21:47:40');
insert  into `purchase_bill_head`(`id`,`fk_account_master_id`,`v_date`,`expense`,`total_expense`,`other_expense`,`net_amt`,`total_weight`,`total_amount`,`description`,`fix_time`,`edit_no`,`talli_no`,`user_cd`,`time_stamp`) values ('PB000002','AM000006','2019-01-07',100.000,1200.000,0.000,1800.000,12.000,600.000,'','22:31:12',0,'12',1,'2019-01-07 22:31:12');
insert  into `purchase_bill_head`(`id`,`fk_account_master_id`,`v_date`,`expense`,`total_expense`,`other_expense`,`net_amt`,`total_weight`,`total_amount`,`description`,`fix_time`,`edit_no`,`talli_no`,`user_cd`,`time_stamp`) values ('PB000003','AM000006','2019-04-14',100.000,30000.000,0.000,31000.000,300.000,1000.000,'','17:49:17',0,'T0001',1,'2019-04-14 17:49:17');
insert  into `purchase_bill_head`(`id`,`fk_account_master_id`,`v_date`,`expense`,`total_expense`,`other_expense`,`net_amt`,`total_weight`,`total_amount`,`description`,`fix_time`,`edit_no`,`talli_no`,`user_cd`,`time_stamp`) values ('PB000004','AM000005','2019-05-17',0.000,0.000,0.000,1.000,1.000,1.000,'','23:43:33',0,'1',1,'2019-05-17 23:43:33');

/*Table structure for table `sale_bill_detail` */

DROP TABLE IF EXISTS `sale_bill_detail`;

CREATE TABLE `sale_bill_detail` (
  `ref_no` varchar(7) NOT NULL,
  `sr_no` int(11) NOT NULL,
  `fk_main_category_id` varchar(11) default NULL,
  `fk_sub_category_id` varchar(11) default NULL,
  `fk_slab_category_id` varchar(11) NOT NULL,
  `qty` decimal(15,5) default NULL,
  `rate` decimal(15,5) NOT NULL,
  `amt` decimal(15,5) NOT NULL,
  PRIMARY KEY  (`ref_no`,`sr_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `sale_bill_detail` */

insert  into `sale_bill_detail`(`ref_no`,`sr_no`,`fk_main_category_id`,`fk_sub_category_id`,`fk_slab_category_id`,`qty`,`rate`,`amt`) values ('SL00001',1,NULL,NULL,'SL000001',10.00000,100.00000,1000.00000);
insert  into `sale_bill_detail`(`ref_no`,`sr_no`,`fk_main_category_id`,`fk_sub_category_id`,`fk_slab_category_id`,`qty`,`rate`,`amt`) values ('SL00002',1,NULL,NULL,'SL000003',15.00000,200.00000,3000.00000);
insert  into `sale_bill_detail`(`ref_no`,`sr_no`,`fk_main_category_id`,`fk_sub_category_id`,`fk_slab_category_id`,`qty`,`rate`,`amt`) values ('SL00003',1,NULL,NULL,'SL000001',12.00000,100.00000,1200.00000);
insert  into `sale_bill_detail`(`ref_no`,`sr_no`,`fk_main_category_id`,`fk_sub_category_id`,`fk_slab_category_id`,`qty`,`rate`,`amt`) values ('SL00004',1,NULL,NULL,'SL000001',12.00000,100.00000,1200.00000);
insert  into `sale_bill_detail`(`ref_no`,`sr_no`,`fk_main_category_id`,`fk_sub_category_id`,`fk_slab_category_id`,`qty`,`rate`,`amt`) values ('SL00005',1,'MC000001','SC000001','SL000001',30.00000,100.00000,3000.00000);
insert  into `sale_bill_detail`(`ref_no`,`sr_no`,`fk_main_category_id`,`fk_sub_category_id`,`fk_slab_category_id`,`qty`,`rate`,`amt`) values ('SL00006',1,'MC000001','SC000001','SL000001',40.00000,100.00000,4000.00000);
insert  into `sale_bill_detail`(`ref_no`,`sr_no`,`fk_main_category_id`,`fk_sub_category_id`,`fk_slab_category_id`,`qty`,`rate`,`amt`) values ('SL00007',1,'MC000004','SC000008','SL000009',12.00000,1.00000,12.00000);

/*Table structure for table `sale_bill_head` */

DROP TABLE IF EXISTS `sale_bill_head`;

CREATE TABLE `sale_bill_head` (
  `ref_no` varchar(7) NOT NULL,
  `voucher_date` date default NULL,
  `bill_no` varchar(15) NOT NULL,
  `amount_type` int(3) default NULL,
  `fk_account_id` varchar(10) NOT NULL,
  `total_qty` decimal(15,5) NOT NULL default '0.00000',
  `total_rate` decimal(15,5) NOT NULL default '0.00000',
  `total_amt` decimal(15,5) NOT NULL default '0.00000',
  `disc_per` decimal(10,3) default '0.000',
  `disc_rs` decimal(10,3) default '0.000',
  `amount_total` decimal(10,3) default '0.000',
  `bill_amount` decimal(10,3) default '0.000',
  `adj_amount` decimal(10,3) default '0.000',
  `net_amount` decimal(10,3) default '0.000',
  `remark` varchar(255) character set utf8 collate utf8_unicode_ci default NULL,
  `p_date` date default NULL,
  `chck` int(3) default NULL,
  `user_cd` decimal(3,0) NOT NULL,
  `edit_no` decimal(3,0) NOT NULL default '0',
  `time_stamp` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`ref_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `sale_bill_head` */

insert  into `sale_bill_head`(`ref_no`,`voucher_date`,`bill_no`,`amount_type`,`fk_account_id`,`total_qty`,`total_rate`,`total_amt`,`disc_per`,`disc_rs`,`amount_total`,`bill_amount`,`adj_amount`,`net_amount`,`remark`,`p_date`,`chck`,`user_cd`,`edit_no`,`time_stamp`) values ('SL00001','2018-12-26','1',NULL,'AM000005',10.00000,0.00000,1000.00000,0.000,0.000,1000.000,1000.000,0.000,1000.000,'','2018-12-01',0,1,0,'2018-12-26 08:08:04');
insert  into `sale_bill_head`(`ref_no`,`voucher_date`,`bill_no`,`amount_type`,`fk_account_id`,`total_qty`,`total_rate`,`total_amt`,`disc_per`,`disc_rs`,`amount_total`,`bill_amount`,`adj_amount`,`net_amount`,`remark`,`p_date`,`chck`,`user_cd`,`edit_no`,`time_stamp`) values ('SL00002','2018-12-30','2',0,'AM000005',15.00000,0.00000,3000.00000,0.000,0.000,3000.000,3000.000,0.000,3000.000,'','2018-12-01',0,1,1,'2018-12-30 18:50:46');
insert  into `sale_bill_head`(`ref_no`,`voucher_date`,`bill_no`,`amount_type`,`fk_account_id`,`total_qty`,`total_rate`,`total_amt`,`disc_per`,`disc_rs`,`amount_total`,`bill_amount`,`adj_amount`,`net_amount`,`remark`,`p_date`,`chck`,`user_cd`,`edit_no`,`time_stamp`) values ('SL00003','2019-01-07','3',0,'AM000005',12.00000,0.00000,1200.00000,0.000,0.000,1200.000,1200.000,0.000,1200.000,'',NULL,0,1,0,'2019-01-07 08:34:33');
insert  into `sale_bill_head`(`ref_no`,`voucher_date`,`bill_no`,`amount_type`,`fk_account_id`,`total_qty`,`total_rate`,`total_amt`,`disc_per`,`disc_rs`,`amount_total`,`bill_amount`,`adj_amount`,`net_amount`,`remark`,`p_date`,`chck`,`user_cd`,`edit_no`,`time_stamp`) values ('SL00004','2019-01-07','4',1,'AM000005',12.00000,0.00000,1200.00000,0.000,0.000,1200.000,1200.000,0.000,1200.000,'',NULL,0,1,0,'2019-01-07 08:34:46');
insert  into `sale_bill_head`(`ref_no`,`voucher_date`,`bill_no`,`amount_type`,`fk_account_id`,`total_qty`,`total_rate`,`total_amt`,`disc_per`,`disc_rs`,`amount_total`,`bill_amount`,`adj_amount`,`net_amount`,`remark`,`p_date`,`chck`,`user_cd`,`edit_no`,`time_stamp`) values ('SL00005','2019-04-20','5',0,'AM000006',30.00000,0.00000,3000.00000,0.000,0.000,3000.000,3000.000,0.000,3000.000,'','2019-04-15',0,1,0,'2019-04-20 10:59:55');
insert  into `sale_bill_head`(`ref_no`,`voucher_date`,`bill_no`,`amount_type`,`fk_account_id`,`total_qty`,`total_rate`,`total_amt`,`disc_per`,`disc_rs`,`amount_total`,`bill_amount`,`adj_amount`,`net_amount`,`remark`,`p_date`,`chck`,`user_cd`,`edit_no`,`time_stamp`) values ('SL00006','2019-04-20','6',0,'AM000006',40.00000,0.00000,4000.00000,0.000,0.000,4000.000,4000.000,0.000,4000.000,'','2019-04-16',0,1,1,'2019-04-20 11:01:20');
insert  into `sale_bill_head`(`ref_no`,`voucher_date`,`bill_no`,`amount_type`,`fk_account_id`,`total_qty`,`total_rate`,`total_amt`,`disc_per`,`disc_rs`,`amount_total`,`bill_amount`,`adj_amount`,`net_amount`,`remark`,`p_date`,`chck`,`user_cd`,`edit_no`,`time_stamp`) values ('SL00007','2019-05-17','7',0,'AM000005',12.00000,0.00000,12.00000,0.000,0.000,12.000,12.000,0.000,12.000,'',NULL,0,1,0,'2019-05-17 23:43:58');

/*Table structure for table `slab_category` */

DROP TABLE IF EXISTS `slab_category`;

CREATE TABLE `slab_category` (
  `id` varchar(8) NOT NULL,
  `name` varchar(255) default NULL,
  `short_name` varchar(5) default NULL,
  `rate` decimal(15,3) default NULL,
  `status` tinyint(1) default NULL,
  `fk_sub_category_id` varchar(8) default NULL,
  `edit_no` decimal(3,0) default '0',
  `user_cd` int(11) default NULL,
  `time_stamp` timestamp NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `slab_category` */

insert  into `slab_category`(`id`,`name`,`short_name`,`rate`,`status`,`fk_sub_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SL000001','SL1','sl',100.000,0,'SC000001',0,1,'2018-12-24 21:41:32');
insert  into `slab_category`(`id`,`name`,`short_name`,`rate`,`status`,`fk_sub_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SL000002','SL3','SL',300.000,0,'SC000002',0,1,'2018-12-24 21:43:23');
insert  into `slab_category`(`id`,`name`,`short_name`,`rate`,`status`,`fk_sub_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SL000003','SL2','SL',200.000,0,'SC000001',0,1,'2018-12-24 21:43:48');
insert  into `slab_category`(`id`,`name`,`short_name`,`rate`,`status`,`fk_sub_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SL000004','SL4','SL',400.000,0,'SC000002',0,1,'2018-12-24 21:44:06');
insert  into `slab_category`(`id`,`name`,`short_name`,`rate`,`status`,`fk_sub_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SL000005','SL5','SL',500.000,0,'SC000002',0,1,'2018-12-24 21:44:17');
insert  into `slab_category`(`id`,`name`,`short_name`,`rate`,`status`,`fk_sub_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SL000006','SLAB7','SL7',30.000,0,'SC000006',0,1,'2019-04-14 18:56:08');
insert  into `slab_category`(`id`,`name`,`short_name`,`rate`,`status`,`fk_sub_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SL000007','SLAB7_2','SL7_2',50.000,0,'SC000006',0,1,'2019-04-14 18:57:04');
insert  into `slab_category`(`id`,`name`,`short_name`,`rate`,`status`,`fk_sub_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SL000008','SLAB8','SL8',80.000,0,'SC000007',0,1,'2019-04-14 18:57:23');
insert  into `slab_category`(`id`,`name`,`short_name`,`rate`,`status`,`fk_sub_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SL000009','1.1.1','1',1.000,0,'SC000008',0,1,'2019-05-17 23:40:30');
insert  into `slab_category`(`id`,`name`,`short_name`,`rate`,`status`,`fk_sub_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SL000010','1.1.2','2',2.000,0,'SC000008',1,1,'2019-05-17 23:40:50');
insert  into `slab_category`(`id`,`name`,`short_name`,`rate`,`status`,`fk_sub_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SL000011','1.2.1','1.1',3.000,0,'SC000009',0,1,'2019-05-17 23:41:05');
insert  into `slab_category`(`id`,`name`,`short_name`,`rate`,`status`,`fk_sub_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SL000012','1.2.2','13',4.000,0,'SC000009',0,1,'2019-05-17 23:41:19');

/*Table structure for table `stock0_1` */

DROP TABLE IF EXISTS `stock0_1`;

CREATE TABLE `stock0_1` (
  `stk01_cd` bigint(10) NOT NULL auto_increment,
  `fk_slab_category_id` varchar(10) default NULL,
  `opb` decimal(10,3) default NULL,
  `pur` decimal(10,3) default NULL,
  `sal` decimal(10,3) default NULL,
  `qty` decimal(10,3) default NULL,
  `block` decimal(10,3) default NULL,
  `block_used` decimal(10,3) default NULL,
  `bal` decimal(10,3) default NULL,
  `time_stamp` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`stk01_cd`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;

/*Data for the table `stock0_1` */

insert  into `stock0_1`(`stk01_cd`,`fk_slab_category_id`,`opb`,`pur`,`sal`,`qty`,`block`,`block_used`,`bal`,`time_stamp`) values (3,'SL000002',0.000,0.000,0.000,0.000,0.000,0.000,0.000,'2018-12-24 21:43:29');
insert  into `stock0_1`(`stk01_cd`,`fk_slab_category_id`,`opb`,`pur`,`sal`,`qty`,`block`,`block_used`,`bal`,`time_stamp`) values (5,'SL000004',0.000,0.000,0.000,0.000,0.000,0.000,0.000,'2018-12-24 21:44:06');
insert  into `stock0_1`(`stk01_cd`,`fk_slab_category_id`,`opb`,`pur`,`sal`,`qty`,`block`,`block_used`,`bal`,`time_stamp`) values (6,'SL000005',0.000,0.000,0.000,0.000,0.000,0.000,0.000,'2018-12-24 21:44:17');
insert  into `stock0_1`(`stk01_cd`,`fk_slab_category_id`,`opb`,`pur`,`sal`,`qty`,`block`,`block_used`,`bal`,`time_stamp`) values (7,'SL000001',0.000,28.000,106.000,930.000,250.000,0.000,NULL,'2018-12-24 21:59:33');
insert  into `stock0_1`(`stk01_cd`,`fk_slab_category_id`,`opb`,`pur`,`sal`,`qty`,`block`,`block_used`,`bal`,`time_stamp`) values (8,'SL000003',0.000,39.000,18.000,770.000,150.000,0.000,NULL,'2018-12-24 21:59:47');
insert  into `stock0_1`(`stk01_cd`,`fk_slab_category_id`,`opb`,`pur`,`sal`,`qty`,`block`,`block_used`,`bal`,`time_stamp`) values (16,'SL000006',0.000,0.000,0.000,0.000,0.000,0.000,0.000,'2019-04-14 18:56:08');
insert  into `stock0_1`(`stk01_cd`,`fk_slab_category_id`,`opb`,`pur`,`sal`,`qty`,`block`,`block_used`,`bal`,`time_stamp`) values (17,'SL000007',0.000,0.000,0.000,0.000,0.000,0.000,0.000,'2019-04-14 18:57:04');
insert  into `stock0_1`(`stk01_cd`,`fk_slab_category_id`,`opb`,`pur`,`sal`,`qty`,`block`,`block_used`,`bal`,`time_stamp`) values (18,'SL000008',0.000,0.000,0.000,0.000,0.000,0.000,0.000,'2019-04-14 18:57:23');
insert  into `stock0_1`(`stk01_cd`,`fk_slab_category_id`,`opb`,`pur`,`sal`,`qty`,`block`,`block_used`,`bal`,`time_stamp`) values (19,'SL000009',0.000,0.000,12.000,2.000,0.000,0.000,0.000,'2019-05-17 23:40:30');
insert  into `stock0_1`(`stk01_cd`,`fk_slab_category_id`,`opb`,`pur`,`sal`,`qty`,`block`,`block_used`,`bal`,`time_stamp`) values (20,'SL000010',0.000,0.000,0.000,3.000,0.000,0.000,0.000,'2019-05-17 23:40:45');
insert  into `stock0_1`(`stk01_cd`,`fk_slab_category_id`,`opb`,`pur`,`sal`,`qty`,`block`,`block_used`,`bal`,`time_stamp`) values (21,'SL000011',0.000,0.000,0.000,0.000,0.000,0.000,0.000,'2019-05-17 23:41:05');
insert  into `stock0_1`(`stk01_cd`,`fk_slab_category_id`,`opb`,`pur`,`sal`,`qty`,`block`,`block_used`,`bal`,`time_stamp`) values (22,'SL000012',0.000,0.000,0.000,0.000,0.000,0.000,0.000,'2019-05-17 23:41:19');

/*Table structure for table `stock0_2` */

DROP TABLE IF EXISTS `stock0_2`;

CREATE TABLE `stock0_2` (
  `stock2_id` bigint(10) NOT NULL auto_increment,
  `doc_id` varchar(10) default NULL,
  `fk_slab_category_id` varchar(10) default NULL,
  `trns_id` tinyint(1) default '0' COMMENT '1-OPB, 2-GRADE, 3-PURR, 4-SAL, 5-SALR',
  `opb` decimal(10,2) default NULL,
  `pur` decimal(10,2) default NULL,
  `pur_r` decimal(10,2) default NULL,
  `sal` decimal(10,2) default NULL,
  `sal_r` decimal(10,2) default NULL,
  `qty` decimal(10,2) default NULL,
  `block` decimal(10,2) default NULL,
  `block_used` decimal(10,2) default NULL,
  `time_stamp` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`stock2_id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=latin1;

/*Data for the table `stock0_2` */

insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (6,NULL,'SL000002',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2018-12-24 21:43:32');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (8,NULL,'SL000004',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2018-12-24 21:44:06');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (9,NULL,'SL000005',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2018-12-24 21:44:17');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (29,NULL,'SL000006',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2019-04-14 18:56:08');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (30,NULL,'SL000007',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2019-04-14 18:57:04');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (31,NULL,'SL000008',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2019-04-14 18:57:23');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (42,'BK000007','SL000001',2,NULL,NULL,NULL,NULL,NULL,100.00,0.00,78.00,'2019-04-20 10:59:07');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (43,'BK000007','SL000003',2,NULL,NULL,NULL,NULL,NULL,200.00,0.00,8.00,'2019-04-20 10:59:07');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (44,'SL00005','SL000001',4,NULL,NULL,NULL,30.00,NULL,NULL,NULL,NULL,'2019-04-20 10:59:56');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (46,'SL00006','SL000001',4,NULL,NULL,NULL,40.00,NULL,NULL,NULL,NULL,'2019-04-20 11:01:20');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (47,NULL,'SL000009',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2019-05-17 23:40:30');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (48,NULL,'SL000010',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2019-05-17 23:40:45');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (49,NULL,'SL000011',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2019-05-17 23:41:05');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (50,NULL,'SL000012',0,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,'2019-05-17 23:41:19');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (51,'SL00007','SL000009',4,NULL,NULL,NULL,12.00,NULL,NULL,NULL,NULL,'2019-05-17 23:43:58');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (52,'BK000008','SL000009',2,NULL,NULL,NULL,NULL,NULL,2.00,0.00,0.00,'2019-05-17 23:45:37');
insert  into `stock0_2`(`stock2_id`,`doc_id`,`fk_slab_category_id`,`trns_id`,`opb`,`pur`,`pur_r`,`sal`,`sal_r`,`qty`,`block`,`block_used`,`time_stamp`) values (53,'BK000008','SL000010',2,NULL,NULL,NULL,NULL,NULL,3.00,0.00,0.00,'2019-05-17 23:45:37');

/*Table structure for table `sub_category` */

DROP TABLE IF EXISTS `sub_category`;

CREATE TABLE `sub_category` (
  `id` varchar(8) NOT NULL,
  `name` varchar(255) default NULL,
  `short_name` varchar(5) default NULL,
  `status` tinyint(1) default NULL,
  `fk_main_category_id` varchar(8) NOT NULL,
  `edit_no` decimal(3,0) default '0',
  `user_cd` int(11) default NULL,
  `time_stamp` timestamp NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `sub_category` */

insert  into `sub_category`(`id`,`name`,`short_name`,`status`,`fk_main_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SC000001','S1','',0,'MC000001',0,1,'2018-12-24 21:24:59');
insert  into `sub_category`(`id`,`name`,`short_name`,`status`,`fk_main_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SC000002','S2','',0,'MC000001',0,1,'2018-12-24 21:25:14');
insert  into `sub_category`(`id`,`name`,`short_name`,`status`,`fk_main_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SC000003','S3','',0,'MC000001',0,1,'2018-12-24 21:25:28');
insert  into `sub_category`(`id`,`name`,`short_name`,`status`,`fk_main_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SC000004','S4','',0,'MC000002',0,1,'2018-12-24 21:25:49');
insert  into `sub_category`(`id`,`name`,`short_name`,`status`,`fk_main_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SC000005','s5','',0,'MC000002',0,1,'2018-12-24 21:25:59');
insert  into `sub_category`(`id`,`name`,`short_name`,`status`,`fk_main_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SC000006','S7','S7',0,'MC000003',0,1,'2019-04-14 18:49:44');
insert  into `sub_category`(`id`,`name`,`short_name`,`status`,`fk_main_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SC000007','S8','S8',0,'MC000003',0,1,'2019-04-14 18:49:54');
insert  into `sub_category`(`id`,`name`,`short_name`,`status`,`fk_main_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SC000008','1.1','1',0,'MC000004',0,1,'2019-05-17 23:40:09');
insert  into `sub_category`(`id`,`name`,`short_name`,`status`,`fk_main_category_id`,`edit_no`,`user_cd`,`time_stamp`) values ('SC000009','1.2','2',0,'MC000004',0,1,'2019-05-17 23:40:16');

/*Table structure for table `unt_mst` */

DROP TABLE IF EXISTS `unt_mst`;

CREATE TABLE `unt_mst` (
  `unt_cd` int(10) NOT NULL,
  `unt_name` varchar(50) default NULL,
  `unt_symbol` varchar(30) default NULL,
  `lower_cd` int(2) default '0',
  `upper_cd` int(2) default '0',
  `edit_no` decimal(3,0) NOT NULL default '0',
  `user_cd` decimal(3,0) default NULL,
  `time_stamp` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`unt_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `unt_mst` */

insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (1,'KILO','KG',2,0,1,1,'2016-02-09 19:09:39');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (2,'GRAM','GM',0,1,0,1,'2016-02-09 19:09:05');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (3,'LITER','LT',4,0,0,1,'2016-02-09 19:09:12');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (4,'MILILITER','ML',0,3,0,1,'2016-02-09 19:09:56');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (5,'NOS.','NOS.',0,0,2,1,'2016-03-06 16:55:07');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (6,'NONE','NONE',0,0,0,1,'2016-02-09 19:10:07');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (7,'GLASSE(S)','GL',0,0,0,1,'2016-02-09 19:10:14');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (8,'NO(S)','NO(S)',0,0,0,1,'2016-02-09 19:10:19');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (9,'PKT(S)','PKT(S)',0,0,0,1,'2016-02-09 19:10:26');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (10,'PCS(S)','PCS(S)',0,0,0,1,'2016-02-09 19:10:31');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (11,'PLT(S)','PLT(S)',0,0,0,1,'2016-02-09 19:15:33');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (12,'DOZEN(S)','DOZEN(S)',0,0,0,1,'2016-02-09 19:15:39');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (13,'MTR(S)','MTR(S)',0,0,2,1,'2017-04-04 11:04:58');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (14,'DF','EW',0,0,0,1,'2018-03-31 01:01:37');

/*Table structure for table `user_mst` */

DROP TABLE IF EXISTS `user_mst`;

CREATE TABLE `user_mst` (
  `user_cd` int(10) NOT NULL auto_increment,
  `username` varchar(20) default NULL,
  `password` varchar(20) default NULL,
  `user_photo` blob,
  `isactive` smallint(2) default '1',
  `edit_no` smallint(2) default '0',
  `modifiedby` int(5) default NULL,
  `time_stamp` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`user_cd`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Data for the table `user_mst` */

insert  into `user_mst`(`user_cd`,`username`,`password`,`user_photo`,`isactive`,`edit_no`,`modifiedby`,`time_stamp`) values (1,'admin','',NULL,1,0,NULL,'2016-02-08 18:40:35');

/*Table structure for table `user_rights` */

DROP TABLE IF EXISTS `user_rights`;

CREATE TABLE `user_rights` (
  `user_cd` decimal(5,0) NOT NULL,
  `form_cd` decimal(5,0) NOT NULL,
  `views` decimal(1,0) default '0',
  `edit` decimal(1,0) default '0',
  `adds` decimal(1,0) default '0',
  `deletes` decimal(1,0) default '0',
  `print` decimal(1,0) default '0',
  `navigate_view` decimal(1,0) default '0',
  PRIMARY KEY  (`form_cd`,`user_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `user_rights` */

insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,1,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,2,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,3,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,4,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,5,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,41,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,42,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,43,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,44,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,45,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,46,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,47,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,70,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,71,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,72,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,73,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,74,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,75,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,76,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,111,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,112,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,113,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,114,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,115,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,116,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,117,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,118,1,1,1,1,1,1);
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values (1,119,1,1,1,1,1,1);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
