/*
SQLyog Community v9.63 
MySQL - 5.0.67-community-nt : Database - dhananiforzal
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`dhananiforzal` /*!40100 DEFAULT CHARACTER SET latin1 */;

/*Table structure for table `cmpny_mst` */

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
  `cash_ac_cd` varchar(7) default '',
  `lab_inc_ac` varchar(7) default '',
  `lab_exp_ac` varchar(7) default '',
  `sale_ac` varchar(7) default '',
  `purchase_ac` varchar(7) default '',
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

insert  into `cmpny_mst`(`cmpn_cd`,`cmpn_code`,`branch_code`,`cmpn_name`,`ac_year`,`mnth`,`sh_name`,`digit`,`invoice_type`,`image_path`,`add1`,`add2`,`area_cd`,`city_cd`,`pincode`,`mob_no`,`phone_no`,`licence_no`,`email`,`fax_no`,`pan_no`,`tin_no`,`cst_no`,`tax_no`,`bank_name`,`ac_no`,`branch_name`,`cash_ac_cd`,`lab_inc_ac`,`lab_exp_ac`,`sale_ac`,`purchase_ac`,`mypwd`,`contact_person`,`website`,`edit_no`,`user_cd`,`time_stamp`,`sls_chr_lbl`,`delete_pwd`,`ifsc_code`,`bill_supply_type`,`bill_supply`,`bill_supply_desc`,`is_retail`,`multiple_company_data`,`corraddress1`,`corraddress2`) values ('C000001','01','001','SPECIFIC ELECTRONIC1','2017','01','SE','2','0','','B/51, Electronics Zone, G.I.D.C.,','Sector-25, Gandhinagar - 382025. dsdf sfdfsdf sdfsdf','','','','990-404-5566','7383149333','','sales@specificelectronics.com','','ACNFS5720N','24060305663ABC','24560305663123','','PUNJAB NATIONAL BANK','30425911901','BAPUNAGAR BR.','A000001','0','0','0','0','','Harsh Patel','http://www.specificelectronics.com','0','1','2016-01-10 22:04:45','Loading Charge','123','SBI29051693',0,'BILL OF SUPPLY','Composition Taxable Person, Not Eligible to Collect Tax on Supplies',0,0,'B/51, Electronics Zone, G.I.D.C., rere','Sector-25, Gandhinagar - 382025. dsdf sfdfsdf sdfsdf');

/*Table structure for table `form_mst` */

CREATE TABLE `form_mst` (
  `form_cd` decimal(5,0) NOT NULL,
  `form_name` varchar(255) default NULL,
  `menu_cd` decimal(5,0) default NULL,
  PRIMARY KEY  (`form_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `form_mst` */

insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('1','TAX MASTER','1');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('2','GROUP MASTER','1');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('3','ACCOUNT MASTER','1');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('4','UNIT MASTER','1');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('5','MAIN ITEM MASTER','1');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('6','ITEM MASTER','1');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('7','BANK MASTER','1');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('8','COUNTRY MASTER','1');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('9','STATE MASTER','1');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('10','CITY MASTER','1');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('11','AREA MASTER','1');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('12','NOTES IN SALES','1');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('21','CASH PAYMENT','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('22','CASH RECEIPT','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('23','BANK PAYMENT','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('24','BANK RECEIPT','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('25','JOURNAL VOUCHER','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('26','CONTRA VOUCHER','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('27','TAX INVOICE','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('28','CREDIT NOTES','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('29','PURCHASE BILL','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('30','DEBIT NOTES','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('31','SALES REPLACEMENT ISSUE','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('32','SALES REPLACEMENT RETURN','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('33','RETAIL INVOICE','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('34','LABOUR INVOICE','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('35','PURCHASE ORDER','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('36','CHALLAN','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('37','IN STOCK','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('38','PURCHASE LABOUR INVOICE','2');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('41','COLLECTION REPORT','3');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('42','GROUP SUMMARY','3');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('43','GENERAL LEDGER','3');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('44','GENERAL LEDGER SUMMARY','3');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('45','STOCK LEDGER','3');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('46','STOCK SUMMARY','3');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('47','DAILY ACTIVITY REPORT','3');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('48','LAST DATE PAYMENT','3');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('49','SALES MARGIN','3');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('50','KARIGAR LEDGER','3');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('51','PROFIT AND LOSS','3');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('52','TOTAL SUMMARY','3');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('53','MONTHLY LEDGER','3');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('54','SALES REPLACEMENT LEDGER','3');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('61','TAX INVOICE REPORT','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('62','RETAIL INVOICE REPORT','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('63','LABOUR INVOICE REPORT','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('64','PURCHASE ORDER REPORT','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('65','CHALLAN REPORT','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('66','CREDIT NOTES REPORT','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('67','PURCHASE BILL REPORT','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('68','PURCHASE LABOUR INVOICE REPORT','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('69','DEBIT NOTES REPORT','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('70','SALES REPLACEMENT ISSUE REPORT','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('71','SALES REPLACEMENT RETURN REPORT','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('72','IN-STOCK REPORT','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('73','GST REPORT','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('74','CHECK PRINT REPORT','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('75','CASH BOOK','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('76','BANK BOOK','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('77','ACCOUNT CONFIRMATION REPORT','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('78','TRANSACTION LIST','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('79','ACCOUNT LIST','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('80','BANK RECONSILATION','4');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('101','SALES CHART','5');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('102','STOCK CHART','5');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('103','ACCOUNT CHART','5');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('111','COMPANY SETTING','6');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('112','MANAGE USER','6');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('113','USER RIGHTS','6');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('114','MANAGE EMAIL','6');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('115','CHANGE PASSWORD','6');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('116','QUICK OPEN','6');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('117','BACK UP','6');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('118','RESET','6');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('119','EMAIL','6');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('120','EXPORT DATA','6');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('121','CHECK PRINT','6');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('122','NEW YEAR','6');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('123','STICKER PRINT','6');
insert  into `form_mst`(`form_cd`,`form_name`,`menu_cd`) values ('124','CHANGE THEMES','6');

/*Table structure for table `manage_email` */

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

insert  into `manage_email`(`manage_id`,`manage_email`,`manage_pwd`,`manage_port`,`manage_host`,`manage_mobno`) values (1,'dhameliya.jaydeep@yahoo.com','jdhiral_009','587','smtp.mail.yahoo.com','7405116442');

/*Table structure for table `menu_mst` */

CREATE TABLE `menu_mst` (
  `menu_cd` decimal(5,0) NOT NULL,
  `menu_name` varchar(255) NOT NULL,
  PRIMARY KEY  (`menu_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `menu_mst` */

insert  into `menu_mst`(`menu_cd`,`menu_name`) values ('1','MASTER');
insert  into `menu_mst`(`menu_cd`,`menu_name`) values ('2','TRANSACTION');
insert  into `menu_mst`(`menu_cd`,`menu_name`) values ('3','ACCOUNTS');
insert  into `menu_mst`(`menu_cd`,`menu_name`) values ('4','REPORTS');
insert  into `menu_mst`(`menu_cd`,`menu_name`) values ('5','CHART');
insert  into `menu_mst`(`menu_cd`,`menu_name`) values ('6','UTILITY');

/*Table structure for table `unt_mst` */

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

insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (1,'KILO','KG',2,0,'1','1','2016-02-09 19:09:39');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (2,'GRAM','GM',0,1,'0','1','2016-02-09 19:09:05');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (3,'LITER','LT',4,0,'0','1','2016-02-09 19:09:12');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (4,'MILILITER','ML',0,3,'0','1','2016-02-09 19:09:56');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (5,'NOS.','NOS.',0,0,'2','1','2016-03-06 16:55:07');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (6,'NONE','NONE',0,0,'0','1','2016-02-09 19:10:07');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (7,'GLASSE(S)','GL',0,0,'0','1','2016-02-09 19:10:14');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (8,'NO(S)','NO(S)',0,0,'0','1','2016-02-09 19:10:19');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (9,'PKT(S)','PKT(S)',0,0,'0','1','2016-02-09 19:10:26');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (10,'PCS(S)','PCS(S)',0,0,'0','1','2016-02-09 19:10:31');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (11,'PLT(S)','PLT(S)',0,0,'0','1','2016-02-09 19:15:33');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (12,'DOZEN(S)','DOZEN(S)',0,0,'0','1','2016-02-09 19:15:39');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (13,'MTR(S)','MTR(S)',0,0,'2','1','2017-04-04 11:04:58');
insert  into `unt_mst`(`unt_cd`,`unt_name`,`unt_symbol`,`lower_cd`,`upper_cd`,`edit_no`,`user_cd`,`time_stamp`) values (14,'DF','EW',0,0,'0','1','2018-03-31 01:01:37');

/*Table structure for table `user_mst` */

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

insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','1','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','2','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','3','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','4','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','5','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','6','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','7','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','8','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','9','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','10','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','11','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','12','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','21','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','22','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','23','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','24','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','25','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','26','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','27','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','28','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','29','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','30','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','31','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','32','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','33','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','34','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','35','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','36','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','37','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','38','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','41','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','42','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','43','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','44','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','45','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','46','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','47','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','48','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','49','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','50','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','51','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','52','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','53','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','61','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','62','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','63','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','64','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','65','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','66','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','67','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','68','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','69','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','70','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','71','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','72','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','73','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','74','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','75','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','76','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','77','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','78','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','79','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','80','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','81','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','82','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','101','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','102','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','103','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','111','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','112','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','113','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','114','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','115','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','116','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','117','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','118','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','119','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','120','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','121','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','122','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','123','1','1','1','1','1','1');
insert  into `user_rights`(`user_cd`,`form_cd`,`views`,`edit`,`adds`,`deletes`,`print`,`navigate_view`) values ('1','124','1','1','1','1','1','1');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
