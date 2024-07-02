CREATE SCHEMA codelink COLLATE utf8mb4_general_ci;;
ALTER DATABASE codelink CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

use codelink;

-- 用户表
CREATE TABLE user
(
	uid 								BIGINT(7) auto_increment														COMMENT 'uid' 	PRIMARY KEY,
	userAccount 						VARCHAR(256)																	NULL COMMENT '用户名',
	username 							VARCHAR(256)																	NULL COMMENT '用户昵称',
	userPassword 					    VARCHAR(256)																	NULL COMMENT '用户密码',
	userAvatar 							VARCHAR(1024)	 DEFAULT 'https://s2.loli.net/2024/01/02/2jxDOEZhTS9HUi3.png'	COMMENT '用户头像',
	userGender 							TINYINT	DEFAULT 0																NOT NULL COMMENT '性别 0-未知 1-男  2-女',
	userAge 							TINYINT																			NULL COMMENT '年龄',
	userArea 							VARCHAR(256)																	NULL COMMENT '地区',
	userPhone  							VARCHAR(256)																	NULL COMMENT '电话',
	userEmail 							VARCHAR(256)																	NULL COMMENT '邮箱',
	userTags 							VARCHAR(1024)                     										    	NULL COMMENT '标签 json 列表',
	github								VARCHAR(1024)																	NULL COMMENT 'github个人主页',
	personalWeb						    VARCHAR(1024)																	NULL COMMENT '个人博客',
	csdn								VARCHAR(1024)																	NULL COMMENT 'csdn个人主页',
    teamIds								VARCHAR(512)                   											        NULL COMMENT '队伍tid列表',
    friendsIds							VARCHAR(512)                      										        NULL COMMENT '好友uid列表',
	pid									VARCHAR(1024)																	NULL COMMENT '参加的项目',
    createTime 							datetime DEFAULT CURRENT_TIMESTAMP		                                        NULL COMMENT '创建时间',
    updateTime 							datetime DEFAULT CURRENT_TIMESTAMP		                                        NULL COMMENT '更新时间'
)COMMENT '用户表';
ALTER TABLE user AUTO_INCREMENT=1000001;
-- ALTER TABLE user ADD pid VARCHAR(1024) NULL COMMENT '参加的项目';


-- 项目表
CREATE TABLE project
(
	pid									BIGINT(6) auto_increment														COMMENT 'pid' PRIMARY KEY,
	createUserId						BIGINT																			NULL COMMENT '项目发起人uid',
	tid									BIGINT																			NULL COMMENT '对应队伍的tid',
	projectName						    VARCHAR(256)																	NULL COMMENT '项目名称',
	projectDes							VARCHAR(1024)																	NULL COMMENT '项目简介',
	projectTags							VARCHAR(1024)																	NULL COMMENT '项目标签',
	needTags							VARCHAR(1024)																	NULL COMMENT '项目所需标签',
	createTime 							datetime DEFAULT CURRENT_TIMESTAMP		                                        NULL COMMENT '创建时间',
	isFinish 							TINYINT	DEFAULT 0																NOT NULL COMMENT '是否已经完成'
)COMMENT '项目表';
ALTER TABLE project AUTO_INCREMENT=100001;


-- 队伍表
CREATE TABLE team
(
	tid									BIGINT(6) auto_increment														COMMENT 'tid' PRIMARY KEY,
	pid									BIGINT																			COMMENT '队伍对应的pid',
	createUserId						BIGINT																			NULL COMMENT '队长uid',
	usersId								VARCHAR(1024)																	NULL COMMENT '队员id',
	teamAvatar							VARCHAR(1024)	DEFAULT 'https://sm.ms/image/nwbmQvJphgfrAxz.png'	            COMMENT '队伍头像',
	isPersonal							TINYINT DEFAULT 0																NOT NULL COMMENT '是否是私人队伍',
	teamPassword					    VARCHAR(256)																	NULL COMMENT '队伍密码',
	isFull								TINYINT  DEFAULT 0          													NOT NULL COMMENT '是否满员 0-未满员 1-满员',
	createTime 							datetime DEFAULT CURRENT_TIMESTAMP		                                        NULL COMMENT '创建时间',
    updateTime 							datetime DEFAULT CURRENT_TIMESTAMP		                                        NULL COMMENT '更新时间',
	deleteTime							datetime 																		NULL COMMENT '删除时间',
    isDelete 							TINYINT  DEFAULT 0          													NOT NULL COMMENT '是否删除'
)COMMENT '队伍表';
ALTER TABLE team AUTO_INCREMENT=100001; 

-- 队伍消息表
CREATE TABLE team_message
(
	nid         						BIGINT(11) auto_increment 														COMMENT '消息id' PRIMARY KEY,
	recipientUid						BIGINT																			NULL COMMENT '接收人uid',
	tid									BIGINT																			COMMENT '被申请队伍tid',
	createUserId						BIGINT																			NULL COMMENT '队长uid',
	applyUserId							BIGINT																			NOT NULL DEFAULT 0 COMMENT '申请人uid',
	applyMessage					    TINYINT  																		NOT NULL DEFAULT 4 COMMENT '加入申请 0-申请中 1-通过 2-拒绝 3-已满员 4-不是此类消息',
	quitMessage							TINYINT																			NOT NULL DEFAULT 0 COMMENT '退出消息 0-不是此类消息 1-是此类消息',
	breakMessage					    TINYINT																			NOT NULL DEFAULT 0 COMMENT '解散消息 0-不是此类消息 1-是此类消息',
	FinishedMessage			            TINYINT																			NOT NULL DEFAULT 0 COMMENT '完成消息 0-不是此类消息 1-是此类消息',
	isRead								TINYINT  																		NOT NULL COMMENT '是否已读 0-未读 1-已读',
	remark     							VARCHAR(256)                      											    NULL COMMENT '申请备注信息',
	createTime 							datetime DEFAULT CURRENT_TIMESTAMP		                                        NULL COMMENT '创建时间'
)COMMENT '队伍消息表';

-- 好友表
CREATE TABLE friends
(
	uid									BIGINT																			COMMENT '用户id' PRIMARY KEY,
	friendsUid							VARCHAR(1024)																	NULL COMMENT '好友用户id' ,
	createTime 							datetime DEFAULT CURRENT_TIMESTAMP		                                        NULL COMMENT '创建时间'
)COMMENT '好友表';
INSERT INTO friends(uid, friendsUid) VALUES (1000006, '[1000001, 1000007]');
INSERT INTO friends(uid, friendsUid) VALUES (1000001, '[1000006, 1000007]');


-- 好友申请表
CREATE TABLE friends_application
(
	aid         					    BIGINT(11) auto_increment 														COMMENT '申请id' PRIMARY KEY,
	uid									BIGINT																			COMMENT '被申请人uid' ,
	applyUserId							BIGINT																			NOT NULL COMMENT '申请人uid',
	applyStatus							TINYINT  																		NOT NULL DEFAULT 0 COMMENT '申请状态 0-申请中 1-通过 2-拒绝 3-过期',
	isRead								TINYINT  																		NOT NULL DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
	remark     							VARCHAR(256)                      									    		NULL COMMENT '申请备注信息',
	createTime 							datetime DEFAULT CURRENT_TIMESTAMP		                                        NULL COMMENT '创建时间',
    isDelete 							TINYINT  DEFAULT 0          													NOT NULL COMMENT '是否删除'
)COMMENT '好友申请表';

-- 聊天消息表
CREATE TABLE chat
(
	chatId         						BIGINT(11) auto_increment 														COMMENT '聊天记录id' PRIMARY KEY,
	fromUid     						BIGINT                                 											NOT NULL COMMENT '发送消息Uid',
	toUid      							BIGINT                                  										NULL COMMENT '接收消息Uid',
	text       							VARCHAR(512) collate utf8mb4_unicode_ci 		                                NULL COMMENT '内容',
	chatType   							TINYINT                                 										NOT NULL COMMENT '聊天类型 1-私聊 2-群聊',
	createTime 							datetime DEFAULT CURRENT_TIMESTAMP		                                        NULL COMMENT '创建时间',
	tid     							BIGINT                                  										NULL COMMENT '队伍聊天时的队伍id',
    isRead								TINYINT  																		NOT NULL DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
    isWithdraw							TINYINT  																		NOT NULL DEFAULT 0 COMMENT '是否撤回 0-未撤回 1-已撤回'
)COMMENT '聊天消息表';
ALTER TABLE chat AUTO_INCREMENT=10000000001;


-- 网站样例表
CREATE TABLE web_example
(
	wid									BIGINT(4) auto_increment 														COMMENT 'wid' PRIMARY KEY,
	webname								VARCHAR(256)																	NOT NULL COMMENT '网站样例名',
	webDesc								VARCHAR(256)																	NULL COMMENT '网站简介',
	webSite								VARCHAR(1024)																	NOT NULL COMMENT '网站预览地址',
	webPic								VARCHAR(1024)																	NULL COMMENT '网站预览图片',
	webDownloadSite			            VARCHAR(1024)																	NOT NULL COMMENT '网站下载地址'
)COMMENT '网站样例表';
ALTER TABLE web_example AUTO_INCREMENT=1001;
INSERT INTO web_example(webname, webDesc, webSite, webPic, webDownloadSite) VALUES ('用户管理系统', '企业中最常用的用户管理系统, 预览账号：admin，预览密码：66666666', 'https://joy-admin.ojason.top/user/login', 'https://s2.loli.net/2024/03/08/DMeciynWqHR4owg.png', 'http://gitlab.code-nav.cn/2650498254/joy-admin-end');
INSERT INTO web_example(webname, webDesc, webSite, webPic, webDownloadSite) VALUES ('智评判题系统', '在线评测系统，能根据预先设定的测试用例对代码进行判定, 预览账号：test1234，预览密码：12345678', 'http://www.zpoj.online/login', 'https://s2.loli.net/2024/03/08/LIoD5TZA84thmMn.png', '暂无');


