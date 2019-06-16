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
  `edit_no` decimal(3,0) default '0',
  `user_cd` int(11) default NULL,
  `time_stamp` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `lock_date` date default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `account_master` */

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
  `opp_ac_cd` varchar(8) default NULL,
  `amount_type` int(11) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `oldb2_2` */

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
  `slab` int(11) default '0',
  PRIMARY KEY  (`ref_no`,`sr_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `sale_bill_detail` */

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `stock0_1` */

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `stock0_2` */

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
