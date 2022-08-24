/*
SQLyog Community v13.1.6 (64 bit)
MySQL - 8.0.19 : Database - mall_admin_manage
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`mall_admin_manage` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `mall_admin_manage`;

/*Table structure for table `QRTZ_BLOB_TRIGGERS` */

DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;

CREATE TABLE `QRTZ_BLOB_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `QRTZ_BLOB_TRIGGERS` */

/*Table structure for table `QRTZ_CALENDARS` */

DROP TABLE IF EXISTS `QRTZ_CALENDARS`;

CREATE TABLE `QRTZ_CALENDARS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `CALENDAR_NAME` varchar(200) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `QRTZ_CALENDARS` */

/*Table structure for table `QRTZ_CRON_TRIGGERS` */

DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;

CREATE TABLE `QRTZ_CRON_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `CRON_EXPRESSION` varchar(120) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `QRTZ_CRON_TRIGGERS` */

insert  into `QRTZ_CRON_TRIGGERS`(`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`CRON_EXPRESSION`,`TIME_ZONE_ID`) values 
('RenrenScheduler','TASK_1','DEFAULT','0 0/30 * * * ?','Asia/Shanghai');

/*Table structure for table `QRTZ_FIRED_TRIGGERS` */

DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;

CREATE TABLE `QRTZ_FIRED_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `FIRED_TIME` bigint NOT NULL,
  `SCHED_TIME` bigint NOT NULL,
  `PRIORITY` int NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(200) DEFAULT NULL,
  `JOB_GROUP` varchar(200) DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`),
  KEY `IDX_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
  KEY `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `QRTZ_FIRED_TRIGGERS` */

/*Table structure for table `QRTZ_JOB_DETAILS` */

DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;

CREATE TABLE `QRTZ_JOB_DETAILS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_NONCONCURRENT` varchar(1) NOT NULL,
  `IS_UPDATE_DATA` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_J_GRP` (`SCHED_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `QRTZ_JOB_DETAILS` */

insert  into `QRTZ_JOB_DETAILS`(`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`,`DESCRIPTION`,`JOB_CLASS_NAME`,`IS_DURABLE`,`IS_NONCONCURRENT`,`IS_UPDATE_DATA`,`REQUESTS_RECOVERY`,`JOB_DATA`) values 
('RenrenScheduler','TASK_1','DEFAULT',NULL,'io.renren.modules.job.utils.ScheduleJob','0','0','0','0','��\0sr\0org.quartz.JobDataMap���迩��\0\0xr\0&org.quartz.utils.StringKeyDirtyFlagMap�����](\0Z\0allowsTransientDataxr\0org.quartz.utils.DirtyFlagMap�.�(v\n�\0Z\0dirtyL\0mapt\0Ljava/util/Map;xpsr\0java.util.HashMap���`�\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0\rJOB_PARAM_KEYsr\0.io.renren.modules.job.entity.ScheduleJobEntity\0\0\0\0\0\0\0\0L\0beanNamet\0Ljava/lang/String;L\0\ncreateTimet\0Ljava/util/Date;L\0cronExpressionq\0~\0	L\0jobIdt\0Ljava/lang/Long;L\0paramsq\0~\0	L\0remarkq\0~\0	L\0statust\0Ljava/lang/Integer;xpt\0testTasksr\0java.util.Datehj�KYt\0\0xpw\0\0~F��xt\00 0/30 * * * ?sr\0java.lang.Long;��̏#�\0J\0valuexr\0java.lang.Number������\0\0xp\0\0\0\0\0\0\0t\0renrent\0参数测试sr\0java.lang.Integer⠤���8\0I\0valuexq\0~\0\0\0\0\0x\0');

/*Table structure for table `QRTZ_LOCKS` */

DROP TABLE IF EXISTS `QRTZ_LOCKS`;

CREATE TABLE `QRTZ_LOCKS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `QRTZ_LOCKS` */

insert  into `QRTZ_LOCKS`(`SCHED_NAME`,`LOCK_NAME`) values 
('RenrenScheduler','STATE_ACCESS'),
('RenrenScheduler','TRIGGER_ACCESS');

/*Table structure for table `QRTZ_PAUSED_TRIGGER_GRPS` */

DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;

CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `QRTZ_PAUSED_TRIGGER_GRPS` */

/*Table structure for table `QRTZ_SCHEDULER_STATE` */

DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;

CREATE TABLE `QRTZ_SCHEDULER_STATE` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `LAST_CHECKIN_TIME` bigint NOT NULL,
  `CHECKIN_INTERVAL` bigint NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `QRTZ_SCHEDULER_STATE` */

insert  into `QRTZ_SCHEDULER_STATE`(`SCHED_NAME`,`INSTANCE_NAME`,`LAST_CHECKIN_TIME`,`CHECKIN_INTERVAL`) values 
('RenrenScheduler','DESKTOP-N8VDK7C1656926480693',1656926576827,15000);

/*Table structure for table `QRTZ_SIMPLE_TRIGGERS` */

DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;

CREATE TABLE `QRTZ_SIMPLE_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `REPEAT_COUNT` bigint NOT NULL,
  `REPEAT_INTERVAL` bigint NOT NULL,
  `TIMES_TRIGGERED` bigint NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `QRTZ_SIMPLE_TRIGGERS` */

/*Table structure for table `QRTZ_SIMPROP_TRIGGERS` */

DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;

CREATE TABLE `QRTZ_SIMPROP_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `STR_PROP_1` varchar(512) DEFAULT NULL,
  `STR_PROP_2` varchar(512) DEFAULT NULL,
  `STR_PROP_3` varchar(512) DEFAULT NULL,
  `INT_PROP_1` int DEFAULT NULL,
  `INT_PROP_2` int DEFAULT NULL,
  `LONG_PROP_1` bigint DEFAULT NULL,
  `LONG_PROP_2` bigint DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_SIMPROP_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `QRTZ_SIMPROP_TRIGGERS` */

/*Table structure for table `QRTZ_TRIGGERS` */

DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;

CREATE TABLE `QRTZ_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint DEFAULT NULL,
  `PREV_FIRE_TIME` bigint DEFAULT NULL,
  `PRIORITY` int DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint NOT NULL,
  `END_TIME` bigint DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) DEFAULT NULL,
  `MISFIRE_INSTR` smallint DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
  KEY `IDX_QRTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `QRTZ_JOB_DETAILS` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `QRTZ_TRIGGERS` */

insert  into `QRTZ_TRIGGERS`(`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`JOB_NAME`,`JOB_GROUP`,`DESCRIPTION`,`NEXT_FIRE_TIME`,`PREV_FIRE_TIME`,`PRIORITY`,`TRIGGER_STATE`,`TRIGGER_TYPE`,`START_TIME`,`END_TIME`,`CALENDAR_NAME`,`MISFIRE_INSTR`,`JOB_DATA`) values 
('RenrenScheduler','TASK_1','DEFAULT','TASK_1','DEFAULT',NULL,1656927000000,-1,5,'WAITING','CRON',1641901715000,0,NULL,2,'��\0sr\0org.quartz.JobDataMap���迩��\0\0xr\0&org.quartz.utils.StringKeyDirtyFlagMap�����](\0Z\0allowsTransientDataxr\0org.quartz.utils.DirtyFlagMap�.�(v\n�\0Z\0dirtyL\0mapt\0Ljava/util/Map;xpsr\0java.util.HashMap���`�\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0\rJOB_PARAM_KEYsr\0.io.renren.modules.job.entity.ScheduleJobEntity\0\0\0\0\0\0\0\0L\0beanNamet\0Ljava/lang/String;L\0\ncreateTimet\0Ljava/util/Date;L\0cronExpressionq\0~\0	L\0jobIdt\0Ljava/lang/Long;L\0paramsq\0~\0	L\0remarkq\0~\0	L\0statust\0Ljava/lang/Integer;xpt\0testTasksr\0java.util.Datehj�KYt\0\0xpw\0\0~F��xt\00 0/30 * * * ?sr\0java.lang.Long;��̏#�\0J\0valuexr\0java.lang.Number������\0\0xp\0\0\0\0\0\0\0t\0renrent\0参数测试sr\0java.lang.Integer⠤���8\0I\0valuexq\0~\0\0\0\0\0x\0');

/*Table structure for table `schedule_job` */

DROP TABLE IF EXISTS `schedule_job`;

CREATE TABLE `schedule_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  `params` varchar(2000) DEFAULT NULL COMMENT '参数',
  `cron_expression` varchar(100) DEFAULT NULL COMMENT 'cron表达式',
  `status` tinyint DEFAULT NULL COMMENT '任务状态  0：正常  1：暂停',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务';

/*Data for the table `schedule_job` */

insert  into `schedule_job`(`job_id`,`bean_name`,`params`,`cron_expression`,`status`,`remark`,`create_time`) values 
(1,'testTask','renren','0 0/30 * * * ?',0,'参数测试','2022-01-11 09:32:37');

/*Table structure for table `schedule_job_log` */

DROP TABLE IF EXISTS `schedule_job_log`;

CREATE TABLE `schedule_job_log` (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志id',
  `job_id` bigint NOT NULL COMMENT '任务id',
  `bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  `params` varchar(2000) DEFAULT NULL COMMENT '参数',
  `status` tinyint NOT NULL COMMENT '任务状态    0：成功    1：失败',
  `error` varchar(2000) DEFAULT NULL COMMENT '失败信息',
  `times` int NOT NULL COMMENT '耗时(单位：毫秒)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`log_id`),
  KEY `job_id` (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=544 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务日志';

/*Data for the table `schedule_job_log` */

insert  into `schedule_job_log`(`log_id`,`job_id`,`bean_name`,`params`,`status`,`error`,`times`,`create_time`) values 
(1,1,'testTask','renren',0,NULL,0,'2022-01-15 01:00:00'),
(2,1,'testTask','renren',0,NULL,1,'2022-01-15 14:30:00'),
(3,1,'testTask','renren',0,NULL,1,'2022-01-15 15:00:00'),
(4,1,'testTask','renren',0,NULL,1,'2022-01-15 15:30:00'),
(5,1,'testTask','renren',0,NULL,1,'2022-01-15 16:00:00'),
(6,1,'testTask','renren',0,NULL,1,'2022-01-15 16:30:00'),
(7,1,'testTask','renren',0,NULL,1,'2022-01-15 17:00:00'),
(8,1,'testTask','renren',0,NULL,0,'2022-01-15 17:30:00'),
(9,1,'testTask','renren',0,NULL,2,'2022-01-15 18:00:00'),
(10,1,'testTask','renren',0,NULL,1,'2022-01-15 18:30:00'),
(11,1,'testTask','renren',0,NULL,0,'2022-01-15 19:00:00'),
(12,1,'testTask','renren',0,NULL,1,'2022-01-15 19:30:00'),
(13,1,'testTask','renren',0,NULL,0,'2022-01-15 20:00:00'),
(14,1,'testTask','renren',0,NULL,1,'2022-01-15 20:30:00'),
(15,1,'testTask','renren',0,NULL,2,'2022-01-15 21:00:00'),
(16,1,'testTask','renren',0,NULL,0,'2022-01-15 21:30:00'),
(17,1,'testTask','renren',0,NULL,1,'2022-01-15 23:00:00'),
(18,1,'testTask','renren',0,NULL,0,'2022-01-20 20:30:00'),
(19,1,'testTask','renren',0,NULL,0,'2022-01-20 21:00:00'),
(20,1,'testTask','renren',0,NULL,0,'2022-01-20 21:30:00'),
(21,1,'testTask','renren',0,NULL,1,'2022-01-21 18:00:00'),
(22,1,'testTask','renren',0,NULL,1,'2022-01-21 18:30:00'),
(23,1,'testTask','renren',0,NULL,1,'2022-01-21 19:00:00'),
(24,1,'testTask','renren',0,NULL,1,'2022-01-21 19:30:00'),
(25,1,'testTask','renren',0,NULL,1,'2022-01-21 20:00:00'),
(26,1,'testTask','renren',0,NULL,1,'2022-01-21 20:30:00'),
(27,1,'testTask','renren',0,NULL,1,'2022-01-21 21:00:00'),
(28,1,'testTask','renren',0,NULL,1,'2022-01-21 21:30:00'),
(29,1,'testTask','renren',0,NULL,0,'2022-01-21 22:00:00'),
(30,1,'testTask','renren',0,NULL,0,'2022-01-25 19:30:00'),
(31,1,'testTask','renren',0,NULL,1,'2022-01-25 20:00:00'),
(32,1,'testTask','renren',0,NULL,1,'2022-01-25 20:30:00'),
(33,1,'testTask','renren',0,NULL,2,'2022-01-25 21:00:00'),
(34,1,'testTask','renren',0,NULL,0,'2022-01-26 14:30:00'),
(35,1,'testTask','renren',0,NULL,1,'2022-01-26 15:00:00'),
(36,1,'testTask','renren',0,NULL,0,'2022-01-26 15:30:00'),
(37,1,'testTask','renren',0,NULL,1,'2022-01-26 16:00:00'),
(38,1,'testTask','renren',0,NULL,1,'2022-01-26 16:30:00'),
(39,1,'testTask','renren',0,NULL,0,'2022-01-26 17:00:00'),
(40,1,'testTask','renren',0,NULL,1,'2022-01-26 17:30:00'),
(41,1,'testTask','renren',0,NULL,2,'2022-01-26 18:00:00'),
(42,1,'testTask','renren',0,NULL,1,'2022-01-26 19:30:00'),
(43,1,'testTask','renren',0,NULL,2,'2022-01-26 20:00:00'),
(44,1,'testTask','renren',0,NULL,1,'2022-01-26 20:30:00'),
(45,1,'testTask','renren',0,NULL,2,'2022-01-26 21:00:00'),
(46,1,'testTask','renren',0,NULL,1,'2022-01-27 00:30:00'),
(47,1,'testTask','renren',0,NULL,0,'2022-01-27 01:00:00'),
(48,1,'testTask','renren',0,NULL,0,'2022-01-27 01:30:00'),
(49,1,'testTask','renren',0,NULL,0,'2022-01-27 02:00:00'),
(50,1,'testTask','renren',0,NULL,0,'2022-01-27 02:30:00'),
(51,1,'testTask','renren',0,NULL,1,'2022-01-27 11:30:00'),
(52,1,'testTask','renren',0,NULL,0,'2022-01-27 12:00:00'),
(53,1,'testTask','renren',0,NULL,1,'2022-01-27 12:30:00'),
(54,1,'testTask','renren',0,NULL,1,'2022-01-27 13:00:00'),
(55,1,'testTask','renren',0,NULL,1,'2022-01-27 13:30:00'),
(56,1,'testTask','renren',0,NULL,1,'2022-01-27 14:00:00'),
(57,1,'testTask','renren',0,NULL,1,'2022-01-27 14:30:00'),
(58,1,'testTask','renren',0,NULL,1,'2022-01-27 15:00:00'),
(59,1,'testTask','renren',0,NULL,1,'2022-01-27 15:30:00'),
(60,1,'testTask','renren',0,NULL,1,'2022-01-27 16:00:00'),
(61,1,'testTask','renren',0,NULL,1,'2022-01-27 16:30:00'),
(62,1,'testTask','renren',0,NULL,1,'2022-01-27 17:00:00'),
(63,1,'testTask','renren',0,NULL,1,'2022-01-27 17:30:00'),
(64,1,'testTask','renren',0,NULL,1,'2022-01-27 18:00:00'),
(65,1,'testTask','renren',0,NULL,1,'2022-01-27 18:30:00'),
(66,1,'testTask','renren',0,NULL,1,'2022-01-27 19:00:00'),
(67,1,'testTask','renren',0,NULL,0,'2022-01-27 19:30:00'),
(68,1,'testTask','renren',0,NULL,0,'2022-01-27 20:00:00'),
(69,1,'testTask','renren',0,NULL,0,'2022-01-27 20:30:00'),
(70,1,'testTask','renren',0,NULL,1,'2022-01-27 21:00:00'),
(71,1,'testTask','renren',0,NULL,1,'2022-01-27 21:30:00'),
(72,1,'testTask','renren',0,NULL,1,'2022-01-27 22:00:00'),
(73,1,'testTask','renren',0,NULL,0,'2022-01-27 22:30:00'),
(74,1,'testTask','renren',0,NULL,1,'2022-01-27 23:00:00'),
(75,1,'testTask','renren',0,NULL,1,'2022-01-27 23:30:00'),
(76,1,'testTask','renren',0,NULL,19,'2022-01-28 00:00:00'),
(77,1,'testTask','renren',0,NULL,4,'2022-01-28 00:30:00'),
(78,1,'testTask','renren',0,NULL,1,'2022-01-28 01:00:00'),
(79,1,'testTask','renren',0,NULL,1,'2022-01-28 01:30:00'),
(80,1,'testTask','renren',0,NULL,1,'2022-01-28 02:00:00'),
(81,1,'testTask','renren',0,NULL,1,'2022-01-28 11:30:00'),
(82,1,'testTask','renren',0,NULL,1,'2022-01-28 12:00:00'),
(83,1,'testTask','renren',0,NULL,1,'2022-01-28 12:30:00'),
(84,1,'testTask','renren',0,NULL,1,'2022-01-28 13:00:00'),
(85,1,'testTask','renren',0,NULL,1,'2022-01-28 13:30:00'),
(86,1,'testTask','renren',0,NULL,1,'2022-01-28 14:00:00'),
(87,1,'testTask','renren',0,NULL,1,'2022-01-28 14:30:00'),
(88,1,'testTask','renren',0,NULL,0,'2022-01-28 15:00:00'),
(89,1,'testTask','renren',0,NULL,0,'2022-01-28 15:30:00'),
(90,1,'testTask','renren',0,NULL,0,'2022-01-28 16:00:00'),
(91,1,'testTask','renren',0,NULL,1,'2022-01-28 16:30:00'),
(92,1,'testTask','renren',0,NULL,1,'2022-01-28 17:00:00'),
(93,1,'testTask','renren',0,NULL,1,'2022-01-28 17:30:00'),
(94,1,'testTask','renren',0,NULL,1,'2022-01-28 18:00:00'),
(95,1,'testTask','renren',0,NULL,1,'2022-01-28 18:30:00'),
(96,1,'testTask','renren',0,NULL,1,'2022-01-28 19:00:00'),
(97,1,'testTask','renren',0,NULL,0,'2022-01-28 19:30:00'),
(98,1,'testTask','renren',0,NULL,3,'2022-01-28 20:00:00'),
(99,1,'testTask','renren',0,NULL,1,'2022-01-28 20:30:00'),
(100,1,'testTask','renren',0,NULL,0,'2022-01-28 21:00:00'),
(101,1,'testTask','renren',0,NULL,1,'2022-01-28 21:30:00'),
(102,1,'testTask','renren',0,NULL,1,'2022-01-28 22:00:00'),
(103,1,'testTask','renren',0,NULL,1,'2022-01-28 22:30:00'),
(104,1,'testTask','renren',0,NULL,1,'2022-01-28 23:00:00'),
(105,1,'testTask','renren',0,NULL,0,'2022-01-28 23:30:00'),
(106,1,'testTask','renren',0,NULL,6,'2022-01-29 00:00:00'),
(107,1,'testTask','renren',0,NULL,2,'2022-01-29 00:30:00'),
(108,1,'testTask','renren',0,NULL,0,'2022-01-29 01:00:00'),
(109,1,'testTask','renren',0,NULL,1,'2022-01-29 01:30:00'),
(110,1,'testTask','renren',0,NULL,0,'2022-01-29 02:00:00'),
(111,1,'testTask','renren',0,NULL,0,'2022-01-29 11:30:00'),
(112,1,'testTask','renren',0,NULL,1,'2022-01-29 12:00:00'),
(113,1,'testTask','renren',0,NULL,1,'2022-01-29 12:30:00'),
(114,1,'testTask','renren',0,NULL,0,'2022-01-29 13:00:00'),
(115,1,'testTask','renren',0,NULL,2,'2022-01-29 13:30:00'),
(116,1,'testTask','renren',0,NULL,1,'2022-01-29 14:00:00'),
(117,1,'testTask','renren',0,NULL,1,'2022-01-29 14:30:00'),
(118,1,'testTask','renren',0,NULL,1,'2022-01-29 15:00:00'),
(119,1,'testTask','renren',0,NULL,1,'2022-01-29 15:30:00'),
(120,1,'testTask','renren',0,NULL,1,'2022-01-29 16:00:00'),
(121,1,'testTask','renren',0,NULL,2,'2022-01-29 16:30:00'),
(122,1,'testTask','renren',0,NULL,1,'2022-01-29 17:00:00'),
(123,1,'testTask','renren',0,NULL,1,'2022-01-29 17:30:00'),
(124,1,'testTask','renren',0,NULL,1,'2022-01-29 18:00:00'),
(125,1,'testTask','renren',0,NULL,1,'2022-01-29 18:30:00'),
(126,1,'testTask','renren',0,NULL,0,'2022-01-29 19:00:00'),
(127,1,'testTask','renren',0,NULL,0,'2022-01-29 19:30:00'),
(128,1,'testTask','renren',0,NULL,1,'2022-01-29 20:00:00'),
(129,1,'testTask','renren',0,NULL,1,'2022-01-29 20:30:00'),
(130,1,'testTask','renren',0,NULL,3,'2022-01-30 01:00:00'),
(131,1,'testTask','renren',0,NULL,1,'2022-01-30 01:30:00'),
(132,1,'testTask','renren',0,NULL,0,'2022-01-30 11:00:00'),
(133,1,'testTask','renren',0,NULL,0,'2022-01-30 11:30:00'),
(134,1,'testTask','renren',0,NULL,1,'2022-01-30 12:00:00'),
(135,1,'testTask','renren',0,NULL,1,'2022-01-30 12:30:00'),
(136,1,'testTask','renren',0,NULL,1,'2022-01-30 13:00:00'),
(137,1,'testTask','renren',0,NULL,1,'2022-01-30 13:30:00'),
(138,1,'testTask','renren',0,NULL,1,'2022-01-30 14:00:00'),
(139,1,'testTask','renren',0,NULL,0,'2022-01-30 14:30:00'),
(140,1,'testTask','renren',0,NULL,1,'2022-01-30 15:00:00'),
(141,1,'testTask','renren',0,NULL,0,'2022-01-30 15:30:00'),
(142,1,'testTask','renren',0,NULL,1,'2022-01-30 16:00:00'),
(143,1,'testTask','renren',0,NULL,0,'2022-01-30 16:30:00'),
(144,1,'testTask','renren',0,NULL,1,'2022-01-30 17:00:00'),
(145,1,'testTask','renren',0,NULL,2,'2022-01-30 17:30:00'),
(146,1,'testTask','renren',0,NULL,1,'2022-01-30 18:00:00'),
(147,1,'testTask','renren',0,NULL,1,'2022-01-30 18:30:00'),
(148,1,'testTask','renren',0,NULL,0,'2022-01-30 19:00:00'),
(149,1,'testTask','renren',0,NULL,1,'2022-01-30 19:30:00'),
(150,1,'testTask','renren',0,NULL,0,'2022-01-30 20:00:00'),
(151,1,'testTask','renren',0,NULL,0,'2022-01-30 20:30:00'),
(152,1,'testTask','renren',0,NULL,2,'2022-01-30 23:30:00'),
(153,1,'testTask','renren',0,NULL,13,'2022-01-31 00:00:00'),
(154,1,'testTask','renren',0,NULL,4,'2022-01-31 00:30:00'),
(155,1,'testTask','renren',0,NULL,1,'2022-01-31 01:00:00'),
(156,1,'testTask','renren',0,NULL,0,'2022-01-31 01:30:00'),
(157,1,'testTask','renren',0,NULL,1,'2022-01-31 10:00:00'),
(158,1,'testTask','renren',0,NULL,1,'2022-01-31 10:30:00'),
(159,1,'testTask','renren',0,NULL,1,'2022-01-31 11:00:00'),
(160,1,'testTask','renren',0,NULL,1,'2022-01-31 11:30:00'),
(161,1,'testTask','renren',0,NULL,0,'2022-01-31 12:00:00'),
(162,1,'testTask','renren',0,NULL,1,'2022-01-31 12:30:00'),
(163,1,'testTask','renren',0,NULL,0,'2022-01-31 13:00:00'),
(164,1,'testTask','renren',0,NULL,0,'2022-01-31 13:30:00'),
(165,1,'testTask','renren',0,NULL,0,'2022-01-31 14:00:00'),
(166,1,'testTask','renren',0,NULL,1,'2022-01-31 14:30:00'),
(167,1,'testTask','renren',0,NULL,2,'2022-01-31 15:00:00'),
(168,1,'testTask','renren',0,NULL,0,'2022-01-31 15:30:00'),
(169,1,'testTask','renren',0,NULL,0,'2022-01-31 16:00:00'),
(170,1,'testTask','renren',0,NULL,1,'2022-01-31 16:30:00'),
(171,1,'testTask','renren',0,NULL,1,'2022-01-31 17:00:00'),
(172,1,'testTask','renren',0,NULL,1,'2022-01-31 17:30:00'),
(173,1,'testTask','renren',0,NULL,0,'2022-01-31 18:00:00'),
(174,1,'testTask','renren',0,NULL,2,'2022-01-31 18:30:00'),
(175,1,'testTask','renren',0,NULL,1,'2022-01-31 19:00:00'),
(176,1,'testTask','renren',0,NULL,1,'2022-01-31 19:30:00'),
(177,1,'testTask','renren',0,NULL,0,'2022-01-31 20:00:00'),
(178,1,'testTask','renren',0,NULL,1,'2022-01-31 20:30:00'),
(179,1,'testTask','renren',0,NULL,1,'2022-01-31 21:00:00'),
(180,1,'testTask','renren',0,NULL,1,'2022-01-31 21:30:00'),
(181,1,'testTask','renren',0,NULL,1,'2022-01-31 22:00:00'),
(182,1,'testTask','renren',0,NULL,0,'2022-01-31 22:30:00'),
(183,1,'testTask','renren',0,NULL,0,'2022-01-31 23:00:00'),
(184,1,'testTask','renren',0,NULL,1,'2022-01-31 23:30:00'),
(185,1,'testTask','renren',0,NULL,49,'2022-02-01 00:00:00'),
(186,1,'testTask','renren',0,NULL,3,'2022-02-01 00:30:00'),
(187,1,'testTask','renren',0,NULL,1,'2022-02-01 01:00:00'),
(188,1,'testTask','renren',0,NULL,0,'2022-02-01 01:30:00'),
(189,1,'testTask','renren',0,NULL,0,'2022-02-01 02:00:00'),
(190,1,'testTask','renren',0,NULL,0,'2022-02-01 02:30:00'),
(191,1,'testTask','renren',0,NULL,1,'2022-02-01 03:00:00'),
(192,1,'testTask','renren',0,NULL,0,'2022-02-01 15:00:00'),
(193,1,'testTask','renren',0,NULL,1,'2022-02-01 15:30:00'),
(194,1,'testTask','renren',0,NULL,1,'2022-02-01 16:00:00'),
(195,1,'testTask','renren',0,NULL,1,'2022-02-01 16:30:00'),
(196,1,'testTask','renren',0,NULL,1,'2022-02-01 17:00:00'),
(197,1,'testTask','renren',0,NULL,1,'2022-02-01 17:30:00'),
(198,1,'testTask','renren',0,NULL,1,'2022-02-01 18:00:00'),
(199,1,'testTask','renren',0,NULL,0,'2022-02-01 18:30:00'),
(200,1,'testTask','renren',0,NULL,0,'2022-02-01 19:00:00'),
(201,1,'testTask','renren',0,NULL,0,'2022-02-01 19:30:00'),
(202,1,'testTask','renren',0,NULL,1,'2022-02-01 20:00:00'),
(203,1,'testTask','renren',0,NULL,0,'2022-02-01 20:30:00'),
(204,1,'testTask','renren',0,NULL,2,'2022-02-01 21:00:00'),
(205,1,'testTask','renren',0,NULL,1,'2022-02-01 21:30:00'),
(206,1,'testTask','renren',0,NULL,0,'2022-02-01 22:00:00'),
(207,1,'testTask','renren',0,NULL,0,'2022-02-01 22:30:00'),
(208,1,'testTask','renren',0,NULL,1,'2022-02-01 23:00:00'),
(209,1,'testTask','renren',0,NULL,1,'2022-02-01 23:30:00'),
(210,1,'testTask','renren',0,NULL,21,'2022-02-02 00:00:00'),
(211,1,'testTask','renren',0,NULL,7,'2022-02-02 00:30:00'),
(212,1,'testTask','renren',0,NULL,3,'2022-02-03 00:00:00'),
(213,1,'testTask','renren',0,NULL,1,'2022-02-03 00:30:00'),
(214,1,'testTask','renren',0,NULL,1,'2022-02-03 22:00:00'),
(215,1,'testTask','renren',0,NULL,1,'2022-02-03 22:30:00'),
(216,1,'testTask','renren',0,NULL,2,'2022-02-03 23:00:00'),
(217,1,'testTask','renren',0,NULL,1,'2022-02-03 23:30:00'),
(218,1,'testTask','renren',0,NULL,4,'2022-02-04 00:00:00'),
(219,1,'testTask','renren',0,NULL,0,'2022-02-04 21:30:00'),
(220,1,'testTask','renren',0,NULL,1,'2022-02-04 22:00:00'),
(221,1,'testTask','renren',0,NULL,1,'2022-02-04 22:30:00'),
(222,1,'testTask','renren',0,NULL,0,'2022-02-04 23:00:00'),
(223,1,'testTask','renren',0,NULL,1,'2022-02-04 23:30:00'),
(224,1,'testTask','renren',0,NULL,8,'2022-02-05 00:00:00'),
(225,1,'testTask','renren',0,NULL,2,'2022-02-05 00:30:00'),
(226,1,'testTask','renren',0,NULL,0,'2022-02-05 01:00:00'),
(227,1,'testTask','renren',0,NULL,1,'2022-02-05 01:30:00'),
(228,1,'testTask','renren',0,NULL,1,'2022-02-05 02:00:00'),
(229,1,'testTask','renren',0,NULL,1,'2022-02-05 02:30:00'),
(230,1,'testTask','renren',0,NULL,1,'2022-02-05 03:00:00'),
(231,1,'testTask','renren',0,NULL,1,'2022-02-05 03:30:00'),
(232,1,'testTask','renren',0,NULL,1,'2022-02-05 04:00:00'),
(233,1,'testTask','renren',0,NULL,0,'2022-02-05 14:00:00'),
(234,1,'testTask','renren',0,NULL,2,'2022-02-05 14:30:00'),
(235,1,'testTask','renren',0,NULL,1,'2022-02-05 15:00:00'),
(236,1,'testTask','renren',0,NULL,1,'2022-02-05 15:30:00'),
(237,1,'testTask','renren',0,NULL,0,'2022-02-05 16:00:00'),
(238,1,'testTask','renren',0,NULL,0,'2022-02-05 16:30:00'),
(239,1,'testTask','renren',0,NULL,1,'2022-02-05 17:00:00'),
(240,1,'testTask','renren',0,NULL,1,'2022-02-05 17:30:00'),
(241,1,'testTask','renren',0,NULL,1,'2022-02-05 18:00:00'),
(242,1,'testTask','renren',0,NULL,1,'2022-02-05 18:30:00'),
(243,1,'testTask','renren',0,NULL,1,'2022-02-05 19:00:00'),
(244,1,'testTask','renren',0,NULL,1,'2022-02-05 19:30:00'),
(245,1,'testTask','renren',0,NULL,0,'2022-02-05 20:00:00'),
(246,1,'testTask','renren',0,NULL,1,'2022-02-05 20:30:00'),
(247,1,'testTask','renren',0,NULL,0,'2022-02-05 21:00:00'),
(248,1,'testTask','renren',0,NULL,1,'2022-02-05 21:30:00'),
(249,1,'testTask','renren',0,NULL,0,'2022-02-05 22:00:00'),
(250,1,'testTask','renren',0,NULL,1,'2022-02-05 22:30:00'),
(251,1,'testTask','renren',0,NULL,1,'2022-02-05 23:00:00'),
(252,1,'testTask','renren',0,NULL,1,'2022-02-05 23:30:00'),
(253,1,'testTask','renren',0,NULL,34,'2022-02-06 00:00:00'),
(254,1,'testTask','renren',0,NULL,1,'2022-02-06 00:30:00'),
(255,1,'testTask','renren',0,NULL,1,'2022-02-06 01:00:00'),
(256,1,'testTask','renren',0,NULL,1,'2022-02-06 01:30:00'),
(257,1,'testTask','renren',0,NULL,0,'2022-02-06 02:00:00'),
(258,1,'testTask','renren',0,NULL,1,'2022-02-06 02:30:00'),
(259,1,'testTask','renren',0,NULL,1,'2022-02-06 03:00:00'),
(260,1,'testTask','renren',0,NULL,1,'2022-02-06 11:30:00'),
(261,1,'testTask','renren',0,NULL,1,'2022-02-06 12:00:00'),
(262,1,'testTask','renren',0,NULL,1,'2022-02-06 12:30:00'),
(263,1,'testTask','renren',0,NULL,1,'2022-02-06 13:00:00'),
(264,1,'testTask','renren',0,NULL,0,'2022-02-06 13:30:00'),
(265,1,'testTask','renren',0,NULL,0,'2022-02-06 14:00:00'),
(266,1,'testTask','renren',0,NULL,1,'2022-02-06 14:30:00'),
(267,1,'testTask','renren',0,NULL,0,'2022-02-06 15:00:00'),
(268,1,'testTask','renren',0,NULL,1,'2022-02-06 15:30:00'),
(269,1,'testTask','renren',0,NULL,1,'2022-02-06 16:00:00'),
(270,1,'testTask','renren',0,NULL,1,'2022-02-06 16:30:00'),
(271,1,'testTask','renren',0,NULL,1,'2022-02-06 17:00:00'),
(272,1,'testTask','renren',0,NULL,0,'2022-02-06 17:30:00'),
(273,1,'testTask','renren',0,NULL,0,'2022-02-06 18:00:00'),
(274,1,'testTask','renren',0,NULL,1,'2022-02-06 18:30:00'),
(275,1,'testTask','renren',0,NULL,1,'2022-02-06 19:00:00'),
(276,1,'testTask','renren',0,NULL,0,'2022-02-06 19:30:00'),
(277,1,'testTask','renren',0,NULL,0,'2022-02-06 20:00:00'),
(278,1,'testTask','renren',0,NULL,1,'2022-02-06 20:30:00'),
(279,1,'testTask','renren',0,NULL,1,'2022-02-06 21:00:00'),
(280,1,'testTask','renren',0,NULL,0,'2022-02-06 21:30:00'),
(281,1,'testTask','renren',0,NULL,1,'2022-02-06 22:00:00'),
(282,1,'testTask','renren',0,NULL,1,'2022-02-06 22:30:00'),
(283,1,'testTask','renren',0,NULL,0,'2022-02-06 23:00:00'),
(284,1,'testTask','renren',0,NULL,0,'2022-02-06 23:30:00'),
(285,1,'testTask','renren',0,NULL,33,'2022-02-07 00:00:00'),
(286,1,'testTask','renren',0,NULL,1,'2022-02-07 00:30:00'),
(287,1,'testTask','renren',0,NULL,0,'2022-02-07 01:00:00'),
(288,1,'testTask','renren',0,NULL,1,'2022-02-07 01:30:00'),
(289,1,'testTask','renren',0,NULL,0,'2022-02-07 16:30:00'),
(290,1,'testTask','renren',0,NULL,0,'2022-02-07 17:00:00'),
(291,1,'testTask','renren',0,NULL,0,'2022-02-07 17:30:00'),
(292,1,'testTask','renren',0,NULL,0,'2022-02-07 18:00:00'),
(293,1,'testTask','renren',0,NULL,1,'2022-02-07 18:30:00'),
(294,1,'testTask','renren',0,NULL,1,'2022-02-07 19:00:00'),
(295,1,'testTask','renren',0,NULL,1,'2022-02-07 19:30:00'),
(296,1,'testTask','renren',0,NULL,1,'2022-02-07 20:00:00'),
(297,1,'testTask','renren',0,NULL,0,'2022-02-07 20:30:00'),
(298,1,'testTask','renren',0,NULL,0,'2022-02-07 21:00:00'),
(299,1,'testTask','renren',0,NULL,1,'2022-02-07 21:30:00'),
(300,1,'testTask','renren',0,NULL,1,'2022-02-07 22:00:00'),
(301,1,'testTask','renren',0,NULL,0,'2022-02-07 22:30:00'),
(302,1,'testTask','renren',0,NULL,1,'2022-02-07 23:00:00'),
(303,1,'testTask','renren',0,NULL,1,'2022-02-07 23:30:00'),
(304,1,'testTask','renren',0,NULL,8,'2022-02-08 00:00:00'),
(305,1,'testTask','renren',0,NULL,2,'2022-02-08 00:30:00'),
(306,1,'testTask','renren',0,NULL,0,'2022-02-08 01:00:00'),
(307,1,'testTask','renren',0,NULL,0,'2022-02-08 16:00:00'),
(308,1,'testTask','renren',0,NULL,1,'2022-02-08 16:30:00'),
(309,1,'testTask','renren',0,NULL,0,'2022-02-08 17:00:00'),
(310,1,'testTask','renren',0,NULL,1,'2022-02-08 17:30:00'),
(311,1,'testTask','renren',0,NULL,1,'2022-02-08 18:00:00'),
(312,1,'testTask','renren',0,NULL,0,'2022-02-08 18:30:00'),
(313,1,'testTask','renren',0,NULL,0,'2022-02-08 19:00:00'),
(314,1,'testTask','renren',0,NULL,2,'2022-02-09 00:00:00'),
(315,1,'testTask','renren',0,NULL,0,'2022-02-09 00:30:00'),
(316,1,'testTask','renren',0,NULL,0,'2022-02-09 01:00:00'),
(317,1,'testTask','renren',0,NULL,1,'2022-02-09 11:00:00'),
(318,1,'testTask','renren',0,NULL,1,'2022-02-09 11:30:00'),
(319,1,'testTask','renren',0,NULL,1,'2022-02-09 12:00:00'),
(320,1,'testTask','renren',0,NULL,0,'2022-02-09 12:30:00'),
(321,1,'testTask','renren',0,NULL,1,'2022-02-09 16:30:00'),
(322,1,'testTask','renren',0,NULL,0,'2022-02-09 17:00:00'),
(323,1,'testTask','renren',0,NULL,1,'2022-02-09 17:30:00'),
(324,1,'testTask','renren',0,NULL,1,'2022-02-09 18:00:00'),
(325,1,'testTask','renren',0,NULL,0,'2022-02-09 18:30:00'),
(326,1,'testTask','renren',0,NULL,0,'2022-02-09 19:00:00'),
(327,1,'testTask','renren',0,NULL,1,'2022-02-09 19:30:00'),
(328,1,'testTask','renren',0,NULL,0,'2022-02-10 01:00:00'),
(329,1,'testTask','renren',0,NULL,1,'2022-02-10 01:30:00'),
(330,1,'testTask','renren',0,NULL,0,'2022-02-10 02:00:00'),
(331,1,'testTask','renren',0,NULL,1,'2022-02-10 22:30:00'),
(332,1,'testTask','renren',0,NULL,0,'2022-02-10 23:00:00'),
(333,1,'testTask','renren',0,NULL,0,'2022-02-10 23:30:00'),
(334,1,'testTask','renren',0,NULL,10,'2022-02-11 00:00:00'),
(335,1,'testTask','renren',0,NULL,3,'2022-02-11 00:30:00'),
(336,1,'testTask','renren',0,NULL,1,'2022-02-11 01:00:00'),
(337,1,'testTask','renren',0,NULL,1,'2022-02-11 01:30:00'),
(338,1,'testTask','renren',0,NULL,0,'2022-02-11 13:00:00'),
(339,1,'testTask','renren',0,NULL,1,'2022-02-11 13:30:00'),
(340,1,'testTask','renren',0,NULL,1,'2022-02-11 14:00:00'),
(341,1,'testTask','renren',0,NULL,0,'2022-02-11 14:30:00'),
(342,1,'testTask','renren',0,NULL,1,'2022-02-11 15:00:00'),
(343,1,'testTask','renren',0,NULL,2,'2022-02-11 15:30:00'),
(344,1,'testTask','renren',0,NULL,0,'2022-02-11 16:00:00'),
(345,1,'testTask','renren',0,NULL,1,'2022-02-11 16:30:00'),
(346,1,'testTask','renren',0,NULL,0,'2022-02-11 17:00:00'),
(347,1,'testTask','renren',0,NULL,1,'2022-02-11 17:30:00'),
(348,1,'testTask','renren',0,NULL,1,'2022-02-11 18:00:00'),
(349,1,'testTask','renren',0,NULL,0,'2022-02-11 18:30:00'),
(350,1,'testTask','renren',0,NULL,1,'2022-02-11 19:00:00'),
(351,1,'testTask','renren',0,NULL,1,'2022-02-11 19:30:00'),
(352,1,'testTask','renren',0,NULL,0,'2022-02-11 20:00:00'),
(353,1,'testTask','renren',0,NULL,2,'2022-02-11 20:30:00'),
(354,1,'testTask','renren',0,NULL,2,'2022-02-11 21:00:00'),
(355,1,'testTask','renren',0,NULL,1,'2022-02-11 21:30:00'),
(356,1,'testTask','renren',0,NULL,1,'2022-02-11 22:30:00'),
(357,1,'testTask','renren',0,NULL,0,'2022-02-11 23:00:00'),
(358,1,'testTask','renren',0,NULL,1,'2022-02-11 23:30:00'),
(359,1,'testTask','renren',0,NULL,11,'2022-02-12 00:00:00'),
(360,1,'testTask','renren',0,NULL,0,'2022-02-12 00:30:00'),
(361,1,'testTask','renren',0,NULL,1,'2022-02-12 01:00:00'),
(362,1,'testTask','renren',0,NULL,1,'2022-02-12 13:30:00'),
(363,1,'testTask','renren',0,NULL,1,'2022-02-12 14:00:00'),
(364,1,'testTask','renren',0,NULL,0,'2022-02-12 14:30:00'),
(365,1,'testTask','renren',0,NULL,1,'2022-02-12 15:00:00'),
(366,1,'testTask','renren',0,NULL,1,'2022-02-12 15:30:00'),
(367,1,'testTask','renren',0,NULL,0,'2022-02-12 16:00:00'),
(368,1,'testTask','renren',0,NULL,1,'2022-02-12 16:30:00'),
(369,1,'testTask','renren',0,NULL,0,'2022-02-12 17:00:00'),
(370,1,'testTask','renren',0,NULL,1,'2022-02-12 17:30:00'),
(371,1,'testTask','renren',0,NULL,1,'2022-02-12 19:00:00'),
(372,1,'testTask','renren',0,NULL,0,'2022-02-12 19:30:00'),
(373,1,'testTask','renren',0,NULL,1,'2022-02-12 20:00:00'),
(374,1,'testTask','renren',0,NULL,1,'2022-02-12 20:30:00'),
(375,1,'testTask','renren',0,NULL,0,'2022-02-12 21:00:00'),
(376,1,'testTask','renren',0,NULL,1,'2022-02-13 18:30:00'),
(377,1,'testTask','renren',0,NULL,1,'2022-02-13 19:00:00'),
(378,1,'testTask','renren',0,NULL,0,'2022-02-13 19:30:00'),
(379,1,'testTask','renren',0,NULL,0,'2022-02-13 20:00:00'),
(380,1,'testTask','renren',0,NULL,0,'2022-02-13 20:30:00'),
(381,1,'testTask','renren',0,NULL,1,'2022-02-13 21:00:00'),
(382,1,'testTask','renren',0,NULL,1,'2022-02-13 21:30:00'),
(383,1,'testTask','renren',0,NULL,0,'2022-02-13 22:00:00'),
(384,1,'testTask','renren',0,NULL,0,'2022-02-13 22:30:00'),
(385,1,'testTask','renren',0,NULL,1,'2022-02-13 23:00:00'),
(386,1,'testTask','renren',0,NULL,0,'2022-02-13 23:30:00'),
(387,1,'testTask','renren',0,NULL,6,'2022-02-14 00:00:00'),
(388,1,'testTask','renren',0,NULL,0,'2022-02-14 00:30:00'),
(389,1,'testTask','renren',0,NULL,0,'2022-02-14 01:00:00'),
(390,1,'testTask','renren',0,NULL,1,'2022-02-14 01:30:00'),
(391,1,'testTask','renren',0,NULL,1,'2022-02-14 14:30:00'),
(392,1,'testTask','renren',0,NULL,2,'2022-02-14 15:00:00'),
(393,1,'testTask','renren',0,NULL,1,'2022-02-14 15:30:00'),
(394,1,'testTask','renren',0,NULL,1,'2022-02-14 16:00:00'),
(395,1,'testTask','renren',0,NULL,0,'2022-02-14 16:30:00'),
(396,1,'testTask','renren',0,NULL,1,'2022-02-14 17:00:00'),
(397,1,'testTask','renren',0,NULL,0,'2022-02-14 17:30:00'),
(398,1,'testTask','renren',0,NULL,1,'2022-02-14 18:00:00'),
(399,1,'testTask','renren',0,NULL,0,'2022-02-15 16:30:00'),
(400,1,'testTask','renren',0,NULL,0,'2022-02-15 17:00:00'),
(401,1,'testTask','renren',0,NULL,1,'2022-02-15 17:30:00'),
(402,1,'testTask','renren',0,NULL,0,'2022-02-15 18:00:00'),
(403,1,'testTask','renren',0,NULL,1,'2022-02-15 18:30:00'),
(404,1,'testTask','renren',0,NULL,0,'2022-02-15 19:00:00'),
(405,1,'testTask','renren',0,NULL,0,'2022-02-15 19:30:00'),
(406,1,'testTask','renren',0,NULL,0,'2022-02-15 20:00:00'),
(407,1,'testTask','renren',0,NULL,1,'2022-02-15 20:30:00'),
(408,1,'testTask','renren',0,NULL,0,'2022-02-15 21:00:00'),
(409,1,'testTask','renren',0,NULL,5,'2022-02-15 21:30:00'),
(410,1,'testTask','renren',0,NULL,1,'2022-02-15 22:00:00'),
(411,1,'testTask','renren',0,NULL,0,'2022-02-15 22:30:00'),
(412,1,'testTask','renren',0,NULL,0,'2022-02-15 23:00:00'),
(413,1,'testTask','renren',0,NULL,1,'2022-02-15 23:30:00'),
(414,1,'testTask','renren',0,NULL,13,'2022-02-16 00:00:00'),
(415,1,'testTask','renren',0,NULL,1,'2022-02-16 00:30:00'),
(416,1,'testTask','renren',0,NULL,0,'2022-02-16 01:00:00'),
(417,1,'testTask','renren',0,NULL,0,'2022-02-16 01:30:00'),
(418,1,'testTask','renren',0,NULL,0,'2022-02-16 02:00:00'),
(419,1,'testTask','renren',0,NULL,1,'2022-02-16 02:30:00'),
(420,1,'testTask','renren',0,NULL,0,'2022-02-18 12:30:00'),
(421,1,'testTask','renren',0,NULL,0,'2022-02-18 13:00:00'),
(422,1,'testTask','renren',0,NULL,1,'2022-02-18 13:30:00'),
(423,1,'testTask','renren',0,NULL,1,'2022-02-18 14:00:00'),
(424,1,'testTask','renren',0,NULL,0,'2022-02-18 14:30:00'),
(425,1,'testTask','renren',0,NULL,0,'2022-02-18 15:00:00'),
(426,1,'testTask','renren',0,NULL,1,'2022-02-18 15:30:00'),
(427,1,'testTask','renren',0,NULL,1,'2022-02-18 18:00:00'),
(428,1,'testTask','renren',0,NULL,1,'2022-02-18 18:30:00'),
(429,1,'testTask','renren',0,NULL,1,'2022-02-18 19:00:00'),
(430,1,'testTask','renren',0,NULL,0,'2022-02-18 19:30:00'),
(431,1,'testTask','renren',0,NULL,1,'2022-02-18 20:00:00'),
(432,1,'testTask','renren',0,NULL,4,'2022-02-18 20:30:00'),
(433,1,'testTask','renren',0,NULL,2,'2022-02-18 21:30:00'),
(434,1,'testTask','renren',0,NULL,9,'2022-02-18 22:00:00'),
(435,1,'testTask','renren',0,NULL,1,'2022-02-19 13:30:00'),
(436,1,'testTask','renren',0,NULL,1,'2022-02-19 14:00:00'),
(437,1,'testTask','renren',0,NULL,1,'2022-02-19 14:30:00'),
(438,1,'testTask','renren',0,NULL,2,'2022-02-19 19:30:00'),
(439,1,'testTask','renren',0,NULL,1,'2022-02-19 20:00:00'),
(440,1,'testTask','renren',0,NULL,2,'2022-02-19 20:30:00'),
(441,1,'testTask','renren',0,NULL,1,'2022-02-19 21:00:00'),
(442,1,'testTask','renren',0,NULL,1,'2022-02-19 21:30:00'),
(443,1,'testTask','renren',0,NULL,0,'2022-02-19 22:00:00'),
(444,1,'testTask','renren',0,NULL,0,'2022-02-19 22:30:00'),
(445,1,'testTask','renren',0,NULL,1,'2022-02-19 23:00:00'),
(446,1,'testTask','renren',0,NULL,0,'2022-02-19 23:30:00'),
(447,1,'testTask','renren',0,NULL,7,'2022-02-20 00:00:00'),
(448,1,'testTask','renren',0,NULL,1,'2022-02-20 00:30:00'),
(449,1,'testTask','renren',0,NULL,16,'2022-02-20 01:00:00'),
(450,1,'testTask','renren',0,NULL,2,'2022-02-20 12:30:00'),
(451,1,'testTask','renren',0,NULL,2,'2022-02-20 13:00:00'),
(452,1,'testTask','renren',0,NULL,1,'2022-02-20 13:30:00'),
(453,1,'testTask','renren',0,NULL,1,'2022-02-20 14:00:00'),
(454,1,'testTask','renren',0,NULL,2,'2022-02-20 14:30:00'),
(455,1,'testTask','renren',0,NULL,0,'2022-02-20 15:00:00'),
(456,1,'testTask','renren',0,NULL,1,'2022-02-20 15:30:00'),
(457,1,'testTask','renren',0,NULL,1,'2022-02-20 16:00:00'),
(458,1,'testTask','renren',0,NULL,3,'2022-02-20 16:30:00'),
(459,1,'testTask','renren',0,NULL,1,'2022-02-20 17:00:00'),
(460,1,'testTask','renren',0,NULL,2,'2022-02-20 17:30:00'),
(461,1,'testTask','renren',0,NULL,1,'2022-02-20 18:00:00'),
(462,1,'testTask','renren',0,NULL,0,'2022-02-20 18:30:00'),
(463,1,'testTask','renren',0,NULL,1,'2022-02-20 19:00:00'),
(464,1,'testTask','renren',0,NULL,3,'2022-02-20 19:30:00'),
(465,1,'testTask','renren',0,NULL,2,'2022-02-20 20:00:00'),
(466,1,'testTask','renren',0,NULL,1,'2022-02-20 20:30:00'),
(467,1,'testTask','renren',0,NULL,1,'2022-02-20 21:00:00'),
(468,1,'testTask','renren',0,NULL,2,'2022-02-20 21:30:00'),
(469,1,'testTask','renren',0,NULL,0,'2022-02-20 22:00:00'),
(470,1,'testTask','renren',0,NULL,0,'2022-02-20 22:30:00'),
(471,1,'testTask','renren',0,NULL,1,'2022-02-20 23:00:00'),
(472,1,'testTask','renren',0,NULL,1,'2022-02-20 23:30:00'),
(473,1,'testTask','renren',0,NULL,66,'2022-02-21 00:00:00'),
(474,1,'testTask','renren',0,NULL,6,'2022-02-21 00:30:00'),
(475,1,'testTask','renren',0,NULL,1,'2022-02-21 01:00:00'),
(476,1,'testTask','renren',0,NULL,0,'2022-03-15 20:30:00'),
(477,1,'testTask','renren',0,NULL,1,'2022-03-15 21:00:00'),
(478,1,'testTask','renren',0,NULL,0,'2022-03-15 21:30:00'),
(479,1,'testTask','renren',0,NULL,1,'2022-03-15 22:00:00'),
(480,1,'testTask','renren',0,NULL,0,'2022-03-15 22:30:00'),
(481,1,'testTask','renren',0,NULL,1,'2022-03-15 23:00:00'),
(482,1,'testTask','renren',0,NULL,1,'2022-03-15 23:30:00'),
(483,1,'testTask','renren',0,NULL,1,'2022-03-17 23:30:00'),
(484,1,'testTask','renren',0,NULL,2,'2022-03-18 01:30:00'),
(485,1,'testTask','renren',0,NULL,1,'2022-03-21 18:00:00'),
(486,1,'testTask','renren',0,NULL,1,'2022-03-23 20:30:00'),
(487,1,'testTask','renren',0,NULL,1,'2022-03-23 21:00:00'),
(488,1,'testTask','renren',0,NULL,0,'2022-03-23 21:30:00'),
(489,1,'testTask','renren',0,NULL,1,'2022-03-24 12:30:00'),
(490,1,'testTask','renren',0,NULL,2,'2022-03-24 13:00:00'),
(491,1,'testTask','renren',0,NULL,1,'2022-03-24 13:30:00'),
(492,1,'testTask','renren',0,NULL,1,'2022-03-24 14:00:00'),
(493,1,'testTask','renren',0,NULL,1,'2022-03-24 14:30:00'),
(494,1,'testTask','renren',0,NULL,1,'2022-03-24 15:00:00'),
(495,1,'testTask','renren',0,NULL,0,'2022-03-24 15:30:00'),
(496,1,'testTask','renren',0,NULL,0,'2022-03-24 16:00:00'),
(497,1,'testTask','renren',0,NULL,0,'2022-03-24 19:00:00'),
(498,1,'testTask','renren',0,NULL,1,'2022-03-24 19:30:00'),
(499,1,'testTask','renren',0,NULL,1,'2022-03-24 20:00:00'),
(500,1,'testTask','renren',0,NULL,1,'2022-03-24 20:30:00'),
(501,1,'testTask','renren',0,NULL,1,'2022-03-24 21:00:00'),
(502,1,'testTask','renren',0,NULL,1,'2022-03-24 22:00:00'),
(503,1,'testTask','renren',0,NULL,2,'2022-03-24 22:30:00'),
(504,1,'testTask','renren',0,NULL,5,'2022-03-24 23:00:00'),
(505,1,'testTask','renren',0,NULL,0,'2022-03-24 23:30:00'),
(506,1,'testTask','renren',0,NULL,6,'2022-03-25 00:00:00'),
(507,1,'testTask','renren',0,NULL,0,'2022-03-25 00:30:00'),
(508,1,'testTask','renren',0,NULL,0,'2022-03-25 01:00:00'),
(509,1,'testTask','renren',0,NULL,0,'2022-03-25 01:30:00'),
(510,1,'testTask','renren',0,NULL,1,'2022-03-25 18:00:00'),
(511,1,'testTask','renren',0,NULL,0,'2022-03-25 18:30:00'),
(512,1,'testTask','renren',0,NULL,1,'2022-03-25 19:00:00'),
(513,1,'testTask','renren',0,NULL,0,'2022-03-25 19:30:00'),
(514,1,'testTask','renren',0,NULL,0,'2022-03-25 20:00:00'),
(515,1,'testTask','renren',0,NULL,1,'2022-03-25 20:30:00'),
(516,1,'testTask','renren',0,NULL,0,'2022-03-25 21:00:00'),
(517,1,'testTask','renren',0,NULL,1,'2022-03-25 21:30:00'),
(518,1,'testTask','renren',0,NULL,1,'2022-03-25 22:00:00'),
(519,1,'testTask','renren',0,NULL,0,'2022-03-25 22:30:00'),
(520,1,'testTask','renren',0,NULL,0,'2022-03-25 23:00:00'),
(521,1,'testTask','renren',0,NULL,0,'2022-03-27 22:30:00'),
(522,1,'testTask','renren',0,NULL,0,'2022-03-27 23:00:00'),
(523,1,'testTask','renren',0,NULL,1,'2022-03-27 23:30:00'),
(524,1,'testTask','renren',0,NULL,3,'2022-03-28 00:00:00'),
(525,1,'testTask','renren',0,NULL,2,'2022-03-28 00:30:00'),
(526,1,'testTask','renren',0,NULL,1,'2022-03-28 01:00:00'),
(527,1,'testTask','renren',0,NULL,1,'2022-03-28 01:30:00'),
(528,1,'testTask','renren',0,NULL,0,'2022-03-28 21:00:00'),
(529,1,'testTask','renren',0,NULL,0,'2022-03-28 21:30:00'),
(530,1,'testTask','renren',0,NULL,0,'2022-03-28 22:00:00'),
(531,1,'testTask','renren',0,NULL,1,'2022-03-28 22:30:00'),
(532,1,'testTask','renren',0,NULL,1,'2022-03-28 23:00:00'),
(533,1,'testTask','renren',0,NULL,1,'2022-03-28 23:30:00'),
(534,1,'testTask','renren',0,NULL,5,'2022-03-29 00:00:00'),
(535,1,'testTask','renren',0,NULL,1,'2022-03-29 00:30:00'),
(536,1,'testTask','renren',0,NULL,0,'2022-03-29 01:00:00'),
(537,1,'testTask','renren',0,NULL,1,'2022-03-29 01:30:00'),
(538,1,'testTask','renren',0,NULL,1,'2022-03-29 12:30:00'),
(539,1,'testTask','renren',0,NULL,1,'2022-03-29 13:00:00'),
(540,1,'testTask','renren',0,NULL,1,'2022-03-29 13:30:00'),
(541,1,'testTask','renren',0,NULL,1,'2022-03-29 14:00:00'),
(542,1,'testTask','renren',0,NULL,1,'2022-03-29 14:30:00'),
(543,1,'testTask','renren',0,NULL,1,'2022-03-29 15:00:00');

/*Table structure for table `sys_captcha` */

DROP TABLE IF EXISTS `sys_captcha`;

CREATE TABLE `sys_captcha` (
  `uuid` char(36) NOT NULL COMMENT 'uuid',
  `code` varchar(6) NOT NULL COMMENT '验证码',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统验证码';

/*Data for the table `sys_captcha` */

insert  into `sys_captcha`(`uuid`,`code`,`expire_time`) values 
('054218e8-0e3d-4a1f-8ea4-e0e29df80964','ecpx2','2022-01-29 21:15:49'),
('09d3cb40-0ff8-426b-83e3-a04133f931d2','3pdp5','2022-01-15 17:09:07'),
('10d93120-f130-4b2e-8c10-83f0bad2a071','n3gf2','2022-03-18 00:26:51'),
('11c3d170-8ef3-43ea-8c20-13aa8cfa6fdc','g4fw6','2022-01-15 16:29:59'),
('16ff3c44-6381-4d1c-80d0-7734bf053aed','p2cgn','2022-02-10 02:00:44'),
('1845803c-f77b-4704-8a60-477a66bc73d3','b256g','2022-01-15 17:17:35'),
('18caf30f-7bd5-4f62-8712-9c3430ba8e33','8ax8p','2022-01-15 18:32:07'),
('1c70dffe-2a1e-4e05-8cee-8cff1e3c10ed','x3557','2022-02-14 14:37:28'),
('205d1f65-1052-46fc-8568-e34955989566','x7dgy','2022-01-15 17:17:06'),
('24295a1c-286e-4ad2-8af6-3106750affe4','8maec','2022-02-15 17:00:49'),
('29037dc7-e2a3-4321-87f7-b47cb10e4b8d','e2ne4','2022-02-08 15:46:35'),
('39233441-72fb-41b5-866e-6083dc55ceb5','gd645','2022-01-15 16:29:59'),
('45503583-5348-41e0-82b6-0a81ea01400f','aew7y','2022-01-15 16:34:48'),
('582d8baa-4597-4ece-80a7-837d8d003f08','2c2p6','2022-01-15 15:14:55'),
('5990905c-2879-4830-85e4-24695a834dc9','5x3nm','2022-01-15 16:44:25'),
('5ad4caf4-d17a-4491-8d47-d610b8721edc','xdnnc','2022-02-02 23:43:43'),
('5b9bb973-b190-49b8-89e4-050ed9b8de54','f7aab','2022-01-15 18:32:07'),
('5f61e3d2-295b-4f65-819b-87424e802bcc','83m5y','2022-01-15 16:27:39'),
('76a5a9a8-09fe-4995-86f1-9be97bcb1db1','w566a','2022-01-31 10:46:41'),
('7fb791a3-37fd-41d5-8c18-b202ee5e86d1','dyncf','2022-01-15 16:23:28'),
('855a6230-d8b0-4354-81b6-44abd842df3f','62xa4','2022-01-21 22:33:01'),
('86296e5c-cabe-4f71-82ad-2a3ccc9be460','8n8d5','2022-01-15 17:17:31'),
('89ba629b-ae3a-4d69-8832-d98bfa94de58','nnp53','2022-01-20 20:33:45'),
('965e380a-3b89-4f7e-885e-9ed80a0315b2','pc3nf','2022-01-21 21:14:09'),
('967c541f-ecbc-44c6-8085-0685fa5d9f21','wdcn4','2022-01-15 17:09:02'),
('9e55e736-682e-4588-8f43-3c638b468281','pa7my','2022-01-15 18:32:07'),
('9f2d8c5b-7a26-48f2-85b7-bd53df9b41a7','w72me','2022-02-10 22:36:26'),
('b533628b-68b6-43f8-81cf-2daeeddda2cc','5bp4c','2022-01-15 16:32:52'),
('c09177ce-7745-4c41-86eb-e204758599a3','d3np3','2022-01-15 17:12:09'),
('cace9096-5116-4a94-88f2-58da6b5907f7','nacf5','2022-01-15 17:05:40'),
('ce0ebffa-62d1-452b-871f-9cd7c6371dfa','fnd35','2022-01-15 16:33:35'),
('d78d5f24-880b-4c91-88c1-b71e78a6f394','w8mcc','2022-01-27 00:38:25'),
('fa569692-3680-4c60-8493-8d96d9863768','wpdma','2022-02-01 18:50:09'),
('fdd5c079-3bd7-48e5-8bf0-59f1a8d7a31d','2eegf','2022-03-21 17:40:08');

/*Table structure for table `sys_config` */

DROP TABLE IF EXISTS `sys_config`;

CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `param_key` varchar(50) DEFAULT NULL COMMENT 'key',
  `param_value` varchar(2000) DEFAULT NULL COMMENT 'value',
  `status` tinyint DEFAULT '1' COMMENT '状态   0：隐藏   1：显示',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `param_key` (`param_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置信息表';

/*Data for the table `sys_config` */

insert  into `sys_config`(`id`,`param_key`,`param_value`,`status`,`remark`) values 
(1,'CLOUD_STORAGE_CONFIG_KEY','{\"aliyunAccessKeyId\":\"\",\"aliyunAccessKeySecret\":\"\",\"aliyunBucketName\":\"\",\"aliyunDomain\":\"\",\"aliyunEndPoint\":\"\",\"aliyunPrefix\":\"\",\"qcloudBucketName\":\"\",\"qcloudDomain\":\"\",\"qcloudPrefix\":\"\",\"qcloudSecretId\":\"\",\"qcloudSecretKey\":\"\",\"qiniuAccessKey\":\"NrgMfABZxWLo5B-YYSjoE8-AZ1EISdi1Z3ubLOeZ\",\"qiniuBucketName\":\"ios-app\",\"qiniuDomain\":\"http://7xqbwh.dl1.z0.glb.clouddn.com\",\"qiniuPrefix\":\"upload\",\"qiniuSecretKey\":\"uIwJHevMRWU0VLxFvgy0tAcOdGqasdtVlJkdy6vV\",\"type\":1}',0,'云存储配置信息');

/*Table structure for table `sys_log` */

DROP TABLE IF EXISTS `sys_log`;

CREATE TABLE `sys_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(50) DEFAULT NULL COMMENT '用户操作',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` varchar(5000) DEFAULT NULL COMMENT '请求参数',
  `time` bigint NOT NULL COMMENT '执行时长(毫秒)',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统日志';

/*Data for the table `sys_log` */

insert  into `sys_log`(`id`,`username`,`operation`,`method`,`params`,`time`,`ip`,`create_date`) values 
(1,'admin','保存菜单','io.renren.modules.sys.controller.SysMenuController.save()','[{\"menuId\":31,\"parentId\":0,\"name\":\"商品系统\",\"url\":\"\",\"perms\":\"\",\"type\":0,\"icon\":\"editor\",\"orderNum\":0,\"list\":[]}]',14,'0:0:0:0:0:0:0:1','2022-01-15 00:46:06'),
(2,'admin','保存菜单','io.renren.modules.sys.controller.SysMenuController.save()','[{\"menuId\":32,\"parentId\":31,\"name\":\"分类维护\",\"url\":\"product/category\",\"perms\":\"menu\",\"type\":1,\"icon\":\"menu\",\"orderNum\":0,\"list\":[]}]',9,'0:0:0:0:0:0:0:1','2022-01-15 00:48:24'),
(3,'admin','保存菜单','io.renren.modules.sys.controller.SysMenuController.save()','[{\"menuId\":33,\"parentId\":0,\"name\":\"品牌管理\",\"url\":\"product/brand\",\"perms\":\"\",\"type\":1,\"icon\":\"zhedie\",\"orderNum\":0,\"list\":[]}]',16,'0:0:0:0:0:0:0:1','2022-01-29 21:01:31'),
(4,'admin','修改菜单','io.renren.modules.sys.controller.SysMenuController.update()','[{\"menuId\":39,\"parentId\":37,\"name\":\"规格参数\",\"url\":\"product/attr-specification\",\"perms\":\"\",\"type\":1,\"icon\":\"log\",\"orderNum\":0,\"list\":[]}]',12,'0:0:0:0:0:0:0:1','2022-02-07 18:38:37'),
(5,'admin','修改菜单','io.renren.modules.sys.controller.SysMenuController.update()','[{\"menuId\":39,\"parentId\":37,\"name\":\"规格参数\",\"url\":\"product/attr-specification\",\"perms\":\"\",\"type\":1,\"icon\":\"log\",\"orderNum\":0,\"list\":[]}]',5,'0:0:0:0:0:0:0:1','2022-02-07 18:39:14'),
(6,'admin','修改菜单','io.renren.modules.sys.controller.SysMenuController.update()','[{\"menuId\":53,\"parentId\":43,\"name\":\"仓库维护\",\"url\":\"houseware/info\",\"perms\":\"\",\"type\":1,\"icon\":\"shouye\",\"orderNum\":0,\"list\":[]}]',13,'0:0:0:0:0:0:0:1','2022-02-13 23:49:38'),
(7,'admin','修改菜单','io.renren.modules.sys.controller.SysMenuController.update()','[{\"menuId\":55,\"parentId\":43,\"name\":\"商品库存\",\"url\":\"houseware/sku\",\"perms\":\"\",\"type\":1,\"icon\":\"jiesuo\",\"orderNum\":0,\"list\":[]}]',6,'0:0:0:0:0:0:0:1','2022-02-13 23:51:07'),
(8,'admin','修改菜单','io.renren.modules.sys.controller.SysMenuController.update()','[{\"menuId\":54,\"parentId\":43,\"name\":\"库存工作单\",\"url\":\"houseware/ordertask\",\"perms\":\"\",\"type\":1,\"icon\":\"log\",\"orderNum\":0,\"list\":[]}]',5,'0:0:0:0:0:0:0:1','2022-02-13 23:51:42'),
(9,'admin','修改菜单','io.renren.modules.sys.controller.SysMenuController.update()','[{\"menuId\":71,\"parentId\":70,\"name\":\"采购需求\",\"url\":\"houseware/purchasedetail\",\"perms\":\"\",\"type\":1,\"icon\":\"editor\",\"orderNum\":0,\"list\":[]}]',7,'0:0:0:0:0:0:0:1','2022-02-13 23:52:49'),
(10,'admin','修改菜单','io.renren.modules.sys.controller.SysMenuController.update()','[{\"menuId\":72,\"parentId\":70,\"name\":\"采购单\",\"url\":\"houseware/purchase\",\"perms\":\"\",\"type\":1,\"icon\":\"menu\",\"orderNum\":0,\"list\":[]}]',6,'0:0:0:0:0:0:0:1','2022-02-13 23:53:19'),
(11,'admin','修改菜单','io.renren.modules.sys.controller.SysMenuController.update()','[{\"menuId\":71,\"parentId\":70,\"name\":\"采购需求\",\"url\":\"houseware/purchasedetail\",\"perms\":\"\",\"type\":1,\"icon\":\"editor\",\"orderNum\":0,\"list\":[]}]',4,'0:0:0:0:0:0:0:1','2022-02-13 23:54:05'),
(12,'admin','修改菜单','io.renren.modules.sys.controller.SysMenuController.update()','[{\"menuId\":53,\"parentId\":43,\"name\":\"仓库维护\",\"url\":\"houseware/info\",\"perms\":\"\",\"type\":1,\"icon\":\"shouye\",\"orderNum\":0,\"list\":[]}]',4,'0:0:0:0:0:0:0:1','2022-02-14 00:20:54'),
(13,'admin','修改菜单','io.renren.modules.sys.controller.SysMenuController.update()','[{\"menuId\":53,\"parentId\":43,\"name\":\"仓库维护\",\"url\":\"houseware/info\",\"perms\":\"\",\"type\":0,\"icon\":\"shouye\",\"orderNum\":0,\"list\":[]}]',14,'0:0:0:0:0:0:0:1','2022-02-14 00:54:38'),
(14,'admin','修改菜单','io.renren.modules.sys.controller.SysMenuController.update()','[{\"menuId\":53,\"parentId\":43,\"name\":\"仓库维护\",\"url\":\"houseware/info/list\",\"perms\":\"\",\"type\":1,\"icon\":\"shouye\",\"orderNum\":0,\"list\":[]}]',7,'0:0:0:0:0:0:0:1','2022-02-14 00:55:05'),
(15,'admin','修改菜单','io.renren.modules.sys.controller.SysMenuController.update()','[{\"menuId\":53,\"parentId\":43,\"name\":\"仓库维护\",\"url\":\"houseware/wareinfo\",\"perms\":\"\",\"type\":1,\"icon\":\"shouye\",\"orderNum\":0,\"list\":[]}]',7,'0:0:0:0:0:0:0:1','2022-02-14 01:03:16'),
(16,'admin','修改菜单','io.renren.modules.sys.controller.SysMenuController.update()','[{\"menuId\":54,\"parentId\":43,\"name\":\"库存工作单\",\"url\":\"houseware/wareordertask\",\"perms\":\"\",\"type\":1,\"icon\":\"log\",\"orderNum\":0,\"list\":[]}]',6,'0:0:0:0:0:0:0:1','2022-02-14 01:07:32'),
(17,'admin','保存用户','io.renren.modules.sys.controller.SysUserController.save()','[{\"userId\":2,\"username\":\"采购员1\",\"password\":\"a6ae720cf5092cfaafbd4231944c908515f4013e9f83ce1e442bfee0ff2f4aae\",\"salt\":\"yxczCRd9yRpDQ8Y6wExR\",\"email\":\"123@qq.com\",\"mobile\":\"12345678910\",\"status\":1,\"roleIdList\":[],\"createUserId\":1,\"createTime\":\"Feb 15, 2022 6:34:38 PM\"}]',297,'0:0:0:0:0:0:0:1','2022-02-15 18:34:39');

/*Table structure for table `sys_menu` */

DROP TABLE IF EXISTS `sys_menu`;

CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `name` varchar(50) DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(200) DEFAULT NULL COMMENT '菜单URL',
  `perms` varchar(500) DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
  `type` int DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
  `icon` varchar(50) DEFAULT NULL COMMENT '菜单图标',
  `order_num` int DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单管理';

/*Data for the table `sys_menu` */

insert  into `sys_menu`(`menu_id`,`parent_id`,`name`,`url`,`perms`,`type`,`icon`,`order_num`) values 
(1,0,'系统管理',NULL,NULL,0,'system',0),
(2,1,'管理员列表','sys/user',NULL,1,'admin',1),
(3,1,'角色管理','sys/role',NULL,1,'role',2),
(4,1,'菜单管理','sys/menu',NULL,1,'menu',3),
(5,1,'SQL监控','http://localhost:8080/renren-fast/druid/sql.html',NULL,1,'sql',4),
(6,1,'定时任务','job/schedule',NULL,1,'job',5),
(7,6,'查看',NULL,'sys:schedule:list,sys:schedule:info',2,NULL,0),
(8,6,'新增',NULL,'sys:schedule:save',2,NULL,0),
(9,6,'修改',NULL,'sys:schedule:update',2,NULL,0),
(10,6,'删除',NULL,'sys:schedule:delete',2,NULL,0),
(11,6,'暂停',NULL,'sys:schedule:pause',2,NULL,0),
(12,6,'恢复',NULL,'sys:schedule:resume',2,NULL,0),
(13,6,'立即执行',NULL,'sys:schedule:run',2,NULL,0),
(14,6,'日志列表',NULL,'sys:schedule:log',2,NULL,0),
(15,2,'查看',NULL,'sys:user:list,sys:user:info',2,NULL,0),
(16,2,'新增',NULL,'sys:user:save,sys:role:select',2,NULL,0),
(17,2,'修改',NULL,'sys:user:update,sys:role:select',2,NULL,0),
(18,2,'删除',NULL,'sys:user:delete',2,NULL,0),
(19,3,'查看',NULL,'sys:role:list,sys:role:info',2,NULL,0),
(20,3,'新增',NULL,'sys:role:save,sys:menu:list',2,NULL,0),
(21,3,'修改',NULL,'sys:role:update,sys:menu:list',2,NULL,0),
(22,3,'删除',NULL,'sys:role:delete',2,NULL,0),
(23,4,'查看',NULL,'sys:menu:list,sys:menu:info',2,NULL,0),
(24,4,'新增',NULL,'sys:menu:save,sys:menu:select',2,NULL,0),
(25,4,'修改',NULL,'sys:menu:update,sys:menu:select',2,NULL,0),
(26,4,'删除',NULL,'sys:menu:delete',2,NULL,0),
(27,1,'参数管理','sys/config','sys:config:list,sys:config:info,sys:config:save,sys:config:update,sys:config:delete',1,'config',6),
(29,1,'系统日志','sys/log','sys:log:list',1,'log',7),
(30,1,'文件上传','oss/oss','sys:oss:all',1,'oss',6),
(31,0,'商品系统','','',0,'editor',0),
(32,31,'分类维护','product/category','',1,'menu',0),
(34,31,'品牌管理','product/brand','',1,'editor',0),
(37,31,'平台属性','','',0,'system',0),
(38,37,'属性分组','product/attrgroup','',1,'tubiao',0),
(39,37,'规格参数','product/attr-specification','',1,'log',0),
(40,37,'销售属性','product/saleattr','',1,'zonghe',0),
(41,31,'商品维护','product/spu','',0,'zonghe',0),
(42,0,'优惠营销','','',0,'mudedi',0),
(43,0,'库存系统','','',0,'shouye',0),
(44,0,'订单系统','','',0,'config',0),
(45,0,'用户系统','','',0,'admin',0),
(46,0,'内容管理','','',0,'sousuo',0),
(47,42,'优惠券管理','coupon/coupon','',1,'zhedie',0),
(48,42,'发放记录','coupon/history','',1,'sql',0),
(49,42,'专题活动','coupon/subject','',1,'tixing',0),
(50,42,'秒杀活动','coupon/seckill','',1,'daohang',0),
(51,42,'积分维护','coupon/bounds','',1,'geren',0),
(52,42,'满减折扣','coupon/full','',1,'shoucang',0),
(53,43,'仓库维护','houseware/wareinfo','',1,'shouye',0),
(54,43,'库存工作单','houseware/wareordertask','',1,'log',0),
(55,43,'商品库存','houseware/sku','',1,'jiesuo',0),
(56,44,'订单查询','order/order','',1,'zhedie',0),
(57,44,'退货单处理','order/return','',1,'shanchu',0),
(58,44,'等级规则','order/settings','',1,'system',0),
(59,44,'支付流水查询','order/payment','',1,'job',0),
(60,44,'退款流水查询','order/refund','',1,'mudedi',0),
(61,45,'会员列表','member/member','',1,'geren',0),
(62,45,'会员等级','member/level','',1,'tubiao',0),
(63,45,'积分变化','member/growth','',1,'bianji',0),
(64,45,'统计信息','member/statistics','',1,'sql',0),
(65,46,'首页推荐','content/index','',1,'shouye',0),
(66,46,'分类热门','content/category','',1,'zhedie',0),
(67,46,'评论管理','content/comments','',1,'pinglun',0),
(68,41,'spu管理','product/spu','',1,'config',0),
(69,41,'发布商品','product/spuadd','',1,'bianji',0),
(70,43,'采购单维护','','',0,'tubiao',0),
(71,70,'采购需求','houseware/purchasedetail','',1,'editor',0),
(72,70,'采购单','houseware/purchase','',1,'menu',0),
(73,41,'商品管理','product/manager','',1,'zonghe',0),
(74,42,'会员价格','coupon/memberprice','',1,'admin',0),
(75,42,'每日秒杀','coupon/seckillsession','',1,'job',0),
(76,37,'规格维护','product/attrupdate','',2,'log',0);

/*Table structure for table `sys_oss` */

DROP TABLE IF EXISTS `sys_oss`;

CREATE TABLE `sys_oss` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `url` varchar(200) DEFAULT NULL COMMENT 'URL地址',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文件上传';

/*Data for the table `sys_oss` */

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) DEFAULT NULL COMMENT '角色名称',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色';

/*Data for the table `sys_role` */

/*Table structure for table `sys_role_menu` */

DROP TABLE IF EXISTS `sys_role_menu`;

CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  `menu_id` bigint DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色与菜单对应关系';

/*Data for the table `sys_role_menu` */

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `salt` varchar(20) DEFAULT NULL COMMENT '盐',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(100) DEFAULT NULL COMMENT '手机号',
  `status` tinyint DEFAULT NULL COMMENT '状态  0：禁用   1：正常',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户';

/*Data for the table `sys_user` */

insert  into `sys_user`(`user_id`,`username`,`password`,`salt`,`email`,`mobile`,`status`,`create_user_id`,`create_time`) values 
(1,'admin','9ec9750e709431dad22365cabc5c625482e574c74adaebba7dd02f1129e4ce1d','YzcmCZNvbXocrsz9dm8e','root@renren.io','13612345678',1,1,'2016-11-11 11:11:11'),
(2,'采购员1','a6ae720cf5092cfaafbd4231944c908515f4013e9f83ce1e442bfee0ff2f4aae','yxczCRd9yRpDQ8Y6wExR','123@qq.com','12345678910',1,1,'2022-02-15 18:34:39');

/*Table structure for table `sys_user_role` */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与角色对应关系';

/*Data for the table `sys_user_role` */

/*Table structure for table `sys_user_token` */

DROP TABLE IF EXISTS `sys_user_token`;

CREATE TABLE `sys_user_token` (
  `user_id` bigint NOT NULL,
  `token` varchar(100) NOT NULL COMMENT 'token',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户Token';

/*Data for the table `sys_user_token` */

insert  into `sys_user_token`(`user_id`,`token`,`expire_time`,`update_time`) values 
(1,'0d72feb58cad4913d366d248ae2ae731','2022-03-30 00:02:53','2022-03-29 12:02:53');

/*Table structure for table `tb_user` */

DROP TABLE IF EXISTS `tb_user`;

CREATE TABLE `tb_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `mobile` varchar(20) NOT NULL COMMENT '手机号',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户';

/*Data for the table `tb_user` */

insert  into `tb_user`(`user_id`,`username`,`mobile`,`password`,`create_time`) values 
(1,'mark','13612345678','8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918','2017-03-23 22:37:41');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
